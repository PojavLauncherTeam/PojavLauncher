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

#define LOG_TAG "DEBUG"

#include <elf.h>
#include <stdint.h>
#include <stdlib.h>
#include <string.h>

#include <string>

#include <backtrace/Backtrace.h>
#include <android-base/stringprintf.h>
#include <log/log.h>

#include "elf_utils.h"

#define NOTE_ALIGN(size)  ((size + 3) & ~3)

template <typename HdrType, typename PhdrType, typename NhdrType>
static bool get_build_id(
    Backtrace* backtrace, uintptr_t base_addr, uint8_t* e_ident, std::string* build_id) {
  HdrType hdr;

  memcpy(&hdr.e_ident[0], e_ident, EI_NIDENT);

  // First read the rest of the header.
  if (backtrace->Read(base_addr + EI_NIDENT, reinterpret_cast<uint8_t*>(&hdr) + EI_NIDENT,
                      sizeof(HdrType) - EI_NIDENT) != sizeof(HdrType) - EI_NIDENT) {
    return false;
  }

  for (size_t i = 0; i < hdr.e_phnum; i++) {
    PhdrType phdr;
    if (backtrace->Read(base_addr + hdr.e_phoff + i * hdr.e_phentsize,
                        reinterpret_cast<uint8_t*>(&phdr), sizeof(phdr)) != sizeof(phdr)) {
      return false;
    }
    // Looking for the .note.gnu.build-id note.
    if (phdr.p_type == PT_NOTE) {
      size_t hdr_size = phdr.p_filesz;
      uintptr_t addr = base_addr + phdr.p_offset;
      while (hdr_size >= sizeof(NhdrType)) {
        NhdrType nhdr;
        if (backtrace->Read(addr, reinterpret_cast<uint8_t*>(&nhdr), sizeof(nhdr)) != sizeof(nhdr)) {
          return false;
        }
        addr += sizeof(nhdr);
        if (nhdr.n_type == NT_GNU_BUILD_ID) {
          // Skip the name (which is the owner and should be "GNU").
          addr += NOTE_ALIGN(nhdr.n_namesz);
          uint8_t build_id_data[160];
          if (nhdr.n_descsz > sizeof(build_id_data)) {
            ALOGE("Possible corrupted note, desc size value is too large: %u",
                  nhdr.n_descsz);
            return false;
          }
          if (backtrace->Read(addr, build_id_data, nhdr.n_descsz) != nhdr.n_descsz) {
            return false;
          }

          build_id->clear();
          for (size_t bytes = 0; bytes < nhdr.n_descsz; bytes++) {
            *build_id += android::base::StringPrintf("%02x", build_id_data[bytes]);
          }

          return true;
        } else {
          // Move past the extra note data.
          hdr_size -= sizeof(nhdr);
          size_t skip_bytes = NOTE_ALIGN(nhdr.n_namesz) + NOTE_ALIGN(nhdr.n_descsz);
          addr += skip_bytes;
          if (hdr_size < skip_bytes) {
            break;
          }
          hdr_size -= skip_bytes;
        }
      }
    }
  }
  return false;
}

bool elf_get_build_id(Backtrace* backtrace, uintptr_t addr, std::string* build_id) {
  // Read and verify the elf magic number first.
  uint8_t e_ident[EI_NIDENT];
  if (backtrace->Read(addr, e_ident, SELFMAG) != SELFMAG) {
    return false;
  }

  if (memcmp(e_ident, ELFMAG, SELFMAG) != 0) {
    return false;
  }

  // Read the rest of EI_NIDENT.
  if (backtrace->Read(addr + SELFMAG, e_ident + SELFMAG, EI_NIDENT - SELFMAG) != EI_NIDENT - SELFMAG) {
    return false;
  }

  if (e_ident[EI_CLASS] == ELFCLASS32) {
    return get_build_id<Elf32_Ehdr, Elf32_Phdr, Elf32_Nhdr>(backtrace, addr, e_ident, build_id);
  } else if (e_ident[EI_CLASS] == ELFCLASS64) {
    return get_build_id<Elf64_Ehdr, Elf64_Phdr, Elf64_Nhdr>(backtrace, addr, e_ident, build_id);
  }

  return false;
}
