//
// Created by maks on 05.06.2023.
//
#include <android/dlext.h>
#include <string.h>
#include <stdio.h>
// Silence the warnings about using reserved identifiers (we need to link to these to not pollute the global symtab)
//NOLINTBEGIN
static void* (*android_dlopen_ext_p)(const char* filename,
                                  int flags,
                                  const android_dlextinfo* extinfo,
                                  const void* caller_addr);
static struct android_namespace_t* (*android_get_exported_namespace_p)(const char* name);
//NOLINTEND
static void* ready_handle;

static const char *sphal_namespaces[3] = {
        "sphal", "vendor", "default"
};


__attribute__((visibility("default"), used)) void app__pojav_linkerhook_pass_handles(void* data, void* android_dlopen_ext,
                                                                                    void* android_get_exported_namespace) {
    ready_handle = data;
    android_dlopen_ext_p = android_dlopen_ext;
    android_get_exported_namespace_p = android_get_exported_namespace;
}

__attribute__((visibility("default"), used)) void *android_dlopen_ext(const char *filename, int flags, const android_dlextinfo *extinfo) {
    if(!strstr(filename, "vulkan."))
        return android_dlopen_ext_p(filename, flags, extinfo, &android_dlopen_ext);
    return ready_handle;
}

__attribute__((visibility("default"), used)) void *android_load_sphal_library(const char *filename, int flags) {
    if(strstr(filename, "vulkan.")) {
        return ready_handle;
    }
    //printf("__loader_android_get_exported_namespace = %p\n__loader_android_dlopen_ext = %p\n", __loader_android_get_exported_namespace,
    //       __loader_android_dlopen_ext);
    struct android_namespace_t* androidNamespace;
    for(int i = 0; i < 3; i++) {
        androidNamespace = android_get_exported_namespace_p(sphal_namespaces[i]);
        if(androidNamespace != NULL) break;
    }
    android_dlextinfo info;
    info.flags = ANDROID_DLEXT_USE_NAMESPACE;
    info.library_namespace = androidNamespace;
    return android_dlopen_ext_p(filename, flags, &info, &android_dlopen_ext);
}

// This is done for older android versions which don't
// export this function. Technically this is wrong
// but for our usage it's fine enough
__attribute__((visibility("default"), used)) uint64_t atrace_get_enabled_tags() {
    return 0;
}