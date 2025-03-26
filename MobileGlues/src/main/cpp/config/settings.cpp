//
// Created by hanji on 2025/2/9.
//

#include "settings.h"
#include "config.h"
#include "../gl/log.h"
#include "../gl/envvars.h"
#include "gpu_utils.h"

#define DEBUG 0

struct global_settings_t global_settings;

void init_settings() {
    int success = initialized;
    if (!success) {
        success = config_refresh();
        if (!success) {
            LOG_V("Failed to load config. Use default config.")
        }
    }

    int enableANGLE = success ? config_get_int("enableANGLE") : 0;
    int enableNoError = success ? config_get_int("enableNoError") : 0;
    int enableExtGL43 = success ? config_get_int("enableExtGL43") : 0;
    int enableExtComputeShader = success ? config_get_int("enableExtComputeShader") : 0;
    int enableCompatibleMode = success ? config_get_int("enableCompatibleMode") : 0;
    size_t maxGlslCacheSize = 0;
    if (config_get_int("maxGlslCacheSize") > 0)
        maxGlslCacheSize = success ? config_get_int("maxGlslCacheSize") * 1024 * 1024 : 0;

    if (enableANGLE < 0 || enableANGLE > 3)
        enableANGLE = 0;
    if (enableNoError < 0 || enableNoError > 3)
        enableNoError = 0;
    if (enableExtGL43 < 0 || enableExtGL43 > 1)
        enableExtGL43 = 0;
    if (enableExtComputeShader < 0 || enableExtComputeShader > 1)
        enableExtComputeShader = 0;
    if (enableCompatibleMode < 0 || enableCompatibleMode > 1)
        enableCompatibleMode = 0;

    // 1205
    int fclVersion = 0;
    GetEnvVarInt("FCL_VERSION_CODE", &fclVersion, 0);
    // 140000
    int zlVersion = 0;
    GetEnvVarInt("ZALITH_VERSION_CODE", &zlVersion, 0);
    // unknown
    int pgwVersion = 0;
    GetEnvVarInt("PGW_VERSION_CODE", &pgwVersion, 0);

    char* var = getenv("MG_DIR_PATH");

    if (fclVersion == 0 && zlVersion == 0 && pgwVersion == 0 && !var) {
        LOG_V("Unsupported launcher detected, force using default config.")
        enableANGLE = 0;
        enableNoError = 0;
        enableExtGL43 = 0;
        enableExtComputeShader = 0;
        maxGlslCacheSize = 0;
        enableCompatibleMode = 0;
    }

    const char* gpuString = getGPUInfo();
    LOG_D("GPU: %s", gpuString);

    if (enableANGLE == 2 || enableANGLE == 3) {
        // Force enable / disable
        global_settings.angle = enableANGLE - 2;
    } else {
        int isQcom = isAdreno(gpuString);
        int is740 = isAdreno740(gpuString);
        int is830 = isAdreno830(gpuString);
        int hasVk13 = hasVulkan13();

        LOG_D("Is Adreno? = %s", isQcom ? "true" : "false")
        LOG_D("Is Adreno 830? = %s", is830 ? "true" : "false")
        LOG_D("Is Adreno 740? = %s", is740 ? "true" : "false")
        LOG_D("Has Vulkan 1.3? = %s", hasVk13 ? "true" : "false")

        if (is830)
            global_settings.angle = 1;
        if (is740)
            global_settings.angle = 0;
        else
            global_settings.angle = hasVk13 && enableANGLE;
    }
    LOG_D("enableANGLE = %d", enableANGLE)
    LOG_D("global_settings.angle = %d", global_settings.angle)

//    if (enableANGLE == 1) {
//        global_settings.angle = (isAdreno740(gpuString) || !hasVulkan13()) ? 0 : 1;
//    } else if (enableANGLE == 2 || enableANGLE == 3) {
//        global_settings.angle = enableANGLE - 2;
//    } else {
//        int is830 = isAdreno830(gpuString);
//        LOG_D("Is Adreno 830? = %s", is830 ? "true" : "false")
//        global_settings.angle = is830 ? 1 : 0;
//    }


    if (global_settings.angle) {
        setenv("LIBGL_GLES", "libGLESv2_angle.so", 1);
        setenv("LIBGL_EGL", "libEGL_angle.so", 1);
    }

    if (enableNoError == 1 || enableNoError == 2 || enableNoError == 3) {
        global_settings.ignore_error = enableNoError - 1;
    } else {
        global_settings.ignore_error = 0;
    }

    global_settings.ext_gl43 = enableExtGL43;

    global_settings.ext_compute_shader = enableExtComputeShader;
    
    global_settings.maxGlslCacheSize = maxGlslCacheSize;

    global_settings.enableCompatibleMode = enableCompatibleMode;
}