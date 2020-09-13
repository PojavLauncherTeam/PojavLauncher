#include <jni.h>

struct X11GraphicsConfigIDs x11GraphicsConfigIDs;
struct X11GraphicsDeviceIDs x11GraphicsDeviceIDs;

struct char* display = ":0";

JNIEXPORT void JNICALL Java_sun_awt_X11GraphicsConfig_initIDs (JNIEnv *env, jclass cls) {
    
}

JNIEXPORT void JNICALL Java_sun_awt_X11GraphicsDevice_initIDs (JNIEnv *env, jclass cls) {
    
}

JNIEXPORT jboolean JNICALL Java_sun_awt_X11GraphicsEnvironment_initGLX(JNIEnv *env, jclass cls) {
    // Return true if want try enable it
    return JNI_FALSE;
}

JNIEXPORT jboolean JNICALL Java_sun_awt_X11GraphicsEnvironment_initXRender(JNIEnv *env, jclass cls, jboolean verbose) {
    return JNI_FALSE;
}

JNIEXPORT jstring JNICALL Java_sun_awt_X11GraphicsEnvironment_getDisplayString(JNIEnv *env, jclass cls) {
    return (*env)->NewStringUTF(env, display);
}

JNIEXPORT jint JNICALL Java_sun_awt_X11GraphicsEnvironment_getNumScreens(JNIEnv *env, jobject this) {
    return (jint) 1;
}

/**
 * Checks if Shared Memory extension can be used.
 * Returns:
 *   -1 if server doesn't support MITShm
 *    1 if server supports it and it can be used
 *    0 otherwise
 */
JNIEXPORT jint JNICALL Java_sun_awt_X11GraphicsEnvironment_checkShmExt(JNIEnv *env, jclass cls, jboolean verbose) {
    // This could be return 0 once MITShm implementation added
    return (jint) -1;
}

JNIEXPORT jlong JNICALL Java_sun_awt_X11GraphicsDevice_getDisplay(JNIEnv *env, jobject this) {
    return (jlong) &display;
}

JNIEXPORT jint JNICALL Java_sun_awt_X11GraphicsDevice_getNumConfigs(JNIEnv *env, jobject this, jint screen) {
    return (jint) 0;
}

JNIEXPORT jint JNICALL Java_sun_awt_X11GraphicsDevice_getConfigVisualId(JNIEnv *env, jobject this, jint index, jint screen) {
    // Should be zero?
    return (jint)0;
/*
    int visNum;

    ensureConfigsInited(env, screen);
    if (index == 0) {
        return ((jint)x11Screens[screen].defaultConfig->awt_visInfo.visualid);
    } else {
        return ((jint)x11Screens[screen].configs[index]->awt_visInfo.visualid);
    }
*/
}

JNIEXPORT jint JNICALL Java_sun_awt_X11GraphicsDevice_getConfigDepth(JNIEnv *env, jobject this, jint index, jint screen) {
    return (jint) 24;
}

JNIEXPORT jint JNICALL Java_sun_awt_X11GraphicsDevice_getConfigColormap(JNIEnv *env, jobject this, jint index, jint screen) {
    // Should be zero?
    return (jint) 0;
/*
    int visNum;

    ensureConfigsInited(env, screen);
    if (index == 0) {
        return ((jint)x11Screens[screen].defaultConfig->awt_cmap);
    } else {
        return ((jint)x11Screens[screen].configs[index]->awt_cmap);
    }
*/
}

JNIEXPORT void JNICALL Java_sun_awt_X11GraphicsDevice_resetNativeData(JNIEnv *env, jclass x11gd, jint screen) {
    
}

JNIEXPORT void JNICALL Java_sun_awt_X11GraphicsConfig_dispose(JNIEnv *env, jclass x11gc, jlong configData) {
    /*
     * The native GLXGraphicsConfig data needs to be disposed separately
     * on the OGL queue flushing thread (should not be called while
     * the AWT lock is held).
     */
/*
    JNU_CallStaticMethodByName(env, NULL,
                               "sun/java2d/opengl/OGLRenderQueue",
                               "disposeGraphicsConfig", "(J)V",
                               ptr_to_jlong(aData->glxInfo));
*/
}

JNIEXPORT jdouble JNICALL Java_sun_awt_X11GraphicsConfig_getXResolution(JNIEnv *env, jobject this, jint screen) {
    /* FIXME implement this, maybe
     *  AndroidDpWidth * 25.4 / AndroidPxWidth
     */
    
    return (jdouble) 1d;
}

JNIEXPORT jdouble JNICALL Java_sun_awt_X11GraphicsConfig_getYResolution(JNIEnv *env, jobject this, jint screen) {
    /* FIXME implement this, maybe
     *  AndroidDpHeight * 25.4 / AndroidPxHeight
     */
    
    return (jdouble) 1d;
}

JNIEXPORT jint JNICALL Java_sun_awt_X11GraphicsConfig_getNumColors(JNIEnv *env, jobject this) {
    // FIXME stub num colors
    return (jint) 256;
}

JNIEXPORT void JNICALL Java_sun_awt_X11GraphicsConfig_init(JNIEnv *env, jobject this, jint visualNum, jint screen) {
    /*
    (*env)->SetIntField(env, this, x11GraphicsConfigIDs.bitsPerPixel,
                        (jint)tempImage->bits_per_pixel);
    */
}

JNIEXPORT jobject JNICALL Java_sun_awt_X11GraphicsConfig_makeColorModel(JNIEnv *env, jobject this) {
    return NULL;
}

