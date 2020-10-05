LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
# Link GLESv2 for test
LOCAL_LDLIBS := -ldl -llog -landroid -lEGL -lGLESv2
LOCAL_MODULE := pojavexec
LOCAL_CFLAGS += -DGLES_TEST
LOCAL_SRC_FILES := \
    egl_bridge.c \
    input_bridge.c \
    jre_launcher.c \
    utils.c
include $(BUILD_SHARED_LIBRARY)

# Helper to get current thread
# include $(CLEAR_VARS)
# LOCAL_MODULE := thread64helper
# LOCAL_SRC_FILES := thread_helper.cpp
# include $(BUILD_SHARED_LIBRARY)

# libawt_xawt without X11
include $(CLEAR_VARS)
LOCAL_PATH := $(LOCAL_PATH)/awt_xawt
include $(CLEAR_VARS)
LOCAL_MODULE := awt_xawt
# LOCAL_CFLAGS += -DHEADLESS
LOCAL_SRC_FILES := \
    xawt_fake.c \
    awt_AWTEvent.c \
    awt_Event.c \
    awt_GraphicsEnv.c \
    awt_InputMethod.c \
    awt_Insets.c \
    awt_Robot.c \
    awt_UNIXToolkit.c
include $(BUILD_SHARED_LIBRARY)

