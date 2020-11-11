/*
 *
 * Copyright 2006, The Android Open Source Project
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

#include <errno.h>
#include <stdint.h>
#include <string.h>
#include <sys/ptrace.h>

#include <backtrace/Backtrace.h>
#include <log/log.h>

#include "machine.h"
#include "utility.h"

void dump_memory_and_code(log_t* log, Backtrace* backtrace) {
  pt_regs regs;
  if (ptrace(PTRACE_GETREGS, backtrace->Tid(), 0, &regs)) {
    ALOGE("cannot get registers: %s\n", strerror(errno));
    return;
  }

  static const char reg_names[] = "r0r1r2r3r4r5r6r7r8r9slfpipsp";

  for (int reg = 0; reg < 14; reg++) {
    dump_memory(log, backtrace, regs.uregs[reg], "memory near %.2s:", &reg_names[reg * 2]);
  }

  dump_memory(log, backtrace, static_cast<uintptr_t>(regs.ARM_pc), "code around pc:");

  if (regs.ARM_pc != regs.ARM_lr) {
    dump_memory(log, backtrace, static_cast<uintptr_t>(regs.ARM_lr), "code around lr:");
  }
}

void dump_registers(log_t* log, pid_t tid) {
  pt_regs r;
  if (ptrace(PTRACE_GETREGS, tid, 0, &r)) {
    ALOGE("cannot get registers: %s\n", strerror(errno));
    return;
  }

  _LOG(log, logtype::REGISTERS, "    r0 %08x  r1 %08x  r2 %08x  r3 %08x\n",
       static_cast<uint32_t>(r.ARM_r0), static_cast<uint32_t>(r.ARM_r1),
       static_cast<uint32_t>(r.ARM_r2), static_cast<uint32_t>(r.ARM_r3));
  _LOG(log, logtype::REGISTERS, "    r4 %08x  r5 %08x  r6 %08x  r7 %08x\n",
       static_cast<uint32_t>(r.ARM_r4), static_cast<uint32_t>(r.ARM_r5),
       static_cast<uint32_t>(r.ARM_r6), static_cast<uint32_t>(r.ARM_r7));
  _LOG(log, logtype::REGISTERS, "    r8 %08x  r9 %08x  sl %08x  fp %08x\n",
       static_cast<uint32_t>(r.ARM_r8), static_cast<uint32_t>(r.ARM_r9),
       static_cast<uint32_t>(r.ARM_r10), static_cast<uint32_t>(r.ARM_fp));
  _LOG(log, logtype::REGISTERS, "    ip %08x  sp %08x  lr %08x  pc %08x  cpsr %08x\n",
       static_cast<uint32_t>(r.ARM_ip), static_cast<uint32_t>(r.ARM_sp),
       static_cast<uint32_t>(r.ARM_lr), static_cast<uint32_t>(r.ARM_pc),
       static_cast<uint32_t>(r.ARM_cpsr));

  user_vfp vfp_regs;
  if (ptrace(PTRACE_GETVFPREGS, tid, 0, &vfp_regs)) {
    ALOGE("cannot get FP registers: %s\n", strerror(errno));
    return;
  }

  for (size_t i = 0; i < 32; i += 2) {
    _LOG(log, logtype::FP_REGISTERS, "    d%-2d %016llx  d%-2d %016llx\n",
         i, vfp_regs.fpregs[i], i+1, vfp_regs.fpregs[i+1]);
  }
  _LOG(log, logtype::FP_REGISTERS, "    scr %08lx\n", vfp_regs.fpscr);
}
