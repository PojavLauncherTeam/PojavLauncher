/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include "BacktraceOffline.h"

extern "C" {
#define UNW_REMOTE_ONLY
#include <dwarf.h>
}

#include <stdint.h>
#include <stdio.h>
#include <string.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <ucontext.h>
#include <unistd.h>

#include <memory>
#include <string>
#include <vector>

#include <android-base/file.h>
#include <backtrace/Backtrace.h>
#include <backtrace/BacktraceMap.h>
#include <ziparchive/zip_archive.h>

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunused-parameter"

#include <llvm/ADT/StringRef.h>
#include <llvm/Object/Binary.h>
#include <llvm/Object/ELFObjectFile.h>
#include <llvm/Object/ObjectFile.h>

#pragma clang diagnostic pop

#include "BacktraceLog.h"

void Space::Clear() {
  start = 0;
  end = 0;
  data = nullptr;
}

size_t Space::Read(uint64_t addr, uint8_t* buffer, size_t size) {
  if (addr >= start && addr < end) {
    size_t read_size = std::min(size, static_cast<size_t>(end - addr));
    memcpy(buffer, data + (addr - start), read_size);
    return read_size;
  }
  return 0;
}

static int FindProcInfo(unw_addr_space_t addr_space, unw_word_t ip, unw_proc_info* proc_info,
                        int need_unwind_info, void* arg) {
  BacktraceOffline* backtrace = reinterpret_cast<BacktraceOffline*>(arg);
  bool result = backtrace->FindProcInfo(addr_space, ip, proc_info, need_unwind_info);
  return result ? 0 : -UNW_EINVAL;
}

static void PutUnwindInfo(unw_addr_space_t, unw_proc_info_t*, void*) {
}

static int GetDynInfoListAddr(unw_addr_space_t, unw_word_t*, void*) {
  return -UNW_ENOINFO;
}

static int AccessMem(unw_addr_space_t, unw_word_t addr, unw_word_t* value, int write, void* arg) {
  if (write == 1) {
    return -UNW_EINVAL;
  }
  BacktraceOffline* backtrace = reinterpret_cast<BacktraceOffline*>(arg);
  *value = 0;
  size_t read_size = backtrace->Read(addr, reinterpret_cast<uint8_t*>(value), sizeof(unw_word_t));
  // Strictly we should check if read_size matches sizeof(unw_word_t), but it is possible in
  // .eh_frame_hdr that the section can end at a position not aligned in sizeof(unw_word_t), and
  // we should permit the read at the end of the section.
  return (read_size > 0u ? 0 : -UNW_EINVAL);
}

static int AccessReg(unw_addr_space_t, unw_regnum_t unwind_reg, unw_word_t* value, int write,
                     void* arg) {
  if (write == 1) {
    return -UNW_EINVAL;
  }
  BacktraceOffline* backtrace = reinterpret_cast<BacktraceOffline*>(arg);
  uint64_t reg_value;
  bool result = backtrace->ReadReg(unwind_reg, &reg_value);
  if (result) {
    *value = static_cast<unw_word_t>(reg_value);
  }
  return result ? 0 : -UNW_EINVAL;
}

static int AccessFpReg(unw_addr_space_t, unw_regnum_t, unw_fpreg_t*, int, void*) {
  return -UNW_EINVAL;
}

static int Resume(unw_addr_space_t, unw_cursor_t*, void*) {
  return -UNW_EINVAL;
}

static int GetProcName(unw_addr_space_t, unw_word_t, char*, size_t, unw_word_t*, void*) {
  return -UNW_EINVAL;
}

static unw_accessors_t accessors = {
    .find_proc_info = FindProcInfo,
    .put_unwind_info = PutUnwindInfo,
    .get_dyn_info_list_addr = GetDynInfoListAddr,
    .access_mem = AccessMem,
    .access_reg = AccessReg,
    .access_fpreg = AccessFpReg,
    .resume = Resume,
    .get_proc_name = GetProcName,
};

bool BacktraceOffline::Unwind(size_t num_ignore_frames, ucontext_t* context) {
  if (context == nullptr) {
    BACK_LOGW("The context is needed for offline backtracing.");
    error_ = BACKTRACE_UNWIND_ERROR_NO_CONTEXT;
    return false;
  }
  context_ = context;
  error_ = BACKTRACE_UNWIND_NO_ERROR;

  unw_addr_space_t addr_space = unw_create_addr_space(&accessors, 0);
  unw_cursor_t cursor;
  int ret = unw_init_remote(&cursor, addr_space, this);
  if (ret != 0) {
    BACK_LOGW("unw_init_remote failed %d", ret);
    unw_destroy_addr_space(addr_space);
    error_ = BACKTRACE_UNWIND_ERROR_SETUP_FAILED;
    return false;
  }
  size_t num_frames = 0;
  do {
    unw_word_t pc;
    ret = unw_get_reg(&cursor, UNW_REG_IP, &pc);
    if (ret < 0) {
      BACK_LOGW("Failed to read IP %d", ret);
      break;
    }
    unw_word_t sp;
    ret = unw_get_reg(&cursor, UNW_REG_SP, &sp);
    if (ret < 0) {
      BACK_LOGW("Failed to read SP %d", ret);
      break;
    }

    if (num_ignore_frames == 0) {
      frames_.resize(num_frames + 1);
      backtrace_frame_data_t* frame = &frames_[num_frames];
      frame->num = num_frames;
      frame->pc = static_cast<uintptr_t>(pc);
      frame->sp = static_cast<uintptr_t>(sp);
      frame->stack_size = 0;

      if (num_frames > 0) {
        backtrace_frame_data_t* prev = &frames_[num_frames - 1];
        prev->stack_size = frame->sp - prev->sp;
      }
      frame->func_name = GetFunctionName(frame->pc, &frame->func_offset);
      FillInMap(frame->pc, &frame->map);
      num_frames++;
    } else {
      num_ignore_frames--;
    }
    ret = unw_step(&cursor);
  } while (ret > 0 && num_frames < MAX_BACKTRACE_FRAMES);

  unw_destroy_addr_space(addr_space);
  context_ = nullptr;
  return true;
}

bool BacktraceOffline::ReadWord(uintptr_t ptr, word_t* out_value) {
  size_t bytes_read = Read(ptr, reinterpret_cast<uint8_t*>(out_value), sizeof(word_t));
  return bytes_read == sizeof(word_t);
}

size_t BacktraceOffline::Read(uintptr_t addr, uint8_t* buffer, size_t bytes) {
  // Normally, libunwind needs stack information and call frame information to do remote unwinding.
  // If call frame information is stored in .debug_frame, libunwind can read it from file
  // by itself. If call frame information is stored in .eh_frame, we need to provide data in
  // .eh_frame/.eh_frame_hdr sections.
  // The order of readings below doesn't matter, as the spaces don't overlap with each other.
  size_t read_size = eh_frame_hdr_space_.Read(addr, buffer, bytes);
  if (read_size != 0) {
    return read_size;
  }
  read_size = eh_frame_space_.Read(addr, buffer, bytes);
  if (read_size != 0) {
    return read_size;
  }
  read_size = stack_space_.Read(addr, buffer, bytes);
  return read_size;
}

static bool FileOffsetToVaddr(
    const std::vector<DebugFrameInfo::EhFrame::ProgramHeader>& program_headers,
    uint64_t file_offset, uint64_t* vaddr) {
  for (auto& header : program_headers) {
    if (file_offset >= header.file_offset && file_offset < header.file_offset + header.file_size) {
      // TODO: Consider load_bias?
      *vaddr = file_offset - header.file_offset + header.vaddr;
      return true;
    }
  }
  return false;
}

bool BacktraceOffline::FindProcInfo(unw_addr_space_t addr_space, uint64_t ip,
                                    unw_proc_info_t* proc_info, int need_unwind_info) {
  backtrace_map_t map;
  FillInMap(ip, &map);
  if (!BacktraceMap::IsValid(map)) {
    return false;
  }
  const std::string& filename = map.name;
  DebugFrameInfo* debug_frame = GetDebugFrameInFile(filename);
  if (debug_frame == nullptr) {
    return false;
  }
  if (debug_frame->is_eh_frame) {
    uint64_t ip_offset = ip - map.start + map.offset;
    uint64_t ip_vaddr;  // vaddr in the elf file.
    bool result = FileOffsetToVaddr(debug_frame->eh_frame.program_headers, ip_offset, &ip_vaddr);
    if (!result) {
      return false;
    }
    // Calculate the addresses where .eh_frame_hdr and .eh_frame stay when the process was running.
    eh_frame_hdr_space_.start = (ip - ip_vaddr) + debug_frame->eh_frame.eh_frame_hdr_vaddr;
    eh_frame_hdr_space_.end =
        eh_frame_hdr_space_.start + debug_frame->eh_frame.eh_frame_hdr_data.size();
    eh_frame_hdr_space_.data = debug_frame->eh_frame.eh_frame_hdr_data.data();

    eh_frame_space_.start = (ip - ip_vaddr) + debug_frame->eh_frame.eh_frame_vaddr;
    eh_frame_space_.end = eh_frame_space_.start + debug_frame->eh_frame.eh_frame_data.size();
    eh_frame_space_.data = debug_frame->eh_frame.eh_frame_data.data();

    unw_dyn_info di;
    memset(&di, '\0', sizeof(di));
    di.start_ip = map.start;
    di.end_ip = map.end;
    di.format = UNW_INFO_FORMAT_REMOTE_TABLE;
    di.u.rti.name_ptr = 0;
    di.u.rti.segbase = eh_frame_hdr_space_.start;
    di.u.rti.table_data =
        eh_frame_hdr_space_.start + debug_frame->eh_frame.fde_table_offset_in_eh_frame_hdr;
    di.u.rti.table_len = (eh_frame_hdr_space_.end - di.u.rti.table_data) / sizeof(unw_word_t);
    int ret = dwarf_search_unwind_table(addr_space, ip, &di, proc_info, need_unwind_info, this);
    return ret == 0;
  }

  eh_frame_hdr_space_.Clear();
  eh_frame_space_.Clear();
  unw_dyn_info_t di;
  unw_word_t segbase = map.start - map.offset;
  int found = dwarf_find_debug_frame(0, &di, ip, segbase, filename.c_str(), map.start, map.end);
  if (found == 1) {
    int ret = dwarf_search_unwind_table(addr_space, ip, &di, proc_info, need_unwind_info, this);
    return ret == 0;
  }
  return false;
}

bool BacktraceOffline::ReadReg(size_t reg, uint64_t* value) {
  bool result = true;
#if defined(__arm__)
  switch (reg) {
    case UNW_ARM_R0:
      *value = context_->uc_mcontext.arm_r0;
      break;
    case UNW_ARM_R1:
      *value = context_->uc_mcontext.arm_r1;
      break;
    case UNW_ARM_R2:
      *value = context_->uc_mcontext.arm_r2;
      break;
    case UNW_ARM_R3:
      *value = context_->uc_mcontext.arm_r3;
      break;
    case UNW_ARM_R4:
      *value = context_->uc_mcontext.arm_r4;
      break;
    case UNW_ARM_R5:
      *value = context_->uc_mcontext.arm_r5;
      break;
    case UNW_ARM_R6:
      *value = context_->uc_mcontext.arm_r6;
      break;
    case UNW_ARM_R7:
      *value = context_->uc_mcontext.arm_r7;
      break;
    case UNW_ARM_R8:
      *value = context_->uc_mcontext.arm_r8;
      break;
    case UNW_ARM_R9:
      *value = context_->uc_mcontext.arm_r9;
      break;
    case UNW_ARM_R10:
      *value = context_->uc_mcontext.arm_r10;
      break;
    case UNW_ARM_R11:
      *value = context_->uc_mcontext.arm_fp;
      break;
    case UNW_ARM_R12:
      *value = context_->uc_mcontext.arm_ip;
      break;
    case UNW_ARM_R13:
      *value = context_->uc_mcontext.arm_sp;
      break;
    case UNW_ARM_R14:
      *value = context_->uc_mcontext.arm_lr;
      break;
    case UNW_ARM_R15:
      *value = context_->uc_mcontext.arm_pc;
      break;
    default:
      result = false;
  }
#elif defined(__aarch64__)
  if (reg <= UNW_AARCH64_PC) {
    *value = context_->uc_mcontext.regs[reg];
  } else {
    result = false;
  }
#elif defined(__x86_64__)
  switch (reg) {
    case UNW_X86_64_R8:
      *value = context_->uc_mcontext.gregs[REG_R8];
      break;
    case UNW_X86_64_R9:
      *value = context_->uc_mcontext.gregs[REG_R9];
      break;
    case UNW_X86_64_R10:
      *value = context_->uc_mcontext.gregs[REG_R10];
      break;
    case UNW_X86_64_R11:
      *value = context_->uc_mcontext.gregs[REG_R11];
      break;
    case UNW_X86_64_R12:
      *value = context_->uc_mcontext.gregs[REG_R12];
      break;
    case UNW_X86_64_R13:
      *value = context_->uc_mcontext.gregs[REG_R13];
      break;
    case UNW_X86_64_R14:
      *value = context_->uc_mcontext.gregs[REG_R14];
      break;
    case UNW_X86_64_R15:
      *value = context_->uc_mcontext.gregs[REG_R15];
      break;
    case UNW_X86_64_RDI:
      *value = context_->uc_mcontext.gregs[REG_RDI];
      break;
    case UNW_X86_64_RSI:
      *value = context_->uc_mcontext.gregs[REG_RSI];
      break;
    case UNW_X86_64_RBP:
      *value = context_->uc_mcontext.gregs[REG_RBP];
      break;
    case UNW_X86_64_RBX:
      *value = context_->uc_mcontext.gregs[REG_RBX];
      break;
    case UNW_X86_64_RDX:
      *value = context_->uc_mcontext.gregs[REG_RDX];
      break;
    case UNW_X86_64_RAX:
      *value = context_->uc_mcontext.gregs[REG_RAX];
      break;
    case UNW_X86_64_RCX:
      *value = context_->uc_mcontext.gregs[REG_RCX];
      break;
    case UNW_X86_64_RSP:
      *value = context_->uc_mcontext.gregs[REG_RSP];
      break;
    case UNW_X86_64_RIP:
      *value = context_->uc_mcontext.gregs[REG_RIP];
      break;
    default:
      result = false;
  }
#elif defined(__i386__)
  switch (reg) {
    case UNW_X86_GS:
      *value = context_->uc_mcontext.gregs[REG_GS];
      break;
    case UNW_X86_FS:
      *value = context_->uc_mcontext.gregs[REG_FS];
      break;
    case UNW_X86_ES:
      *value = context_->uc_mcontext.gregs[REG_ES];
      break;
    case UNW_X86_DS:
      *value = context_->uc_mcontext.gregs[REG_DS];
      break;
    case UNW_X86_EAX:
      *value = context_->uc_mcontext.gregs[REG_EAX];
      break;
    case UNW_X86_EBX:
      *value = context_->uc_mcontext.gregs[REG_EBX];
      break;
    case UNW_X86_ECX:
      *value = context_->uc_mcontext.gregs[REG_ECX];
      break;
    case UNW_X86_EDX:
      *value = context_->uc_mcontext.gregs[REG_EDX];
      break;
    case UNW_X86_ESI:
      *value = context_->uc_mcontext.gregs[REG_ESI];
      break;
    case UNW_X86_EDI:
      *value = context_->uc_mcontext.gregs[REG_EDI];
      break;
    case UNW_X86_EBP:
      *value = context_->uc_mcontext.gregs[REG_EBP];
      break;
    case UNW_X86_EIP:
      *value = context_->uc_mcontext.gregs[REG_EIP];
      break;
    case UNW_X86_ESP:
      *value = context_->uc_mcontext.gregs[REG_ESP];
      break;
    case UNW_X86_TRAPNO:
      *value = context_->uc_mcontext.gregs[REG_TRAPNO];
      break;
    case UNW_X86_CS:
      *value = context_->uc_mcontext.gregs[REG_CS];
      break;
    case UNW_X86_EFLAGS:
      *value = context_->uc_mcontext.gregs[REG_EFL];
      break;
    case UNW_X86_SS:
      *value = context_->uc_mcontext.gregs[REG_SS];
      break;
    default:
      result = false;
  }
#endif
  return result;
}

std::string BacktraceOffline::GetFunctionNameRaw(uintptr_t, uintptr_t* offset) {
  // We don't have enough information to support this. And it is expensive.
  *offset = 0;
  return "";
}

std::unordered_map<std::string, std::unique_ptr<DebugFrameInfo>> BacktraceOffline::debug_frames_;
std::unordered_set<std::string> BacktraceOffline::debug_frame_missing_files_;

static DebugFrameInfo* ReadDebugFrameFromFile(const std::string& filename);

DebugFrameInfo* BacktraceOffline::GetDebugFrameInFile(const std::string& filename) {
  if (cache_file_) {
    auto it = debug_frames_.find(filename);
    if (it != debug_frames_.end()) {
      return it->second.get();
    }
    if (debug_frame_missing_files_.find(filename) != debug_frame_missing_files_.end()) {
      return nullptr;
    }
  }
  DebugFrameInfo* debug_frame = ReadDebugFrameFromFile(filename);
  if (cache_file_) {
    if (debug_frame != nullptr) {
      debug_frames_.emplace(filename, std::unique_ptr<DebugFrameInfo>(debug_frame));
    } else {
      debug_frame_missing_files_.insert(filename);
    }
  } else {
    if (last_debug_frame_ != nullptr) {
      delete last_debug_frame_;
    }
    last_debug_frame_ = debug_frame;
  }
  return debug_frame;
}

static bool OmitEncodedValue(uint8_t encode, const uint8_t*& p) {
  if (encode == DW_EH_PE_omit) {
    return 0;
  }
  uint8_t format = encode & 0x0f;
  switch (format) {
    case DW_EH_PE_ptr:
      p += sizeof(unw_word_t);
      break;
    case DW_EH_PE_uleb128:
    case DW_EH_PE_sleb128:
      while ((*p & 0x80) != 0) {
        ++p;
      }
      ++p;
      break;
    case DW_EH_PE_udata2:
    case DW_EH_PE_sdata2:
      p += 2;
      break;
    case DW_EH_PE_udata4:
    case DW_EH_PE_sdata4:
      p += 4;
      break;
    case DW_EH_PE_udata8:
    case DW_EH_PE_sdata8:
      p += 8;
      break;
    default:
      return false;
  }
  return true;
}

static bool GetFdeTableOffsetInEhFrameHdr(const std::vector<uint8_t>& data,
                                          uint64_t* table_offset_in_eh_frame_hdr) {
  const uint8_t* p = data.data();
  const uint8_t* end = p + data.size();
  if (p + 4 > end) {
    return false;
  }
  uint8_t version = *p++;
  if (version != 1) {
    return false;
  }
  uint8_t eh_frame_ptr_encode = *p++;
  uint8_t fde_count_encode = *p++;
  uint8_t fde_table_encode = *p++;

  if (fde_table_encode != (DW_EH_PE_datarel | DW_EH_PE_sdata4)) {
    return false;
  }

  if (!OmitEncodedValue(eh_frame_ptr_encode, p) || !OmitEncodedValue(fde_count_encode, p)) {
    return false;
  }
  if (p >= end) {
    return false;
  }
  *table_offset_in_eh_frame_hdr = p - data.data();
  return true;
}

using ProgramHeader = DebugFrameInfo::EhFrame::ProgramHeader;

template <class ELFT>
DebugFrameInfo* ReadDebugFrameFromELFFile(const llvm::object::ELFFile<ELFT>* elf) {
  bool has_eh_frame_hdr = false;
  uint64_t eh_frame_hdr_vaddr = 0;
  std::vector<uint8_t> eh_frame_hdr_data;
  bool has_eh_frame = false;
  uint64_t eh_frame_vaddr = 0;
  std::vector<uint8_t> eh_frame_data;

  for (auto it = elf->section_begin(); it != elf->section_end(); ++it) {
    llvm::ErrorOr<llvm::StringRef> name = elf->getSectionName(&*it);
    if (name) {
      if (name.get() == ".debug_frame") {
        DebugFrameInfo* debug_frame = new DebugFrameInfo;
        debug_frame->is_eh_frame = false;
        return debug_frame;
      }
      if (name.get() == ".eh_frame_hdr") {
        has_eh_frame_hdr = true;
        eh_frame_hdr_vaddr = it->sh_addr;
        llvm::ErrorOr<llvm::ArrayRef<uint8_t>> data = elf->getSectionContents(&*it);
        if (data) {
          eh_frame_hdr_data.insert(eh_frame_hdr_data.begin(), data->data(),
                                   data->data() + data->size());
        } else {
          return nullptr;
        }
      } else if (name.get() == ".eh_frame") {
        has_eh_frame = true;
        eh_frame_vaddr = it->sh_addr;
        llvm::ErrorOr<llvm::ArrayRef<uint8_t>> data = elf->getSectionContents(&*it);
        if (data) {
          eh_frame_data.insert(eh_frame_data.begin(), data->data(), data->data() + data->size());
        } else {
          return nullptr;
        }
      }
    }
  }
  if (!(has_eh_frame_hdr && has_eh_frame)) {
    return nullptr;
  }
  uint64_t fde_table_offset;
  if (!GetFdeTableOffsetInEhFrameHdr(eh_frame_hdr_data, &fde_table_offset)) {
    return nullptr;
  }

  std::vector<ProgramHeader> program_headers;
  for (auto it = elf->program_header_begin(); it != elf->program_header_end(); ++it) {
    ProgramHeader header;
    header.vaddr = it->p_vaddr;
    header.file_offset = it->p_offset;
    header.file_size = it->p_filesz;
    program_headers.push_back(header);
  }
  DebugFrameInfo* debug_frame = new DebugFrameInfo;
  debug_frame->is_eh_frame = true;
  debug_frame->eh_frame.eh_frame_hdr_vaddr = eh_frame_hdr_vaddr;
  debug_frame->eh_frame.eh_frame_vaddr = eh_frame_vaddr;
  debug_frame->eh_frame.fde_table_offset_in_eh_frame_hdr = fde_table_offset;
  debug_frame->eh_frame.eh_frame_hdr_data = std::move(eh_frame_hdr_data);
  debug_frame->eh_frame.eh_frame_data = std::move(eh_frame_data);
  debug_frame->eh_frame.program_headers = program_headers;
  return debug_frame;
}

static bool IsValidElfPath(const std::string& filename) {
  static const char elf_magic[] = {0x7f, 'E', 'L', 'F'};

  struct stat st;
  if (stat(filename.c_str(), &st) != 0 || !S_ISREG(st.st_mode)) {
    return false;
  }
  FILE* fp = fopen(filename.c_str(), "reb");
  if (fp == nullptr) {
    return false;
  }
  char buf[4];
  if (fread(buf, 4, 1, fp) != 1) {
    fclose(fp);
    return false;
  }
  fclose(fp);
  return memcmp(buf, elf_magic, 4) == 0;
}

static bool IsValidApkPath(const std::string& apk_path) {
  static const char zip_preamble[] = {0x50, 0x4b, 0x03, 0x04};
  struct stat st;
  if (stat(apk_path.c_str(), &st) != 0 || !S_ISREG(st.st_mode)) {
    return false;
  }
  FILE* fp = fopen(apk_path.c_str(), "reb");
  if (fp == nullptr) {
    return false;
  }
  char buf[4];
  if (fread(buf, 4, 1, fp) != 1) {
    fclose(fp);
    return false;
  }
  fclose(fp);
  return memcmp(buf, zip_preamble, 4) == 0;
}

class ScopedZiparchiveHandle {
 public:
  ScopedZiparchiveHandle(ZipArchiveHandle handle) : handle_(handle) {
  }

  ~ScopedZiparchiveHandle() {
    CloseArchive(handle_);
  }

 private:
  ZipArchiveHandle handle_;
};

llvm::object::OwningBinary<llvm::object::Binary> OpenEmbeddedElfFile(const std::string& filename) {
  llvm::object::OwningBinary<llvm::object::Binary> nothing;
  size_t pos = filename.find("!/");
  if (pos == std::string::npos) {
    return nothing;
  }
  std::string apk_file = filename.substr(0, pos);
  std::string elf_file = filename.substr(pos + 2);
  if (!IsValidApkPath(apk_file)) {
    BACK_LOGW("%s is not a valid apk file", apk_file.c_str());
    return nothing;
  }
  ZipArchiveHandle handle;
  int32_t ret_code = OpenArchive(apk_file.c_str(), &handle);
  if (ret_code != 0) {
    CloseArchive(handle);
    BACK_LOGW("failed to open archive %s: %s", apk_file.c_str(), ErrorCodeString(ret_code));
    return nothing;
  }
  ScopedZiparchiveHandle scoped_handle(handle);
  ZipEntry zentry;
  ret_code = FindEntry(handle, ZipString(elf_file.c_str()), &zentry);
  if (ret_code != 0) {
    BACK_LOGW("failed to find %s in %s: %s", elf_file.c_str(), apk_file.c_str(),
              ErrorCodeString(ret_code));
    return nothing;
  }
  if (zentry.method != kCompressStored || zentry.compressed_length != zentry.uncompressed_length) {
    BACK_LOGW("%s is compressed in %s, which doesn't support running directly", elf_file.c_str(),
              apk_file.c_str());
    return nothing;
  }
  auto buffer_or_err = llvm::MemoryBuffer::getOpenFileSlice(GetFileDescriptor(handle), apk_file,
                                                            zentry.uncompressed_length,
                                                            zentry.offset);
  if (!buffer_or_err) {
    BACK_LOGW("failed to read %s in %s: %s", elf_file.c_str(), apk_file.c_str(),
              buffer_or_err.getError().message().c_str());
    return nothing;
  }
  auto binary_or_err = llvm::object::createBinary(buffer_or_err.get()->getMemBufferRef());
  if (!binary_or_err) {
    BACK_LOGW("failed to create binary for %s in %s: %s", elf_file.c_str(), apk_file.c_str(),
              binary_or_err.getError().message().c_str());
    return nothing;
  }
  return llvm::object::OwningBinary<llvm::object::Binary>(std::move(binary_or_err.get()),
                                                          std::move(buffer_or_err.get()));
}

static DebugFrameInfo* ReadDebugFrameFromFile(const std::string& filename) {
  llvm::object::OwningBinary<llvm::object::Binary> owning_binary;
  if (filename.find("!/") != std::string::npos) {
    owning_binary = OpenEmbeddedElfFile(filename);
  } else {
    if (!IsValidElfPath(filename)) {
      return nullptr;
    }
    auto binary_or_err = llvm::object::createBinary(llvm::StringRef(filename));
    if (!binary_or_err) {
      return nullptr;
    }
    owning_binary = std::move(binary_or_err.get());
  }
  llvm::object::Binary* binary = owning_binary.getBinary();
  auto obj = llvm::dyn_cast<llvm::object::ObjectFile>(binary);
  if (obj == nullptr) {
    return nullptr;
  }
  if (auto elf = llvm::dyn_cast<llvm::object::ELF32LEObjectFile>(obj)) {
    return ReadDebugFrameFromELFFile(elf->getELFFile());
  }
  if (auto elf = llvm::dyn_cast<llvm::object::ELF64LEObjectFile>(obj)) {
    return ReadDebugFrameFromELFFile(elf->getELFFile());
  }
  return nullptr;
}

Backtrace* Backtrace::CreateOffline(pid_t pid, pid_t tid, BacktraceMap* map,
                                    const backtrace_stackinfo_t& stack, bool cache_file) {
  return new BacktraceOffline(pid, tid, map, stack, cache_file);
}
