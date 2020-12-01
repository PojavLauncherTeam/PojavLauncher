/*
 * Copyright (c) 2002, 2020, Oracle and/or its affiliates. All rights reserved.
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

#include <stdlib.h>
#include <unistd.h>

// extern JavaVM *jvm;

static int32_t num_buttons = 0;

JNIEXPORT void JNICALL
Java_sun_awt_X11_XFontPeer_initIDs
  (JNIEnv *env, jclass cls)
{
    
}

/* This function gets called from the static initializer for FileDialog.java
   to initialize the fieldIDs for fields that may be accessed from C */

JNIEXPORT void JNICALL
Java_java_awt_FileDialog_initIDs
  (JNIEnv *env, jclass cls)
{

}

JNIEXPORT void JNICALL
Java_sun_awt_X11_XToolkit_initIDs
  (JNIEnv *env, jclass clazz)
{
/*
    jfieldID fid = (*env)->GetStaticFieldID(env, clazz, "numLockMask", "I");
    CHECK_NULL(fid);
    awt_NumLockMask = (*env)->GetStaticIntField(env, clazz, fid);
    DTRACE_PRINTLN1("awt_NumLockMask = %u", awt_NumLockMask);
    fid = (*env)->GetStaticFieldID(env, clazz, "modLockIsShiftLock", "I");
    CHECK_NULL(fid);
    awt_ModLockIsShiftLock = (*env)->GetStaticIntField(env, clazz, fid) != 0 ? True : False;
*/
}

/*
 * Class:     sun_awt_X11_XToolkit
 * Method:    getTrayIconDisplayTimeout
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_sun_awt_X11_XToolkit_getTrayIconDisplayTimeout
  (JNIEnv *env, jclass clazz)
{
#ifndef JAVASE_EMBEDDED
    return (jlong) 2000;
#else
    return (jlong) 10000;
#endif
}

/*
 * Class:     sun_awt_X11_XToolkit
 * Method:    getDefaultXColormap
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_sun_awt_X11_XToolkit_getDefaultXColormap
  (JNIEnv *env, jclass clazz)
{
/*
    AwtGraphicsConfigDataPtr defaultConfig =
        getDefaultConfig(DefaultScreen(awt_display));

    return (jlong) defaultConfig->awt_cmap;
*/

    return (jlong) 0; 
}

/*
 * Class:     sun_awt_X11_XToolkit
 * Method:    nativeLoadSystemColors
 * Signature: ([I)V
 */
JNIEXPORT void JNICALL Java_sun_awt_X11_XToolkit_nativeLoadSystemColors
  (JNIEnv *env, jobject this, jintArray systemColors)
{
/*
    AwtGraphicsConfigDataPtr defaultConfig =
        getDefaultConfig(DefaultScreen(awt_display));
    awtJNI_CreateColorData(env, defaultConfig, 1);
*/
}

JNIEXPORT void JNICALL
Java_java_awt_Component_initIDs
  (JNIEnv *env, jclass cls)
{
/*
    jclass keyclass = NULL;


    componentIDs.x = (*env)->GetFieldID(env, cls, "x", "I");
    CHECK_NULL(componentIDs.x);
    componentIDs.y = (*env)->GetFieldID(env, cls, "y", "I");
    CHECK_NULL(componentIDs.y);
    componentIDs.width = (*env)->GetFieldID(env, cls, "width", "I");
    CHECK_NULL(componentIDs.width);
    componentIDs.height = (*env)->GetFieldID(env, cls, "height", "I");
    CHECK_NULL(componentIDs.height);
    componentIDs.isPacked = (*env)->GetFieldID(env, cls, "isPacked", "Z");
    CHECK_NULL(componentIDs.isPacked);
    componentIDs.peer =
      (*env)->GetFieldID(env, cls, "peer", "Ljava/awt/peer/ComponentPeer;");
    CHECK_NULL(componentIDs.peer);
    componentIDs.background =
      (*env)->GetFieldID(env, cls, "background", "Ljava/awt/Color;");
    CHECK_NULL(componentIDs.background);
    componentIDs.foreground =
      (*env)->GetFieldID(env, cls, "foreground", "Ljava/awt/Color;");
    CHECK_NULL(componentIDs.foreground);
    componentIDs.graphicsConfig =
        (*env)->GetFieldID(env, cls, "graphicsConfig",
                           "Ljava/awt/GraphicsConfiguration;");
    CHECK_NULL(componentIDs.graphicsConfig);
    componentIDs.name =
      (*env)->GetFieldID(env, cls, "name", "Ljava/lang/String;");
    CHECK_NULL(componentIDs.name);

    // Use _NoClientCode() methods for trusted methods, so that we
    //  know that we are not invoking client code on trusted threads
     
    componentIDs.getParent =
      (*env)->GetMethodID(env, cls, "getParent_NoClientCode",
                         "()Ljava/awt/Container;");
    CHECK_NULL(componentIDs.getParent);

    componentIDs.getLocationOnScreen =
      (*env)->GetMethodID(env, cls, "getLocationOnScreen_NoTreeLock",
                         "()Ljava/awt/Point;");
    CHECK_NULL(componentIDs.getLocationOnScreen);

    keyclass = (*env)->FindClass(env, "java/awt/event/KeyEvent");
    CHECK_NULL(keyclass);

    componentIDs.isProxyActive =
        (*env)->GetFieldID(env, keyclass, "isProxyActive",
                           "Z");
    CHECK_NULL(componentIDs.isProxyActive);

    componentIDs.appContext =
        (*env)->GetFieldID(env, cls, "appContext",
                           "Lsun/awt/AppContext;");

    (*env)->DeleteLocalRef(env, keyclass);
*/
}


JNIEXPORT void JNICALL
Java_java_awt_Container_initIDs
  (JNIEnv *env, jclass cls)
{

}


JNIEXPORT void JNICALL
Java_java_awt_Button_initIDs
  (JNIEnv *env, jclass cls)
{

}

JNIEXPORT void JNICALL
Java_java_awt_Scrollbar_initIDs
  (JNIEnv *env, jclass cls)
{

}


JNIEXPORT void JNICALL
Java_java_awt_Window_initIDs
  (JNIEnv *env, jclass cls)
{

}

JNIEXPORT void JNICALL
Java_java_awt_Frame_initIDs
  (JNIEnv *env, jclass cls)
{

}


JNIEXPORT void JNICALL
Java_java_awt_MenuComponent_initIDs(JNIEnv *env, jclass cls)
{
/*
    menuComponentIDs.appContext =
      (*env)->GetFieldID(env, cls, "appContext", "Lsun/awt/AppContext;");
*/
}

JNIEXPORT void JNICALL
Java_java_awt_Cursor_initIDs(JNIEnv *env, jclass cls)
{
}


JNIEXPORT void JNICALL Java_java_awt_MenuItem_initIDs
  (JNIEnv *env, jclass cls)
{
}


JNIEXPORT void JNICALL Java_java_awt_Menu_initIDs
  (JNIEnv *env, jclass cls)
{
}

JNIEXPORT void JNICALL
Java_java_awt_TextArea_initIDs
  (JNIEnv *env, jclass cls)
{
}


JNIEXPORT void JNICALL
Java_java_awt_Checkbox_initIDs
  (JNIEnv *env, jclass cls)
{
}


JNIEXPORT void JNICALL Java_java_awt_ScrollPane_initIDs
  (JNIEnv *env, jclass cls)
{
}

JNIEXPORT void JNICALL
Java_java_awt_TextField_initIDs
  (JNIEnv *env, jclass cls)
{
}

JNIEXPORT jboolean JNICALL AWTIsHeadless() {
#ifdef HEADLESS
    return JNI_TRUE;
#else
    return JNI_FALSE;
#endif
}

JNIEXPORT void JNICALL Java_java_awt_Dialog_initIDs (JNIEnv *env, jclass cls)
{
}


/* ========================== Begin poll section ================================ */

JNIEXPORT void JNICALL Java_sun_awt_X11_XToolkit_waitForEvents (JNIEnv *env, jclass class, jlong nextTaskTime) {
    // waitForEvents(env, nextTaskTime);
}

JNIEXPORT void JNICALL Java_sun_awt_X11_XToolkit_awt_1toolkit_1init (JNIEnv *env, jclass class) {
    // awt_MainThread = pthread_self();

    // awt_pipe_init();
    // readEnv();
}

JNIEXPORT void JNICALL Java_sun_awt_X11_XToolkit_awt_1output_1flush (JNIEnv *env, jclass class) {
    // awt_output_flush();
}

JNIEXPORT void JNICALL Java_sun_awt_X11_XToolkit_wakeup_1poll (JNIEnv *env, jclass class) {
    // wakeUp();
}

/* ========================== End poll section ================================= */

/*
 * Class:     java_awt_KeyboardFocusManager
 * Method:    initIDs
 * Signature: ()V
 */
JNIEXPORT void JNICALL
Java_java_awt_KeyboardFocusManager_initIDs
    (JNIEnv *env, jclass cls)
{
}

/*
 * Class:     sun_awt_X11_XToolkit
 * Method:    getEnv
 * Signature: (Ljava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_sun_awt_X11_XToolkit_getEnv
(JNIEnv *env , jclass clazz, jstring key) {
    char *ptr = NULL;
    const char *keystr = NULL;
    jstring ret = NULL;

    keystr = (*env)->GetStringUTFChars(env, key, NULL);
    if (keystr) {
        ptr = getenv(keystr);
        if (ptr) {
            ret = (*env)->NewStringUTF(env, (const char *) ptr);
        }
        (*env)->ReleaseStringUTFChars(env, key, (const char*)keystr);
    }
    return ret;
}

/*
 * Old, compatibility, backdoor for DT.  This is a different
 * implementation.  It keeps the signature, but acts on
 * awt_root_shell, not the frame passed as an argument.  Note, that
 * the code that uses the old backdoor doesn't work correctly with
 * gnome session proxy that checks for WM_COMMAND when the window is
 * firts mapped, because DT code calls this old backdoor *after* the
 * frame is shown or it would get NPE with old AWT (previous
 * implementation of this backdoor) otherwise.  Old style session
 * managers (e.g. CDE) that check WM_COMMAND only during session
 * checkpoint should work fine, though.
 *
 * NB: The function name looks deceptively like a JNI native method
 * name.  It's not!  It's just a plain function.
 */

JNIEXPORT void JNICALL
Java_sun_awt_motif_XsessionWMcommand(JNIEnv *env, jobject this,
    jobject frame, jstring jcommand)
{
/*
    const char *command;
    XTextProperty text_prop;
    char *c[1];
    int32_t status;
    Window xawt_root_window;

    AWT_LOCK();
    xawt_root_window = get_xawt_root_shell(env);

    if ( xawt_root_window == None ) {
        AWT_UNLOCK();
        JNU_ThrowNullPointerException(env, "AWT root shell is unrealized");
        return;
    }

    command = (char *) JNU_GetStringPlatformChars(env, jcommand, NULL);
    if (command != NULL) {
        c[0] = (char *)command;
        status = XmbTextListToTextProperty(awt_display, c, 1,
                                           XStdICCTextStyle, &text_prop);

        if (status == Success || status > 0) {
            XSetTextProperty(awt_display, xawt_root_window,
                             &text_prop, XA_WM_COMMAND);
            if (text_prop.value != NULL)
                XFree(text_prop.value);
        }
        JNU_ReleaseStringPlatformChars(env, jcommand, command);
    }
    AWT_UNLOCK();
*/
}


/*
 * New DT backdoor to set WM_COMMAND.  New code should use this
 * backdoor and call it *before* the first frame is shown so that
 * gnome session proxy can correctly handle it.
 *
 * NB: The function name looks deceptively like a JNI native method
 * name.  It's not!  It's just a plain function.
 */
JNIEXPORT void JNICALL
Java_sun_awt_motif_XsessionWMcommand_New(JNIEnv *env, jobjectArray jarray)
{
/*
    jsize length;
    char ** array;
    XTextProperty text_prop;
    int status;
    Window xawt_root_window;

    AWT_LOCK();
    xawt_root_window = get_xawt_root_shell(env);

    if (xawt_root_window == None) {
      AWT_UNLOCK();
      JNU_ThrowNullPointerException(env, "AWT root shell is unrealized");
      return;
    }

    array = stringArrayToNative(env, jarray, &length);

    if (array != NULL) {
        status = XmbTextListToTextProperty(awt_display, array, length,
                                           XStdICCTextStyle, &text_prop);
        if (status < 0) {
            switch (status) {
            case XNoMemory:
                JNU_ThrowOutOfMemoryError(env,
                    "XmbTextListToTextProperty: XNoMemory");
                break;
            case XLocaleNotSupported:
                JNU_ThrowInternalError(env,
                    "XmbTextListToTextProperty: XLocaleNotSupported");
                break;
            case XConverterNotFound:
                JNU_ThrowNullPointerException(env,
                    "XmbTextListToTextProperty: XConverterNotFound");
                break;
            default:
                JNU_ThrowInternalError(env,
                    "XmbTextListToTextProperty: unknown error");
            }
        } else {
            XSetTextProperty(awt_display, xawt_root_window,
                                 &text_prop, XA_WM_COMMAND);
        }

        if (text_prop.value != NULL)
            XFree(text_prop.value);

        freeNativeStringArray(array, length);
    }
    AWT_UNLOCK();
*/
}

/*
 * Class:     java_awt_TrayIcon
 * Method:    initIDs
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_java_awt_TrayIcon_initIDs(JNIEnv *env , jclass clazz)
{
}


/*
 * Class:     java_awt_Cursor
 * Method:    finalizeImpl
 * Signature: ()V
 */
JNIEXPORT void JNICALL
Java_java_awt_Cursor_finalizeImpl(JNIEnv *env, jclass clazz, jlong pData)
{
/*
    Cursor xcursor;

    xcursor = (Cursor)pData;
    if (xcursor != None) {
        AWT_LOCK();
        XFreeCursor(awt_display, xcursor);
        AWT_UNLOCK();
    }
*/
}


/*
 * Class:     sun_awt_X11_XToolkit
 * Method:    getNumberOfButtonsImpl
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_sun_awt_X11_XToolkit_getNumberOfButtonsImpl
(JNIEnv * env, jobject cls){
    if (num_buttons == 0) {
        num_buttons = 3;
        // getNumButtons()
    }
    return num_buttons;
}

/*
 * Class:     sun_awt_X11_XWindowPeer
 * Method:    getJvmPID
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_sun_awt_X11_XWindowPeer_getJvmPID
(JNIEnv *env, jclass cls)
{
    /* Return the JVM's PID. */
    return getpid();
}

#ifndef HOST_NAME_MAX
#define HOST_NAME_MAX 1024 /* Overestimated */
#endif

/*
 * Class:     sun_awt_X11_XWindowPeer
 * Method:    getLocalHostname
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_sun_awt_X11_XWindowPeer_getLocalHostname
(JNIEnv *env, jclass cls)
{
    /* Return the machine's FQDN. */
/*
    char hostname[HOST_NAME_MAX + 1];
    if (gethostname(hostname, HOST_NAME_MAX + 1) == 0) {
        hostname[HOST_NAME_MAX] = '\0';
        jstring res = (*env)->NewStringUTF(env, hostname);
        return res;
    }

    return (jstring)NULL;
*/

    jstring res = (*env)->NewStringUTF(env, "localhost");
    return res;
}
