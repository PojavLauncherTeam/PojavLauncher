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

#include "android-base/logging.h"
#include "android-base/test_utils.h"
#include "utils/Compat.h" // For OS_PATH_SEPARATOR.

#include <fcntl.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/stat.h>
#include <unistd.h>

#if defined(_WIN32)
#include <windows.h>
#include <direct.h>
#endif

#include <string>

#ifdef _WIN32
int mkstemp(char* template_name) {
  if (_mktemp(template_name) == nullptr) {
    return -1;
  }
  // Use open() to match the close() that TemporaryFile's destructor does.
  // Use O_BINARY to match base file APIs.
  return open(template_name, O_CREAT | O_EXCL | O_RDWR | O_BINARY,
              S_IRUSR | S_IWUSR);
}

char* mkdtemp(char* template_name) {
  if (_mktemp(template_name) == nullptr) {
    return nullptr;
  }
  if (_mkdir(template_name) == -1) {
    return nullptr;
  }
  return template_name;
}
#endif

static std::string GetSystemTempDir() {
#if defined(__ANDROID__)
  return "/data/local/tmp";
#elif defined(_WIN32)
  char tmp_dir[MAX_PATH];
  DWORD result = GetTempPathA(sizeof(tmp_dir), tmp_dir);
  CHECK_NE(result, 0ul) << "GetTempPathA failed, error: " << GetLastError();
  CHECK_LT(result, sizeof(tmp_dir)) << "path truncated to: " << result;

  // GetTempPath() returns a path with a trailing slash, but init()
  // does not expect that, so remove it.
  CHECK_EQ(tmp_dir[result - 1], '\\');
  tmp_dir[result - 1] = '\0';
  return tmp_dir;
#else
  return "/tmp";
#endif
}

TemporaryFile::TemporaryFile() {
  init(GetSystemTempDir());
}

TemporaryFile::~TemporaryFile() {
  close(fd);
  unlink(path);
}

void TemporaryFile::init(const std::string& tmp_dir) {
  snprintf(path, sizeof(path), "%s%cTemporaryFile-XXXXXX", tmp_dir.c_str(),
           OS_PATH_SEPARATOR);
  fd = mkstemp(path);
}

TemporaryDir::TemporaryDir() {
  init(GetSystemTempDir());
}

TemporaryDir::~TemporaryDir() {
  rmdir(path);
}

bool TemporaryDir::init(const std::string& tmp_dir) {
  snprintf(path, sizeof(path), "%s%cTemporaryDir-XXXXXX", tmp_dir.c_str(),
           OS_PATH_SEPARATOR);
  return (mkdtemp(path) != nullptr);
}
