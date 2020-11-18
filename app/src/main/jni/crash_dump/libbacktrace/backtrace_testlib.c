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

#include <stdio.h>

int test_level_four(int one, int two, int three, int four,
                    void (*callback_func)(void*), void* data) {
  if (callback_func != NULL) {
    callback_func(data);
  } else {
    while (1) {
    }
  }
  return one + two + three + four;
}

int test_level_three(int one, int two, int three, int four,
                     void (*callback_func)(void*), void* data) {
  return test_level_four(one+3, two+6, three+9, four+12, callback_func, data) + 3;
}

int test_level_two(int one, int two, int three, int four,
                   void (*callback_func)(void*), void* data) {
  return test_level_three(one+2, two+4, three+6, four+8, callback_func, data) + 2;
}

int test_level_one(int one, int two, int three, int four,
                   void (*callback_func)(void*), void* data) {
  return test_level_two(one+1, two+2, three+3, four+4, callback_func, data) + 1;
}

int test_recursive_call(int level, void (*callback_func)(void*), void* data) {
  if (level > 0) {
    return test_recursive_call(level - 1, callback_func, data) + level;
  } else if (callback_func != NULL) {
    callback_func(data);
  } else {
    while (1) {
    }
  }
  return 0;
}
