//
// Created by maks on 05.06.2023.
//
#include <android/dlext.h>
#include <android/log.h>
#include <string.h>

typedef void* (*android_dlopen_ext_t)(const char *filename, int flags, const android_dlextinfo *extinfo);
typedef struct android_namespace_t* (*android_get_exported_namespace_t)(const char* name);

static void* ready_handle;
static android_dlopen_ext_t original_func;
static android_get_exported_namespace_t android_get_exported_namespace;

static const char *sphal_namespaces[3] = {
        "sphal", "vendor", "default"
};


__attribute__((visibility("default"))) void app__pojav_linkerhook_set_data(void* data, void* data1, void* data2) {
    ready_handle = data;
    original_func = data1;
    android_get_exported_namespace = data2;
}

__attribute__((visibility("default"))) void *android_dlopen_ext(const char *filename, int flags, const android_dlextinfo *extinfo) {
    if(!strstr(filename, "vulkan."))
        return original_func(filename, flags, extinfo);
    return ready_handle;
}

__attribute__((visibility("default"))) void *android_load_sphal_library(const char *filename, int flags) {
    if(strstr(filename, "vulkan.")) return ready_handle;
    struct android_namespace_t* androidNamespace;
    for(int i = 0; i < 3; i++) {
        androidNamespace = android_get_exported_namespace(sphal_namespaces[i]);
        if(androidNamespace != NULL) break;
    }
    android_dlextinfo info;
    info.flags = ANDROID_DLEXT_USE_NAMESPACE;
    info.library_namespace = androidNamespace;
    return original_func(filename, flags, &info);
}