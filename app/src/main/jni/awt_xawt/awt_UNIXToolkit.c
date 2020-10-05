/*
 * Copyright (c) 2004, 2016, Oracle and/or its affiliates. All rights reserved.
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

#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <dlfcn.h>

#include <jni.h>
#include "sizecalc.h"
// #include "sun_awt_UNIXToolkit.h"

static jclass this_class = NULL;
static jmethodID icon_upcall_method = NULL;


/*
 * Class:     sun_awt_UNIXToolkit
 * Method:    check_gtk
 * Signature: (I)Z
 */
JNIEXPORT jboolean JNICALL
Java_sun_awt_UNIXToolkit_check_1gtk(JNIEnv *env, jclass klass, jint version) {
    return JNI_FALSE;
}


/*
 * Class:     sun_awt_UNIXToolkit
 * Method:    load_gtk
 * Signature: (I)Z
 */
JNIEXPORT jboolean JNICALL
Java_sun_awt_UNIXToolkit_load_1gtk(JNIEnv *env, jclass klass, jint version,
                                                             jboolean verbose) {
    return JNI_FALSE;
}


/*
 * Class:     sun_awt_UNIXToolkit
 * Method:    unload_gtk
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL
Java_sun_awt_UNIXToolkit_unload_1gtk(JNIEnv *env, jclass klass)
{
    return JNI_FALSE;
}

/*
 * Class:     sun_awt_UNIXToolkit
 * Method:    load_gtk_icon
 * Signature: (Ljava/lang/String)Z
 *
 * This method assumes that GTK libs are present.
 */
JNIEXPORT jboolean JNICALL
Java_sun_awt_UNIXToolkit_load_1gtk_1icon(JNIEnv *env, jobject this,
        jstring filename)
{
    return JNI_FALSE;
}

/*
 * Class:     sun_awt_UNIXToolkit
 * Method:    load_stock_icon
 * Signature: (ILjava/lang/String;IILjava/lang/String;)Z
 *
 * This method assumes that GTK libs are present.
 */
JNIEXPORT jboolean JNICALL
Java_sun_awt_UNIXToolkit_load_1stock_1icon(JNIEnv *env, jobject this,
        jint widget_type, jstring stock_id, jint icon_size,
        jint text_direction, jstring detail)
{
    return JNI_FALSE;
}

/*
 * Class:     sun_awt_UNIXToolkit
 * Method:    nativeSync
 * Signature: ()V
 */
JNIEXPORT void JNICALL
Java_sun_awt_UNIXToolkit_nativeSync(JNIEnv *env, jobject this)
{

}

/*
 * Class:     sun_awt_SunToolkit
 * Method:    closeSplashScreen
 * Signature: ()V
 */
JNIEXPORT void JNICALL
Java_sun_awt_SunToolkit_closeSplashScreen(JNIEnv *env, jclass cls)
{
    
}

/*
 * Class:     sun_awt_UNIXToolkit
 * Method:    gtkCheckVersionImpl
 * Signature: (III)Ljava/lang/String;
 */
JNIEXPORT jboolean JNICALL
Java_sun_awt_UNIXToolkit_gtkCheckVersionImpl(JNIEnv *env, jobject this,
        jint major, jint minor, jint micro)
{
    return JNI_FALSE;
}

/*
 * Class:     sun_awt_UNIXToolkit
 * Method:    get_gtk_version
 * Signature: ()I
 */
JNIEXPORT jint JNICALL
Java_sun_awt_UNIXToolkit_get_1gtk_1version(JNIEnv *env, jclass klass)
{
    // return GTK_ANY;
    return (jint) 1;
}

#undef HEADLESS

