#include <jni.h>
#include <stdlib.h>

//
// Created by Judge on 12/23/2021.
//

void setContextPtrVar(jobject ctx) {
    setenv("MCXR_APPLICATION_CONTEXT_PTR", (const char *) ctx, 1);
}

void setJavaVMPtrVar(jobject javaVM) {
    setenv("MCXR_JAVA_VM_PTR", (const char *) javaVM, 1);
}

JNIEXPORT void JNICALL
Java_net_sorenon_mcxr_play_MCXRLoader_setJavaVM(JNIEnv *env, jclass clazz) {
    setJavaVMPtrVar((*env)->GetJavaVM);
}

JNIEXPORT jlong JNICALL
Java_net_sorenon_mcxr_play_MCXRLoader_getContextPtr(JNIEnv *env, jclass clazz) {
    return (long) getenv("MCXR_APPLICATION_CONTEXT_PTR");
}

JNIEXPORT jlong JNICALL
Java_net_sorenon_mcxr_play_MCXRLoader_getJavaVMPtr(JNIEnv *env, jclass clazz) {
    return (long) getenv("MCXR_JAVA_VM_PTR");
}

JNIEXPORT void JNICALL
Java_net_sorenon_mcxr_play_MCXRLoader_setContext(JNIEnv *env, jclass clazz, jobject ctx) {
    setContextPtrVar(ctx);
}