#include <jni.h>

#include "utils.h"

JNIEXPORT void JNICALL Java_net_kdt_pojavlaunch_LWJGLInputSender_sendDataToJRE(JNIEnv* env, jclass clazz, jint type, jstring data) {
	if (isEglMakeCurrentCalled == 1) {
        // Convert Dalvik string to JRE string: should or use directly?
        char *data_c = (char*)(*env)->GetStringUTFChars(env, data, 0);
        jstring data_jre = (*runtimeJNIEnvPtr)->NewStringUTF(runtimeJNIEnvPtr, data_c);
        (*env)->ReleaseStringUTFChars(env, data, data_c);
	
        jclass sendClass = (*runtimeJNIEnvPtr)->FindClass(runtimeJNIEnvPtr, "org/lwjgl/glfw/CallbackReceiver");
        jmethodID sendMethod = (*runtimeJNIEnvPtr)->GetStaticMethodID(runtimeJNIEnvPtr, sendClass, "receiveCallback", "(ILjava/lang/String;)V");
        (*runtimeJNIEnvPtr)->CallStaticVoidMethod(runtimeJNIEnvPtr, sendClass, sendMethod, type, data_jre);
	}
    // else: too early!
}

