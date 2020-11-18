#include <jni.h>
#include <stdlib.h>

#include "awt_global.h"

jint JNI_OnLoad(JavaVM* vm, void* reserved) {
    GLOBAL_WIDTH = atoi(getenv("AWTSTUB_WIDTH"));
    GLOBAL_HEIGHT = atoi(getenv("AWTSTUB_HEIGHT"));
    
    return JNI_VERSION_1_4;
}

JNIEXPORT void JNICALL Java_java_awt_FileDialog_initIDs(JNIEnv *env, jclass cls) {}
JNIEXPORT void JNICALL Java_java_awt_Font_initIDs(JNIEnv *env, jclass cls) {}
JNIEXPORT void JNICALL Java_java_awt_Component_initIDs(JNIEnv *env, jclass cls) {}
JNIEXPORT void JNICALL Java_java_awt_Container_initIDs(JNIEnv *env, jclass cls) {}
JNIEXPORT void JNICALL Java_java_awt_Button_initIDs(JNIEnv *env, jclass cls) {}
JNIEXPORT void JNICALL Java_java_awt_Scrollbar_initIDs(JNIEnv *env, jclass cls) {}
JNIEXPORT void JNICALL Java_java_awt_Window_initIDs(JNIEnv *env, jclass cls) {}
JNIEXPORT void JNICALL Java_java_awt_Frame_initIDs(JNIEnv *env, jclass cls) {}
JNIEXPORT void JNICALL Java_java_awt_MenuComponent_initIDs(JNIEnv *env, jclass cls) {}
JNIEXPORT void JNICALL Java_java_awt_Cursor_initIDs(JNIEnv *env, jclass cls) {}
JNIEXPORT void JNICALL Java_java_awt_MenuItem_initIDs(JNIEnv *env, jclass cls) {}
JNIEXPORT void JNICALL Java_java_awt_Menu_initIDs(JNIEnv *env, jclass cls) {}
JNIEXPORT void JNICALL Java_java_awt_TextArea_initIDs(JNIEnv *env, jclass cls) {}
JNIEXPORT void JNICALL Java_java_awt_Checkbox_initIDs(JNIEnv *env, jclass cls) {}
JNIEXPORT void JNICALL Java_java_awt_ScrollPane_initIDs(JNIEnv *env, jclass cls) {}
JNIEXPORT void JNICALL Java_java_awt_TextField_initIDs(JNIEnv *env, jclass cls) {}
JNIEXPORT void JNICALL Java_java_awt_Dialog_initIDs(JNIEnv *env, jclass cls) {}
JNIEXPORT void JNICALL Java_java_awt_KeyboardFocusManager_initIDs(JNIEnv *env, jclass cls) {}
JNIEXPORT void JNICALL Java_java_awt_TrayIcon_initIDs(JNIEnv *env, jclass cls) {}
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

