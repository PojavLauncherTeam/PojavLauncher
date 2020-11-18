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

#ifndef _LIBBACKTRACE_UNWIND_CURRENT_H
#define _LIBBACKTRACE_UNWIND_CURRENT_H

#include <stdint.h>
#include <sys/types.h>
#include <ucontext.h>

#include <string>

#include <backtrace/Backtrace.h>
#include <backtrace/BacktraceMap.h>

#include "BacktraceCurrent.h"

#define UNW_LOCAL_ONLY
#include <libunwind.h>

class UnwindCurrent : public BacktraceCurrent {
public:
  UnwindCurrent(pid_t pid, pid_t tid, BacktraceMap* map) : BacktraceCurrent(pid, tid, map) {}
  virtual ~UnwindCurrent() {}

  std::string GetFunctionNameRaw(uintptr_t pc, uintptr_t* offset) override;

private:
  void GetUnwContextFromUcontext(const ucontext_t* ucontext);

  bool UnwindFromContext(size_t num_ignore_frames, ucontext_t* ucontext) override;

  unw_context_t context_;
};

#endif // _LIBBACKTRACE_UNWIND_CURRENT_H
