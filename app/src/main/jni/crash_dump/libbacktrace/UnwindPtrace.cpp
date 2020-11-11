/*
 * Copyright (C) 2013 The Android Open Source Project
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

#include <stdint.h>
#include <sys/types.h>
#include <ucontext.h>

#include <libunwind.h>
#include <libunwind-ptrace.h>

#include <backtrace/Backtrace.h>
#include <backtrace/BacktraceMap.h>

#include "BacktraceLog.h"
#include "UnwindMap.h"
#include "UnwindPtrace.h"

UnwindPtrace::UnwindPtrace(pid_t pid, pid_t tid, BacktraceMap* map)
    : BacktracePtrace(pid, tid, map), addr_space_(nullptr), upt_info_(nullptr) {
}

UnwindPtrace::~UnwindPtrace() {
  if (upt_info_) {
    _UPT_destroy(upt_info_);
    upt_info_ = nullptr;
  }
  if (addr_space_) {
    // Remove the map from the address space before destroying it.
    // It will be freed in the UnwindMap destructor.
    unw_map_set(addr_space_, nullptr);

    unw_destroy_addr_space(addr_space_);
    addr_space_ = nullptr;
  }
}

bool UnwindPtrace::Unwind(size_t num_ignore_frames, ucontext_t* ucontext) {
  if (GetMap() == nullptr) {
    // Without a map object, we can't do anything.
    error_ = BACKTRACE_UNWIND_ERROR_MAP_MISSING;
    return false;
  }

  error_ = BACKTRACE_UNWIND_NO_ERROR;

  if (ucontext) {
    BACK_LOGW("Unwinding from a specified context not supported yet.");
    error_ = BACKTRACE_UNWIND_ERROR_UNSUPPORTED_OPERATION;
    return false;
  }

  addr_space_ = unw_create_addr_space(&_UPT_accessors, 0);
  if (!addr_space_) {
    BACK_LOGW("unw_create_addr_space failed.");
    error_ = BACKTRACE_UNWIND_ERROR_SETUP_FAILED;
    return false;
  }

  UnwindMap* map = static_cast<UnwindMap*>(GetMap());
  unw_map_set(addr_space_, map->GetMapCursor());

  upt_info_ = reinterpret_cast<struct UPT_info*>(_UPT_create(Tid()));
  if (!upt_info_) {
    BACK_LOGW("Failed to create upt info.");
    error_ = BACKTRACE_UNWIND_ERROR_SETUP_FAILED;
    return false;
  }

  unw_cursor_t cursor;
  int ret = unw_init_remote(&cursor, addr_space_, upt_info_);
  if (ret < 0) {
    BACK_LOGW("unw_init_remote failed %d", ret);
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
      frames_.resize(num_frames+1);
      backtrace_frame_data_t* frame = &frames_.at(num_frames);
      frame->num = num_frames;
      frame->pc = static_cast<uintptr_t>(pc);
      frame->sp = static_cast<uintptr_t>(sp);
      frame->stack_size = 0;

      if (num_frames > 0) {
        backtrace_frame_data_t* prev = &frames_.at(num_frames-1);
        prev->stack_size = frame->sp - prev->sp;
      }

      frame->func_name = GetFunctionName(frame->pc, &frame->func_offset);

      FillInMap(frame->pc, &frame->map);

      num_frames++;
    } else {
      num_ignore_frames--;
    }
    ret = unw_step (&cursor);
  } while (ret > 0 && num_frames < MAX_BACKTRACE_FRAMES);

  return true;
}

std::string UnwindPtrace::GetFunctionNameRaw(uintptr_t pc, uintptr_t* offset) {
  *offset = 0;
  char buf[512];
  unw_word_t value;
  if (unw_get_proc_name_by_ip(addr_space_, pc, buf, sizeof(buf), &value,
                              upt_info_) >= 0 && buf[0] != '\0') {
    *offset = static_cast<uintptr_t>(value);
    return buf;
  }
  return "";
}
