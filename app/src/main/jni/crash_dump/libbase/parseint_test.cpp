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

#include "android-base/parseint.h"

#include <gtest/gtest.h>

TEST(parseint, signed_smoke) {
  int i;
  ASSERT_FALSE(android::base::ParseInt("x", &i));
  ASSERT_FALSE(android::base::ParseInt("123x", &i));

  ASSERT_TRUE(android::base::ParseInt("123", &i));
  ASSERT_EQ(123, i);
  ASSERT_TRUE(android::base::ParseInt("-123", &i));
  ASSERT_EQ(-123, i);

  short s;
  ASSERT_TRUE(android::base::ParseInt("1234", &s));
  ASSERT_EQ(1234, s);

  ASSERT_TRUE(android::base::ParseInt("12", &i, 0, 15));
  ASSERT_EQ(12, i);
  ASSERT_FALSE(android::base::ParseInt("-12", &i, 0, 15));
  ASSERT_FALSE(android::base::ParseInt("16", &i, 0, 15));
}

TEST(parseint, unsigned_smoke) {
  unsigned int i;
  ASSERT_FALSE(android::base::ParseUint("x", &i));
  ASSERT_FALSE(android::base::ParseUint("123x", &i));

  ASSERT_TRUE(android::base::ParseUint("123", &i));
  ASSERT_EQ(123u, i);
  ASSERT_FALSE(android::base::ParseUint("-123", &i));

  unsigned short s;
  ASSERT_TRUE(android::base::ParseUint("1234", &s));
  ASSERT_EQ(1234u, s);

  ASSERT_TRUE(android::base::ParseUint("12", &i, 15u));
  ASSERT_EQ(12u, i);
  ASSERT_FALSE(android::base::ParseUint("-12", &i, 15u));
  ASSERT_FALSE(android::base::ParseUint("16", &i, 15u));
}

TEST(parseint, no_implicit_octal) {
  int i;
  ASSERT_TRUE(android::base::ParseInt("0123", &i));
  ASSERT_EQ(123, i);

  unsigned int u;
  ASSERT_TRUE(android::base::ParseUint("0123", &u));
  ASSERT_EQ(123u, u);
}

TEST(parseint, explicit_hex) {
  int i;
  ASSERT_TRUE(android::base::ParseInt("0x123", &i));
  ASSERT_EQ(0x123, i);

  unsigned int u;
  ASSERT_TRUE(android::base::ParseUint("0x123", &u));
  ASSERT_EQ(0x123u, u);
}
