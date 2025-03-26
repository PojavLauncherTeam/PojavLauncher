//
// Created by Swung 0x48 on 2024/10/7.
//

#ifndef MOBILEGLUES_INCLUDES_H
#define MOBILEGLUES_INCLUDES_H

#define RENDERERNAME "MobileGlues"
#include <android/log.h>
#include <dlfcn.h>

#include <EGL/egl.h>
#include <GLES3/gl32.h>

#include "egl/egl.h"
#include "egl/loader.h"

#if PROFILING
#include <perfetto.h>
PERFETTO_DEFINE_CATEGORIES(
        perfetto::Category("glcalls")
                .SetDescription("Calls from OpenGL"),
        perfetto::Category("internal")
                .SetDescription("Internal calls"));
#endif

#ifdef __cplusplus
extern "C" {
#endif

static int g_initialized = 0;

void proc_init();

#ifdef __cplusplus
}
#endif

#endif //MOBILEGLUES_INCLUDES_H
