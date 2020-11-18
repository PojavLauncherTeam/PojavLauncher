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
#include <ucontext.h>

#include <memory>
#include <string>

#define UNW_LOCAL_ONLY
#include <libunwind.h>

#include <backtrace/Backtrace.h>

#include "BacktraceLog.h"
#include "UnwindCurrent.h"

std::string UnwindCurrent::GetFunctionNameRaw(uintptr_t pc, uintptr_t* offset) {
  *offset = 0;
  char buf[512];
  unw_word_t value;
  if (unw_get_proc_name_by_ip(unw_local_addr_space, pc, buf, sizeof(buf),
                              &value, &context_) >= 0 && buf[0] != '\0') {
    *offset = static_cast<uintptr_t>(value);
    return buf;
  }
  return "";
}

void UnwindCurrent::GetUnwContextFromUcontext(const ucontext_t* ucontext) {
  unw_tdep_context_t* unw_context = reinterpret_cast<unw_tdep_context_t*>(&context_);

#if defined(__arm__)
  unw_context->regs[0] = ucontext->uc_mcontext.arm_r0;
  unw_context->regs[1] = ucontext->uc_mcontext.arm_r1;
  unw_context->regs[2] = ucontext->uc_mcontext.arm_r2;
  unw_context->regs[3] = ucontext->uc_mcontext.arm_r3;
  unw_context->regs[4] = ucontext->uc_mcontext.arm_r4;
  unw_context->regs[5] = ucontext->uc_mcontext.arm_r5;
  unw_context->regs[6] = ucontext->uc_mcontext.arm_r6;
  unw_context->regs[7] = ucontext->uc_mcontext.arm_r7;
  unw_context->regs[8] = ucontext->uc_mcontext.arm_r8;
  unw_context->regs[9] = ucontext->uc_mcontext.arm_r9;
  unw_context->regs[10] = ucontext->uc_mcontext.arm_r10;
  unw_context->regs[11] = ucontext->uc_mcontext.arm_fp;
  unw_context->regs[12] = ucontext->uc_mcontext.arm_ip;
  unw_context->regs[13] = ucontext->uc_mcontext.arm_sp;
  unw_context->regs[14] = ucontext->uc_mcontext.arm_lr;
  unw_context->regs[15] = ucontext->uc_mcontext.arm_pc;
#else
  unw_context->uc_mcontext = ucontext->uc_mcontext;
#endif
}

bool UnwindCurrent::UnwindFromContext(size_t num_ignore_frames, ucontext_t* ucontext) {
  if (ucontext == nullptr) {
    int ret = unw_getcontext(&context_);
    if (ret < 0) {
      BACK_LOGW("unw_getcontext failed %d", ret);
      error_ = BACKTRACE_UNWIND_ERROR_SETUP_FAILED;
      return false;
    }
  } else {
    GetUnwContextFromUcontext(ucontext);
  }

  // The cursor structure is pretty large, do not put it on the stack.
  std::unique_ptr<unw_cursor_t> cursor(new unw_cursor_t);
  int ret = unw_init_local(cursor.get(), &context_);
  if (ret < 0) {
    BACK_LOGW("unw_init_local failed %d", ret);
    error_ = BACKTRACE_UNWIND_ERROR_SETUP_FAILED;
    return false;
  }

  size_t num_frames = 0;
  do {
    unw_word_t pc;
    ret = unw_get_reg(cursor.get(), UNW_REG_IP, &pc);
    if (ret < 0) {
      BACK_LOGW("Failed to read IP %d", ret);
      break;
    }
    unw_word_t sp;
    ret = unw_get_reg(cursor.get(), UNW_REG_SP, &sp);
    if (ret < 0) {
      BACK_LOGW("Failed to read SP %d", ret);
      break;
    }

    frames_.resize(num_frames+1);
    backtrace_frame_data_t* frame = &frames_.at(num_frames);
    frame->num = num_frames;
    frame->pc = static_cast<uintptr_t>(pc);
    frame->sp = static_cast<uintptr_t>(sp);
    frame->stack_size = 0;

    FillInMap(frame->pc, &frame->map);
    // Check to see if we should skip this frame because it's coming
    // from within the library, and we are doing a local unwind.
    if (ucontext != nullptr || num_frames != 0 || !DiscardFrame(*frame)) {
      if (num_ignore_frames == 0) {
        // GetFunctionName is an expensive call, only do it if we are
        // keeping the frame.
        frame->func_name = GetFunctionName(frame->pc, &frame->func_offset);
        if (num_frames > 0) {
          // Set the stack size for the previous frame.
          backtrace_frame_data_t* prev = &frames_.at(num_frames-1);
          prev->stack_size = frame->sp - prev->sp;
        }
        num_frames++;
      } else {
        num_ignore_frames--;
      }
    }
    ret = unw_step (cursor.get());
  } while (ret > 0 && num_frames < MAX_BACKTRACE_FRAMES);

  return true;
}
