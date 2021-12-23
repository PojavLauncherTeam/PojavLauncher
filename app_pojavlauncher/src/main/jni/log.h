#ifdef __ANDROID__
#include <android/log.h>

#define TAG "jrelog"
#endif

#ifdef __cplusplus
extern "C" {
#endif

#define LOGE(...) __android_log_print(ANDROID_LOG_INFO,    TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_SILENT,    TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_SILENT,    TAG, __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_INFO,    TAG, __VA_ARGS__)

#ifdef __cplusplus
}
#endif

