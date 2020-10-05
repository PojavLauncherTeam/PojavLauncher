/*
 * Copyright (c) 1999, 2017, Oracle and/or its affiliates. All rights reserved.
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
#ifdef HEADLESS
    #error This file should not be included in headless library
#endif

#include "jvm_md.h"
#include <dlfcn.h>

#include "awt_p.h"
#include "awt_GraphicsEnv.h"
#define XK_MISCELLANY
#include <X11/keysymdef.h>
#include <X11/Xutil.h>
#include <X11/Xmd.h>
#include <X11/extensions/xtestext1.h>
#include <X11/extensions/XTest.h>
#include <X11/extensions/XInput.h>
#include <X11/extensions/XI.h>
#include <jni.h>
#include <sizecalc.h>
#include "robot_common.h"
#include "canvas.h"
#include "wsutils.h"
#include "list.h"
#include "multiVis.h"
#include "gtk_interface.h"
*/

#include <jni.h>

#if defined(__linux__) || defined(MACOSX)
#include <sys/socket.h>
#endif

/*
static Bool   (*compositeQueryExtension)   (Display*, int*, int*);
static Status (*compositeQueryVersion)     (Display*, int*, int*);
static Window (*compositeGetOverlayWindow) (Display *, Window);

extern struct X11GraphicsConfigIDs x11GraphicsConfigIDs;

static jint * masks;
static jint num_buttons;
*/

// TODO Implement if need

/*********************************************************************************************/

// this should be called from XRobotPeer constructor
JNIEXPORT void JNICALL
Java_sun_awt_X11_XRobotPeer_setup (JNIEnv * env, jclass cls, jint numberOfButtons, jintArray buttonDownMasks)
{
/*
    int32_t xtestAvailable;
    jint *tmp;
    int i;

    DTRACE_PRINTLN("RobotPeer: setup()");

    num_buttons = numberOfButtons;
    tmp = (*env)->GetIntArrayElements(env, buttonDownMasks, JNI_FALSE);
    CHECK_NULL(tmp);

    masks = (jint *)SAFE_SIZE_ARRAY_ALLOC(malloc, sizeof(jint), num_buttons);
    if (masks == (jint *) NULL) {
        (*env)->ExceptionClear(env);
        (*env)->ReleaseIntArrayElements(env, buttonDownMasks, tmp, 0);
        JNU_ThrowOutOfMemoryError((JNIEnv *)JNU_GetEnv(jvm, JNI_VERSION_1_2), NULL);
        return;
    }
    for (i = 0; i < num_buttons; i++) {
        masks[i] = tmp[i];
    }
    (*env)->ReleaseIntArrayElements(env, buttonDownMasks, tmp, 0);

    AWT_LOCK();
    xtestAvailable = isXTestAvailable();
    DTRACE_PRINTLN1("RobotPeer: XTest available = %d", xtestAvailable);
    if (!xtestAvailable) {
        JNU_ThrowByName(env, "java/awt/AWTException", "java.awt.Robot requires your X server support the XTEST extension version 2.2");
    }

    AWT_UNLOCK();
*/
}


JNIEXPORT void JNICALL
Java_sun_awt_X11_XRobotPeer_getRGBPixelsImpl( JNIEnv *env,
                             jclass cls,
                             jobject xgc,
                             jint jx,
                             jint jy,
                             jint jwidth,
                             jint jheight,
                             jintArray pixelArray,
                             jboolean useGtk) {
/*
    XImage *image;
    jint *ary;               /* Array of jints for sending pixel values back
                              * to parent process.
                              * /
    Window rootWindow;
    XWindowAttributes attr;
    AwtGraphicsConfigDataPtr adata;

    DTRACE_PRINTLN6("RobotPeer: getRGBPixelsImpl(%lx, %d, %d, %d, %d, %x)", xgc, jx, jy, jwidth, jheight, pixelArray);

    if (jwidth <= 0 || jheight <= 0) {
        return;
    }

    adata = (AwtGraphicsConfigDataPtr) JNU_GetLongFieldAsPtr(env, xgc, x11GraphicsConfigIDs.aData);
    DASSERT(adata != NULL);

    AWT_LOCK();

    rootWindow = XRootWindow(awt_display, adata->awt_visInfo.screen);

    if (!useGtk) {
        if (hasXCompositeOverlayExtension(awt_display) &&
            isXCompositeDisplay(awt_display, adata->awt_visInfo.screen))
        {
            rootWindow = compositeGetOverlayWindow(awt_display, rootWindow);
        }
    }

    if (!XGetWindowAttributes(awt_display, rootWindow, &attr)
            || jx + jwidth <= attr.x
            || attr.x + attr.width <= jx
            || jy + jheight <= attr.y
            || attr.y + attr.height <= jy) {

        AWT_UNLOCK();
        return; // Does not intersect with root window
    }

    gboolean gtk_failed = TRUE;
    jint _x, _y;

    jint x = MAX(jx, attr.x);
    jint y = MAX(jy, attr.y);
    jint width = MIN(jx + jwidth, attr.x + attr.width) - x;
    jint height = MIN(jy + jheight, attr.y + attr.height) - y;

    int dx = attr.x > jx ? attr.x - jx : 0;
    int dy = attr.y > jy ? attr.y - jy : 0;

    int index;

    if (useGtk) {
        gtk->gdk_threads_enter();
        gtk_failed = gtk->get_drawable_data(env, pixelArray, x, y, width,
                                            height, jwidth, dx, dy, 1);
        gtk->gdk_threads_leave();
    }

    if (gtk_failed) {
        image = getWindowImage(awt_display, rootWindow, x, y, width, height);

        ary = (*env)->GetPrimitiveArrayCritical(env, pixelArray, NULL);

        if (!ary) {
            XDestroyImage(image);
            AWT_UNLOCK();
            return;
        }

        /* convert to Java ARGB pixels * /
        for (_y = 0; _y < height; _y++) {
            for (_x = 0; _x < width; _x++) {
                jint pixel = (jint) XGetPixel(image, _x, _y);
                                                              /* Note ignore upper
                                                               * 32-bits on 64-bit
                                                               * OSes.
                                                               * /
                pixel |= 0xff000000; /* alpha - full opacity * /

                index = (_y + dy) * jwidth + (_x + dx);
                ary[index] = pixel;
            }
        }

        XDestroyImage(image);
        (*env)->ReleasePrimitiveArrayCritical(env, pixelArray, ary, 0);
    }
    AWT_UNLOCK();
*/
}

JNIEXPORT void JNICALL
Java_sun_awt_X11_XRobotPeer_keyPressImpl (JNIEnv *env,
                         jclass cls,
                         jint keycode) {
/*
    AWT_LOCK();

    DTRACE_PRINTLN1("RobotPeer: keyPressImpl(%i)", keycode);

    XTestFakeKeyEvent(awt_display,
                      XKeysymToKeycode(awt_display, awt_getX11KeySym(keycode)),
                      True,
                      CurrentTime);

    XSync(awt_display, False);

    AWT_UNLOCK();
*/
}

JNIEXPORT void JNICALL
Java_sun_awt_X11_XRobotPeer_keyReleaseImpl (JNIEnv *env,
                           jclass cls,
                           jint keycode) {
/*
    AWT_LOCK();

    DTRACE_PRINTLN1("RobotPeer: keyReleaseImpl(%i)", keycode);

    XTestFakeKeyEvent(awt_display,
                      XKeysymToKeycode(awt_display, awt_getX11KeySym(keycode)),
                      False,
                      CurrentTime);

    XSync(awt_display, False);
    AWT_UNLOCK();
*/
}

JNIEXPORT void JNICALL
Java_sun_awt_X11_XRobotPeer_mouseMoveImpl (JNIEnv *env,
                          jclass cls,
                          jobject xgc,
                          jint root_x,
                          jint root_y) {
/*
    AwtGraphicsConfigDataPtr adata;

    AWT_LOCK();

    DTRACE_PRINTLN3("RobotPeer: mouseMoveImpl(%lx, %i, %i)", xgc, root_x, root_y);

    adata = (AwtGraphicsConfigDataPtr) JNU_GetLongFieldAsPtr(env, xgc, x11GraphicsConfigIDs.aData);
    DASSERT(adata != NULL);

    XWarpPointer(awt_display, None, XRootWindow(awt_display, adata->awt_visInfo.screen), 0, 0, 0, 0, root_x, root_y);
    XSync(awt_display, False);

    AWT_UNLOCK();
*/
}

JNIEXPORT void JNICALL
Java_sun_awt_X11_XRobotPeer_mousePressImpl (JNIEnv *env,
                           jclass cls,
                           jint buttonMask) {
    // mouseAction(env, cls, buttonMask, True);
}

JNIEXPORT void JNICALL
Java_sun_awt_X11_XRobotPeer_mouseReleaseImpl (JNIEnv *env,
                             jclass cls,
                             jint buttonMask) {
    // mouseAction(env, cls, buttonMask, False);
}

JNIEXPORT void JNICALL
Java_sun_awt_X11_XRobotPeer_mouseWheelImpl (JNIEnv *env,
                           jclass cls,
                           jint wheelAmt) {
/* Mouse wheel is implemented as a button press of button 4 and 5, so it */
/* probably could have been hacked into robot_mouseButtonEvent, but it's */
/* cleaner to give it its own command type, in case the implementation   */
/* needs to be changed later.  -bchristi, 6/20/01                        */

/*
    int32_t repeat = abs(wheelAmt);
    int32_t button = wheelAmt < 0 ? 4 : 5;  // wheel up:   button 4
                                            // wheel down: button 5
    int32_t loopIdx;

    AWT_LOCK();

    DTRACE_PRINTLN1("RobotPeer: mouseWheelImpl(%i)", wheelAmt);

    for (loopIdx = 0; loopIdx < repeat; loopIdx++) { // do nothing for
                                                     // wheelAmt == 0
        XTestFakeButtonEvent(awt_display, button, True, CurrentTime);
        XTestFakeButtonEvent(awt_display, button, False, CurrentTime);
    }
    XSync(awt_display, False);

    AWT_UNLOCK();
*/
}

JNIEXPORT void JNICALL
Java_sun_awt_X11_XRobotPeer_loadNativeLibraries (JNIEnv *env, jclass cls) {
    // initXCompositeFunctions();
}
