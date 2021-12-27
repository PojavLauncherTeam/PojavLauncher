#include <stdlib.h>
#include <jni.h>

//
// Created by Judge on 12/23/2021.
//

jobject* context;
JavaVM* vm;

        JNIEXPORT jlong JNICALL
Java_net_kdt_pojavlaunch_MCXRLoader_getContextPtr(JNIEnv *env, jclass clazz) {
    return (jlong) &context;
}

JNIEXPORT jlong JNICALL
Java_net_kdt_pojavlaunch_MCXRLoader_getJavaVMPtr(JNIEnv *env, jclass clazz) {
    return (jlong) &vm;
}

jint JNI_OnLoad(JavaVM *jvm, void *unused) {
    vm = jvm;
    return JNI_VERSION_1_6;
}

JNIEXPORT void JNICALL
Java_net_kdt_pojavlaunch_MCXRLoader_setContext(JNIEnv *env, jclass clazz, jobject ctx) {
    context = ctx;
}