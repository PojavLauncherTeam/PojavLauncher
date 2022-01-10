#include <jni.h>
#include <cstdlib>
#include <vector>
#include <android/asset_manager_jni.h>
#include <cstring>
#include <android/window.h>
#include <android/native_activity.h>
#include <mcxr_loader/android_native_app_glue.h>

#include <mcxr_loader/openxr_platform.h>
#include <mcxr_loader/openxr_platform_defines.h>
#include <mcxr_loader/openxr.h>

//
// Created by Judge on 12/23/2021.
//

jobject* context;
JavaVM* jvm;

JNIEXPORT JNICALL
extern "C" jlong
Java_net_kdt_pojavlaunch_MCXRLoader_getContextPtr(JNIEnv *env, jclass clazz) {
    return reinterpret_cast<jlong>(&context);
}

JNIEXPORT JNICALL
extern "C" void
Java_net_kdt_pojavlaunch_MCXRLoader_setContext(JNIEnv *env, jclass clazz, jobject ctx) {
    context = reinterpret_cast<jobject*>(env->NewGlobalRef(ctx));
}

jint JNI_OnLoad(JavaVM* vm, void* reserved) {
    if (jvm == nullptr) {
        jvm = reinterpret_cast<JavaVM*>(vm);
    }
    return JNI_VERSION_1_4;
}

JNIEXPORT JNICALL
extern "C" jlong
Java_net_sorenon_mcxr_play_MCXRNativeLoad_getJVMPtr(JNIEnv *env, jclass clazz) {
    return reinterpret_cast<jlong>(&jvm);
}