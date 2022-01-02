#include <stdlib.h>
#include <jni.h>
#include "openxr/openxr.h"
#include "openxr/openxr_platform.h"

//
// Created by Judge on 12/23/2021.
//

jobject* context;

        JNIEXPORT jlong JNICALL
Java_net_kdt_pojavlaunch_MCXRLoader_getContextPtr(JNIEnv *env, jclass clazz) {
    return (jlong) &context;
}

JNIEXPORT jlong JNICALL
Java_net_kdt_pojavlaunch_MCXRLoader_getJavaVMPtr(JNIEnv *env, jclass clazz) {
    JavaVM vm = (JavaVM) (*env)->GetJavaVM;
    return (jlong) &vm;
}

JNIEXPORT void JNICALL
Java_net_kdt_pojavlaunch_MCXRLoader_setContext(JNIEnv *env, jclass clazz, jobject ctx) {
    context = ctx;
}

JNIEXPORT jlong JNICALL
Java_net_sorenon_mcxr_play_MCXRNativeLoad_getBaseHeaderAddress(JNIEnv *env, jclass clazz, jobject loader) {
    XrLoaderInitInfoAndroidKHR* loaderInitializeInfoAndroid = loader;
    return (jlong) (XrLoaderInitInfoBaseHeaderKHR *) &loaderInitializeInfoAndroid;
}