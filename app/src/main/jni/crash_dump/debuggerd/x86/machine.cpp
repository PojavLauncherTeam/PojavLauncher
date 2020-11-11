/*
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
  struct pt_regs r;
  if (ptrace(PTRACE_GETREGS, backtrace->Tid(), 0, &r) == -1) {
    ALOGE("cannot get registers: %s\n", strerror(errno));
    return;
  }

  dump_memory(log, backtrace, static_cast<uintptr_t>(r.eax), "memory near eax:");
  dump_memory(log, backtrace, static_cast<uintptr_t>(r.ebx), "memory near ebx:");
  dump_memory(log, backtrace, static_cast<uintptr_t>(r.ecx), "memory near ecx:");
  dump_memory(log, backtrace, static_cast<uintptr_t>(r.edx), "memory near edx:");
  dump_memory(log, backtrace, static_cast<uintptr_t>(r.esi), "memory near esi:");
  dump_memory(log, backtrace, static_cast<uintptr_t>(r.edi), "memory near edi:");

  dump_memory(log, backtrace, static_cast<uintptr_t>(r.eip), "code around eip:");
}

void dump_registers(log_t* log, pid_t tid) {
  struct pt_regs r;
  if (ptrace(PTRACE_GETREGS, tid, 0, &r) == -1) {
    ALOGE("cannot get registers: %s\n", strerror(errno));
    return;
  }

  _LOG(log, logtype::REGISTERS, "    eax %08lx  ebx %08lx  ecx %08lx  edx %08lx\n",
       r.eax, r.ebx, r.ecx, r.edx);
  _LOG(log, logtype::REGISTERS, "    esi %08lx  edi %08lx\n",
       r.esi, r.edi);
  _LOG(log, logtype::REGISTERS, "    xcs %08x  xds %08x  xes %08x  xfs %08x  xss %08x\n",
       r.xcs, r.xds, r.xes, r.xfs, r.xss);
  _LOG(log, logtype::REGISTERS, "    eip %08lx  ebp %08lx  esp %08lx  flags %08lx\n",
       r.eip, r.ebp, r.esp, r.eflags);
}
