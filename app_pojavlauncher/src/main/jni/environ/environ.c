//
// Created by maks on 24.09.2022.
//

#include <stdlib.h>
#include <android/log.h>
#include <assert.h>
#include <string.h>
#include "environ.h"
struct pojav_environ_s *pojav_environ;
__attribute__((constructor)) void env_init() {
    char* strptr_env = getenv("POJAV_ENVIRON");
    if(strptr_env == NULL) {
        __android_log_print(ANDROID_LOG_INFO, "Environ", "No environ found, creating...");
        pojav_environ = calloc(sizeof(struct pojav_environ_s), 1);
        assert(pojav_environ);
        if(asprintf(&strptr_env, "%p", pojav_environ) == -1) abort();
        setenv("POJAV_ENVIRON", strptr_env, 1);
        free(strptr_env);
    }else{
        __android_log_print(ANDROID_LOG_INFO, "Environ", "Found existing environ: %s", strptr_env);
        pojav_environ = (void*) strtoul(strptr_env, NULL, 0x10);
    }
    __android_log_print(ANDROID_LOG_INFO, "Environ", "%p", pojav_environ);
}
