//
// Created by Swung 0x48 on 2024/10/10.
//

#ifndef FOLD_CRAFT_LAUNCHER_EGL_LOADER_H
#define FOLD_CRAFT_LAUNCHER_EGL_LOADER_H


#ifdef __cplusplus
extern "C" {
#endif

#include "egl.h"

typedef EGLBoolean (*eglBindAPI_PTR)(EGLenum api);

typedef EGLBoolean (*eglBindTexImage_PTR)(EGLDisplay dpy, EGLSurface surface, EGLint buffer);

typedef EGLBoolean (*eglChooseConfig_PTR)(EGLDisplay dpy, const EGLint *attrib_list,
                                          EGLConfig *configs, EGLint config_size,
                                          EGLint *num_config);

typedef EGLBoolean (*eglCopyBuffers_PTR)(EGLDisplay dpy, EGLSurface surface,
                                         EGLNativePixmapType target);

typedef EGLContext (*eglCreateContext_PTR)(EGLDisplay dpy, EGLConfig config,
                                           EGLContext share_context, const EGLint *attrib_list);

typedef EGLSurface (*eglCreatePbufferFromClientBuffer_PTR)(EGLDisplay dpy, EGLenum buftype,
                                                           EGLClientBuffer buffer, EGLConfig config,
                                                           const EGLint *attrib_list);

typedef EGLSurface (*eglCreatePbufferSurface_PTR)(EGLDisplay dpy, EGLConfig config,
                                                  const EGLint *attrib_list);

typedef EGLSurface (*eglCreatePixmapSurface_PTR)(EGLDisplay dpy, EGLConfig config,
                                                 EGLNativePixmapType pixmap,
                                                 const EGLint *attrib_list);

typedef EGLSurface (*eglCreatePlatformWindowSurface_PTR)(EGLDisplay display, EGLConfig config,
                                                         void *native_window,
                                                         const EGLint *attrib_list);

typedef EGLSurface (*eglCreateWindowSurface_PTR)(EGLDisplay dpy, EGLConfig config,
                                                 EGLNativeWindowType win,
                                                 const EGLint *attrib_list);

typedef EGLBoolean (*eglDestroyContext_PTR)(EGLDisplay dpy, EGLContext ctx);

typedef EGLBoolean (*eglDestroySurface_PTR)(EGLDisplay dpy, EGLSurface surface);

typedef EGLBoolean (*eglGetConfigAttrib_PTR)(EGLDisplay dpy, EGLConfig config, EGLint attribute,
                                             EGLint *value);

typedef EGLBoolean (*eglGetConfigs_PTR)(EGLDisplay dpy, EGLConfig *configs, EGLint config_size,
                                        EGLint *num_config);

typedef EGLContext (*eglGetCurrentContext_PTR)();

typedef EGLDisplay (*eglGetCurrentDisplay_PTR)();

typedef EGLSurface (*eglGetCurrentSurface_PTR)(EGLint readdraw);

typedef EGLDisplay (*eglGetDisplay_PTR)(EGLNativeDisplayType display_id);

typedef EGLDisplay (*eglGetPlatformDisplay_PTR)(EGLenum platform, void *native_display,
                                                const EGLint *attrib_list);

typedef EGLint (*eglGetError_PTR)();

typedef __eglMustCastToProperFunctionPointerType (*eglGetProcAddress_PTR)(const char *procname);

typedef EGLBoolean (*eglInitialize_PTR)(EGLDisplay dpy, EGLint *major, EGLint *minor);

typedef EGLBoolean (*eglMakeCurrent_PTR)(EGLDisplay dpy, EGLSurface draw, EGLSurface read,
                                         EGLContext ctx);

typedef EGLenum (*eglQueryAPI_PTR)();

typedef EGLBoolean (*eglQueryContext_PTR)(EGLDisplay dpy, EGLContext ctx, EGLint attribute,
                                          EGLint *value);

typedef const char *(*eglQueryString_PTR)(EGLDisplay dpy, EGLint name);

typedef EGLBoolean (*eglQuerySurface_PTR)(EGLDisplay dpy, EGLSurface surface, EGLint attribute,
                                          EGLint *value);

typedef EGLBoolean (*eglReleaseTexImage_PTR)(EGLDisplay dpy, EGLSurface surface, EGLint buffer);

typedef EGLBoolean (*eglReleaseThread_PTR)();

typedef EGLBoolean (*eglSurfaceAttrib_PTR)(EGLDisplay dpy, EGLSurface surface, EGLint attribute,
                                           EGLint value);

typedef EGLBoolean (*eglSwapBuffers_PTR)(EGLDisplay dpy, EGLSurface surface);

typedef EGLBoolean (*eglSwapBuffersWithDamageEXT_PTR)(EGLDisplay dpy, EGLSurface surface,
                                                      EGLint *rects, EGLint n_rects);

typedef EGLBoolean (*eglSwapInterval_PTR)(EGLDisplay dpy, EGLint interval);

typedef EGLBoolean (*eglTerminate_PTR)(EGLDisplay dpy);

typedef EGLBoolean (*eglUnlockSurfaceKHR_PTR)(EGLDisplay display, EGLSurface surface);

typedef EGLBoolean (*eglWaitClient_PTR)();

typedef EGLBoolean (*eglWaitGL_PTR)();

typedef EGLBoolean (*eglWaitNative_PTR)(EGLint engine);

struct egl_func_t {
    eglBindAPI_PTR eglBindAPI;
    eglBindTexImage_PTR eglBindTexImage;
    eglChooseConfig_PTR eglChooseConfig;
    eglCopyBuffers_PTR eglCopyBuffers;
    eglCreateContext_PTR eglCreateContext;
    eglCreatePbufferFromClientBuffer_PTR eglCreatePbufferFromClientBuffer;
    eglCreatePbufferSurface_PTR eglCreatePbufferSurface;
    eglCreatePixmapSurface_PTR eglCreatePixmapSurface;
    eglCreatePlatformWindowSurface_PTR eglCreatePlatformWindowSurface;
    eglCreateWindowSurface_PTR eglCreateWindowSurface;
    eglDestroyContext_PTR eglDestroyContext;
    eglDestroySurface_PTR eglDestroySurface;
    eglGetConfigAttrib_PTR eglGetConfigAttrib;
    eglGetConfigs_PTR eglGetConfigs;
    eglGetCurrentContext_PTR eglGetCurrentContext;
    eglGetCurrentDisplay_PTR eglGetCurrentDisplay;
    eglGetCurrentSurface_PTR eglGetCurrentSurface;
    eglGetDisplay_PTR eglGetDisplay;
    eglGetPlatformDisplay_PTR eglGetPlatformDisplay;
    eglGetError_PTR eglGetError;
    eglGetProcAddress_PTR eglGetProcAddress;
    eglInitialize_PTR eglInitialize;
    eglMakeCurrent_PTR eglMakeCurrent;
    eglQueryAPI_PTR eglQueryAPI;
    eglQueryContext_PTR eglQueryContext;
    eglQueryString_PTR eglQueryString;
    eglQuerySurface_PTR eglQuerySurface;
    eglReleaseTexImage_PTR eglReleaseTexImage;
    eglReleaseThread_PTR eglReleaseThread;
    eglSurfaceAttrib_PTR eglSurfaceAttrib;
    eglSwapBuffers_PTR eglSwapBuffers;
    eglSwapBuffersWithDamageEXT_PTR eglSwapBuffersWithDamageEXT;
    eglSwapInterval_PTR eglSwapInterval;
    eglTerminate_PTR eglTerminate;
    eglUnlockSurfaceKHR_PTR eglUnlockSurfaceKHR;
    eglWaitClient_PTR eglWaitClient;
    eglWaitGL_PTR eglWaitGL;
    eglWaitNative_PTR eglWaitNative;
};

/*
struct egl_func_t {
    EGLGETPROCADDRESSPROCP eglGetProcAddress;
    EGLCREATECONTEXTPROCP eglCreateContext;
    EGLDESTROYCONTEXTPROCP eglDestroyContext;
    EGLMAKECURRENTPROCP eglMakeCurrent;
    EGLQUERYSTRINGPROCP eglQueryString;
    EGLTERMINATEPROCP eglTerminate;
    EGLCHOOSECONFIGPROCP eglChooseConfig;
    EGLBINDAPIPROCP eglBindAPI;
    EGLINITIALIZEPROCP eglInitialize;
    EGLGETDISPLAYP eglGetDisplay;
    EGLCREATEPBUFFERSURFACEPROCP eglCreatePbufferSurface;
    EGLDESTROYSURFACEPROCP eglDestroySurface;
    EGLGETERRORPROCP eglGetError;
    EGLCREATEWINDOWSURFACEPROCP eglCreateWindowSurface;
};
EGLContext mglues_eglCreateContext(EGLDisplay dpy, EGLConfig config, EGLContext share_context, const EGLint *attrib_list);
EGLBoolean mglues_eglDestroyContext(EGLDisplay dpy, EGLContext ctx);
EGLBoolean mglues_eglMakeCurrent(EGLDisplay dpy, EGLSurface draw, EGLSurface read, EGLContext ctx);
*/

void init_target_egl();
void destroy_temp_egl_ctx();


#ifdef __cplusplus
}
#endif

#endif //FOLD_CRAFT_LAUNCHER_EGL_LOADER_H
