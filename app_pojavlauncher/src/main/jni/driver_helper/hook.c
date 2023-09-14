//
// Created by maks on 05.06.2023.
//
#include <android/dlext.h>
#include <string.h>
// Silence the warnings about using reserved identifiers (we need to link to these to not pollute the global symtab)
//NOLINTBEGIN
__attribute__((weak)) void*  __loader_android_dlopen_ext(const char* filename,
                                  int flags,
                                  const android_dlextinfo* extinfo,
                                  const void* caller_addr);
__attribute__((weak)) struct android_namespace_t* __loader_android_get_exported_namespace(const char* name);
//NOLINTEND
static void* ready_handle;

static const char *sphal_namespaces[3] = {
        "sphal", "vendor", "default"
};


__attribute__((visibility("default"), used)) void app__pojav_linkerhook_pass_handle(void* data) {
    ready_handle = data;
}

__attribute__((visibility("default"), used)) void *android_dlopen_ext(const char *filename, int flags, const android_dlextinfo *extinfo) {
    if(!strstr(filename, "vulkan."))
        return __loader_android_dlopen_ext(filename, flags, extinfo, &android_dlopen_ext);
    return ready_handle;
}

__attribute__((visibility("default"), used)) void *android_load_sphal_library(const char *filename, int flags) {
    if(strstr(filename, "vulkan.")) {
        return ready_handle;
    }
    struct android_namespace_t* androidNamespace;
    for(int i = 0; i < 3; i++) {
        androidNamespace = __loader_android_get_exported_namespace(sphal_namespaces[i]);
        if(androidNamespace != NULL) break;
    }
    android_dlextinfo info;
    info.flags = ANDROID_DLEXT_USE_NAMESPACE;
    info.library_namespace = androidNamespace;
    return __loader_android_dlopen_ext(filename, flags, &info, &android_dlopen_ext);
}

// This is done for older android versions which don't
// export this function. Technically this is wrong
// but for our usage it's fine enough
__attribute__((visibility("default"), used)) uint64_t atrace_get_enabled_tags() {
    return 0;
}