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

#include "android-base/file.h"

#include <errno.h>
#include <fcntl.h>
#include <sys/stat.h>
#include <sys/types.h>

#include <string>

#include "android-base/macros.h"  // For TEMP_FAILURE_RETRY on Darwin.
#include "android-base/utf8.h"
#define LOG_TAG "base.file"
#include "cutils/log.h"
#include "utils/Compat.h"

namespace android {
namespace base {

// Versions of standard library APIs that support UTF-8 strings.
using namespace android::base::utf8;

bool ReadFdToString(int fd, std::string* content) {
  content->clear();

  char buf[BUFSIZ];
  ssize_t n;
  while ((n = TEMP_FAILURE_RETRY(read(fd, &buf[0], sizeof(buf)))) > 0) {
    content->append(buf, n);
  }
  return (n == 0) ? true : false;
}

bool ReadFileToString(const std::string& path, std::string* content) {
  content->clear();

  int fd = TEMP_FAILURE_RETRY(open(path.c_str(), O_RDONLY | O_CLOEXEC | O_NOFOLLOW | O_BINARY));
  if (fd == -1) {
    return false;
  }
  bool result = ReadFdToString(fd, content);
  close(fd);
  return result;
}

bool WriteStringToFd(const std::string& content, int fd) {
  const char* p = content.data();
  size_t left = content.size();
  while (left > 0) {
    ssize_t n = TEMP_FAILURE_RETRY(write(fd, p, left));
    if (n == -1) {
      return false;
    }
    p += n;
    left -= n;
  }
  return true;
}

static bool CleanUpAfterFailedWrite(const std::string& path) {
  // Something went wrong. Let's not leave a corrupt file lying around.
  int saved_errno = errno;
  unlink(path.c_str());
  errno = saved_errno;
  return false;
}

#if !defined(_WIN32)
bool WriteStringToFile(const std::string& content, const std::string& path,
                       mode_t mode, uid_t owner, gid_t group) {
  int flags = O_WRONLY | O_CREAT | O_TRUNC | O_CLOEXEC | O_NOFOLLOW | O_BINARY;
  int fd = TEMP_FAILURE_RETRY(open(path.c_str(), flags, mode));
  if (fd == -1) {
    ALOGE("android::WriteStringToFile open failed: %s", strerror(errno));
    return false;
  }

  // We do an explicit fchmod here because we assume that the caller really
  // meant what they said and doesn't want the umask-influenced mode.
  if (fchmod(fd, mode) == -1) {
    ALOGE("android::WriteStringToFile fchmod failed: %s", strerror(errno));
    return CleanUpAfterFailedWrite(path);
  }
  if (fchown(fd, owner, group) == -1) {
    ALOGE("android::WriteStringToFile fchown failed: %s", strerror(errno));
    return CleanUpAfterFailedWrite(path);
  }
  if (!WriteStringToFd(content, fd)) {
    ALOGE("android::WriteStringToFile write failed: %s", strerror(errno));
    return CleanUpAfterFailedWrite(path);
  }
  close(fd);
  return true;
}
#endif

bool WriteStringToFile(const std::string& content, const std::string& path) {
  int flags = O_WRONLY | O_CREAT | O_TRUNC | O_CLOEXEC | O_NOFOLLOW | O_BINARY;
  int fd = TEMP_FAILURE_RETRY(open(path.c_str(), flags, DEFFILEMODE));
  if (fd == -1) {
    return false;
  }

  bool result = WriteStringToFd(content, fd);
  close(fd);
  return result || CleanUpAfterFailedWrite(path);
}

bool ReadFully(int fd, void* data, size_t byte_count) {
  uint8_t* p = reinterpret_cast<uint8_t*>(data);
  size_t remaining = byte_count;
  while (remaining > 0) {
    ssize_t n = TEMP_FAILURE_RETRY(read(fd, p, remaining));
    if (n <= 0) return false;
    p += n;
    remaining -= n;
  }
  return true;
}

bool WriteFully(int fd, const void* data, size_t byte_count) {
  const uint8_t* p = reinterpret_cast<const uint8_t*>(data);
  size_t remaining = byte_count;
  while (remaining > 0) {
    ssize_t n = TEMP_FAILURE_RETRY(write(fd, p, remaining));
    if (n == -1) return false;
    p += n;
    remaining -= n;
  }
  return true;
}

bool RemoveFileIfExists(const std::string& path, std::string* err) {
  struct stat st;
#if defined(_WIN32)
  //TODO: Windows version can't handle symbol link correctly.
  int result = stat(path.c_str(), &st);
  bool file_type_removable = (result == 0 && S_ISREG(st.st_mode));
#else
  int result = lstat(path.c_str(), &st);
  bool file_type_removable = (result == 0 && (S_ISREG(st.st_mode) || S_ISLNK(st.st_mode)));
#endif
  if (result == 0) {
    if (!file_type_removable) {
      if (err != nullptr) {
        *err = "is not a regular or symbol link file";
      }
      return false;
    }
    if (unlink(path.c_str()) == -1) {
      if (err != nullptr) {
        *err = strerror(errno);
      }
      return false;
    }
  }
  return true;
}

}  // namespace base
}  // namespace android
