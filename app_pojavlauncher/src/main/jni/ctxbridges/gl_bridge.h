//
// Created by maks on 17.09.2022.
//
#include <EGL//egl.h>
#include <stdbool.h>
#ifndef POJAVLAUNCHER_GL_BRIDGE_H
#define POJAVLAUNCHER_GL_BRIDGE_H

typedef struct {
    char       state;
    EGLConfig  config;
    EGLint     format;
    EGLContext context;
    EGLSurface surface;
    struct ANativeWindow *nativeSurface;
    struct ANativeWindow *newNativeSurface;
} render_window_t;

bool gl_init();
render_window_t* gl_init_context(render_window_t* share);
void gl_make_current(render_window_t* bundle);
void gl_swap_buffers();
void gl_setup_window();
void gl_swap_interval(int swapInterval);


#endif //POJAVLAUNCHER_GL_BRIDGE_H
