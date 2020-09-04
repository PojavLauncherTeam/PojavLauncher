LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_LDLIBS := -ldl -llog -lEGL -landroid
LOCAL_MODULE := pojavexec

LOCAL_SRC_FILES := \
    egl_bridge.c \
    jre_launcher.c \
    utils.c

include $(BUILD_SHARED_LIBRARY)

