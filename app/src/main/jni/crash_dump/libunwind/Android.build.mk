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

include $(CLEAR_VARS)

ifeq ($(TARGET_ARCH),$(filter $(TARGET_ARCH),mips mips64 x86_64))
# Many init services failed to start with clang for x86_64, mips, mips64
LOCAL_CLANG := false
endif

LOCAL_MODULE := $(libunwind_module)
LOCAL_MODULE_TAGS := $(libunwind_module_tag)
ifeq ($(libunwind_build_type),host)
# Always make host multilib, and always use clang.
LOCAL_MULTILIB := both
LOCAL_CLANG := true
else
LOCAL_MULTILIB := $($(libunwind_module)_multilib)
endif

ifneq ($(findstring LIBRARY, $(libunwind_build_target)),LIBRARY)
ifeq ($(LOCAL_MULTILIB),both)
    LOCAL_MODULE_STEM_32 := $(libunwind_module)32
    LOCAL_MODULE_STEM_64 := $(libunwind_module)64
endif
endif

LOCAL_ADDITIONAL_DEPENDENCIES := \
    $(LOCAL_PATH)/Android.mk \
    $(LOCAL_PATH)/Android.build.mk \

LOCAL_CFLAGS += \
    $(libunwind_common_cflags) \
    $(libunwind_common_cflags_$(libunwind_build_type)) \
    $($(libunwind_module)_cflags) \
    $($(libunwind_module)_cflags_$(libunwind_build_type)) \

LOCAL_CLANG_CFLAGS += \
    $(libunwind_common_clang_cflags) \
    $(libunwind_common_clang_cflags_$(libunwind_build_type)) \
    $($(libunwind_module)_clang_cflags) \
    $($(libunwind_module)_clang_cflags_$(libunwind_build_type)) \

LOCAL_CONLYFLAGS += \
    $(libunwind_common_conlyflags) \
    $(libunwind_common_conlyflags_$(libunwind_build_type)) \
    $($(libunwind_module)_conlyflags) \
    $($(libunwind_module)_conlyflags_$(libunwind_build_type)) \

LOCAL_CPPFLAGS += \
    $(libunwind_common_cppflags) \
    $($(libunwind_module)_cppflags) \
    $($(libunwind_module)_cppflags_$(libunwind_build_type)) \

LOCAL_C_INCLUDES := \
    $(libunwind_common_c_includes) \
    $($(libunwind_module)_c_includes) \
    $($(libunwind_module)_c_includes_$(libunwind_build_type)) \

LOCAL_EXPORT_C_INCLUDE_DIRS := \
    $($(libunwind_module)_export_c_include_dirs)

$(foreach arch,$(libunwind_arches), \
    $(eval LOCAL_C_INCLUDES_$(arch) := $(libunwind_common_c_includes_$(arch))))

LOCAL_SRC_FILES := \
    $($(libunwind_module)_src_files) \
    $($(libunwind_module)_src_files_$(build_type)) \

$(foreach arch,$(libunwind_arches), \
    $(eval LOCAL_SRC_FILES_$(arch) := $($(libunwind_module)_src_files_$(arch))))

LOCAL_SRC_FILES_32 := $($(libunwind_module)_src_files_32)
LOCAL_SRC_FILES_64 := $($(libunwind_module)_src_files_64)

LOCAL_STATIC_LIBRARIES := \
    $($(libunwind_module)_static_libraries) \
    $($(libunwind_module)_static_libraries_$(libunwind_build_type)) \

LOCAL_WHOLE_STATIC_LIBRARIES := \
    $($(libunwind_module)_whole_static_libraries) \
    $($(libunwind_module)_whole_static_libraries_$(libunwind_build_type)) \

LOCAL_SHARED_LIBRARIES := \
    $($(libunwind_module)_shared_libraries) \
    $($(libunwind_module)_shared_libraries_$(libunwind_build_type)) \

LOCAL_LDLIBS := \
    $($(libunwind_module)_ldlibs) \
    $($(libunwind_module)_ldlibs_$(libunwind_build_type)) \

LOCAL_LDFLAGS := \
    $($(libunwind_module)_ldflags) \
    $($(libunwind_module)_ldflags_$(libunwind_build_type)) \

# Translate arm64 to aarch64 in c includes and src files.
LOCAL_C_INCLUDES_arm64 := \
    $(subst tdep-arm64,tdep-aarch64,$(LOCAL_C_INCLUDES_arm64))

LOCAL_SRC_FILES_arm64 := \
    $(subst src/arm64,src/aarch64,$(LOCAL_SRC_FILES_arm64))

LOCAL_SANITIZE := never

ifeq ($(libunwind_build_type),target)
  include $(BUILD_$(libunwind_build_target))
endif

ifeq ($(libunwind_build_type),host)
  # Only build if host builds are supported.
  ifeq ($(libunwind_build_host),true)
    include $(BUILD_HOST_$(libunwind_build_target))
  endif
endif
