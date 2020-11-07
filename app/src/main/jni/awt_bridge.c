#include <jni.h>
#include <assert.h>

#include "log.h"
#include "utils.h"

// jclass class_awt;
// jmethodID method_awt;

// TODO: check for memory leaks
// int printed = 0;
JNIEXPORT jintArray JNICALL Java_net_kdt_pojavlaunch_JREUtils_renderAWTScreenFrame(JNIEnv* env, jclass clazz /*, jobject canvas, jint width, jint height */) {
    if (runtimeJNIEnvPtr_ANDROID == NULL) {
        if (runtimeJavaVMPtr == NULL) {
            return NULL;
        } else {
            (*runtimeJavaVMPtr)->AttachCurrentThread(runtimeJavaVMPtr, &runtimeJNIEnvPtr_ANDROID, NULL);
        }
    }

    int *rgbArray;
    jintArray jreRgbArray, androidRgbArray;
  
    jclass class_awt = (*runtimeJNIEnvPtr_ANDROID)->FindClass(runtimeJNIEnvPtr_ANDROID, "net/java/openjdk/cacio/ctc/CTCScreen");
    assert(class_awt != NULL);
    jmethodID method_awt = (*runtimeJNIEnvPtr_ANDROID)->GetStaticMethodID(runtimeJNIEnvPtr_ANDROID, class_awt, "getCurrentScreenRGB", "()[I");
    assert(class_awt != NULL);
    jreRgbArray = (jintArray) (*runtimeJNIEnvPtr_ANDROID)->CallStaticObjectMethod(
        runtimeJNIEnvPtr_ANDROID,
        class_awt,
        method_awt
    );
    if (jreRgbArray == NULL) {
        return NULL;
    }
    
    // Copy JRE RGB array memory to Android.
    int arrayLength = (*runtimeJNIEnvPtr_ANDROID)->GetArrayLength(runtimeJNIEnvPtr_ANDROID, jreRgbArray);
    rgbArray = (*runtimeJNIEnvPtr_ANDROID)->GetIntArrayElements(runtimeJNIEnvPtr_ANDROID, jreRgbArray, 0);
    androidRgbArray = (*env)->NewIntArray(env, arrayLength);
    (*env)->SetIntArrayRegion(env, androidRgbArray, 0, arrayLength, rgbArray);

    (*runtimeJNIEnvPtr_ANDROID)->ReleaseIntArrayElements(runtimeJNIEnvPtr_ANDROID, jreRgbArray, rgbArray, NULL);
    // (*env)->DeleteLocalRef(env, androidRgbArray);
    // free(rgbArray);
    
    return androidRgbArray;
}

