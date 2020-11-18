/*
** Copyright 2013, The Android Open Source Project
**
** Licensed under the Apache License, Version 2.0 (the "License");
** you may not use this file except in compliance with the License.
** You may obtain a copy of the License at
**
**     http://www.apache.org/licenses/LICENSE-2.0
**
** Unless required by applicable law or agreed to in writing, software
** distributed under the License is distributed on an "AS IS" BASIS,
** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
** See the License for the specific language governing permissions and
** limitations under the License.
*/

#define LOG_TAG "DEBUG"

#include <errno.h>
#include <stdint.h>
#include <sys/ptrace.h>
#include <string.h>
#include <sys/user.h>

#include <backtrace/Backtrace.h>
#include <log/log.h>

#include "machine.h"
#include "utility.h"

void dump_memory_and_code(log_t* log, Backtrace* backtrace) {
  struct user_regs_struct r;
  if (ptrace(PTRACE_GETREGS, backtrace->Tid(), 0, &r) == -1) {
    ALOGE("cannot get registers: %s\n", strerror(errno));
    return;
  }

  dump_memory(log, backtrace, static_cast<uintptr_t>(r.rax), "memory near rax:");
  dump_memory(log, backtrace, static_cast<uintptr_t>(r.rbx), "memory near rbx:");
  dump_memory(log, backtrace, static_cast<uintptr_t>(r.rcx), "memory near rcx:");
  dump_memory(log, backtrace, static_cast<uintptr_t>(r.rdx), "memory near rdx:");
  dump_memory(log, backtrace, static_cast<uintptr_t>(r.rsi), "memory near rsi:");
  dump_memory(log, backtrace, static_cast<uintptr_t>(r.rdi), "memory near rdi:");

  dump_memory(log, backtrace, static_cast<uintptr_t>(r.rip), "code around rip:");
}

void dump_registers(log_t* log, pid_t tid) {
  struct user_regs_struct r;
  if (ptrace(PTRACE_GETREGS, tid, 0, &r) == -1) {
    ALOGE("cannot get registers: %s\n", strerror(errno));
    return;
  }

  _LOG(log, logtype::REGISTERS, "    rax %016lx  rbx %016lx  rcx %016lx  rdx %016lx\n",
       r.rax, r.rbx, r.rcx, r.rdx);
  _LOG(log, logtype::REGISTERS, "    rsi %016lx  rdi %016lx\n",
       r.rsi, r.rdi);
  _LOG(log, logtype::REGISTERS, "    r8  %016lx  r9  %016lx  r10 %016lx  r11 %016lx\n",
       r.r8, r.r9, r.r10, r.r11);
  _LOG(log, logtype::REGISTERS, "    r12 %016lx  r13 %016lx  r14 %016lx  r15 %016lx\n",
       r.r12, r.r13, r.r14, r.r15);
  _LOG(log, logtype::REGISTERS, "    cs  %016lx  ss  %016lx\n",
       r.cs, r.ss);
  _LOG(log, logtype::REGISTERS, "    rip %016lx  rbp %016lx  rsp %016lx  eflags %016lx\n",
       r.rip, r.rbp, r.rsp, r.eflags);
}
