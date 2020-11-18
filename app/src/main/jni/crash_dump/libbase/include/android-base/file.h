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

#ifndef ANDROID_BASE_FILE_H
#define ANDROID_BASE_FILE_H

#include <sys/stat.h>
#include <string>

#if !defined(_WIN32) && !defined(O_BINARY)
#define O_BINARY 0
#endif

namespace android {
namespace base {

bool ReadFdToString(int fd, std::string* content);
bool ReadFileToString(const std::string& path, std::string* content);

bool WriteStringToFile(const std::string& content, const std::string& path);
bool WriteStringToFd(const std::string& content, int fd);

#if !defined(_WIN32)
bool WriteStringToFile(const std::string& content, const std::string& path,
                       mode_t mode, uid_t owner, gid_t group);
#endif

bool ReadFully(int fd, void* data, size_t byte_count);
bool WriteFully(int fd, const void* data, size_t byte_count);

bool RemoveFileIfExists(const std::string& path, std::string* err = nullptr);

}  // namespace base
}  // namespace android

#endif // ANDROID_BASE_FILE_H
