LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
# Link GLESv2 for test
LOCAL_LDLIBS := -ldl -llog -landroid -lEGL -lGLESv2
LOCAL_MODULE := pojavexec
LOCAL_CFLAGS += -DGLES_TEST
LOCAL_SRC_FILES := \
    egl_bridge.c \
    jre_launcher.c \
    utils.c

include $(BUILD_SHARED_LIBRARY)

