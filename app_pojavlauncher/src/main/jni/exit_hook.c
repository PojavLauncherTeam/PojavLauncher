//
// Created by maks on 15.01.2025.
//

#include <jni.h>
#include <unistd.h>
#include <stdbool.h>
#include <bytehook.h>
#include <dlfcn.h>
#include <android/log.h>
#include <stdlib.h>
#include "stdio_is.h"

static _Atomic bool exit_tripped = false;

typedef void (*exit_func)(int);

static void custom_exit(int code) {
    // If the exit was already done (meaning it is recursive or from a different thread), pass the call through
    if(exit_tripped) {
        BYTEHOOK_CALL_PREV(custom_exit, exit_func, code);
        BYTEHOOK_POP_STACK();
        return;
    }
    exit_tripped = true;
    // Perform a nominal exit, as we expect.
    nominal_exit(code, false);
    BYTEHOOK_POP_STACK();
}

static void custom_atexit() {
    // Same as custom_exit, but without the code or the exit passthrough.
    if(exit_tripped) {
        return;
    }
    exit_tripped = true;
    nominal_exit(0, false);
}

static bool init_exit_hook() {
    void* bytehook_handle = dlopen("libbytehook.so", RTLD_NOW);
    if(bytehook_handle == NULL) {
        goto dlerror;
    }

    bytehook_stub_t (*bytehook_hook_all_p)(const char *callee_path_name, const char *sym_name, void *new_func,
    bytehook_hooked_t hooked, void *hooked_arg);
    int (*bytehook_init_p)(int mode, bool debug);

    bytehook_hook_all_p = dlsym(bytehook_handle, "bytehook_hook_all");
    bytehook_init_p = dlsym(bytehook_handle, "bytehook_init");

    if(bytehook_hook_all_p == NULL || bytehook_init_p == NULL) {
        goto dlerror;
    }
    int bhook_status = bytehook_init_p(BYTEHOOK_MODE_AUTOMATIC, false);
    if(bhook_status == BYTEHOOK_STATUS_CODE_OK) {
        bytehook_stub_t stub = bytehook_hook_all_p(NULL, "exit", &custom_exit, NULL, NULL);
        __android_log_print(ANDROID_LOG_INFO, "exit_hook", "Successfully initialized exit hook, stub=%p", stub);
        return true;
    } else {
        __android_log_print(ANDROID_LOG_INFO, "exit_hook", "bytehook_init failed (%i)", bhook_status);
        dlclose(bytehook_handle);
        return false;
    }

    dlerror:
    if(bytehook_handle != NULL) dlclose(bytehook_handle);
    __android_log_print(ANDROID_LOG_ERROR, "exit_hook", "Failed to load hook library: %s", dlerror());
    return false;
}

JNIEXPORT void JNICALL
Java_net_kdt_pojavlaunch_utils_JREUtils_initializeGameExitHook(JNIEnv *env, jclass clazz) {
    bool hookReady = init_exit_hook();
    if(!hookReady){
        // If we can't hook, register atexit(). This won't report a proper error code,
        // but it will prevent a SIGSEGV or a SIGABRT from the depths of Dalvik that happens
        // on exit().
        atexit(custom_atexit);
    }
}