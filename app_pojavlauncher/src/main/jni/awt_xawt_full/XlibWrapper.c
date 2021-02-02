/*
 * Copyright (c) 2002, 2014, Oracle and/or its affiliates. All rights reserved.
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
/*
#include "sun_awt_X11_XlibWrapper.h"
#include <X11/Xlib.h>
#include <X11/keysym.h>
#include <X11/Xutil.h>
#include <X11/Xos.h>
#include <X11/Xatom.h>
#include <X11/extensions/Xdbe.h>
#include <X11/extensions/shape.h>
*/
#include <string.h>
#include <stdlib.h>
/*
#include <X11/Sunkeysym.h>
*/

#include <jni.h>
/*
#include <jni_util.h>
#include <jlong.h>
#include <sizecalc.h>

#include <awt.h>
#include <awt_util.h>
#include <jvm.h>

#include <Region.h>
#include "utility/rect.h"

#include <X11/XKBlib.h>
*/

#include "awt_global.h"
/*

#if defined(DEBUG) || defined(INTERNAL_BUILD)
static jmethodID lockIsHeldMID = NULL;

static void
CheckHaveAWTLock(JNIEnv *env)
{
    if (lockIsHeldMID == NULL) {
        if (tkClass == NULL) return;
        lockIsHeldMID =
            (*env)->GetStaticMethodID(env, tkClass,
                                      "isAWTLockHeldByCurrentThread", "()Z");
        if (lockIsHeldMID == NULL) return;
    }
    if (!(*env)->CallStaticBooleanMethod(env, tkClass, lockIsHeldMID)) {
        JNU_ThrowInternalError(env, "Current thread does not hold AWT_LOCK!");
    }
}

#define AWT_CHECK_HAVE_LOCK()                       \
    do {                                            \
        CheckHaveAWTLock(env);                      \
        if ((*env)->ExceptionCheck(env)) {          \
            return;                                 \
        }                                           \
    } while (0);                                    \

#define AWT_CHECK_HAVE_LOCK_RETURN(ret)             \
    do {                                            \
        CheckHaveAWTLock(env);                      \
        if ((*env)->ExceptionCheck(env)) {          \
            return (ret);                           \
        }                                           \
    } while (0);                                    \

#else
#define AWT_CHECK_HAVE_LOCK()
#define AWT_CHECK_HAVE_LOCK_RETURN(ret)
#endif

void freeNativeStringArray(char **array, jsize length) {
    int i;
    if (array == NULL) {
        return;
    }
    for (i = 0; i < length; i++) {
        free(array[i]);
    }
    free(array);
}

char** stringArrayToNative(JNIEnv *env, jobjectArray array, jsize * ret_length) {
    Bool err = FALSE;
    char ** strings;
    int index, str_index = 0;
    jsize length = (*env)->GetArrayLength(env, array);

    if (length == 0) {
        return NULL;
    }

    strings = (char**) calloc(length, sizeof (char*));

    if (strings == NULL) {
        JNU_ThrowOutOfMemoryError(env, "");
        return NULL;
    }

    for (index = 0; index < length; index++) {
        jstring str = (*env)->GetObjectArrayElement(env, array, index);
        if (str != NULL) {
            const char * str_char = JNU_GetStringPlatformChars(env, str, NULL);
            if (str_char != NULL) {
                char * dup_str = strdup(str_char);
                if (dup_str != NULL) {
                    strings[str_index++] = dup_str;
                } else {
                    JNU_ThrowOutOfMemoryError(env, "");
                    err = TRUE;
                }
                JNU_ReleaseStringPlatformChars(env, str, str_char);
            } else {
                err = TRUE;
            }
            (*env)->DeleteLocalRef(env, str);
            if (err) {
                break;
            }
        }
    }

    if (err) {
        freeNativeStringArray(strings, str_index);
        strings = NULL;
        str_index = -1;
    }
    *ret_length = str_index;

    return strings;
}
*/

/*
 * Class:     XlibWrapper
 * Method:    XOpenDisplay
 * Signature: (J)J
 */

JNIEXPORT jlong JNICALL Java_sun_awt_X11_XlibWrapper_XOpenDisplay
(JNIEnv *env, jclass clazz, jlong display_name)
{
    return (jlong) 1;
}

JNIEXPORT void JNICALL
Java_sun_awt_X11_XlibWrapper_XCloseDisplay(JNIEnv *env, jclass clazz,
                       jlong display) {
    
}

JNIEXPORT jlong JNICALL
Java_sun_awt_X11_XlibWrapper_XDisplayString(JNIEnv *env, jclass clazz,
                        jlong display) {
    return (jlong) 1;
}

JNIEXPORT void JNICALL
Java_sun_awt_X11_XlibWrapper_XSetCloseDownMode(JNIEnv *env, jclass clazz,
                           jlong display, jint mode) {
    
}

/*
 * Class:     sun_awt_X11_XlibWrapper
 * Method:    DefaultScreen
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_sun_awt_X11_XlibWrapper_DefaultScreen (JNIEnv *env, jclass clazz, jlong display) {

    return (jlong) 1;
}

/*
 * Class:     sun_awt_X11_XlibWrapper
 * Method:    ScreenOfDisplay
 * Signature: (JJ)J
 */
JNIEXPORT jlong JNICALL Java_sun_awt_X11_XlibWrapper_ScreenOfDisplay(JNIEnv *env, jclass clazz, jlong display, jlong screen_number) {
    return (jlong) 1;
}

/*
 * Class:     sun_awt_X11_XlibWrapper
 * Method:    DoesBackingStore
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_sun_awt_X11_XlibWrapper_DoesBackingStore(JNIEnv *env, jclass clazz, jlong screen) {
    return 0;
}

/*
 * Class:     sun_awt_X11_XlibWrapper
 * Method:    DisplayWidth
 * Signature: (JJ)J
 */
JNIEXPORT jlong JNICALL Java_sun_awt_X11_XlibWrapper_DisplayWidth
(JNIEnv *env, jclass clazz, jlong display, jlong screen) {
    return GLOBAL_WIDTH;
}

/*
 * Class:     sun_awt_X11_XlibWrapper
 * Method:    DisplayWidthMM
 * Signature: (JJ)J
 */
JNIEXPORT jlong JNICALL Java_sun_awt_X11_XlibWrapper_DisplayWidthMM
(JNIEnv *env, jclass clazz, jlong display, jlong screen) {
    return GLOBAL_WIDTH;
}

/*
 * Class:     sun_awt_X11_XlibWrapper
 * Method:    DisplayHeight
 * Signature: (JJ)J
 */
JNIEXPORT jlong JNICALL Java_sun_awt_X11_XlibWrapper_DisplayHeight
(JNIEnv *env, jclass clazz, jlong display, jlong screen) {
    return GLOBAL_HEIGHT;
}
/*
 * Class:     sun_awt_X11_XlibWrapper
 * Method:    DisplayHeightMM
 * Signature: (JJ)J
 */
JNIEXPORT jlong JNICALL Java_sun_awt_X11_XlibWrapper_DisplayHeightMM
(JNIEnv *env, jclass clazz, jlong display, jlong screen) {
    return GLOBAL_HEIGHT;
}

/*
 * Class:     sun_awt_X11_XlibWrapper
 * Method:    RootWindow
 * Signature: (JJ)J
 */
JNIEXPORT jlong JNICALL Java_sun_awt_X11_XlibWrapper_RootWindow
(JNIEnv *env , jclass clazz, jlong display, jlong screen_number) {
    return (jlong) 0;
}

/*
 * Class:     sun_awt_X11_XlibWrapper
 * Method:    ScreenCount
 */
JNIEXPORT jint JNICALL Java_sun_awt_X11_XlibWrapper_ScreenCount
(JNIEnv *env , jclass clazz, jlong display) {
    return 1;
}


/*
 * Class:     XlibWrapper
 * Method:    XCreateWindow
 * Signature: (JJIIIIIIJJJJ)J
 */
JNIEXPORT jlong JNICALL Java_sun_awt_X11_XlibWrapper_XCreateWindow
  (JNIEnv *env, jclass clazz, jlong display, jlong window,
   jint x, jint y, jint w, jint h , jint border_width, jint depth,
   jlong wclass, jlong visual, jlong valuemask, jlong attributes)
{
    return (jlong) 1;
}

/*
 * Class:     XlibWrapper
 * Method:    XConvertCase
 * Signature: (JJJ)V
 */
JNIEXPORT void JNICALL Java_sun_awt_X11_XlibWrapper_XConvertCase
  (JNIEnv *env, jclass clazz, jlong keysym,
   jlong keysym_lowercase, jlong keysym_uppercase)
{

}


/*
 * Class:     XlibWrapper
 * Method:    XMapWindow
 * Signature: (JJ)V
 */
JNIEXPORT void JNICALL Java_sun_awt_X11_XlibWrapper_XMapWindow
(JNIEnv *env, jclass clazz, jlong display, jlong window)
{

}

/*
 * Class:     XlibWrapper
 * Method:    XMapRaised
 * Signature: (JJ)V
 */
JNIEXPORT void JNICALL Java_sun_awt_X11_XlibWrapper_XMapRaised
(JNIEnv *env, jclass clazz, jlong display, jlong window)
{

}

/*
 * Class:     XlibWrapper
 * Method:    XRaiseWindow
 * Signature: (JJ)V
 */
JNIEXPORT void JNICALL Java_sun_awt_X11_XlibWrapper_XRaiseWindow
(JNIEnv *env, jclass clazz, jlong display, jlong window)
{

}

/*
 * Class:     XlibWrapper
 * Method:    XLowerWindow
 * Signature: (JJ)V
 */
JNIEXPORT void JNICALL Java_sun_awt_X11_XlibWrapper_XLowerWindow
(JNIEnv *env, jclass clazz, jlong display, jlong window)
{

}

/*
 * Class:     XlibWrapper
 * Method:    XRestackWindows
 * Signature: (JJI)V
 */
JNIEXPORT void JNICALL Java_sun_awt_X11_XlibWrapper_XRestackWindows
(JNIEnv *env, jclass clazz, jlong display, jlong windows, jint length)
{

}

/*
 * Class:     XlibWrapper
 * Method:    XConfigureWindow
 * Signature: (JJJJ)V
 */
JNIEXPORT void JNICALL Java_sun_awt_X11_XlibWrapper_XConfigureWindow
(JNIEnv *env, jclass clazz, jlong display, jlong window, jlong value_mask,
 jlong values)
{
    
}

/*
 * Class:     XlibWrapper
 * Method:    XSetInputFocus
 * Signature: (JJ)V
 */
JNIEXPORT void JNICALL Java_sun_awt_X11_XlibWrapper_XSetInputFocus
(JNIEnv *env, jclass clazz, jlong display, jlong window)
{

}
/*
 * Class:     XlibWrapper
 * Method:    XSetInputFocus2
 * Signature: (JJJ)V
 */
JNIEXPORT void JNICALL Java_sun_awt_X11_XlibWrapper_XSetInputFocus2
(JNIEnv *env, jclass clazz, jlong display, jlong window, jlong time)
{

}

/*
 * Class:     XlibWrapper
 * Method:    XGetInputFocus
 * Signature: (JJ)V
 */
JNIEXPORT jlong JNICALL Java_sun_awt_X11_XlibWrapper_XGetInputFocus
(JNIEnv *env, jclass clazz, jlong display)
{
    return (jlong) 1;
}


/*
 * Class:     XlibWrapper
 * Method:    XDestroyWindow
 * Signature: (JJ)V
 */
JNIEXPORT void JNICALL Java_sun_awt_X11_XlibWrapper_XDestroyWindow
(JNIEnv *env, jclass clazz, jlong display, jlong window)
{

}

JNIEXPORT jint JNICALL Java_sun_awt_X11_XlibWrapper_XGrabPointer
(JNIEnv *env, jclass clazz, jlong display, jlong window,
 jint owner_events, jint event_mask, jint pointer_mode,
 jint keyboard_mode, jlong confine_to, jlong cursor, jlong time)
{
    return 0;
}

JNIEXPORT void JNICALL Java_sun_awt_X11_XlibWrapper_XUngrabPointer
(JNIEnv *env, jclass clazz, jlong display, jlong time)
{

}

JNIEXPORT jint JNICALL Java_sun_awt_X11_XlibWrapper_XGrabKeyboard
(JNIEnv *env, jclass clazz, jlong display, jlong window,
 jint owner_events, jint pointer_mode,
 jint keyboard_mode, jlong time)
{
    return 0;
}

JNIEXPORT void JNICALL Java_sun_awt_X11_XlibWrapper_XUngrabKeyboard
(JNIEnv *env, jclass clazz, jlong display, jlong time)
{
    
}

JNIEXPORT void JNICALL
Java_sun_awt_X11_XlibWrapper_XGrabServer(JNIEnv *env, jclass clazz,
                                         jlong display) {

}

JNIEXPORT void JNICALL
Java_sun_awt_X11_XlibWrapper_XUngrabServer(JNIEnv *env, jclass clazz,
                                           jlong display) {

}

/*
 * Class:     XlibWrapper
 * Method:    XUnmapWindow
 * Signature: (JJ)V
 */
JNIEXPORT void JNICALL Java_sun_awt_X11_XlibWrapper_XUnmapWindow
(JNIEnv *env, jclass clazz, jlong display, jlong window)
{

}



JNIEXPORT void JNICALL Java_sun_awt_X11_XlibWrapper_XSelectInput
(JNIEnv *env, jclass clazz, jlong display, jlong window, jlong mask)
{

}

JNIEXPORT void JNICALL Java_sun_awt_X11_XlibWrapper_XkbSelectEvents
(JNIEnv *env, jclass clazz, jlong display, jlong device, jlong bits_to_change, jlong values_for_bits)
{

}
JNIEXPORT void JNICALL Java_sun_awt_X11_XlibWrapper_XkbSelectEventDetails
(JNIEnv *env, jclass clazz, jlong display, jlong device, jlong event_type, jlong bits_to_change, jlong values_for_bits)
{

}
JNIEXPORT jboolean JNICALL Java_sun_awt_X11_XlibWrapper_XkbQueryExtension
(JNIEnv *env, jclass clazz, jlong display, jlong opcode_rtrn, jlong event_rtrn,
              jlong error_rtrn, jlong major_in_out, jlong minor_in_out)
{
    return JNI_FALSE;
}
JNIEXPORT jboolean JNICALL Java_sun_awt_X11_XlibWrapper_XkbLibraryVersion
(JNIEnv *env, jclass clazz, jlong lib_major_in_out, jlong lib_minor_in_out)
{
    return JNI_FALSE;
}

JNIEXPORT jlong JNICALL Java_sun_awt_X11_XlibWrapper_XkbGetMap
(JNIEnv *env, jclass clazz, jlong display, jlong which, jlong device_spec)
{
    return 0;
}
JNIEXPORT jlong JNICALL Java_sun_awt_X11_XlibWrapper_XkbGetUpdatedMap
(JNIEnv *env, jclass clazz, jlong display, jlong which, jlong xkb)
{
    return 0;
}
JNIEXPORT void JNICALL Java_sun_awt_X11_XlibWrapper_XkbFreeKeyboard
(JNIEnv *env, jclass clazz, jlong xkb, jlong which, jboolean free_all)
{

}
JNIEXPORT jboolean JNICALL Java_sun_awt_X11_XlibWrapper_XkbTranslateKeyCode
(JNIEnv *env, jclass clazz, jlong xkb, jint keycode, jlong mods, jlong mods_rtrn, jlong keysym_rtrn)
{
    return JNI_FALSE;
}
JNIEXPORT void JNICALL Java_sun_awt_X11_XlibWrapper_XkbSetDetectableAutoRepeat
(JNIEnv *env, jclass clazz, jlong display, jboolean detectable)
{

}
/*
 * Class:     sun_awt_X11_XlibWrapper
 * Method:    XNextEvent
 * Signature: (JJ)V
 */


JNIEXPORT void JNICALL Java_sun_awt_X11_XlibWrapper_XNextEvent
(JNIEnv *env, jclass clazz, jlong display, jlong ptr)
{

}

/*
 * Class:     sun_awt_X11_XlibWrapper
 * Method:    XMaskEvent
 * Signature: (JJJ)V
 */

JNIEXPORT void JNICALL Java_sun_awt_X11_XlibWrapper_XMaskEvent
  (JNIEnv *env, jclass clazz, jlong display, jlong event_mask, jlong event_return)
{
 
}

/*
 * Class:     sun_awt_X11_XlibWrapper
 * Method:    XWindowEvent
 * Signature: (JJJJ)V
 */

JNIEXPORT void JNICALL Java_sun_awt_X11_XlibWrapper_XWindowEvent
  (JNIEnv *env, jclass clazz, jlong display, jlong window, jlong event_mask, jlong event_return)
{
 
}

/*
 * Class:     sun_awt_X11_XlibWrapper
 * Method:    XFilterEvent
 * Signature: (JJ)Z
 */
JNIEXPORT jboolean JNICALL Java_sun_awt_X11_XlibWrapper_XFilterEvent
(JNIEnv *env, jclass clazz, jlong ptr, jlong window)
{
    return JNI_FALSE;
}

/*
 * Class:     sun_awt_X11_XlibWrapper
 * Method:    XSupportsLocale
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_sun_awt_X11_XlibWrapper_XSupportsLocale
(JNIEnv *env, jclass clazz)
{
    return JNI_FALSE;
}

/*
 * Class:     sun_awt_X11_XlibWrapper
 * Method:    XSetLocaleModifiers
 * Signature: (Ljava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_sun_awt_X11_XlibWrapper_XSetLocaleModifiers
(JNIEnv *env, jclass clazz, jstring jstr)
{
    return (*env)->NewStringUTF(env, "");
}


/*
 * Class:     sun_awt_X11_wrappers_XlibWrapper
 * Method:    XPeekEvent
 * Signature: (JJ)V
 */


JNIEXPORT void JNICALL Java_sun_awt_X11_XlibWrapper_XPeekEvent
(JNIEnv *env, jclass clazz, jlong display, jlong ptr)
{
 
    
}


/*
 * Class:     sun_awt_X11_XlibWrapper
 * Method:    XMoveResizeWindow
 * Signature: (JJIIII)V
 */

JNIEXPORT void JNICALL  Java_sun_awt_X11_XlibWrapper_XMoveResizeWindow
(JNIEnv *env, jclass clazz, jlong display, jlong window, jint x , jint y , jint width, jint height) {

}

/*
 * Class:     sun_awt_X11_XlibWrapper
 * Method:    XResizeWindow
 * Signature: (JJII)V
 */

JNIEXPORT void JNICALL  Java_sun_awt_X11_XlibWrapper_XResizeWindow
(JNIEnv *env, jclass clazz, jlong display, jlong window, jint width, jint height)
{

}

/*
 * Class:     sun_awt_X11_XlibWrapper
 * Method:    XMoveWindow
 * Signature: (JJII)V
 */

JNIEXPORT void JNICALL  Java_sun_awt_X11_XlibWrapper_XMoveWindow
(JNIEnv *env, jclass clazz, jlong display, jlong window, jint width, jint height)
{

}


/*
 * Class:     sun_awt_X11_XlibWrapper
 * Method:    XSetWindowBackground
 * Signature: (JJJ)V
 */

JNIEXPORT void JNICALL  Java_sun_awt_X11_XlibWrapper_XSetWindowBackground
(JNIEnv *env, jclass clazz, jlong display, jlong window, jlong background_pixel) {

}


/*
 * Class:     sun_awt_X11_XlibWrapper
 * Method:    XFlush
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_sun_awt_X11_XlibWrapper_XFlush
(JNIEnv *env, jclass clazz, jlong display) {

}

/*
 * Class:     sun_awt_X11_XlibWrapper
 * Method:    XSync
 * Signature: (JI)V
 */
JNIEXPORT void JNICALL Java_sun_awt_X11_XlibWrapper_XSync
(JNIEnv *env, jclass clazz, jlong display, jint discard) {

}

JNIEXPORT jint JNICALL Java_sun_awt_X11_XlibWrapper_XTranslateCoordinates
(JNIEnv *env, jclass clazz, jlong display, jlong src_w, jlong dest_w,
 jlong src_x, jlong src_y, jlong dest_x_return, jlong dest_y_return,
 jlong child_return)
{
    return 0;
}

JNIEXPORT jint JNICALL Java_sun_awt_X11_XlibWrapper_XEventsQueued
(JNIEnv *env, jclass clazz, jlong display, jint mode) {

    return 0;
}

/*
 * Class:     sun_awt_X11_XlibWrapper
 * Method:    SetProperty
 * Signature: (JJJLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_sun_awt_X11_XlibWrapper_SetProperty
(JNIEnv *env, jclass clazz, jlong display, jlong window, jlong atom, jstring jstr) {
    
}

/*
 * Class:     sun_awt_X11_XlibWrapper
 * Method:    XChangeProperty
 * Signature: (JJJJJJJJJJJ)V
 */
JNIEXPORT void JNICALL Java_sun_awt_X11_XlibWrapper_XChangePropertyImpl(
    JNIEnv *env, jclass clazz, jlong display, jlong window, jlong property,
    jlong type, jint format, jint mode, jlong data, jint nelements)
{
    
}
/*
 * Class:     sun_awt_X11_XlibWrapper
 * Method:    XChangePropertyS
 * Signature: (JJJJJJJJJLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_sun_awt_X11_XlibWrapper_XChangePropertyS(
    JNIEnv *env, jclass clazz, jlong display, jlong window, jlong property,
    jlong type, jint format, jint mode, jstring value)
{

}

/*
 * Class:     sun_awt_X11_XlibWrapper
 * Method:    XGetWindowProperty
 * Signature: (JJJJJJJJJJJ)J;
 */
JNIEXPORT jint JNICALL Java_sun_awt_X11_XlibWrapper_XGetWindowProperty
(JNIEnv *env, jclass clazz, jlong display, jlong window, jlong property, jlong long_offset,
 jlong long_length, jlong delete, jlong req_type, jlong actual_type,
 jlong actual_format, jlong nitems_ptr, jlong bytes_after, jlong data_ptr)
{
    return 0;
}

/*
 * Class:     sun_awt_X11_XlibWrapper
 * Method:    GetProperty
 * Signature: (JJJ)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_sun_awt_X11_XlibWrapper_GetProperty
(JNIEnv *env, jclass clazz, jlong display, jlong window, jlong atom)
{
    return (*env)->NewStringUTF(env, "");
}

/*
 * Class:     sun_awt_X11_XlibWrapper
 * Method:    InternAtom
 * Signature: (JLjava/lang/String;I)J
 */
JNIEXPORT jlong JNICALL Java_sun_awt_X11_XlibWrapper_InternAtom
(JNIEnv *env, jclass clazz, jlong display, jstring jstr, jint ife) {

    return 0;
}

JNIEXPORT jint JNICALL Java_sun_awt_X11_XlibWrapper_XCreateFontCursor
(JNIEnv *env, jclass clazz, jlong display, jint shape) {
    return 0;
}


/*
 * Class:     sun_awt_X11_XlibWrapper
 * Method:    XCreatePixmapCursor
 * Signature: (JJJJJII)J
 */
JNIEXPORT jlong JNICALL Java_sun_awt_X11_XlibWrapper_XCreatePixmapCursor
(JNIEnv *env , jclass clazz, jlong display, jlong source, jlong mask, jlong fore, jlong back, jint x , jint y) {

    return 0;
}


/*
 * Class:     sun_awt_X11_XlibWrapper
 * Method:    XQueryBestCursor
 * Signature: (JJIIJJ)Z
 */
JNIEXPORT jboolean JNICALL Java_sun_awt_X11_XlibWrapper_XQueryBestCursor
(JNIEnv *env, jclass clazz, jlong display, jlong drawable, jint width, jint height, jlong width_return, jlong height_return) {
    return JNI_FALSE;
}


/*
 * Class:     sun_awt_X11_XlibWrapper
 * Method:    XFreeCursor
 * Signature: (JJ)V
 */
JNIEXPORT void JNICALL Java_sun_awt_X11_XlibWrapper_XFreeCursor
(JNIEnv *env, jclass clazz, jlong display, jlong cursor) {

}

/*
 * Class:     sun_awt_X11_XlibWrapper
 * Method:    XQueryPointer
 * Signature: (JJJJJJJJJ)Z
 */
JNIEXPORT jboolean JNICALL Java_sun_awt_X11_XlibWrapper_XQueryPointer
(JNIEnv *env, jclass clazz, jlong display, jlong w, jlong root_return, jlong child_return, jlong root_x_return , jlong root_y_return, jlong win_x_return, jlong win_y_return, jlong mask_return) {
    return JNI_FALSE;
}

/*
 * Class:     sun_awt_X11_XlibWrapper
 * Method:    XChangeWindowAttributes
 * Signature: (JJJJ)V
 */
JNIEXPORT void JNICALL Java_sun_awt_X11_XlibWrapper_XChangeWindowAttributes
(JNIEnv *env, jclass clazz, jlong display, jlong window, jlong valuemask, jlong attributes) {

}


/*
 * Class:     sun_awt_X11_XlibWrapper
 * Method:    XSetTransientFor
 * Signature: (JJJ)V
 */
JNIEXPORT void JNICALL Java_sun_awt_X11_XlibWrapper_XSetTransientFor
(JNIEnv *env, jclass clazz, jlong display, jlong window, jlong transient_for_window)
{
 
}

/*
 * Class:     sun_awt_X11_XlibWrapper
 * Method:    XSetWMHints
 * Signature: (JJJ)V
 */
JNIEXPORT void JNICALL Java_sun_awt_X11_XlibWrapper_XSetWMHints
(JNIEnv *env, jclass clazz, jlong display, jlong window, jlong hints)
{
    
}

/*
 * Class:     sun_awt_X11_XlibWrapper
 * Method:    XGetWMHints
 * Signature: (JJJ)V
 */
JNIEXPORT void JNICALL Java_sun_awt_X11_XlibWrapper_XGetWMHints
(JNIEnv *env, jclass clazz, jlong display, jlong window, jlong hints)
{
    
}

/*
 * Class:     sun_awt_X11_XlibWrapper
 * Method:    XGetPointerMapping
 * Signature: (JJI)I
 */
JNIEXPORT jint JNICALL Java_sun_awt_X11_XlibWrapper_XGetPointerMapping
(JNIEnv *env, jclass clazz, jlong display, jlong map, jint buttonNumber)
{
    return 0;
}

/*
 * Class:     sun_awt_X11_XlibWrapper
 * Method:    XGetDefault
 * Signature: (JJI)I
 */
JNIEXPORT jstring JNICALL Java_sun_awt_X11_XlibWrapper_XGetDefault
(JNIEnv *env, jclass clazz, jlong display, jstring program, jstring option)
{
        return (*env)->NewStringUTF(env, "");
}


/*
 * Class:     sun_awt_X11_XlibWrapper
 * Method:    getScreenOfWindow
 * Signature: (JJ)J
 */
JNIEXPORT jlong JNICALL Java_sun_awt_X11_XlibWrapper_getScreenOfWindow
(JNIEnv *env, jclass clazz, jlong display, jlong window)
{
    return 0;
}

/*
 * Class:     sun_awt_X11_XlibWrapper
 * Method:    XScreenNumberOfScreen
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_sun_awt_X11_XlibWrapper_XScreenNumberOfScreen
(JNIEnv *env, jclass clazz, jlong screen)
{
    return 0;
}

/*
 * Class:     sun_awt_X11_XlibWrapper
 * Method:    XIconifyWindow
 * Signature: (JJJ)V
 */
JNIEXPORT jint JNICALL Java_sun_awt_X11_XlibWrapper_XIconifyWindow
(JNIEnv *env, jclass clazz, jlong display, jlong window, jlong screenNumber)
{
    return 0;
}

/*
 * Class:     sun_awt_X11_XlibWrapper
 * Method:    XFree
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_sun_awt_X11_XlibWrapper_XFree
(JNIEnv *env, jclass clazz, jlong ptr)
{
    
}

/*
 * Class:     sun_awt_X11_XlibWrapper
 * Method:    XFree
 * Signature: (J)V
 */
JNIEXPORT jbyteArray JNICALL Java_sun_awt_X11_XlibWrapper_getStringBytes
(JNIEnv *env, jclass clazz, jlong str_ptr)
{
/*
    unsigned char * str = (unsigned char*) jlong_to_ptr(str_ptr);
    long length = strlen((char*)str);
    jbyteArray res = (*env)->NewByteArray(env, length);
    CHECK_NULL_RETURN(res, NULL);
    (*env)->SetByteArrayRegion(env, res, 0, length,
                   (const signed char*) str);
    return res;
*/

    return (*env)->NewStringUTF(env, "0");
}


/*
 * Class:     sun_awt_X11_XlibWrapper
 * Method:    ServerVendor
 * Signature: (J)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_sun_awt_X11_XlibWrapper_ServerVendor
(JNIEnv *env, jclass clazz, jlong display)
{
    return (*env)->NewStringUTF(env, "Android");
}
/*
 * Class:     sun_awt_X11_XlibWrapper
 * Method:    VendorRelease
 * Signature: (J)I;
 */
JNIEXPORT jint JNICALL Java_sun_awt_X11_XlibWrapper_VendorRelease
(JNIEnv *env, jclass clazz, jlong display)
{
    return 0;
}
/*
 * Class:     sun_awt_X11_XlibWrapper
 * Method:    IsXsunKPBehavior
 * Signature: (J)Z;
 */
JNIEXPORT jboolean JNICALL Java_sun_awt_X11_XlibWrapper_IsXsunKPBehavior
(JNIEnv *env, jclass clazz, jlong display)
{
    return JNI_FALSE;
}


JNIEXPORT jboolean JNICALL Java_sun_awt_X11_XlibWrapper_IsSunKeyboard
(JNIEnv *env, jclass clazz, jlong display)
{
    return JNI_FALSE;
}

JNIEXPORT jboolean JNICALL Java_sun_awt_X11_XlibWrapper_IsKanaKeyboard
(JNIEnv *env, jclass clazz, jlong display)
{
    return JNI_FALSE;
}

/*
 * Class:     sun_awt_X11_XlibWrapper
 * Method:    SetToolkitErrorHandler
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_sun_awt_X11_XlibWrapper_SetToolkitErrorHandler
(JNIEnv *env, jclass clazz)
{
    return 0;
}

/*
 * Class:     sun_awt_X11_XlibWrapper
 * Method:    XSetErrorHandler
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_sun_awt_X11_XlibWrapper_XSetErrorHandler
(JNIEnv *env, jclass clazz, jlong handler)
{
    
}

/*
 * Class:     sun_awt_X11_XlibWrapper
 * Method:    CallErrorHandler
 * Signature: (JJJ)I
 */
JNIEXPORT jint JNICALL Java_sun_awt_X11_XlibWrapper_CallErrorHandler
(JNIEnv *env, jclass clazz, jlong handler, jlong display, jlong event_ptr)
{
    return 0;
}


/*
 * Class:     sun_awt_X11_XlibWrapper
 * Method:    PrintXErrorEvent
 * Signature: (JJ)V
 */
JNIEXPORT void JNICALL Java_sun_awt_X11_XlibWrapper_PrintXErrorEvent
(JNIEnv *env, jclass clazz, jlong display, jlong event_ptr)
{
    
}


/*
 * Class:     sun_awt_X11_XlibWrapper
 * Method:    XInternAtoms
 * Signature: (J[Ljava/lang/String;ZJ)I
 */
JNIEXPORT jint JNICALL Java_sun_awt_X11_XlibWrapper_XInternAtoms
(JNIEnv *env, jclass clazz, jlong display, jobjectArray names_arr, jboolean only_if_exists, jlong atoms)
{
    return 0;
}



/*
 * Class:     sun_awt_X11_XlibWrapper
 * Method:    XGetWindowAttributes
 * Signature: (JJJ)I
 */
JNIEXPORT jint JNICALL Java_sun_awt_X11_XlibWrapper_XGetWindowAttributes
(JNIEnv *env, jclass clazz, jlong display, jlong window, jlong attr_ptr)
{
    return 0;
}


/*
 * Class:     sun_awt_X11_XlibWrapper
 * Method:    XGetGeometry
 * Signature: (JJJJJJJJJ)I
 */
JNIEXPORT jint JNICALL Java_sun_awt_X11_XlibWrapper_XGetGeometry
(JNIEnv *env, jclass clazz, jlong display, jlong drawable, jlong root_return,
     jlong x_return, jlong y_return, jlong width_return, jlong height_return,
     jlong border_width_return, jlong depth_return)
{
    return 0;
}


/*
 * Class:     sun_awt_X11_XlibWrapper
 * Method:    XGetWMNormalHints
 * Signature: (JJJJ)I
 */
JNIEXPORT jint JNICALL Java_sun_awt_X11_XlibWrapper_XGetWMNormalHints
(JNIEnv *env, jclass clazz, jlong display, jlong window, jlong hints, jlong supplied_return)
{
    return 0;
}

/*
 * Class:     sun_awt_X11_XlibWrapper
 * Method:    XSetWMNormalHints
 * Signature: (JJJ)V
 */
JNIEXPORT void JNICALL Java_sun_awt_X11_XlibWrapper_XSetWMNormalHints
(JNIEnv *env, jclass clazz, jlong display, jlong window, jlong hints)
{
 
}

/*
 * Class:     sun_awt_X11_XlibWrapper
 * Method:    XDeleteProperty
 * Signature: (JJJ)V
 */
JNIEXPORT void JNICALL Java_sun_awt_X11_XlibWrapper_XDeleteProperty
(JNIEnv *env, jclass clazz, jlong display, jlong window, jlong atom)
{
 
}

/*
 * Class:     sun_awt_X11_XlibWrapper
 * Method:    XSendEvent
 * Signature: (JJZJJ)V
 */
JNIEXPORT jint JNICALL Java_sun_awt_X11_XlibWrapper_XSendEvent
(JNIEnv *env, jclass clazz, jlong display, jlong window, jboolean propagate, jlong event_mask, jlong event)
{
    return 0;
}


/*
 * Class:     sun_awt_X11_XlibWrapper
 * Method:    XQueryTree
 * Signature: (JJJJJJ)I
 */
JNIEXPORT jint JNICALL Java_sun_awt_X11_XlibWrapper_XQueryTree
(JNIEnv *env, jclass clazz, jlong display, jlong window, jlong root_return, jlong parent_return, jlong children_return, jlong nchildren_return)
{
    return 0;
}


/*
 * Class:     sun_awt_X11_XlibWrapper
 * Method:    memcpy
 * Signature: (JJJ)V
 */
JNIEXPORT void JNICALL Java_sun_awt_X11_XlibWrapper_memcpy
(JNIEnv *env, jclass clazz, jlong dest_ptr, jlong src_ptr, jlong length)
{
    // memcpy(jlong_to_ptr(dest_ptr), jlong_to_ptr(src_ptr), length);
}


JNIEXPORT void JNICALL Java_sun_awt_X11_XlibWrapper_XSetMinMaxHints
(JNIEnv *env, jclass clazz, jlong display, jlong window, jint x, jint y, jint width, jint height, jlong flags) {
    
}


JNIEXPORT jlong JNICALL Java_sun_awt_X11_XlibWrapper_XGetVisualInfo
(JNIEnv *env, jclass clazz, jlong display, jlong vinfo_mask, jlong vinfo_template,
 jlong nitems_return)
{
    return 0;
}

JNIEXPORT jlong JNICALL Java_sun_awt_X11_XlibWrapper_XAllocSizeHints
  (JNIEnv *env, jclass clazz)
{
    return 0;
}

/*
 * Class:     sun_awt_X11_XlibWrapper
 * Method:    XIconifyWindow
 * Signature: (JJJ)V
 */
JNIEXPORT void JNICALL Java_sun_awt_X11_XlibWrapper_XBell
(JNIEnv *env, jclass clazz, jlong display, jint percent)
{

}


/*
 * Class:     sun_awt_X11_XlibWrapper
 * Method:    XAllocColor
 * Signature: (JJJ)Z
 */
JNIEXPORT jboolean JNICALL Java_sun_awt_X11_XlibWrapper_XAllocColor
(JNIEnv *env, jclass clazz, jlong display , jlong colormap, jlong xcolor) {

    return JNI_FALSE;
}


/*
 * Class:     sun_awt_X11_XlibWrapper
 * Method:    XCreateBitmapFromData
 * Signature: (JJJII)J
 */
JNIEXPORT jlong JNICALL Java_sun_awt_X11_XlibWrapper_XCreateBitmapFromData
(JNIEnv *env, jclass clazz, jlong display, jlong drawable, jlong data, jint width, jint height) {
    return 0;
}


/*
 * Class:     sun_awt_X11_XlibWrapper
 * Method:    XFreePixmap
 * Signature: (JJ)V
 */
JNIEXPORT void JNICALL Java_sun_awt_X11_XlibWrapper_XFreePixmap
(JNIEnv *env, jclass clazz, jlong display, jlong pixmap) {

}

/*
 * Class:     sun_awt_X11_XlibWrapper
 * Method:    XReparentWindow
 * Signature: (JJJII)V
 */
JNIEXPORT void JNICALL Java_sun_awt_X11_XlibWrapper_XReparentWindow
(JNIEnv *env, jclass clazz, jlong display, jlong window, jlong parent, jint x, jint y) {
 
}

/*
 * Class:     sun_awt_X11_XlibWrapper
 * Method:    XConvertSelection
 * Signature: (JJJJJJ)V
 */
JNIEXPORT void JNICALL
Java_sun_awt_X11_XlibWrapper_XConvertSelection(JNIEnv *env, jclass clazz,
                           jlong display, jlong selection,
                           jlong target, jlong property,
                           jlong requestor, jlong time) {

}

/*
 * Class:     sun_awt_X11_XlibWrapper
 * Method:    XSetSelectionOwner
 * Signature: (JJJJ)V
 */
JNIEXPORT void JNICALL
Java_sun_awt_X11_XlibWrapper_XSetSelectionOwner(JNIEnv *env, jclass clazz,
                        jlong display, jlong selection,
                        jlong owner, jlong time) {
 
}

/*
 * Class:     sun_awt_X11_XlibWrapper
 * Method:    XGetSelectionOwner
 * Signature: (JJ)J
 */
JNIEXPORT jlong JNICALL
Java_sun_awt_X11_XlibWrapper_XGetSelectionOwner(JNIEnv *env, jclass clazz,
                        jlong display, jlong selection) {
    return 0;
}

/*
 * Class:     sun_awt_X11_XlibWrapper
 * Method:    XGetAtomName
 * Signature: (JJ)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL
Java_sun_awt_X11_XlibWrapper_XGetAtomName(JNIEnv *env, jclass clazz,
                      jlong display, jlong atom)
{
    return (*env)->NewStringUTF(env, "Unknown");
}

/*
 * Class:     sun_awt_X11_XlibWrapper
 * Method:    XMaxRequestSize
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL
Java_sun_awt_X11_XlibWrapper_XMaxRequestSize(JNIEnv *env, jclass clazz,
                         jlong display) {
    return 0;
}

JNIEXPORT jlong JNICALL
Java_sun_awt_X11_XlibWrapper_XAllocWMHints(JNIEnv *env, jclass clazz)
{
    return 0;
}

JNIEXPORT jlong JNICALL
Java_sun_awt_X11_XlibWrapper_XCreatePixmap(JNIEnv *env, jclass clazz, jlong display, jlong drawable, jint width, jint height, jint depth)
{
    return 0;
}
JNIEXPORT jlong JNICALL
Java_sun_awt_X11_XlibWrapper_XCreateImage
  (JNIEnv *env, jclass clazz, jlong display, jlong visual_ptr,
   jint depth, jint format, jint offset, jlong data, jint width,
   jint height, jint bitmap_pad, jint bytes_per_line)
{
    return 0;
}
JNIEXPORT jlong JNICALL
Java_sun_awt_X11_XlibWrapper_XCreateGC
  (JNIEnv *env, jclass clazz, jlong display, jlong drawable,
   jlong valuemask, jlong values)
{
    return 0;
}

JNIEXPORT void JNICALL
Java_sun_awt_X11_XlibWrapper_XDestroyImage(JNIEnv *env, jclass clazz, jlong image)
{
 
}
JNIEXPORT void JNICALL
Java_sun_awt_X11_XlibWrapper_XPutImage(JNIEnv *env, jclass clazz, jlong display, jlong drawable, jlong gc, jlong image, jint src_x, jint src_y, jint dest_x, jint dest_y, jint width, jint height)
{
 
}
JNIEXPORT void JNICALL
Java_sun_awt_X11_XlibWrapper_XFreeGC(JNIEnv *env, jclass clazz, jlong display, jlong gc)
{
  
}
JNIEXPORT void JNICALL
Java_sun_awt_X11_XlibWrapper_XSetWindowBackgroundPixmap(JNIEnv *env, jclass clazz, jlong display, jlong window, jlong pixmap)
{
  
}
JNIEXPORT void JNICALL
Java_sun_awt_X11_XlibWrapper_XClearWindow(JNIEnv *env, jclass clazz, jlong display, jlong window)
{
  
}

JNIEXPORT jint JNICALL
Java_sun_awt_X11_XlibWrapper_XGetIconSizes(JNIEnv *env, jclass clazz, jlong display, jlong window, jlong ret_sizes, jlong ret_count)
{
    return 0;
}

JNIEXPORT jint JNICALL Java_sun_awt_X11_XlibWrapper_XdbeQueryExtension
  (JNIEnv *env, jclass clazz, jlong display, jlong major_version_return,
   jlong minor_version_return)
{
    return 0;
}

JNIEXPORT jboolean JNICALL Java_sun_awt_X11_XlibWrapper_XQueryExtension
  (JNIEnv *env, jclass clazz, jlong display, jstring jstr, jlong mop_return,
   jlong feve_return, jlong err_return)
{
    return 0;
}

JNIEXPORT jboolean JNICALL Java_sun_awt_X11_XlibWrapper_IsKeypadKey
  (JNIEnv *env, jclass clazz, jlong keysym)
{
    return 0;
}

JNIEXPORT jlong JNICALL Java_sun_awt_X11_XlibWrapper_XdbeAllocateBackBufferName
  (JNIEnv *env, jclass clazz, jlong display, jlong window, jint swap_action)
{
    return 0;
}

JNIEXPORT jint JNICALL Java_sun_awt_X11_XlibWrapper_XdbeDeallocateBackBufferName
  (JNIEnv *env, jclass clazz, jlong display, jlong buffer)
{
    return 0;
}

JNIEXPORT jint JNICALL Java_sun_awt_X11_XlibWrapper_XdbeBeginIdiom
  (JNIEnv *env, jclass clazz, jlong display)
{
    return 0;
}

JNIEXPORT jint JNICALL Java_sun_awt_X11_XlibWrapper_XdbeEndIdiom
  (JNIEnv *env, jclass clazz, jlong display)
{
    return 0;
}

JNIEXPORT jint JNICALL Java_sun_awt_X11_XlibWrapper_XdbeSwapBuffers
  (JNIEnv *env, jclass clazz, jlong display, jlong swap_info, jint num_windows)
{
    return 0;
}
JNIEXPORT void JNICALL Java_sun_awt_X11_XlibWrapper_XQueryKeymap
(JNIEnv *env, jclass clazz, jlong display, jlong vector)
{

 
}

JNIEXPORT jlong JNICALL
Java_sun_awt_X11_XlibWrapper_XKeycodeToKeysym(JNIEnv *env, jclass clazz,
                                              jlong display, jint keycode,
                                              jint index) {
    return 0;
}

JNIEXPORT jint JNICALL
Java_sun_awt_X11_XlibWrapper_XkbGetEffectiveGroup(JNIEnv *env, jclass clazz,
                                              jlong display) {
    return 0;
}
JNIEXPORT jlong JNICALL
Java_sun_awt_X11_XlibWrapper_XkbKeycodeToKeysym(JNIEnv *env, jclass clazz,
                                              jlong display, jint keycode,
                                              jint group, jint level) {
    return 0;
}

JNIEXPORT jint JNICALL
Java_sun_awt_X11_XlibWrapper_XKeysymToKeycode(JNIEnv *env, jclass clazz,
                                              jlong display, jlong keysym) {
    return 0;
}

JNIEXPORT jlong JNICALL
Java_sun_awt_X11_XlibWrapper_XGetModifierMapping(JNIEnv *env, jclass clazz,
                                              jlong display) {
    return 0;
}

JNIEXPORT void JNICALL
Java_sun_awt_X11_XlibWrapper_XFreeModifiermap(JNIEnv *env, jclass clazz,
                                              jlong keymap) {
  
}
/*
 * Class:     sun_awt_X11_XlibWrapper
 * Method:    XRefreshKeyboardMapping
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_sun_awt_X11_XlibWrapper_XRefreshKeyboardMapping
(JNIEnv *env, jclass clazz, jlong event_ptr)
{
 
}

JNIEXPORT void JNICALL
Java_sun_awt_X11_XlibWrapper_XChangeActivePointerGrab(JNIEnv *env, jclass clazz,
                                                      jlong display, jint mask,
                                                      jlong cursor, jlong time) {

}

/******************* Secondary loop support ************************************/
#define AWT_SECONDARY_LOOP_TIMEOUT 250


JNIEXPORT jboolean JNICALL
Java_sun_awt_X11_XlibWrapper_XNextSecondaryLoopEvent(JNIEnv *env, jclass clazz,
                                                     jlong display, jlong ptr) {
    
                                                         
    return JNI_FALSE;
}

JNIEXPORT void JNICALL
Java_sun_awt_X11_XlibWrapper_ExitSecondaryLoop(JNIEnv *env, jclass clazz) {
    
    // AWT_NOTIFY_ALL();
}
/*******************************************************************************/

JNIEXPORT jobjectArray JNICALL
Java_sun_awt_X11_XlibWrapper_XTextPropertyToStringList(JNIEnv *env,
                                                       jclass clazz,
                                                       jbyteArray bytes,
                                                       jlong encodingAtom) {
    return NULL;
}


JNIEXPORT void JNICALL
Java_sun_awt_X11_XlibWrapper_XPutBackEvent(JNIEnv *env,
                                           jclass clazz,
                                           jlong display,
                                           jlong event) {

}
/*
JNIEXPORT jlong JNICALL
Java_sun_awt_X11_XlibWrapper_getAddress(JNIEnv *env,
                                           jclass clazz,
                                           jobject o) {
    return ptr_to_jlong(o);
}

JNIEXPORT void JNICALL
Java_sun_awt_X11_XlibWrapper_copyIntArray(JNIEnv *env,
                                           jclass clazz,
                                           jlong dest, jobject array, jint size) {
    jboolean isCopy = JNI_FALSE;
    jint * ints = (*env)->GetIntArrayElements(env, array, &isCopy);
    memcpy(jlong_to_ptr(dest), ints, size);
    if (isCopy) {
        (*env)->ReleaseIntArrayElements(env, array, ints, JNI_ABORT);
    }
}

JNIEXPORT void JNICALL
Java_sun_awt_X11_XlibWrapper_copyLongArray(JNIEnv *env,
                                           jclass clazz,
                                           jlong dest, jobject array, jint size) {
    jboolean isCopy = JNI_FALSE;
    jlong * longs = (*env)->GetLongArrayElements(env, array, &isCopy);
    memcpy(jlong_to_ptr(dest), longs, size);
    if (isCopy) {
        (*env)->ReleaseLongArrayElements(env, array, longs, JNI_ABORT);
    }
}
*/
JNIEXPORT jint JNICALL
Java_sun_awt_X11_XlibWrapper_XSynchronize(JNIEnv *env, jclass clazz, jlong display, jboolean onoff)
{
    return 0; // (jint) XSynchronize((Display*)jlong_to_ptr(display), (onoff == JNI_TRUE ? True : False));
}

JNIEXPORT jboolean JNICALL
Java_sun_awt_X11_XlibWrapper_XShapeQueryExtension
(JNIEnv *env, jclass clazz, jlong display, jlong event_base_return, jlong error_base_return)
{
    return 0;
}

/*
 * Class:     XlibWrapper
 * Method:    SetRectangularShape
 */

JNIEXPORT void JNICALL
Java_sun_awt_X11_XlibWrapper_SetRectangularShape
(JNIEnv *env, jclass clazz, jlong display, jlong window,
 jint x1, jint y1, jint x2, jint y2,
 jobject region)
{
    
}

/*
 * Class:     XlibWrapper
 * Method:    SetZOrder
 */

JNIEXPORT void JNICALL
Java_sun_awt_X11_XlibWrapper_SetZOrder
(JNIEnv *env, jclass clazz, jlong display, jlong window, jlong above)
{
    
    
}

/*
 * Class:     XlibWrapper
 * Method:    SetBitmapShape
 */
JNIEXPORT void JNICALL
Java_sun_awt_X11_XlibWrapper_SetBitmapShape
(JNIEnv *env, jclass clazz, jlong display, jlong window,
 jint width, jint height, jintArray bitmap)
{
    
    
}

