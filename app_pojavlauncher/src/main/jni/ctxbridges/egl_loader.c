//
// Created by maks on 21.09.2022.
//
#include <stddef.h>
#include <stdlib.h>
#include <dlfcn.h>
#include "egl_loader.h"
#include "loader_dlopen.h"

EGLBoolean (*eglMakeCurrent_p) (EGLDisplay dpy, EGLSurface draw, EGLSurface read, EGLContext ctx);
EGLBoolean (*eglDestroyContext_p) (EGLDisplay dpy, EGLContext ctx);
EGLBoolean (*eglDestroySurface_p) (EGLDisplay dpy, EGLSurface surface);
EGLBoolean (*eglTerminate_p) (EGLDisplay dpy);
EGLBoolean (*eglReleaseThread_p) (void);
EGLContext (*eglGetCurrentContext_p) (void);
EGLDisplay (*eglGetDisplay_p) (NativeDisplayType display);
EGLBoolean (*eglInitialize_p) (EGLDisplay dpy, EGLint *major, EGLint *minor);
EGLBoolean (*eglChooseConfig_p) (EGLDisplay dpy, const EGLint *attrib_list, EGLConfig *configs, EGLint config_size, EGLint *num_config);
EGLBoolean (*eglGetConfigAttrib_p) (EGLDisplay dpy, EGLConfig config, EGLint attribute, EGLint *value);
EGLBoolean (*eglBindAPI_p) (EGLenum api);
EGLSurface (*eglCreatePbufferSurface_p) (EGLDisplay dpy, EGLConfig config, const EGLint *attrib_list);
EGLSurface (*eglCreateWindowSurface_p) (EGLDisplay dpy, EGLConfig config, NativeWindowType window, const EGLint *attrib_list);
EGLBoolean (*eglSwapBuffers_p) (EGLDisplay dpy, EGLSurface draw);
EGLint (*eglGetError_p) (void);
EGLContext (*eglCreateContext_p) (EGLDisplay dpy, EGLConfig config, EGLContext share_list, const EGLint *attrib_list);
EGLBoolean (*eglSwapInterval_p) (EGLDisplay dpy, EGLint interval);
EGLSurface (*eglGetCurrentSurface_p) (EGLint readdraw);
EGLBoolean (*eglQuerySurface_p)( 	EGLDisplay display,
                                           EGLSurface surface,
                                           EGLint attribute,
                                           EGLint * value);
__eglMustCastToProperFunctionPointerType (*eglGetProcAddress_p) (const char *procname);

bool dlsym_EGL() {
    void* dl_handle = loader_dlopen(getenv("POJAVEXEC_EGL"),"libEGL.so", RTLD_LOCAL|RTLD_LAZY);
    if(dl_handle == NULL) return false;
    eglGetProcAddress_p = dlsym(dl_handle, "eglGetProcAddress");
    if(eglGetProcAddress_p == NULL) {
        printf("%s\n", dlerror());
        return false;
    }
    eglBindAPI_p = (void*) eglGetProcAddress_p("eglBindAPI");
    eglChooseConfig_p = (void*) eglGetProcAddress_p("eglChooseConfig");
    eglCreateContext_p = (void*) eglGetProcAddress_p("eglCreateContext");
    eglCreatePbufferSurface_p = (void*) eglGetProcAddress_p("eglCreatePbufferSurface");
    eglCreateWindowSurface_p = (void*) eglGetProcAddress_p("eglCreateWindowSurface");
    eglDestroyContext_p = (void*) eglGetProcAddress_p("eglDestroyContext");
    eglDestroySurface_p = (void*) eglGetProcAddress_p("eglDestroySurface");
    eglGetConfigAttrib_p = (void*) eglGetProcAddress_p("eglGetConfigAttrib");
    eglGetCurrentContext_p = (void*) eglGetProcAddress_p("eglGetCurrentContext");
    eglGetDisplay_p = (void*) eglGetProcAddress_p("eglGetDisplay");
    eglGetError_p = (void*) eglGetProcAddress_p("eglGetError");
    eglInitialize_p = (void*) eglGetProcAddress_p("eglInitialize");
    eglMakeCurrent_p = (void*) eglGetProcAddress_p("eglMakeCurrent");
    eglSwapBuffers_p = (void*) eglGetProcAddress_p("eglSwapBuffers");
    eglReleaseThread_p = (void*) eglGetProcAddress_p("eglReleaseThread");
    eglSwapInterval_p = (void*) eglGetProcAddress_p("eglSwapInterval");
    eglTerminate_p = (void*) eglGetProcAddress_p("eglTerminate");
    eglGetCurrentSurface_p = (void*) eglGetProcAddress_p("eglGetCurrentSurface");
    eglQuerySurface_p = (void*) eglGetProcAddress_p("eglQuerySurface");
    return true;
}
