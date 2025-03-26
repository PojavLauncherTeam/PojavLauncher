//
// Created by Swung 0x48 on 2024/10/8.
//

#ifndef FOLD_CRAFT_LAUNCHER_EGL_H
#define FOLD_CRAFT_LAUNCHER_EGL_H

#include <EGL/egl.h>

typedef intptr_t EGLAttrib;

typedef __eglMustCastToProperFunctionPointerType (*EGLGETPROCADDRESSPROCP) (const char *procname);

typedef EGLint (*EGLGETERRORPROCP)(void);
typedef EGLDisplay (*EGLGETDISPLAYP)(EGLNativeDisplayType display_id);
typedef EGLBoolean (*EGLINITIALIZEPROCP)(EGLDisplay dpy, EGLint *major, EGLint *minor);
typedef EGLBoolean (*EGLTERMINATEPROCP)(EGLDisplay dpy);
typedef const char * (*EGLQUERYSTRINGPROCP)(EGLDisplay dpy, EGLint name);
typedef EGLBoolean (*EGLGETCONFIGSPROCP)(EGLDisplay dpy, EGLConfig *configs, EGLint config_size, EGLint *num_config);
typedef EGLBoolean (*EGLCHOOSECONFIGPROCP)(EGLDisplay dpy, const EGLint *attrib_list, EGLConfig *configs, EGLint config_size, EGLint *num_config);
typedef EGLBoolean (*EGLGETCONFIGATTRIBPROCP)(EGLDisplay dpy, EGLConfig config, EGLint attribute, EGLint *value);

typedef EGLSurface (*EGLCREATEWINDOWSURFACEPROCP)(EGLDisplay dpy, EGLConfig config, EGLNativeWindowType win, const EGLint *attrib_list);
typedef EGLSurface (*EGLCREATEPBUFFERSURFACEPROCP)(EGLDisplay dpy, EGLConfig config, const EGLint *attrib_list);
typedef EGLSurface (*EGLCREATEPIXMAPSURFACEPROCP)(EGLDisplay dpy, EGLConfig config, EGLNativePixmapType pixmap, const EGLint *attrib_list);
typedef EGLBoolean (*EGLDESTROYSURFACEPROCP)(EGLDisplay dpy, EGLSurface surface);
typedef EGLBoolean (*EGLQUERYSURFACEPROCP)(EGLDisplay dpy, EGLSurface surface, EGLint attribute, EGLint *value);
typedef EGLBoolean (*EGLBINDAPIPROCP)(EGLenum api);
typedef EGLenum (*EGLQUERYAPIPROCP)(void);

typedef EGLBoolean (*EGLWAITCLIENTPROCP)(void);
typedef EGLBoolean (*EGLRELEASETHREADPROCP)(void);
typedef EGLSurface (*EGLCREATEPBUFFERFROMCLIENTBUFFERPROCP)(EGLDisplay dpy, EGLenum buftype, EGLClientBuffer buffer, EGLConfig config, const EGLint *attrib_list);
typedef EGLBoolean (*EGLSURFACEATTRIBPROCP)(EGLDisplay dpy, EGLSurface surface, EGLint attribute, EGLint value);
typedef EGLBoolean (*EGLBINDTEXIMAGEPROCP)(EGLDisplay dpy, EGLSurface surface, EGLint buffer);
typedef EGLBoolean (*EGLRELEASETEXIMAGEPROCP)(EGLDisplay dpy, EGLSurface surface, EGLint buffer);
typedef EGLBoolean (*EGLSWAPINTERVALPROCP)(EGLDisplay dpy, EGLint interval);
typedef EGLContext (*EGLCREATECONTEXTPROCP)(EGLDisplay dpy, EGLConfig config, EGLContext share_context, const EGLint *attrib_list);
typedef EGLBoolean (*EGLDESTROYCONTEXTPROCP)(EGLDisplay dpy, EGLContext ctx);
typedef EGLBoolean (*EGLMAKECURRENTPROCP)(EGLDisplay dpy, EGLSurface draw, EGLSurface read, EGLContext ctx);
typedef EGLContext (*EGLGETCURRENTCONTEXTPROCP)(void);
typedef EGLSurface (*EGLGETCURRENTSURFACEPROCP)(EGLint readdraw);
typedef EGLDisplay (*EGLGETCURRENTDISPLAYPROCP)(void);
typedef EGLDisplay (*EGLGETPLATFORMDISPLAYPROCP)(EGLenum platform, void *native_display, const EGLAttrib *attrib_list);
typedef EGLBoolean (*EGLQUERYCONTEXTPROCP)(EGLDisplay dpy, EGLContext ctx, EGLint attribute, EGLint *value);
typedef EGLBoolean (*EGLWAITGLPROCP)(void);
typedef EGLBoolean (*EGLWAITNATIVEPROCP)(EGLint engine);
typedef EGLBoolean (*EGLSWAPBUFFERSPROCP)(EGLDisplay dpy, EGLSurface surface);
typedef EGLBoolean (*EGLCOPYBUFFERSPROCP)(EGLDisplay dpy, EGLSurface surface, EGLNativePixmapType target);

// Undocumented libmali internals, needed for ODROID Go Ultra
//NativePixmapType (*EGL_CREATE_PIXMAP_ID_MAPPINGPROCP)(void *pixmap);
//NativePixmapType (*EGL_DESTROY_PIXMAP_ID_MAPPINGPROCP)(int id);

// Undocumented libmali internals, needed for ODROID Go Ultra
//NativePixmapType (*EGL_CREATE_PIXMAP_ID_MAPPINGPROCP)(void *pixmap);
//NativePixmapType (*EGL_DESTROY_PIXMAP_ID_MAPPINGPROCP)(int id);

#endif //FOLD_CRAFT_LAUNCHER_EGL_H
