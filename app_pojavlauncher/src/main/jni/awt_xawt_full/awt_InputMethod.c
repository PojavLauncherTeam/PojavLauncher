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

/*
 * Class:     sun_awt_X11InputMethod
 * Method:    initIDs
 * Signature: ()V
 */

/* This function gets called from the static initializer for
   X11InputMethod.java
   to initialize the fieldIDs for fields that may be accessed from C */
JNIEXPORT void JNICALL
Java_sun_awt_X11InputMethod_initIDs(JNIEnv *env, jclass cls)
{
    // x11InputMethodIDs.pData = (*env)->GetFieldID(env, cls, "pData", "J");
}


JNIEXPORT jboolean JNICALL
Java_sun_awt_X11_XInputMethod_openXIMNative(JNIEnv *env,
                                          jobject this,
                                          jlong display)
{
/*
    Bool registered;

    AWT_LOCK();

    dpy = (Display *)jlong_to_ptr(display);

/* Use IMInstantiate call back only on Linux, as there is a bug in Solaris
   (4768335)
* /
#if defined(__linux__) || defined(MACOSX)
    registered = XRegisterIMInstantiateCallback(dpy, NULL, NULL,
                     NULL, (XIDProc)OpenXIMCallback, NULL);
    if (!registered) {
        /* directly call openXIM callback * /
#endif
        OpenXIMCallback(dpy, NULL, NULL);
#if defined(__linux__) || defined(MACOSX)
    }
#endif

    AWT_UNLOCK();
*/
    return JNI_TRUE;
}

JNIEXPORT jboolean JNICALL
Java_sun_awt_X11_XInputMethod_createXICNative(JNIEnv *env,
                                                  jobject this,
                                                  jlong window)
{
/*
    X11InputMethodData *pX11IMData;
    jobject globalRef;
    XIC ic;

    AWT_LOCK();

    if (!window) {
        JNU_ThrowNullPointerException(env, "NullPointerException");
        AWT_UNLOCK();
        return JNI_FALSE;
    }

    pX11IMData = (X11InputMethodData *) calloc(1, sizeof(X11InputMethodData));
    if (pX11IMData == NULL) {
        THROW_OUT_OF_MEMORY_ERROR();
        AWT_UNLOCK();
        return JNI_FALSE;
    }

    globalRef = (*env)->NewGlobalRef(env, this);
    pX11IMData->x11inputmethod = globalRef;
#if defined(__linux__) || defined(MACOSX)
    pX11IMData->statusWindow = NULL;
#endif /* __linux__ || MACOSX * /

    pX11IMData->lookup_buf = 0;
    pX11IMData->lookup_buf_len = 0;

    if (createXIC(env, pX11IMData, (Window)window) == False) {
        destroyX11InputMethodData((JNIEnv *) NULL, pX11IMData);
        pX11IMData = (X11InputMethodData *) NULL;
        if ((*env)->ExceptionCheck(env)) {
            goto finally;
        }
    }

    setX11InputMethodData(env, this, pX11IMData);

finally:
    AWT_UNLOCK();
    return (pX11IMData != NULL);
*/

    return JNI_FALSE;
}

JNIEXPORT void JNICALL
Java_sun_awt_X11_XInputMethod_setXICFocusNative(JNIEnv *env,
                                              jobject this,
                                              jlong w,
                                              jboolean req,
                                              jboolean active)
{
/*
    X11InputMethodData *pX11IMData;
    AWT_LOCK();
    pX11IMData = getX11InputMethodData(env, this);
    if (pX11IMData == NULL) {
        AWT_UNLOCK();
        return;
    }

    if (req) {
        if (!w) {
            AWT_UNLOCK();
            return;
        }
        pX11IMData->current_ic = active ?
                        pX11IMData->ic_active : pX11IMData->ic_passive;
        /*
         * On Solaris2.6, setXICWindowFocus() has to be invoked
         * before setting focus.
         * /
        setXICWindowFocus(pX11IMData->current_ic, w);
        setXICFocus(pX11IMData->current_ic, req);
        currentX11InputMethodInstance = pX11IMData->x11inputmethod;
        currentFocusWindow =  w;
#if defined(__linux__) || defined(MACOSX)
        if (active && pX11IMData->statusWindow && pX11IMData->statusWindow->on)
            onoffStatusWindow(pX11IMData, w, True);
#endif
    } else {
        currentX11InputMethodInstance = NULL;
        currentFocusWindow = 0;
#if defined(__linux__) || defined(MACOSX)
        onoffStatusWindow(pX11IMData, 0, False);
        if (pX11IMData->current_ic != NULL)
#endif
        setXICFocus(pX11IMData->current_ic, req);

        pX11IMData->current_ic = (XIC)0;
    }

    XFlush(dpy);
    AWT_UNLOCK();
*/
}

JNIEXPORT void JNICALL
Java_sun_awt_X11InputMethod_turnoffStatusWindow(JNIEnv *env,
                                                jobject this)
{
/*
#if defined(__linux__) || defined(MACOSX)
    X11InputMethodData *pX11IMData;
    StatusWindow *statusWindow;

    AWT_LOCK();

    if (NULL == currentX11InputMethodInstance
        || !isX11InputMethodGRefInList(currentX11InputMethodInstance)
        || NULL == (pX11IMData = getX11InputMethodData(env,currentX11InputMethodInstance))
        || NULL == (statusWindow = pX11IMData->statusWindow)
        || !statusWindow->on ){
        AWT_UNLOCK();
        return;
    }
    onoffStatusWindow(pX11IMData, 0, False);

    AWT_UNLOCK();
#endif
*/
}

JNIEXPORT void JNICALL
Java_sun_awt_X11InputMethod_disposeXIC(JNIEnv *env,
                                             jobject this)
{
/*
    X11InputMethodData *pX11IMData = NULL;

    AWT_LOCK();
    pX11IMData = getX11InputMethodData(env, this);
    if (pX11IMData == NULL) {
        AWT_UNLOCK();
        return;
    }

    setX11InputMethodData(env, this, NULL);

    if (pX11IMData->x11inputmethod == currentX11InputMethodInstance) {
        currentX11InputMethodInstance = NULL;
        currentFocusWindow = 0;
    }
    destroyX11InputMethodData(env, pX11IMData);
    AWT_UNLOCK();
*/
}

JNIEXPORT jstring JNICALL
Java_sun_awt_X11InputMethod_resetXIC(JNIEnv *env,
                                           jobject this)
{
    jstring jText = (jstring)0;
/*
    X11InputMethodData *pX11IMData;
    char *xText = NULL;

    AWT_LOCK();
    pX11IMData = getX11InputMethodData(env, this);
    if (pX11IMData == NULL) {
        AWT_UNLOCK();
        return jText;
    }

    if (pX11IMData->current_ic)
        xText = XmbResetIC(pX11IMData->current_ic);
    else {
        /*
         * If there is no reference to the current XIC, try to reset both XICs.
         * /
        xText = XmbResetIC(pX11IMData->ic_active);
        /*it may also means that the real client component does
          not have focus -- has been deactivated... its xic should
          not have the focus, bug#4284651 showes reset XIC for htt
          may bring the focus back, so de-focus it again.
        * /
        setXICFocus(pX11IMData->ic_active, FALSE);
        if (pX11IMData->ic_active != pX11IMData->ic_passive) {
            char *tmpText = XmbResetIC(pX11IMData->ic_passive);
            setXICFocus(pX11IMData->ic_passive, FALSE);
            if (xText == (char *)NULL && tmpText)
                xText = tmpText;
        }

    }
    if (xText != NULL) {
        jText = JNU_NewStringPlatform(env, (const char *)xText);
        XFree((void *)xText);
    }

    AWT_UNLOCK();
    return jText;
*/

    return jText;
}

/*
 * Class:     sun_awt_X11InputMethod
 * Method:    setCompositionEnabledNative
 * Signature: (ZJ)V
 *
 * This method tries to set the XNPreeditState attribute associated with the current
 * XIC to the passed in 'enable' state.
 *
 * Return JNI_TRUE if XNPreeditState attribute is successfully changed to the
 * 'enable' state; Otherwise, if XSetICValues fails to set this attribute,
 * java.lang.UnsupportedOperationException will be thrown. JNI_FALSE is returned if this
 * method fails due to other reasons.
 *
 */
JNIEXPORT jboolean JNICALL Java_sun_awt_X11InputMethod_setCompositionEnabledNative
  (JNIEnv *env, jobject this, jboolean enable)
{
/*
    X11InputMethodData *pX11IMData;
    char * ret = NULL;

    AWT_LOCK();
    pX11IMData = getX11InputMethodData(env, this);

    if ((pX11IMData == NULL) || (pX11IMData->current_ic == NULL)) {
        AWT_UNLOCK();
        return JNI_FALSE;
    }

    ret = XSetICValues(pX11IMData->current_ic, XNPreeditState,
                       (enable ? XIMPreeditEnable : XIMPreeditDisable), NULL);
    AWT_UNLOCK();

    if ((ret != 0) && (strcmp(ret, XNPreeditState) == 0)) {
        JNU_ThrowByName(env, "java/lang/UnsupportedOperationException", "");
    }

    return (jboolean)(ret == 0);
*/

    return JNI_FALSE;
}

/*
 * Class:     sun_awt_X11InputMethod
 * Method:    isCompositionEnabledNative
 * Signature: (J)Z
 *
 * This method tries to get the XNPreeditState attribute associated with the current XIC.
 *
 * Return JNI_TRUE if the XNPreeditState is successfully retrieved. Otherwise, if
 * XGetICValues fails to get this attribute, java.lang.UnsupportedOperationException
 * will be thrown. JNI_FALSE is returned if this method fails due to other reasons.
 *
 */
JNIEXPORT jboolean JNICALL Java_sun_awt_X11InputMethod_isCompositionEnabledNative
  (JNIEnv *env, jobject this)
{
/*
    X11InputMethodData *pX11IMData = NULL;
    char * ret = NULL;
    XIMPreeditState state;

    AWT_LOCK();
    pX11IMData = getX11InputMethodData(env, this);

    if ((pX11IMData == NULL) || (pX11IMData->current_ic == NULL)) {
        AWT_UNLOCK();
        return JNI_FALSE;
    }

    ret = XGetICValues(pX11IMData->current_ic, XNPreeditState, &state, NULL);
    AWT_UNLOCK();

    if ((ret != 0) && (strcmp(ret, XNPreeditState) == 0)) {
        JNU_ThrowByName(env, "java/lang/UnsupportedOperationException", "");
        return JNI_FALSE;
    }

    return (jboolean)(state == XIMPreeditEnable);
*/

    return JNI_FALSE;
}

JNIEXPORT void JNICALL Java_sun_awt_X11_XInputMethod_adjustStatusWindow
  (JNIEnv *env, jobject this, jlong window)
{
/*
#if defined(__linux__) || defined(MACOSX)
    AWT_LOCK();
    adjustStatusWindow(window);
    AWT_UNLOCK();
#endif
*/
}

