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

#include "android-base/strings.h"

#include <stdlib.h>
#include <string.h>

#include <string>
#include <vector>

namespace android {
namespace base {

#define CHECK_NE(a, b) \
  if ((a) == (b)) abort();

std::vector<std::string> Split(const std::string& s,
                               const std::string& delimiters) {
  CHECK_NE(delimiters.size(), 0U);

  std::vector<std::string> result;

  size_t base = 0;
  size_t found;
  do {
    found = s.find_first_of(delimiters, base);
    result.push_back(s.substr(base, found - base));
    base = found + 1;
  } while (found != s.npos);

  return result;
}

std::string Trim(const std::string& s) {
  std::string result;

  if (s.size() == 0) {
    return result;
  }

  size_t start_index = 0;
  size_t end_index = s.size() - 1;

  // Skip initial whitespace.
  while (start_index < s.size()) {
    if (!isspace(s[start_index])) {
      break;
    }
    start_index++;
  }

  // Skip terminating whitespace.
  while (end_index >= start_index) {
    if (!isspace(s[end_index])) {
      break;
    }
    end_index--;
  }

  // All spaces, no beef.
  if (end_index < start_index) {
    return "";
  }
  // Start_index is the first non-space, end_index is the last one.
  return s.substr(start_index, end_index - start_index + 1);
}

// These cases are probably the norm, so we mark them extern in the header to
// aid compile time and binary size.
template std::string Join(const std::vector<std::string>&, char);
template std::string Join(const std::vector<const char*>&, char);
template std::string Join(const std::vector<std::string>&, const std::string&);
template std::string Join(const std::vector<const char*>&, const std::string&);

bool StartsWith(const std::string& s, const char* prefix) {
  return s.compare(0, strlen(prefix), prefix) == 0;
}

bool EndsWith(const std::string& s, const char* suffix) {
  size_t suffix_length = strlen(suffix);
  size_t string_length = s.size();
  if (suffix_length > string_length) {
    return false;
  }
  size_t offset = string_length - suffix_length;
  return s.compare(offset, suffix_length, suffix) == 0;
}

}  // namespace base
}  // namespace android
