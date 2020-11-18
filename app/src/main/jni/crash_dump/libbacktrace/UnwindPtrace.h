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

#ifndef _LIBBACKTRACE_UNWIND_PTRACE_H
#define _LIBBACKTRACE_UNWIND_PTRACE_H

#include <stdint.h>
#include <sys/types.h>

#include <string>

#ifdef UNW_LOCAL_ONLY
#undef UNW_LOCAL_ONLY
#endif
#include <libunwind.h>

#include "BacktracePtrace.h"

class UnwindPtrace : public BacktracePtrace {
public:
  UnwindPtrace(pid_t pid, pid_t tid, BacktraceMap* map);
  virtual ~UnwindPtrace();

  bool Unwind(size_t num_ignore_frames, ucontext_t* ucontext) override;

  std::string GetFunctionNameRaw(uintptr_t pc, uintptr_t* offset) override;

private:
  unw_addr_space_t addr_space_;
  struct UPT_info* upt_info_;
};

#endif // _LIBBACKTRACE_UNWIND_PTRACE_H
