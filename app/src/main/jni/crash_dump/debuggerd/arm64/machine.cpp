/*
 *
 * Copyright 2014, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#define LOG_TAG "DEBUG"

#include <elf.h>
#include <errno.h>
#include <stdint.h>
#include <string.h>
#include <sys/ptrace.h>
#include <sys/uio.h>

#include <backtrace/Backtrace.h>
#include <log/log.h>

#include "machine.h"
#include "utility.h"

void dump_memory_and_code(log_t* log, Backtrace* backtrace) {
  struct user_pt_regs regs;
  struct iovec io;
  io.iov_base = &regs;
  io.iov_len = sizeof(regs);

  if (ptrace(PTRACE_GETREGSET, backtrace->Tid(), reinterpret_cast<void*>(NT_PRSTATUS), &io) == -1) {
    ALOGE("ptrace failed to get registers: %s", strerror(errno));
    return;
  }

  for (int reg = 0; reg < 31; reg++) {
    dump_memory(log, backtrace, regs.regs[reg], "memory near x%d:", reg);
  }

  dump_memory(log, backtrace, static_cast<uintptr_t>(regs.pc), "code around pc:");

  if (regs.pc != regs.sp) {
    dump_memory(log, backtrace, static_cast<uintptr_t>(regs.sp), "code around sp:");
  }
}

void dump_registers(log_t* log, pid_t tid) {
  struct user_pt_regs r;
  struct iovec io;
  io.iov_base = &r;
  io.iov_len = sizeof(r);

  if (ptrace(PTRACE_GETREGSET, tid, (void*) NT_PRSTATUS, (void*) &io) == -1) {
    ALOGE("ptrace error: %s\n", strerror(errno));
    return;
  }

  for (int i = 0; i < 28; i += 4) {
    _LOG(log, logtype::REGISTERS,
         "    x%-2d  %016llx  x%-2d  %016llx  x%-2d  %016llx  x%-2d  %016llx\n",
         i, r.regs[i],
         i+1, r.regs[i+1],
         i+2, r.regs[i+2],
         i+3, r.regs[i+3]);
  }

  _LOG(log, logtype::REGISTERS, "    x28  %016llx  x29  %016llx  x30  %016llx\n",
       r.regs[28], r.regs[29], r.regs[30]);

  _LOG(log, logtype::REGISTERS, "    sp   %016llx  pc   %016llx  pstate %016llx\n",
       r.sp, r.pc, r.pstate);

  struct user_fpsimd_state f;
  io.iov_base = &f;
  io.iov_len = sizeof(f);

  if (ptrace(PTRACE_GETREGSET, tid, (void*) NT_PRFPREG, (void*) &io) == -1) {
    ALOGE("ptrace error: %s\n", strerror(errno));
    return;
  }

  for (int i = 0; i < 32; i += 2) {
    _LOG(log, logtype::FP_REGISTERS,
         "    v%-2d  %016" PRIx64 "%016" PRIx64 "  v%-2d  %016" PRIx64 "%016" PRIx64 "\n",
         i,
         static_cast<uint64_t>(f.vregs[i] >> 64),
         static_cast<uint64_t>(f.vregs[i]),
         i+1,
         static_cast<uint64_t>(f.vregs[i+1] >> 64),
         static_cast<uint64_t>(f.vregs[i+1]));
  }
  _LOG(log, logtype::FP_REGISTERS, "    fpsr %08x  fpcr %08x\n", f.fpsr, f.fpcr);
}
