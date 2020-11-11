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

LOCAL_MODULE := $(module)
LOCAL_MODULE_TAGS := $(module_tag)
LOCAL_MULTILIB := $($(module)_multilib)
ifeq ($(LOCAL_MULTILIB),both)
ifneq ($(build_target),$(filter $(build_target),SHARED_LIBRARY STATIC_LIBRARY))
  LOCAL_MODULE_STEM_32 := $(LOCAL_MODULE)32
  LOCAL_MODULE_STEM_64 := $(LOCAL_MODULE)64
endif
endif

ifeq ($(build_type),target)
  include $(LLVM_DEVICE_BUILD_MK)
else
  include $(LLVM_HOST_BUILD_MK)
endif

LOCAL_ADDITIONAL_DEPENDENCIES += \
    $(LOCAL_PATH)/Android.mk \
    $(LOCAL_PATH)/Android.build.mk \

LOCAL_CFLAGS += \
    $(libbacktrace_common_cflags) \
    $($(module)_cflags) \
    $($(module)_cflags_$(build_type)) \

LOCAL_CLANG_CFLAGS += \
    $(libbacktrace_common_clang_cflags) \

LOCAL_CONLYFLAGS += \
    $(libbacktrace_common_conlyflags) \
    $($(module)_conlyflags) \
    $($(module)_conlyflags_$(build_type)) \

LOCAL_CPPFLAGS += \
    $(libbacktrace_common_cppflags) \
    $($(module)_cppflags) \
    $($(module)_cppflags_$(build_type)) \

LOCAL_C_INCLUDES += \
    $(libbacktrace_common_c_includes) \
    $($(module)_c_includes) \
    $($(module)_c_includes_$(build_type)) \

LOCAL_SRC_FILES := \
    $($(module)_src_files) \
    $($(module)_src_files_$(build_type)) \

LOCAL_STATIC_LIBRARIES += \
    $($(module)_static_libraries) \
    $($(module)_static_libraries_$(build_type)) \

LOCAL_SHARED_LIBRARIES += \
    $($(module)_shared_libraries) \
    $($(module)_shared_libraries_$(build_type)) \

LOCAL_LDLIBS += \
    $($(module)_ldlibs) \
    $($(module)_ldlibs_$(build_type)) \

LOCAL_STRIP_MODULE := $($(module)_strip_module)

ifeq ($(build_type),target)
  include $(BUILD_$(build_target))
endif

ifeq ($(build_type),host)
  # Only build if host builds are supported.
  ifeq ($(build_host),true)
    # -fno-omit-frame-pointer should be set for host build. Because currently
    # libunwind can't recognize .debug_frame using dwarf version 4, and it relies
    # on stack frame pointer to do unwinding on x86.
    # $(LLVM_HOST_BUILD_MK) overwrites -fno-omit-frame-pointer. so the below line
    # must be after the include.
    LOCAL_CFLAGS += -Wno-extern-c-compat -fno-omit-frame-pointer
    include $(BUILD_HOST_$(build_target))
  endif
endif
