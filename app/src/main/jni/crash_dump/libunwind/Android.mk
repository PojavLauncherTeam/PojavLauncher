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

LOCAL_PATH := $(call my-dir)

libunwind_build_host := false
ifeq ($(HOST_OS),linux)
libunwind_build_host := true
endif

# Set to true to enable a debug build of the libraries.
# To control what is logged, set the environment variable UNW_DEBUG_LEVEL=x,
# where x controls the verbosity (from 1 to 20).
libunwind_debug := false

libunwind_common_cppflags := \
    -Wno-old-style-cast \

libunwind_common_cflags := \
    -Wno-unused-parameter \
    -Werror \

# gcc 4.8 appears to be overeager declaring that a variable is uninitialized,
# under certain circumstances. Turn off this warning only for target so that
# coverage is still present for the host code. When the entire build system
# is switched to 4.9, then this can be removed.
libunwind_common_cflags_target := \
    -Wno-maybe-uninitialized \

# src/mi/backtrace.c is misdetected as a bogus header guard by clang 3.5
# src/x86_64/Gstash_frame.c has unnecessary calls to labs.
libunwind_common_clang_cflags += \
    -Wno-header-guard \
    -Wno-absolute-value \

# The latest clang (r230699) does not allow SP/PC to be declared in inline asm lists.
libunwind_common_clang_cflags += \
    -Wno-inline-asm

ifneq ($(libunwind_debug),true)
libunwind_common_cflags += \
    -DHAVE_CONFIG_H \
    -DNDEBUG \
    -D_GNU_SOURCE \

else
libunwind_common_cflags += \
    -DHAVE_CONFIG_H \
    -DDEBUG \
    -D_GNU_SOURCE \
    -U_FORTIFY_SOURCE \

endif

libunwind_common_c_includes := \
    $(LOCAL_PATH)/src \
    $(LOCAL_PATH)/include \

# Since mips and mips64 use the same source, only generate includes/srcs
# for the below set of arches.
libunwind_generate_arches := arm arm64 mips x86 x86_64
# The complete list of arches used by Android.build.mk to set arch
# variables.
libunwind_arches := $(libunwind_generate_arches) mips64

$(foreach arch,$(libunwind_generate_arches), \
  $(eval libunwind_common_c_includes_$(arch) := $(LOCAL_PATH)/include/tdep-$(arch)))

#-----------------------------------------------------------------------
# libunwind shared library
#-----------------------------------------------------------------------
libunwind_src_files := \
    src/mi/init.c \
    src/mi/flush_cache.c \
    src/mi/mempool.c \
    src/mi/strerror.c \
    src/mi/backtrace.c \
    src/mi/dyn-cancel.c \
    src/mi/dyn-info-list.c \
    src/mi/dyn-register.c \
    src/mi/map.c \
    src/mi/Lmap.c \
    src/mi/Ldyn-extract.c \
    src/mi/Lfind_dynamic_proc_info.c \
    src/mi/Lget_proc_info_by_ip.c \
    src/mi/Lget_proc_name.c \
    src/mi/Lput_dynamic_unwind_info.c \
    src/mi/Ldestroy_addr_space.c \
    src/mi/Lget_reg.c \
    src/mi/Lset_reg.c \
    src/mi/Lget_fpreg.c \
    src/mi/Lset_fpreg.c \
    src/mi/Lset_caching_policy.c \
    src/mi/Gdyn-extract.c \
    src/mi/Gdyn-remote.c \
    src/mi/Gfind_dynamic_proc_info.c \
    src/mi/Gget_accessors.c \
    src/mi/Gget_proc_info_by_ip.c \
    src/mi/Gget_proc_name.c \
    src/mi/Gput_dynamic_unwind_info.c \
    src/mi/Gdestroy_addr_space.c \
    src/mi/Gget_reg.c \
    src/mi/Gset_reg.c \
    src/mi/Gget_fpreg.c \
    src/mi/Gset_fpreg.c \
    src/mi/Gset_caching_policy.c \
    src/dwarf/Lexpr.c \
    src/dwarf/Lfde.c \
    src/dwarf/Lparser.c \
    src/dwarf/Lpe.c \
    src/dwarf/Lstep_dwarf.c \
    src/dwarf/Lfind_proc_info-lsb.c \
    src/dwarf/Lfind_unwind_table.c \
    src/dwarf/Gexpr.c \
    src/dwarf/Gfde.c \
    src/dwarf/Gfind_proc_info-lsb.c \
    src/dwarf/Gfind_unwind_table.c \
    src/dwarf/Gparser.c \
    src/dwarf/Gpe.c \
    src/dwarf/Gstep_dwarf.c \
    src/dwarf/global.c \
    src/os-common.c \
    src/os-linux.c \
    src/Los-common.c \

# ptrace files for remote unwinding.
libunwind_src_files += \
    src/ptrace/_UPT_accessors.c \
    src/ptrace/_UPT_access_fpreg.c \
    src/ptrace/_UPT_access_mem.c \
    src/ptrace/_UPT_access_reg.c \
    src/ptrace/_UPT_create.c \
    src/ptrace/_UPT_destroy.c \
    src/ptrace/_UPT_find_proc_info.c \
    src/ptrace/_UPT_get_dyn_info_list_addr.c \
    src/ptrace/_UPT_put_unwind_info.c \
    src/ptrace/_UPT_get_proc_name.c \
    src/ptrace/_UPT_reg_offset.c \
    src/ptrace/_UPT_resume.c \

# Arch specific source files.
$(foreach arch,$(libunwind_generate_arches), \
  $(eval libunwind_src_files_$(arch) += \
    src/$(arch)/is_fpreg.c \
    src/$(arch)/regname.c \
    src/$(arch)/Gcreate_addr_space.c \
    src/$(arch)/Gget_proc_info.c \
    src/$(arch)/Gget_save_loc.c \
    src/$(arch)/Gglobal.c \
    src/$(arch)/Ginit.c \
    src/$(arch)/Ginit_local.c \
    src/$(arch)/Ginit_remote.c \
    src/$(arch)/Gregs.c \
    src/$(arch)/Gresume.c \
    src/$(arch)/Gstep.c \
    src/$(arch)/Lcreate_addr_space.c \
    src/$(arch)/Lget_proc_info.c \
    src/$(arch)/Lget_save_loc.c \
    src/$(arch)/Lglobal.c \
    src/$(arch)/Linit.c \
    src/$(arch)/Linit_local.c \
    src/$(arch)/Linit_remote.c \
    src/$(arch)/Lregs.c \
    src/$(arch)/Lresume.c \
    src/$(arch)/Lstep.c \
    ))

libunwind_src_files_arm += \
    src/arm/getcontext.S \
    src/arm/Gis_signal_frame.c \
    src/arm/Gex_tables.c \
    src/arm/Lis_signal_frame.c \
    src/arm/Lex_tables.c \

libunwind_src_files_arm64 += \
    src/aarch64/Gis_signal_frame.c \
    src/aarch64/Lis_signal_frame.c \

libunwind_src_files_mips += \
    src/mips/getcontext-android.S \
    src/mips/Gis_signal_frame.c \
    src/mips/Lis_signal_frame.c \

libunwind_src_files_x86 += \
    src/x86/getcontext-linux.S \
    src/x86/Gos-linux.c \
    src/x86/Los-linux.c \

libunwind_src_files_x86_64 += \
    src/x86_64/getcontext.S \
    src/x86_64/Gstash_frame.c \
    src/x86_64/Gtrace.c \
    src/x86_64/Gos-linux.c \
    src/x86_64/Lstash_frame.c \
    src/x86_64/Ltrace.c \
    src/x86_64/Los-linux.c \
    src/x86_64/setcontext.S \

# mips and mips64 use the same sources but define _MIP_SIM differently
# to change the behavior.
#   mips uses o32 abi (_MIPS_SIM == _ABIO32).
#   mips64 uses n64 abi (_MIPS_SIM == _ABI64).
libunwind_common_c_includes_mips64 := $(LOCAL_PATH)/include/tdep-mips
libunwind_src_files_mips64 := $(libunwind_src_files_mips)

# 64-bit architectures
libunwind_src_files_arm64 += src/elf64.c
libunwind_src_files_mips64 += src/elf64.c
libunwind_src_files_x86_64 += src/elf64.c

# 32-bit architectures
libunwind_src_files_arm   += src/elf32.c
libunwind_src_files_mips  += src/elf32.c
libunwind_src_files_x86   += src/elf32.c

libunwind_shared_libraries += liblzma

libunwind_shared_libraries_target := \
    libdl \

libunwind_ldflags_host := \
    -nostdlib

libunwind_ldlibs_host := \
    -lc \
    -lpthread \

libunwind_export_c_include_dirs := \
    $(LOCAL_PATH)/include

ifeq ($(libunwind_debug),true)
libunwind_shared_libraries += \
    liblog \

endif

libunwind_module := libunwind
libunwind_module_tag := optional
libunwind_build_type := target
libunwind_build_target := SHARED_LIBRARY
include $(LOCAL_PATH)/Android.build.mk
libunwind_build_type := host
include $(LOCAL_PATH)/Android.build.mk
libunwind_build_type := target
libunwind_build_target := STATIC_LIBRARY
include $(LOCAL_PATH)/Android.build.mk
libunwind_build_type := host
include $(LOCAL_PATH)/Android.build.mk

#-----------------------------------------------------------------------
# libunwindbacktrace static library
#-----------------------------------------------------------------------
libunwindbacktrace_src_files += \
    src/unwind/BacktraceWrapper.c \
    src/unwind/DeleteException.c \
    src/unwind/FindEnclosingFunction.c \
    src/unwind/ForcedUnwind.c \
    src/unwind/GetBSP.c \
    src/unwind/GetCFA.c \
    src/unwind/GetDataRelBase.c \
    src/unwind/GetGR.c \
    src/unwind/GetIP.c \
    src/unwind/GetIPInfo.c \
    src/unwind/GetLanguageSpecificData.c \
    src/unwind/GetRegionStart.c \
    src/unwind/GetTextRelBase.c \
    src/unwind/RaiseException.c \
    src/unwind/Resume.c \
    src/unwind/Resume_or_Rethrow.c \
    src/unwind/SetGR.c \
    src/unwind/SetIP.c \

libunwindbacktrace_cflags += \
    -Wno-old-style-declaration \
    -fvisibility=hidden

libunwind_module := libunwindbacktrace
libunwind_module_tag := optional
libunwind_build_type := target
libunwind_build_target := STATIC_LIBRARY
libunwindbacktrace_whole_static_libraries := libunwind
include $(LOCAL_PATH)/Android.build.mk
libunwind_build_type := host
include $(LOCAL_PATH)/Android.build.mk

#-----------------------------------------------------------------------
# libunwind testing
#-----------------------------------------------------------------------
libunwind-unit-tests_cflags := \
    -fno-builtin \
    -O0 \
    -g \

libunwind-unit-tests_c_includes := \
    $(LOCAL_PATH)/include \

libunwind-unit-tests_src_files := \
    android/tests/local_test.cpp \

libunwind-unit-tests_shared_libraries := \
    libunwind \

libunwind-unit-tests_multilib := both
libunwind_module := libunwind-unit-tests
libunwind_module_tag := optional
libunwind_build_type := target
libunwind_build_target := NATIVE_TEST
include $(LOCAL_PATH)/Android.build.mk
libunwind_build_type := host
include $(LOCAL_PATH)/Android.build.mk

# Run the unit tests built for x86 or x86_64.
ifeq ($(TARGET_ARCH),$(filter $(TARGET_ARCH),x86 x86_64))
ifneq ($(TARGET_ARCH),$(filter $(TARGET_ARCH),x86))
LINKER = linker64
TEST_SUFFIX = 64
else
LINKER = linker
TEST_SUFFIX = 32
endif

libunwind-unit-tests-run-on-host: libunwind-unit-tests $(TARGET_OUT_EXECUTABLES)/$(LINKER) $(TARGET_OUT_EXECUTABLES)/sh
	if [ ! -d /system -o ! -d /system/bin ]; then \
	  echo "Attempting to create /system/bin"; \
	  sudo mkdir -p -m 0777 /system/bin; \
	fi
	mkdir -p $(TARGET_OUT_DATA)/local/tmp
	cp $(TARGET_OUT_EXECUTABLES)/$(LINKER) /system/bin
	cp $(TARGET_OUT_EXECUTABLES)/sh /system/bin
	ANDROID_DATA=$(TARGET_OUT_DATA) \
	ANDROID_ROOT=$(TARGET_OUT) \
	LD_LIBRARY_PATH=$(TARGET_OUT_SHARED_LIBRARIES) \
		$(TARGET_OUT_DATA_NATIVE_TESTS)/libunwind-unit-tests/libunwind-unit-tests$(TEST_SUFFIX) $(LIBUNWIND_TEST_FLAGS)
endif
