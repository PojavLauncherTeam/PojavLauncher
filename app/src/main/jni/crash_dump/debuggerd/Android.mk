LOCAL_PATH := $(call my-dir)

common_cppflags := \
    -std=gnu++11 \
    -W \
    -Wall \
    -Wextra \
    -Wunused \
    -Werror \

include $(CLEAR_VARS)

LOCAL_SRC_FILES:= \
    backtrace.cpp \
    debuggerd.cpp \
    elf_utils.cpp \
    getevent.cpp \
    signal_sender.cpp \
    tombstone.cpp \
    utility.cpp \
    selinux_fake.cpp

LOCAL_SRC_FILES_arm    := arm/machine.cpp
LOCAL_SRC_FILES_arm64  := arm64/machine.cpp
LOCAL_SRC_FILES_x86    := x86/machine.cpp
LOCAL_SRC_FILES_x86_64 := x86_64/machine.cpp

LOCAL_CPPFLAGS := $(common_cppflags) \
	-I$(HERE_PATH)/crash_dump/libbase/include

LOCAL_INIT_RC_32 := debuggerd.rc
LOCAL_INIT_RC_64 := debuggerd64.rc

ifeq ($(TARGET_IS_64_BIT),true)
LOCAL_CPPFLAGS += -DTARGET_IS_64_BIT
endif

LOCAL_LDLIBS := -llog
LOCAL_SHARED_LIBRARIES := \
    libbacktrace \
    libcrashdumpbase

LOCAL_CLANG := true

LOCAL_MODULE := crashdump
LOCAL_MULTILIB := both

include $(BUILD_SHARED_LIBRARY)



include $(CLEAR_VARS)
LOCAL_SRC_FILES := crasher.c
LOCAL_SRC_FILES_arm    := arm/crashglue.S
LOCAL_SRC_FILES_arm64  := arm64/crashglue.S
LOCAL_SRC_FILES_mips   := mips/crashglue.S
LOCAL_SRC_FILES_mips64 := mips64/crashglue.S
LOCAL_SRC_FILES_x86    := x86/crashglue.S
LOCAL_SRC_FILES_x86_64 := x86_64/crashglue.S
LOCAL_MODULE_PATH := $(TARGET_OUT_OPTIONAL_EXECUTABLES)
LOCAL_MODULE_TAGS := optional
LOCAL_CFLAGS += -fstack-protector-all -Werror -Wno-free-nonheap-object -Wno-date-time \
    -I$(HERE_PATH)/crash_dump/libbase/include

#LOCAL_FORCE_STATIC_EXECUTABLE := true
LOCAL_SHARED_LIBRARIES := libcutils liblog libc

# The arm emulator has VFP but not VFPv3-D32.
ifeq ($(ARCH_ARM_HAVE_VFP_D32),true)
LOCAL_ASFLAGS_arm += -DHAS_VFP_D32
endif

LOCAL_MODULE := crasher
LOCAL_MODULE_STEM_32 := crasher
LOCAL_MODULE_STEM_64 := crasher64
LOCAL_MULTILIB := both

# include $(BUILD_EXECUTABLE)

