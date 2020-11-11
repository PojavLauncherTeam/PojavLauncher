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
	-I$(HERE_PATH)/crash_dump/libunwind/include

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

# LLVM_ROOT_PATH := external/llvm
# include $(LLVM_ROOT_PATH)/llvm.mk

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

libbacktrace_shared_ldlibs := -llog -lunwind
# -lbase

# libbacktrace_shared_libraries := libunwind

module := libbacktrace
module_tag := optional
build_type := target
build_target := SHARED_LIBRARY
include $(LOCAL_PATH)/Android.build.mk
build_type := host
libbacktrace_multilib := both
include $(LOCAL_PATH)/Android.build.mk
libbacktrace_static_ldlibs := -llog -lunwind
# -lbase

# libbacktrace_static_libraries := libunwind


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
# i don't know if this lib is required or not
# include $(LOCAL_PATH)/Android.build.mk
build_type := host
# include $(LOCAL_PATH)/Android.build.mk

