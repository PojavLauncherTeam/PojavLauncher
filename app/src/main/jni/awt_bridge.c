#include <jni.h>
#include <assert.h>

#include "log.h"
#include "utils.h"

// jclass class_awt;
// jmethodID method_awt;

// TODO: check for memory leaks
// int printed = 0;
JNIEXPORT jboolean JNICALL Java_net_kdt_pojavlaunch_JREUtils_renderAWTScreenFrame(JNIEnv* env, jclass clazz, jobject canvas, jint width, jint height) {
    if (runtimeJNIEnvPtr_ANDROID == NULL) {
        if (runtimeJavaVMPtr == NULL) {
            return JNI_FALSE;
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
    if (jreRgbArray  == NULL) {
        return;
    }
    
    // Copy JRE RGB array memory to Android.
    int arrayLength = (*runtimeJNIEnvPtr_ANDROID)->GetArrayLength(runtimeJNIEnvPtr_ANDROID, jreRgbArray);
    rgbArray = (*runtimeJNIEnvPtr_ANDROID)->GetIntArrayElements(runtimeJNIEnvPtr_ANDROID, jreRgbArray, 0);
    androidRgbArray = (*dalvikJNIEnvPtr_ANDROID)->NewIntArray(dalvikJNIEnvPtr_ANDROID, arrayLength);
    (*dalvikJNIEnvPtr_ANDROID)->SetIntArrayRegion(dalvikJNIEnvPtr_ANDROID, androidRgbArray, 0, arrayLength, rgbArray);
/*
    if (printed++ < 200) {
        LOGD("[AWT] First 3 pixels: | %i | %i | %i |", rgbArray[0], rgbArray[1], rgbArray[2]);
    }
*/
    // Maybe use Skia lib instead?
    jclass class_canvas = (*dalvikJNIEnvPtr_ANDROID)->GetObjectClass(dalvikJNIEnvPtr_ANDROID, canvas);
    assert(class_canvas != NULL);
    jmethodID method_canvas = (*dalvikJNIEnvPtr_ANDROID)->GetMethodID(dalvikJNIEnvPtr_ANDROID, class_canvas, "drawBitmap", "([IIIFFIIZLandroid/graphics/Paint;)V");
    assert(method_canvas != NULL);
    (*dalvikJNIEnvPtr_ANDROID)->CallVoidMethod(
        dalvikJNIEnvPtr_ANDROID,
        canvas,
        method_canvas,
        rgbArray, 0, width, 0, 0, width, height, JNI_TRUE, NULL
    );

    // android_graphics_Canvas_native_drawBitmap
    LOGD("step4");
    (*runtimeJNIEnvPtr_ANDROID)->ReleaseIntArrayElements(runtimeJNIEnvPtr_ANDROID, jreRgbArray, rgbArray, NULL);
    (*dalvikJNIEnvPtr_ANDROID)->DeleteLocalRef(dalvikJNIEnvPtr_ANDROID, androidRgbArray);
    // free(rgbArray);
    
    return JNI_TRUE;
}

