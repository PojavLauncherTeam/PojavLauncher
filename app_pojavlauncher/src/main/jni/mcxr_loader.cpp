#include <jni.h>
#include <cstring>
#include <android/window.h>
#include <android/native_activity.h>
#include <mcxr_loader/android_native_app_glue.h>

#include <mcxr_loader/openxr_platform.h>
#include <mcxr_loader/openxr.h>

//
// Created by Judge on 12/23/2021.
//

jobject* context;
JavaVM* vm;

JNIEXPORT JNICALL
extern "C" jlong
Java_net_kdt_pojavlaunch_MCXRLoader_getContextPtr(JNIEnv *env, jclass clazz) {
    return (jlong) &context;
}

JNIEXPORT JNICALL
extern "C" void
Java_net_kdt_pojavlaunch_MCXRLoader_setContext(JNIEnv *env, jclass clazz, jobject ctx) {
    context = reinterpret_cast<jobject *>(ctx);
}

jint JNI_OnLoad(JavaVM* jvm, void* reserved) {
    vm = jvm;
    return JNI_VERSION_1_6;
}

JNIEXPORT JNICALL
extern "C" jint
Java_net_sorenon_mcxr_play_MCXRNativeLoad_load(JNIEnv *env, jclass clazz, jlong ptr, jlong app) {
    auto xrInitializeLoaderKHR = reinterpret_cast<PFN_xrInitializeLoaderKHR>(ptr);
    if (xrInitializeLoaderKHR != nullptr) {
        XrLoaderInitInfoAndroidKHR loaderInitializeInfoAndroid;
        struct android_app *rApp;
        rApp = (struct android_app *) app;
        memset(&loaderInitializeInfoAndroid, 0, sizeof(loaderInitializeInfoAndroid));
        loaderInitializeInfoAndroid.type = XR_TYPE_LOADER_INIT_INFO_ANDROID_KHR;
        loaderInitializeInfoAndroid.next = NULL;
        loaderInitializeInfoAndroid.applicationVM = vm;
        loaderInitializeInfoAndroid.applicationContext = rApp->activity->clazz;
        return xrInitializeLoaderKHR(
                (const XrLoaderInitInfoBaseHeaderKHR *) &loaderInitializeInfoAndroid);
    }
    return XR_ERROR_INITIALIZATION_FAILED;
}

JNIEXPORT JNICALL
extern "C" jlong
Java_net_sorenon_mcxr_play_MCXRNativeLoad_castXrVoidFunctionType(JNIEnv *env, jclass clazz, jlong ptr) {
    return (jlong) ((PFN_xrVoidFunction *) &ptr);
}