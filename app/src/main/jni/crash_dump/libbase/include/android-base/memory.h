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

#ifndef ANDROID_BASE_MEMORY_H
#define ANDROID_BASE_MEMORY_H

namespace android {
namespace base {

// Use packed structures for access to unaligned data on targets with alignment
// restrictions.  The compiler will generate appropriate code to access these
// structures without generating alignment exceptions.
template <typename T>
static inline T get_unaligned(const T* address) {
  struct unaligned {
    T v;
  } __attribute__((packed));
  const unaligned* p = reinterpret_cast<const unaligned*>(address);
  return p->v;
}

template <typename T>
static inline void put_unaligned(T* address, T v) {
  struct unaligned {
    T v;
  } __attribute__((packed));
  unaligned* p = reinterpret_cast<unaligned*>(address);
  p->v = v;
}

} // namespace base
} // namespace android

#endif  // ANDROID_BASE_MEMORY_H
