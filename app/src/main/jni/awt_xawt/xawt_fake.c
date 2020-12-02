#include <jni.h>
#include <stdlib.h>

#include "awt_global.h"

jint JNI_OnLoad(JavaVM* vm, void* reserved) {
    GLOBAL_WIDTH = atoi(getenv("AWTSTUB_WIDTH"));
    GLOBAL_HEIGHT = atoi(getenv("AWTSTUB_HEIGHT"));
    
    return JNI_VERSION_1_4;
}

JNIEXPORT void JNICALL Java_java_awt_Font_initIDs(JNIEnv *env, jclass cls) {}
JNIEXPORT void JNICALL Java_sun_awt_X11_XWindow_initIDs(JNIEnv *env, jclass cls) {}
JNIEXPORT void JNICALL Java_sun_font_SunFontManager_initIDs(JNIEnv *env, jclass cls) {}

JNIEXPORT void JNICALL Java_sun_java2d_x11_X11SurfaceData_initIDs(JNIEnv *env, jclass cls) {}
JNIEXPORT void JNICALL Java_sun_java2d_x11_X11SurfaceData_initSurface(JNIEnv *env, jclass cls, jint depth, jint width, jint height, jlong drawable) {
    // Any ideas to implement?
}
JNIEXPORT jboolean JNICALL Java_sun_java2d_x11_X11SurfaceData_isDgaAvailable(JNIEnv *env, jclass cls) {
    return JNI_FALSE;
}
JNIEXPORT jboolean JNICALL Java_sun_java2d_x11_X11SurfaceData_isShmPMAvailable(JNIEnv *env, jclass cls) {
    return JNI_FALSE;
}
JNIEXPORT void JNICALL Java_sun_java2d_x11_X11SurfaceData_XSetCopyMode(JNIEnv *env, jclass cls, jlong xgc) {}
JNIEXPORT void JNICALL Java_sun_java2d_x11_X11SurfaceData_XSetXorMode(JNIEnv *env, jclass cls, jlong xgv) {}
JNIEXPORT void JNICALL Java_sun_java2d_x11_X11SurfaceData_XSetForeground(JNIEnv *env, jclass cls, jlong xgc, jint pixel) {}

