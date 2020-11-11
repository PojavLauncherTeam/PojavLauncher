#
# Copyright (C) 2015 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

LOCAL_PATH := $(call my-dir)

libbase_src_files := \
    file.cpp \
    logging.cpp \
    parsenetaddress.cpp \
    stringprintf.cpp \
    strings.cpp \
    test_utils.cpp \

libbase_cppflags := \
    -Wall \
    -Wextra \
    -Werror \

libbase_linux_cppflags := \
    -Wexit-time-destructors \

libbase_darwin_cppflags := \
    -Wexit-time-destructors \

# Device
# ------------------------------------------------------------------------------
include $(CLEAR_VARS)
LOCAL_MODULE := libcrashdumpbase
LOCAL_CLANG := true
LOCAL_SRC_FILES := $(libbase_src_files) $(libbase_linux_src_files)
LOCAL_C_INCLUDES := $(LOCAL_PATH)/include
LOCAL_CPPFLAGS := $(libbase_cppflags) $(libbase_linux_cppflags)
LOCAL_EXPORT_C_INCLUDE_DIRS := $(LOCAL_PATH)/include
LOCAL_STATIC_LIBRARIES := liblog
LOCAL_MULTILIB := both
include $(BUILD_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := libcrashdumpbase
LOCAL_CLANG := true
LOCAL_WHOLE_STATIC_LIBRARIES := libcrashdumpbase
LOCAL_SHARED_LIBRARIES := liblog
LOCAL_EXPORT_C_INCLUDE_DIRS := $(LOCAL_PATH)/include
LOCAL_MULTILIB := both
include $(BUILD_SHARED_LIBRARY)

