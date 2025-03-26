//
// Created by BZLZHH on 2025/1/26.
//

#ifndef MOBILEGLUES_LOG_H

#include "mg.h"

#define FORCE_SYNC_WITH_LOG_FILE 0

#define GLOBAL_DEBUG 0

#define LOG_CALLED_FUNCS 0

#ifdef __cplusplus
extern "C" {
#endif

const char *glEnumToString(GLenum e);

#ifdef __cplusplus
}
#endif

#if GLOBAL_DEBUG_FORCE_OFF
#define LOG()  {}
#define LOG_D(...) {}
#define LOG_W(...) {}
#define LOG_E(...) {}
#define LOG_F(...) {}
#else
#if PROFILING
#define LOG() \
        perfetto::StaticString _FUNC_NAME_ = __func__;      \
        TRACE_EVENT("glcalls", _FUNC_NAME_);
#elif LOG_CALLED_FUNCS
#define LOG() \
    if(DEBUG||GLOBAL_DEBUG) { \
        __android_log_print(ANDROID_LOG_DEBUG, RENDERERNAME, "Use function: %s", __FUNCTION__); \
        printf("Use function: %s\n", __FUNCTION__); \
        write_log("Use function: %s\n", __FUNCTION__); \
    } \
    log_unique_function(__FUNCTION__);
void log_unique_function(const char* func_name);
#else
#define LOG() \
    if(DEBUG||GLOBAL_DEBUG) {__android_log_print(ANDROID_LOG_DEBUG, RENDERERNAME, "Use function: %s", __FUNCTION__);printf("Use function: %s\n", __FUNCTION__);write_log("Use function: %s\n", __FUNCTION__);}
#endif

#define LOG_D(...) if(DEBUG||GLOBAL_DEBUG) {__android_log_print(ANDROID_LOG_DEBUG, RENDERERNAME, __VA_ARGS__);printf(__VA_ARGS__);printf("\n");write_log(__VA_ARGS__);}
#define LOG_W(...) if(DEBUG||GLOBAL_DEBUG) {__android_log_print(ANDROID_LOG_WARN, RENDERERNAME, __VA_ARGS__);printf(__VA_ARGS__);printf("\n");write_log(__VA_ARGS__);}
#define LOG_E(...) if(DEBUG||GLOBAL_DEBUG) {__android_log_print(ANDROID_LOG_ERROR, RENDERERNAME, __VA_ARGS__);printf(__VA_ARGS__);printf("\n");write_log(__VA_ARGS__);}
#define LOG_F(...) if(DEBUG||GLOBAL_DEBUG) {__android_log_print(ANDROID_LOG_FATAL, RENDERERNAME, __VA_ARGS__);printf(__VA_ARGS__);printf("\n");write_log(__VA_ARGS__);}
#endif

#define LOG_V(...) {__android_log_print(ANDROID_LOG_VERBOSE, RENDERERNAME, __VA_ARGS__);printf(__VA_ARGS__);printf("\n");write_log(__VA_ARGS__);}
#define LOG_I(...) {__android_log_print(ANDROID_LOG_INFO, RENDERERNAME, __VA_ARGS__);printf(__VA_ARGS__);printf("\n");write_log(__VA_ARGS__);}
#define LOG_W_FORCE(...) {__android_log_print(ANDROID_LOG_WARN, RENDERERNAME, __VA_ARGS__);printf(__VA_ARGS__);printf("\n");write_log(__VA_ARGS__);}

#define MOBILEGLUES_LOG_H

#endif //MOBILEGLUES_LOG_H
