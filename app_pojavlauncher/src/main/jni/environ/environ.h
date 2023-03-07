//
// Created by maks on 24.09.2022.
//

#ifndef POJAVLAUNCHER_ENVIRON_H
#define POJAVLAUNCHER_ENVIRON_H

#include <ctxbridges/gl_bridge.h>
#include <stdatomic.h>
#include <jni.h>

typedef struct {
    int type;
    int i1;
    int i2;
    int i3;
    int i4;
} GLFWInputEvent;

typedef void GLFW_invoke_Char_func(void* window, unsigned int codepoint);
typedef void GLFW_invoke_CharMods_func(void* window, unsigned int codepoint, int mods);
typedef void GLFW_invoke_CursorEnter_func(void* window, int entered);
typedef void GLFW_invoke_CursorPos_func(void* window, double xpos, double ypos);
typedef void GLFW_invoke_FramebufferSize_func(void* window, int width, int height);
typedef void GLFW_invoke_Key_func(void* window, int key, int scancode, int action, int mods);
typedef void GLFW_invoke_MouseButton_func(void* window, int button, int action, int mods);
typedef void GLFW_invoke_Scroll_func(void* window, double xoffset, double yoffset);
typedef void GLFW_invoke_WindowSize_func(void* window, int width, int height);

struct pojav_environ_s {
    struct ANativeWindow* pojavWindow;
    render_window_t* mainWindowBundle;
    int config_renderer;
    bool force_vsync;
    atomic_size_t eventCounter;
    GLFWInputEvent events[8000];
    double cursorX, cursorY, cLastX, cLastY;
    jmethodID method_accessAndroidClipboard;
    jmethodID method_onGrabStateChanged;
    jmethodID method_glftSetWindowAttrib;
    jmethodID method_internalWindowSizeChanged;
    jclass bridgeClazz;
    jclass vmGlfwClass;
    jboolean isGrabbing;
    jbyte* keyDownBuffer;
    jbyte* mouseDownBuffer;
    JavaVM* runtimeJavaVMPtr;
    JNIEnv* runtimeJNIEnvPtr_JRE;
    JavaVM* dalvikJavaVMPtr;
    JNIEnv* dalvikJNIEnvPtr_ANDROID;
    long showingWindow;
    bool isInputReady, isCursorEntered, isUseStackQueueCall;
    int savedWidth, savedHeight;
#define ADD_CALLBACK_WWIN(NAME) \
    GLFW_invoke_##NAME##_func* GLFW_invoke_##NAME;
    ADD_CALLBACK_WWIN(Char);
    ADD_CALLBACK_WWIN(CharMods);
    ADD_CALLBACK_WWIN(CursorEnter);
    ADD_CALLBACK_WWIN(CursorPos);
    ADD_CALLBACK_WWIN(FramebufferSize);
    ADD_CALLBACK_WWIN(Key);
    ADD_CALLBACK_WWIN(MouseButton);
    ADD_CALLBACK_WWIN(Scroll);
    ADD_CALLBACK_WWIN(WindowSize);

#undef ADD_CALLBACK_WWIN
};
extern struct pojav_environ_s *pojav_environ;

#endif //POJAVLAUNCHER_ENVIRON_H
