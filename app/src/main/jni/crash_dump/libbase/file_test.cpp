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

#include "android-base/file.h"

#include <gtest/gtest.h>

#include <errno.h>
#include <fcntl.h>
#include <unistd.h>

#include <string>

#include "android-base/test_utils.h"

TEST(file, ReadFileToString_ENOENT) {
  std::string s("hello");
  errno = 0;
  ASSERT_FALSE(android::base::ReadFileToString("/proc/does-not-exist", &s));
  EXPECT_EQ(ENOENT, errno);
  EXPECT_EQ("", s);  // s was cleared.
}

TEST(file, ReadFileToString_WriteStringToFile) {
  TemporaryFile tf;
  ASSERT_TRUE(tf.fd != -1);
  ASSERT_TRUE(android::base::WriteStringToFile("abc", tf.path))
    << strerror(errno);
  std::string s;
  ASSERT_TRUE(android::base::ReadFileToString(tf.path, &s))
    << strerror(errno);
  EXPECT_EQ("abc", s);
}

// WriteStringToFile2 is explicitly for setting Unix permissions, which make no
// sense on Windows.
#if !defined(_WIN32)
TEST(file, WriteStringToFile2) {
  TemporaryFile tf;
  ASSERT_TRUE(tf.fd != -1);
  ASSERT_TRUE(android::base::WriteStringToFile("abc", tf.path, 0660,
                                               getuid(), getgid()))
      << strerror(errno);
  struct stat sb;
  ASSERT_EQ(0, stat(tf.path, &sb));
  ASSERT_EQ(0660U, static_cast<unsigned int>(sb.st_mode & ~S_IFMT));
  ASSERT_EQ(getuid(), sb.st_uid);
  ASSERT_EQ(getgid(), sb.st_gid);
  std::string s;
  ASSERT_TRUE(android::base::ReadFileToString(tf.path, &s))
    << strerror(errno);
  EXPECT_EQ("abc", s);
}
#endif

TEST(file, WriteStringToFd) {
  TemporaryFile tf;
  ASSERT_TRUE(tf.fd != -1);
  ASSERT_TRUE(android::base::WriteStringToFd("abc", tf.fd));

  ASSERT_EQ(0, lseek(tf.fd, 0, SEEK_SET)) << strerror(errno);

  std::string s;
  ASSERT_TRUE(android::base::ReadFdToString(tf.fd, &s)) << strerror(errno);
  EXPECT_EQ("abc", s);
}

TEST(file, WriteFully) {
  TemporaryFile tf;
  ASSERT_TRUE(tf.fd != -1);
  ASSERT_TRUE(android::base::WriteFully(tf.fd, "abc", 3));

  ASSERT_EQ(0, lseek(tf.fd, 0, SEEK_SET)) << strerror(errno);

  std::string s;
  s.resize(3);
  ASSERT_TRUE(android::base::ReadFully(tf.fd, &s[0], s.size()))
    << strerror(errno);
  EXPECT_EQ("abc", s);

  ASSERT_EQ(0, lseek(tf.fd, 0, SEEK_SET)) << strerror(errno);

  s.resize(1024);
  ASSERT_FALSE(android::base::ReadFully(tf.fd, &s[0], s.size()));
}

TEST(file, RemoveFileIfExist) {
  TemporaryFile tf;
  ASSERT_TRUE(tf.fd != -1);
  close(tf.fd);
  tf.fd = -1;
  std::string err;
  ASSERT_TRUE(android::base::RemoveFileIfExists(tf.path, &err)) << err;
  ASSERT_TRUE(android::base::RemoveFileIfExists(tf.path));
  TemporaryDir td;
  ASSERT_FALSE(android::base::RemoveFileIfExists(td.path));
  ASSERT_FALSE(android::base::RemoveFileIfExists(td.path, &err));
  ASSERT_EQ("is not a regular or symbol link file", err);
}
