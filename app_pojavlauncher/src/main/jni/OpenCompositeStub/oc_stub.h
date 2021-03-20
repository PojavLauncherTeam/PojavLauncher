#pragma once

#include <jni.h>
#include <EGL/egl.h>
#include <android/native_window.h>

#ifdef __cplusplus
extern "C" {
#endif

typedef struct {
    EGLDisplay display;
    EGLConfig config;
    EGLContext context;
} OCWrapper_EGLInitInfo;

void OCWrapper_InitEGL(const OCWrapper_EGLInitInfo *info);

void OCWrapper_InitActivity(JavaVM *vm, jobject activity);

void OCWrapper_Cleanup();

void OCWrapper_IgnoreNextVRInitCall();

#ifdef __cplusplus
}
#endif
