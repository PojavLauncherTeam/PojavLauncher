/*
 * Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

#include <jni.h>
#include <assert.h>

#include "awt_global.h"

/*
struct X11GraphicsConfigIDs x11GraphicsConfigIDs;
struct X11GraphicsDeviceIDs x11GraphicsDeviceIDs;
*/
char* display = ":0";

JNIEXPORT jint JNICALL
Java_sun_awt_X11GraphicsEnvironment_getDefaultScreenNum(
JNIEnv *env, jobject this)
{
    return (jint)0;
}

JNIEXPORT void JNICALL Java_sun_awt_X11GraphicsEnvironment_initDisplay(JNIEnv *env, jobject this, jboolean glxReq) {
    
}

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
    
    return (jdouble) 1;
}

JNIEXPORT jdouble JNICALL Java_sun_awt_X11GraphicsConfig_getYResolution(JNIEnv *env, jobject this, jint screen) {
    /* FIXME implement this, maybe
     *  AndroidDpHeight * 25.4 / AndroidPxHeight
     */
    
    return (jdouble) 1;
}

JNIEXPORT jint JNICALL Java_sun_awt_X11GraphicsConfig_getNumColors(JNIEnv *env, jobject this) {
    // FIXME stub num colors
    return (jint) 256;
}

JNIEXPORT void JNICALL Java_sun_awt_X11GraphicsConfig_init(JNIEnv *env, jobject this, jint visualNum, jint screen) {
    // Android: 16bits pixel
    jclass cls = (*env)->GetObjectClass(env, this);
    jfieldID bitsPerPixel = (*env)->GetFieldID(env, cls, "bitsPerPixel", "I");
    (*env)->SetIntField(env, this, bitsPerPixel,
                        (jint) 16 /* tempImage->bits_per_pixel */);
}

JNIEXPORT jobject JNICALL Java_sun_awt_X11GraphicsConfig_makeColorModel(JNIEnv *env, jobject this) {
    // TODO
    return NULL;
}

JNIEXPORT jobject JNICALL
Java_sun_awt_X11GraphicsConfig_pGetBounds(JNIEnv *env, jobject this, jint screen)
{
    jclass clazz;
    jmethodID mid;
    jobject bounds = NULL;
/*
    AwtGraphicsConfigDataPtr adata;

    adata = (AwtGraphicsConfigDataPtr)
        JNU_GetLongFieldAsPtr(env, this, x11GraphicsConfigIDs.aData);
*/
    clazz = (*env)->FindClass(env, "java/awt/Rectangle");
    // CHECK_NULL_RETURN(clazz, NULL);
    mid = (*env)->GetMethodID(env, clazz, "<init>", "(IIII)V");
    if (mid != NULL) {
        // TODO: change width height
        bounds = (*env)->NewObject(env, clazz, mid,
            0, // fbrects[screen].x,
            0, // fbrects[screen].y,
            GLOBAL_WIDTH, // fbrects[screen].width,
            GLOBAL_HEIGHT); // fbrects[screen].height);
    }
    return bounds;
}

JNIEXPORT jlong JNICALL
Java_sun_awt_X11GraphicsConfig_createBackBuffer
    (JNIEnv *env, jobject this, jlong window, jint swapAction)
{
/*
    int32_t v1, v2;
    XdbeBackBuffer ret = (unsigned long) 0;
    Window w = (Window)window;
    AWT_LOCK();
    if (!XdbeQueryExtension(awt_display, &v1, &v2)) {
        JNU_ThrowByName(env, "java/lang/Exception",
                        "Could not query double-buffer extension");
        AWT_UNLOCK();
        return (jlong)0;
    }
    ret = XdbeAllocateBackBufferName(awt_display, w,
                                     (XdbeSwapAction)swapAction);
    AWT_FLUSH_UNLOCK();
    return (jlong)ret;
*/

    return (jlong)0;
}

JNIEXPORT void JNICALL
Java_sun_awt_X11GraphicsConfig_destroyBackBuffer
    (JNIEnv *env, jobject this, jlong backBuffer)
{
/*
    AWT_LOCK();
    XdbeDeallocateBackBufferName(awt_display, (XdbeBackBuffer)backBuffer);
    AWT_FLUSH_UNLOCK();
*/
}

JNIEXPORT void JNICALL
Java_sun_awt_X11GraphicsConfig_swapBuffers
    (JNIEnv *env, jobject this,
     jlong window, jint swapAction)
{
    /*
     * TODO: implement as
     * - GL.swapBuffers() if use OpenGL render
     * - Canvas.unlockAndPost() if use Android Canvas
     */
}

JNIEXPORT jboolean JNICALL
Java_sun_awt_X11GraphicsConfig_isTranslucencyCapable
    (JNIEnv *env, jobject this, jlong configData)
{
    return JNI_TRUE;
}

JNIEXPORT jboolean JNICALL
Java_sun_awt_X11GraphicsDevice_isDBESupported(JNIEnv *env, jobject this)
{
    // FIXME should be false?
    return JNI_FALSE;
    
/*
#ifdef HEADLESS
    return JNI_FALSE;
#else
    int opcode = 0, firstEvent = 0, firstError = 0;
    jboolean ret;

    AWT_LOCK();
    ret = (jboolean)XQueryExtension(awt_display, "DOUBLE-BUFFER",
                                    &opcode, &firstEvent, &firstError);
    AWT_FLUSH_UNLOCK();
    return ret;
#endif // !HEADLESS
*/
}

JNIEXPORT void JNICALL
Java_sun_awt_X11GraphicsDevice_getDoubleBufferVisuals(JNIEnv *env,
    jobject this, jint screen)
{
    
}

JNIEXPORT jboolean JNICALL
Java_sun_awt_X11GraphicsEnvironment_pRunningXinerama(JNIEnv *env,
    jobject this)
{
    return JNI_FALSE;
}

JNIEXPORT jobject JNICALL
Java_sun_awt_X11GraphicsEnvironment_getXineramaCenterPoint(JNIEnv *env,
    jobject this)
{
    return NULL;
}

JNIEXPORT jboolean JNICALL Java_sun_awt_X11GraphicsDevice_initXrandrExtension(JNIEnv *env, jclass cls) {
    return JNI_FALSE;
}

static jobject
X11GD_CreateDisplayMode(JNIEnv *env, jint width, jint height,
                        jint bitDepth, jint refreshRate)
{
    jclass displayModeClass;
    jmethodID cid;
    jint validRefreshRate = refreshRate;

    displayModeClass = (*env)->FindClass(env, "java/awt/DisplayMode");
    assert(displayModeClass != NULL);

    cid = (*env)->GetMethodID(env, displayModeClass, "<init>", "(IIII)V");
    assert(cid != NULL);
/*
    CHECK_NULL_RETURN(cid, NULL);
    if (cid == NULL) {
        JNU_ThrowInternalError(env,
                               "Could not get display mode constructor");
        return NULL;
    }
*/

    // early versions of xrandr may report "empty" rates (6880694)
    if (validRefreshRate <= 0) {
        validRefreshRate = 60; // REFRESH_RATE_UNKNOWN;
    }

    return (*env)->NewObject(env, displayModeClass, cid,
                             width, height, bitDepth, validRefreshRate);
}

JNIEXPORT jobject JNICALL
Java_sun_awt_X11GraphicsDevice_getCurrentDisplayMode
    (JNIEnv* env, jclass x11gd, jint screen)
{
    // TODO change width height
    return X11GD_CreateDisplayMode(env,
        GLOBAL_WIDTH, // curSize.width,
        GLOBAL_HEIGHT, // curSize.height,
        -1, // BIT_DEPTH_MULTI, // FIXME should be -1?
        60 /* refresh rate */);
}

JNIEXPORT void JNICALL
Java_sun_awt_X11GraphicsDevice_enumDisplayModes
    (JNIEnv* env, jclass x11gd,
     jint screen, jobject arrayList)
{
    // TODO implement
}

JNIEXPORT void JNICALL Java_sun_awt_X11GraphicsDevice_enterFullScreenExclusive(JNIEnv *env, jclass x11gd, jint screen) {
    
}

JNIEXPORT void JNICALL Java_sun_awt_X11GraphicsDevice_exitFullScreenExclusive(JNIEnv *env, jclass x11gd, jint screen) {
    
}

JNIEXPORT jdouble JNICALL Java_sun_awt_X11GraphicsDevice_getNativeScaleFactor(JNIEnv *env, jclass x11gd, jint screen) {
    return (jdouble) 1;
}

