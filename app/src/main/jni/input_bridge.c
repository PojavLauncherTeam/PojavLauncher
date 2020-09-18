#include <jni.h>

#include "utils.h"

jclass inputBridgeClass_ANDROID;
jmethodID inputBridgeMethod_ANDROID;

jclass inputBridgeClass_JRE;
jmethodID inputBridgeMethod_JRE;

JavaVM* firstJavaVM;
JNIEnv* firstJNIEnv;

JavaVM* secondJavaVM;
JNIEnv* secondJNIEnv;

jint JNI_OnLoad(JavaVM* vm, void* reserved) {
    if (dalvikJavaVMPtr == NULL) {
        //Save dalvik global JavaVM pointer
        dalvikJavaVMPtr = vm;
        (*vm)->GetEnv(vm, (void**) &dalvikJNIEnvPtr_ANDROID, JNI_VERSION_1_4);
    } else if (dalvikJavaVMPtr != vm) {
        runtimeJavaVMPtr = vm;
        (*vm)->GetEnv(vm, (void**) &runtimeJNIEnvPtr_JRE, JNI_VERSION_1_4);
    }
    return JNI_VERSION_1_4;
}

void attachThreadIfNeed(bool* isAttached, JNIEnv** secondJNIEnvPtr) {
    if (!*isAttached && secondJavaVM) {
        (*secondJavaVM)->AttachCurrentThread(secondJavaVM, secondJNIEnvPtr, NULL);
        *isAttached = true;
    }
    secondJNIEnv = *secondJNIEnvPtr;
}

void getJavaInputBridge(jclass* clazz, jmethod* method) {
    if (*method == NULL) {
        *clazz = (*secondJNIEnv)->FindClass(secondJNIEnv, "org/lwjgl/glfw/CallbackBridge");
        *method = (*secondJNIEnv)->GetStaticMethodID(secondJNIEnv, *clazz, "receiveCallback", "(ILjava/lang/String;)V");
    }
}

JNIEXPORT void JNICALL Java_org_lwjgl_glfw_CallbackBridge_nativeSendData(JNIEnv* env, jclass clazz, jboolean isAndroid, jint type, jstring data) {
    if (isAndroid == JNI_TRUE) {
        firstJavaVM = dalvikJavaVMPtr;
        firstJNIEnv = dalvikJNIEnvPtr_ANDROID;
        secondJavaVM = runtimeJavaVMPtr;
        
        attachThreadIfNeed(&isAndroidThreadAttached, &runtimeJNIEnvPtr_ANDROID);
        getJavaInputBridge(&inputBridgeClass_ANDROID, &inputBridgeMethod_ANDROID);
    } else {
        firstJavaVM = runtimeJavaVMPtr;
        firstJNIEnv = runtimeJNIEnvPtr_JRE;
        secondJavaVM = dalvikJavaVMPtr;
        
        attachThreadIfNeed(&isRuntimeThreadAttached, &dalvikJNIEnvPtr_JRE);
        getJavaInputBridge(&inputBridgeClass_JRE, &inputBridgeMethod_JRE);
    }
    
    // printf("isAndroid=%p, isSecondJVMNull=%p\n", isAndroid, secondJavaVM == NULL);
    
    if (secondJavaVM != NULL) {
        char *data_c = (char*)(*env)->GetStringUTFChars(env, data, 0);
        // printf("data=%s\n", data_c);
        jstring data_jre = (*secondJNIEnv)->NewStringUTF(secondJNIEnv, data_c);
        (*env)->ReleaseStringUTFChars(env, data, data_c);
        (*secondJNIEnv)->CallStaticVoidMethod(
            secondJNIEnv,
            isAndroid == JNI_TRUE ? inputBridgeClass_ANDROID : inputBridgeClass_JRE,
            isAndroid == JNI_TRUE ? inputBridgeMethod_ANDROID : inputBridgeMethod_JRE,
            type,
            data_jre
        );
    }
    // else: too early!
}

