/*
 * Copyright (C) 2014 The Android Open Source Project
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

#include <gtest/gtest.h>

#define UNW_LOCAL_ONLY
#include <libunwind.h>

#define EXTRA_CONTEXT_BYTES 1024

TEST(libbacktrace, getcontext_size) {
  unw_context_t* context;
  context = reinterpret_cast<unw_context_t*>(malloc(sizeof(unw_context_t) + EXTRA_CONTEXT_BYTES));
  ASSERT_TRUE(context != NULL);
  uint8_t* extra = reinterpret_cast<uint8_t*>(reinterpret_cast<uintptr_t>(context) + sizeof(unw_context_t));
  for (size_t i = 0; i < EXTRA_CONTEXT_BYTES; i++) {
    extra[i] = (i % 255) + 1;
  }
  ASSERT_TRUE(unw_getcontext(context) == 0);
  /* Check that nothing was written past the end of the structure. */
  for (size_t i = 0; i < EXTRA_CONTEXT_BYTES; i++) {
    ASSERT_EQ((i % 255) + 1, extra[i]);
  }

  free(context);
}
