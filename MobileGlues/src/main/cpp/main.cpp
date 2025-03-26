//
// Created by Swung 0x48 on 2024/10/7.
//

#include <string.h>
#include <sys/stat.h>
#include <errno.h>
#include "includes.h"
#include "gl/gl.h"
#include "egl/egl.h"
#include "egl/loader.h"
#include "gles/loader.h"
#include "gl/envvars.h"
#include "gl/log.h"
#include "config/settings.h"

#define DEBUG 0

__attribute__((used)) const char* license = "GNU LGPL-2.1 License";

extern char* (*MesaConvertShader)(const char *src, unsigned int type, unsigned int glsl, unsigned int essl);
void init_libshaderconv() {
    const char *shaderconv_lib = "libshaderconv";
    const char *func_name = "MesaConvertShader";
    const char *glslconv_name[] = {shaderconv_lib, NULL};
    void* glslconv = open_lib(glslconv_name, shaderconv_lib);
    if (glslconv == NULL) {
        LOG_D("%s not found\n", shaderconv_lib);
    }
    else {
        MesaConvertShader = (char * (*)(const char *,unsigned int,unsigned int,unsigned int))dlsym(glslconv, func_name);
        if (MesaConvertShader) {
            LOG_D("%s loaded\n", shaderconv_lib);
        } else {
            LOG_D("failed to load %s\n", shaderconv_lib);
        }
    }
}

void init_config() {
    if (check_path())
        config_refresh();
}

void show_license() {
    LOG_V("The Open Source License of MobileGlues: ");
    LOG_V("  %s", license);
}

#if PROFILING

PERFETTO_TRACK_EVENT_STATIC_STORAGE();

void init_perfetto() {
    perfetto::TracingInitArgs args;

    args.backends |= perfetto::kSystemBackend;

    perfetto::Tracing::Initialize(args);
    perfetto::TrackEvent::Register();
}
#endif

void proc_init() {
    init_config();

    clear_log();
    start_log();

    init_settings();

    LOG_V("Initializing %s ...", RENDERERNAME);
    show_license();

    load_libs();
    init_target_egl();
    init_target_gles();

    init_libshaderconv();

#if PROFILING
    init_perfetto();
#endif

    // Cleanup
    destroy_temp_egl_ctx();
    g_initialized = 1;
}
