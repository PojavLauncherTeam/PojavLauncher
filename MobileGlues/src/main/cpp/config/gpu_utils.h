//
// Created by hanji on 2025/2/9.
//

#ifndef MOBILEGLUES_PLUGIN_GPU_UTILS_H
#define MOBILEGLUES_PLUGIN_GPU_UTILS_H

#include <string.h>

#ifdef __cplusplus
extern "C" {
#endif

const char *getGPUInfo();

int isAdreno(const char *gpu);

int isAdreno740(const char *gpu);

int isAdreno830(const char *gpu);

int hasVulkan13();

#ifdef __cplusplus
}
#endif

#endif //MOBILEGLUES_PLUGIN_GPU_UTILS_H
