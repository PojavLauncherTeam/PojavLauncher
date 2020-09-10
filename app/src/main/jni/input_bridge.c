#include <jni.h>

#include "utils.h"

jclass inputBridgeClass;
jmethodID inputBridgeMethod;

JNIEXPORT void JNICALL Java_net_kdt_pojavlaunch_LWJGLInputSender_sendDataToJRE(JNIEnv* env, jclass clazz, jint type, jstring data) {
	if (runtimeJavaVMPtr != NULL) {
        if (!isAndroidThreadAttached) {
            // Allow invoke JRE reflection from Android side
            (*runtimeJavaVMPtr)->AttachCurrentThread(runtimeJavaVMPtr, &runtimeJNIEnvPtr, NULL);
            isAndroidThreadAttached = true;
        }
        
        // Convert Dalvik string to JRE string: should or use directly?
        char *data_c = (char*)(*env)->GetStringUTFChars(env, data, 0);
        jstring data_jre = (*runtimeJNIEnvPtr)->NewStringUTF(runtimeJNIEnvPtr, data_c);
        (*env)->ReleaseStringUTFChars(env, data, data_c);
	
        if (inputBridgeMethod == NULL) {
            inputBridgeClass = (*runtimeJNIEnvPtr)->FindClass(runtimeJNIEnvPtr, "org/lwjgl/glfw/CallbackReceiver");
            inputBridgeMethod = (*runtimeJNIEnvPtr)->GetStaticMethodID(runtimeJNIEnvPtr, inputBridgeClass, "receiveCallback", "(ILjava/lang/String;)V");
        }
        (*runtimeJNIEnvPtr)->CallStaticVoidMethod(runtimeJNIEnvPtr, inputBridgeClass, inputBridgeMethod, type, data_jre);
	}
    // else: too early!
}

