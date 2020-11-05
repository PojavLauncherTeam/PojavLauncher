#include <jni.h>
#include "utils.h"

// jclass class_awt;
// jmethodID method_awt;

// TODO: check for memory leaks
JNIEXPORT void JNICALL Java_net_kdt_pojavlaunch_JREUtils_renderAWTScreenFrame(JNIEnv* env, jclass clazz, jobject canvas, jint width, jint height) {
    if (runtimeJNIEnvPtr_ANDROID == NULL) return;
    
    int *rgbArray;
    jintArray jreRgbArray, androidRgbArray;
    
    jclass class_awt = (*runtimeJNIEnvPtr_ANDROID)->FindClass(runtimeJNIEnvPtr_ANDROID, "net/java/openjdk/cacio/ctc/CTCScreen");
    jmethodID method_awt = (*runtimeJNIEnvPtr_ANDROID)->GetStaticMethodID(runtimeJNIEnvPtr_ANDROID, class_awt, "getCurrentScreenRGB", "()V");
    jreRgbArray = (jintArray) (*runtimeJNIEnvPtr_ANDROID)->CallStaticObjectMethod(
        runtimeJNIEnvPtr_ANDROID,
        class_awt,
        method_awt
    );
    
    // Copy JRE RGB array memory to Android.
    int arrayLength = (*runtimeJNIEnvPtr_ANDROID)->GetArrayLength(runtimeJNIEnvPtr_ANDROID, jreRgbArray);
    rgbArray = (*runtimeJNIEnvPtr_ANDROID)->GetIntArrayElements(runtimeJNIEnvPtr_ANDROID, jreRgbArray, 0);
    androidRgbArray = (*runtimeJNIEnvPtr_ANDROID)->NewIntArray(runtimeJNIEnvPtr_ANDROID, arrayLength);
    (*runtimeJNIEnvPtr_ANDROID)->SetIntArrayRegion(runtimeJNIEnvPtr_ANDROID, androidRgbArray, 0, arrayLength, rgbArray);
    
    // Maybe use Skia lib instead?
    jclass class_canvas = (*dalvikJNIEnvPtr_ANDROID)->GetObjectClass(dalvikJNIEnvPtr_ANDROID, canvas);
    jmethodID method_canvas = (*dalvikJNIEnvPtr_ANDROID)->GetMethodID(dalvikJNIEnvPtr_ANDROID, class_canvas, "drawBitmap", "([IIIFFIIZLandroid/graphics/Paint;)V");
    (*runtimeJNIEnvPtr_ANDROID)->CallVoidMethod(
        runtimeJNIEnvPtr_ANDROID,
        canvas,
        method_canvas,
        rgbArray, 0, width, 0, 0, width, height, JNI_TRUE, NULL
    );

    // android_graphics_Canvas_native_drawBitmap
    
    (*runtimeJNIEnvPtr_ANDROID)->ReleaseIntArrayElements(runtimeJNIEnvPtr_ANDROID, jreRgbArray, rgbArray, NULL);
    (*dalvikJNIEnvPtr_ANDROID)->DeleteLocalRef(dalvikJNIEnvPtr_ANDROID, androidRgbArray);
    // free(rgbArray);
}

