//
// Created by maks on 21.09.2022.
//
#include <EGL/egl.h>
#include <stdbool.h>
#ifndef POJAVLAUNCHER_EGL_LOADER_H
#define POJAVLAUNCHER_EGL_LOADER_H

extern EGLBoolean (*eglMakeCurrent_p) (EGLDisplay dpy, EGLSurface draw, EGLSurface read, EGLContext ctx);
extern EGLBoolean (*eglDestroyContext_p) (EGLDisplay dpy, EGLContext ctx);
extern EGLBoolean (*eglDestroySurface_p) (EGLDisplay dpy, EGLSurface surface);
extern EGLBoolean (*eglTerminate_p) (EGLDisplay dpy);
extern EGLBoolean (*eglReleaseThread_p) (void);
extern EGLContext (*eglGetCurrentContext_p) (void);
extern EGLDisplay (*eglGetDisplay_p) (NativeDisplayType display);
extern EGLBoolean (*eglInitialize_p) (EGLDisplay dpy, EGLint *major, EGLint *minor);
extern EGLBoolean (*eglChooseConfig_p) (EGLDisplay dpy, const EGLint *attrib_list, EGLConfig *configs, EGLint config_size, EGLint *num_config);
extern EGLBoolean (*eglGetConfigAttrib_p) (EGLDisplay dpy, EGLConfig config, EGLint attribute, EGLint *value);
extern EGLBoolean (*eglBindAPI_p) (EGLenum api);
extern EGLSurface (*eglCreatePbufferSurface_p) (EGLDisplay dpy, EGLConfig config, const EGLint *attrib_list);
extern EGLSurface (*eglCreateWindowSurface_p) (EGLDisplay dpy, EGLConfig config, NativeWindowType window, const EGLint *attrib_list);
extern EGLBoolean (*eglSwapBuffers_p) (EGLDisplay dpy, EGLSurface draw);
extern EGLint (*eglGetError_p) (void);
extern EGLContext (*eglCreateContext_p) (EGLDisplay dpy, EGLConfig config, EGLContext share_list, const EGLint *attrib_list);
extern EGLBoolean (*eglSwapInterval_p) (EGLDisplay dpy, EGLint interval);
extern EGLSurface (*eglGetCurrentSurface_p) (EGLint readdraw);
extern EGLBoolean (*eglQuerySurface_p)( 	EGLDisplay display,
                                      EGLSurface surface,
                                      EGLint attribute,
                                      EGLint * value);
extern __eglMustCastToProperFunctionPointerType (*eglGetProcAddress_p) (const char *procname);

bool dlsym_EGL();

#endif //POJAVLAUNCHER_EGL_LOADER_H
