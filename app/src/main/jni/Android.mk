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

# libawt_xawt without X11
LOCAL_PATH := $(LOCAL_PATH)/awt_xawt
include $(CLEAR_VARS)
LOCAL_MODULE := awt_xawt
LOCAL_CFLAGS += -DHEADLESS
LOCAL_SRC_FILES := \
    xawt_fake.c
#    awt/awt_AWTEvent.c \
#    awt/awt_DrawingSurface.c \
#    awt/awt_Event.c \
#    awt/awt_GraphicsEnv.c \
#    awt/awt_InputMethod.c \
#    awt/awt_Insets.c \
#    awt/awt_Robot.c \
    
    
include $(BUILD_SHARED_LIBRARY)

