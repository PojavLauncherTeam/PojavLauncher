#include <jni.h>

#include "utils.h"

jclass inputBridgeClass;
jmethodID inputBridgeMethod;

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

JNIEXPORT void JNICALL Java_org_lwjgl_glfw_CallbackBridge_nativeSendData(JNIEnv* env, jclass clazz, jboolean isAndroid, jint type, jstring data) {
    if (isAndroid == JNI_TRUE) {
        firstJavaVM = dalvikJavaVMPtr;
        firstJNIEnv = dalvikJNIEnvPtr_ANDROID;
        secondJavaVM = runtimeJavaVMPtr;
        
        attachThreadIfNeed(&isAndroidThreadAttached, &runtimeJNIEnvPtr_ANDROID);
    } else {
        firstJavaVM = runtimeJavaVMPtr;
        firstJNIEnv = runtimeJNIEnvPtr_JRE;
        secondJavaVM = dalvikJavaVMPtr;
        
        attachThreadIfNeed(&isRuntimeThreadAttached, &dalvikJNIEnvPtr_JRE);
    }
    
    // printf("isAndroid=%p, isSecondJVMNull=%p\n", isAndroid, secondJavaVM == NULL);
    
    if (secondJavaVM != NULL) {
        char *data_c = (char*)(*env)->GetStringUTFChars(env, data, 0);
        // printf("data=%s\n", data_c);
        jstring data_jre = (*secondJNIEnv)->NewStringUTF(secondJNIEnv, data_c);
        (*env)->ReleaseStringUTFChars(env, data, data_c);
    
        if (inputBridgeMethod == NULL) {
            inputBridgeClass = (*secondJNIEnv)->FindClass(secondJNIEnv, "org/lwjgl/glfw/CallbackBridge");
            inputBridgeMethod = (*secondJNIEnv)->GetStaticMethodID(secondJNIEnv, inputBridgeClass, "receiveCallback", "(ILjava/lang/String;)V");
        }
        (*secondJNIEnv)->CallStaticVoidMethod(secondJNIEnv, inputBridgeClass, inputBridgeMethod, type, data_jre);
    }
    // else: too early!
}

