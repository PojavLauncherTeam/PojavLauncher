#
# Copyright (C) 2014 The Android Open Source Project
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

LOCAL_PATH:= $(call my-dir)

libbacktrace_common_cflags := \
	-Wall \
	-Werror \

libbacktrace_common_conlyflags := \
	-std=gnu99 \

libbacktrace_common_cppflags := \
	-std=gnu++11 \
	-I external/libunwind/include/tdep \

# The latest clang (r230699) does not allow SP/PC to be declared in inline asm lists.
libbacktrace_common_clang_cflags += \
    -Wno-inline-asm

build_host := false
ifeq ($(HOST_OS),linux)
ifeq ($(HOST_ARCH),$(filter $(HOST_ARCH),x86 x86_64))
build_host := true
endif
endif

LLVM_ROOT_PATH := external/llvm
include $(LLVM_ROOT_PATH)/llvm.mk

#-------------------------------------------------------------------------
# The libbacktrace library.
#-------------------------------------------------------------------------
libbacktrace_src_files := \
	Backtrace.cpp \
	BacktraceCurrent.cpp \
	BacktraceMap.cpp \
	BacktracePtrace.cpp \
	thread_utils.c \
	ThreadEntry.cpp \
	UnwindCurrent.cpp \
	UnwindMap.cpp \
	UnwindPtrace.cpp \

libbacktrace_shared_libraries := \
	libbase \
	liblog \
	libunwind \

libbacktrace_static_libraries := \
	libcutils

module := libbacktrace
module_tag := optional
build_type := target
build_target := SHARED_LIBRARY
include $(LOCAL_PATH)/Android.build.mk
build_type := host
libbacktrace_multilib := both
include $(LOCAL_PATH)/Android.build.mk
libbacktrace_static_libraries := \
	libbase \
	liblog \
	libunwind \

build_target := STATIC_LIBRARY
include $(LOCAL_PATH)/Android.build.mk
libbacktrace_static_libraries :=

#-------------------------------------------------------------------------
# The libbacktrace_offline shared library.
#-------------------------------------------------------------------------
libbacktrace_offline_src_files := \
	BacktraceOffline.cpp \

# Use shared llvm library on device to save space.
libbacktrace_offline_shared_libraries_target := \
	libbacktrace \
	libbase \
	liblog \
	libunwind \
	libutils \
	libLLVM \

libbacktrace_offline_static_libraries_target := \
	libziparchive \
	libz \

# Use static llvm libraries on host to remove dependency on 32-bit llvm shared library
# which is not included in the prebuilt.
libbacktrace_offline_static_libraries_host := \
	libbacktrace \
	libunwind \
	libziparchive-host \
	libz \
	libbase \
	liblog \
	libutils \
	libLLVMObject \
	libLLVMBitReader \
	libLLVMMC \
	libLLVMMCParser \
	libLLVMCore \
	libLLVMSupport \

module := libbacktrace_offline
build_type := target
build_target := STATIC_LIBRARY
libbacktrace_offline_multilib := both
include $(LOCAL_PATH)/Android.build.mk
build_type := host
include $(LOCAL_PATH)/Android.build.mk

#-------------------------------------------------------------------------
# The libbacktrace_test library needed by backtrace_test.
#-------------------------------------------------------------------------
libbacktrace_test_cflags := \
	-O0 \

libbacktrace_test_src_files := \
	backtrace_testlib.c \

libbacktrace_test_strip_module := false

module := libbacktrace_test
module_tag := debug
build_type := target
build_target := SHARED_LIBRARY
libbacktrace_test_multilib := both
include $(LOCAL_PATH)/Android.build.mk
build_type := host
include $(LOCAL_PATH)/Android.build.mk

#-------------------------------------------------------------------------
# The backtrace_test executable.
#-------------------------------------------------------------------------
backtrace_test_cflags := \
	-fno-builtin \
	-O0 \
	-g \

backtrace_test_cflags_target := \
	-DENABLE_PSS_TESTS \

backtrace_test_src_files := \
	backtrace_offline_test.cpp \
	backtrace_test.cpp \
	GetPss.cpp \
	thread_utils.c \

backtrace_test_ldlibs_host := \
	-lpthread \
	-lrt \

backtrace_test_shared_libraries := \
	libbacktrace_test \
	libbacktrace \
	libbase \
	libcutils \
	liblog \
	libunwind \

backtrace_test_shared_libraries_target += \
	libdl \
	libutils \
	libLLVM \

backtrace_test_static_libraries := \
	libbacktrace_offline \

backtrace_test_static_libraries_target := \
	libziparchive \
	libz \

backtrace_test_static_libraries_host := \
	libziparchive-host \
	libz \
	libutils \
	libLLVMObject \
	libLLVMBitReader \
	libLLVMMC \
	libLLVMMCParser \
	libLLVMCore \
	libLLVMSupport \

backtrace_test_ldlibs_host += \
	-ldl \

backtrace_test_strip_module := false

module := backtrace_test
module_tag := debug
build_type := target
build_target := NATIVE_TEST
backtrace_test_multilib := both
include $(LOCAL_PATH)/Android.build.mk
build_type := host
include $(LOCAL_PATH)/Android.build.mk

#----------------------------------------------------------------------------
# Special truncated libbacktrace library for mac.
#----------------------------------------------------------------------------
ifeq ($(HOST_OS),darwin)

include $(CLEAR_VARS)

LOCAL_MODULE := libbacktrace
LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := \
	BacktraceMap.cpp \

LOCAL_MULTILIB := both

include $(BUILD_HOST_SHARED_LIBRARY)

endif # HOST_OS-darwin
