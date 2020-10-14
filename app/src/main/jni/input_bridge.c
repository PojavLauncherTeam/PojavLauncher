#include <jni.h>
#include <assert.h>

#include "utils.h"

typedef void GLFW_invoke_Char_func(void* window, unsigned int codepoint);
typedef void GLFW_invoke_CharMods_func(void* window, unsigned int codepoint, int mods);
typedef void GLFW_invoke_CursorEnter_func(void* window, int entered);
typedef void GLFW_invoke_CursorPos_func(void* window, double xpos, double ypos);
typedef void GLFW_invoke_FramebufferSize_func(void* window, int width, int height);
typedef void GLFW_invoke_Key_func(void* window, int key, int scancode, int action, int mods);
typedef void GLFW_invoke_MouseButton_func(void* window, int button, int action, int mods);
typedef void GLFW_invoke_Scroll_func(void* window, double xoffset, double yoffset);
typedef void GLFW_invoke_WindowSize_func(void* window, int width, int height);

JavaVM* firstJavaVM;
JavaVM* secondJavaVM;

JNIEnv* firstJNIEnv;
JNIEnv* secondJNIEnv;

jclass inputBridgeClass_ANDROID, inputBridgeClass_JRE;
jmethodID inputBridgeMethod_ANDROID, inputBridgeMethod_JRE;

jboolean isGrabbing;

jint JNI_OnLoad(JavaVM* vm, void* reserved) {
    if (dalvikJavaVMPtr == NULL) {
        //Save dalvik global JavaVM pointer
        dalvikJavaVMPtr = vm;
        (*vm)->GetEnv(vm, (void**) &dalvikJNIEnvPtr_ANDROID, JNI_VERSION_1_4);
    } else if (dalvikJavaVMPtr != vm) {
        runtimeJavaVMPtr = vm;
        (*vm)->GetEnv(vm, (void**) &runtimeJNIEnvPtr_JRE, JNI_VERSION_1_4);
    }
    
    isGrabbing = JNI_FALSE;
    
    return JNI_VERSION_1_4;
}

// Should be?
/*
void JNI_OnUnload(JavaVM* vm, void* reserved) {
    if (dalvikJavaVMPtr == vm) {
    } else {
    }
    
    DetachCurrentThread(vm);
    
    return JNI_VERSION_1_4;
}
*/

void attachThreadIfNeed(bool* isAttached, JNIEnv** secondJNIEnvPtr) {
    if (!*isAttached && secondJavaVM) {
        (*secondJavaVM)->AttachCurrentThread(secondJavaVM, secondJNIEnvPtr, NULL);
        *isAttached = true;
    }
    secondJNIEnv = *secondJNIEnvPtr;
}

void getJavaInputBridge(jclass* clazz, jmethodID* method) {
    if (*method == NULL && secondJNIEnv != NULL) {
        *clazz = (*secondJNIEnv)->FindClass(secondJNIEnv, "org/lwjgl/glfw/CallbackBridge");
        assert(*clazz != NULL);
        *method = (*secondJNIEnv)->GetStaticMethodID(secondJNIEnv, *clazz, "receiveCallback", "(ILjava/lang/String;)V");
        assert(*method != NULL);
    }
}

JNIEXPORT void JNICALL Java_org_lwjgl_glfw_CallbackBridge_nativeAttachThreadToJRE(JNIEnv* env, jclass clazz, jboolean isAndroid) {
    if (isAndroid == JNI_TRUE) {
        firstJavaVM = dalvikJavaVMPtr;
        firstJNIEnv = dalvikJNIEnvPtr_ANDROID;
        secondJavaVM = runtimeJavaVMPtr;
        
        attachThreadIfNeed(&isAndroidThreadAttached, &runtimeJNIEnvPtr_ANDROID);
        getJavaInputBridge(&inputBridgeClass_ANDROID, &inputBridgeMethod_ANDROID);
        
        isPrepareGrabPos = true;
    } else {
        firstJavaVM = runtimeJavaVMPtr;
        firstJNIEnv = runtimeJNIEnvPtr_JRE;
        secondJavaVM = dalvikJavaVMPtr;
        
        attachThreadIfNeed(&isRuntimeThreadAttached, &dalvikJNIEnvPtr_JRE);
        getJavaInputBridge(&inputBridgeClass_JRE, &inputBridgeMethod_JRE);
    }
}

// TODO deprecate if implement fully callback in native
JNIEXPORT void JNICALL Java_org_lwjgl_glfw_CallbackBridge_nativeSendData(JNIEnv* env, jclass clazz, jboolean isAndroid, jint type, jstring data) {
    if (isAndroid == JNI_TRUE) {
        firstJavaVM = dalvikJavaVMPtr;
        firstJNIEnv = dalvikJNIEnvPtr_ANDROID;
        secondJavaVM = runtimeJavaVMPtr;
        
        attachThreadIfNeed(&isAndroidThreadAttached, &runtimeJNIEnvPtr_ANDROID);
        getJavaInputBridge(&inputBridgeClass_ANDROID, &inputBridgeMethod_ANDROID);
    } else {
        firstJavaVM = runtimeJavaVMPtr;
        firstJNIEnv = runtimeJNIEnvPtr_JRE;
        secondJavaVM = dalvikJavaVMPtr;
        
        attachThreadIfNeed(&isRuntimeThreadAttached, &dalvikJNIEnvPtr_JRE);
        getJavaInputBridge(&inputBridgeClass_JRE, &inputBridgeMethod_JRE);
    }
    
    // printf("isAndroid=%p, isSecondJVMNull=%p\n", isAndroid, secondJavaVM == NULL);
    
    if (secondJavaVM != NULL) {
        char *data_c = (char*)(*env)->GetStringUTFChars(env, data, 0);
        // printf("data=%s\n", data_c);
        jstring data_jre = (*secondJNIEnv)->NewStringUTF(secondJNIEnv, data_c);
        (*env)->ReleaseStringUTFChars(env, data, data_c);
        (*secondJNIEnv)->CallStaticVoidMethod(
            secondJNIEnv,
            isAndroid == JNI_TRUE ? inputBridgeClass_ANDROID : inputBridgeClass_JRE,
            isAndroid == JNI_TRUE ? inputBridgeMethod_ANDROID : inputBridgeMethod_JRE,
            type,
            data_jre
        );
    }
    // else: too early!
}

JNIEXPORT void JNICALL Java_org_lwjgl_glfw_CallbackBridge_nativeSetGrabbing(JNIEnv* env, jclass clazz, jboolean grabbing) {
    isGrabbing = grabbing;
}

JNIEXPORT jboolean JNICALL Java_org_lwjgl_glfw_CallbackBridge_nativeIsGrabbing(JNIEnv* env, jclass clazz) {
    return isGrabbing;
}

#define ADD_CALLBACK_WWIN(NAME) \
GLFW_invoke_##NAME##_func* GLFW_invoke_##NAME; \
JNIEXPORT jlong JNICALL Java_org_lwjgl_glfw_GLFW_nglfwSet##NAME##Callback(JNIEnv * env, jclass cls, jlong window, jlong callbackptr) { \
    void** oldCallback = &GLFW_invoke_##NAME; \
    GLFW_invoke_##NAME = (GLFW_invoke_##NAME##_func*) (uintptr_t) callbackptr; \
    return (jlong) (uintptr_t) *oldCallback; \
}

ADD_CALLBACK_WWIN(Char);
ADD_CALLBACK_WWIN(CharMods);
ADD_CALLBACK_WWIN(CursorEnter);
ADD_CALLBACK_WWIN(CursorPos);
ADD_CALLBACK_WWIN(FramebufferSize);
ADD_CALLBACK_WWIN(Key);
ADD_CALLBACK_WWIN(MouseButton);
ADD_CALLBACK_WWIN(Scroll);
ADD_CALLBACK_WWIN(WindowSize);

#undef ADD_CALLBACK_WWIN

JNIEXPORT jboolean JNICALL Java_org_lwjgl_glfw_CallbackBridge_nativeSendChar(JNIEnv* env, jclass clazz, jint codepoint) {
    if (GLFW_invoke_Char && isInputReady) {
        GLFW_invoke_Char(showingWindow, codepoint);
        return JNI_TRUE;
    }
    return JNI_FALSE;
}

JNIEXPORT jboolean JNICALL Java_org_lwjgl_glfw_CallbackBridge_nativeSendCharMods(JNIEnv* env, jclass clazz, jint codepoint, jint mods) {
    if (GLFW_invoke_CharMods && isInputReady) {
        GLFW_invoke_CharMods(showingWindow, codepoint, mods);
        return JNI_TRUE;
    }
    return JNI_FALSE;
}
/*
JNIEXPORT void JNICALL Java_org_lwjgl_glfw_CallbackBridge_nativeSendCursorEnter(JNIEnv* env, jclass clazz, jint entered) {
    if (GLFW_invoke_CursorEnter && isInputReady) {
        GLFW_invoke_CursorEnter(showingWindow, entered);
    }
}
*/
JNIEXPORT void JNICALL Java_org_lwjgl_glfw_CallbackBridge_nativeSendCursorPos(JNIEnv* env, jclass clazz, jint x, jint y) {
    if (GLFW_invoke_CursorPos && isInputReady) {
        if (GLFW_invoke_CursorEnter && !isCursorEntered) {
            isCursorEntered = true;
            GLFW_invoke_CursorEnter(showingWindow, 1);
        }
        
        if (isGrabbing) {
            if (!isPrepareGrabPos) {
                grabCursorX += x - lastCursorX;
                grabCursorY += y - lastCursorY;
            } else {
                isPrepareGrabPos = false;
                lastCursorX = x;
                lastCursorY = y;
                return;
            }
        }
        GLFW_invoke_CursorPos(showingWindow, (double) (isGrabbing ? grabCursorX : x), (double) (isGrabbing ? grabCursorY : y));
        lastCursorX = x;
        lastCursorY = y;
    }
}

JNIEXPORT void JNICALL Java_org_lwjgl_glfw_CallbackBridge_nativeSendFramebufferSize(JNIEnv* env, jclass clazz, jint width, jint height) {
    if (GLFW_invoke_FramebufferSize && isInputReady) {
        GLFW_invoke_FramebufferSize(showingWindow, width, height);
    }
}

JNIEXPORT void JNICALL Java_org_lwjgl_glfw_CallbackBridge_nativeSendKey(JNIEnv* env, jclass clazz, jint key, jint scancode, jint action, jint mods) {
    if (GLFW_invoke_Key && isInputReady) {
        GLFW_invoke_Key(showingWindow, key, scancode, action, mods);
    }
}

JNIEXPORT void JNICALL Java_org_lwjgl_glfw_CallbackBridge_nativeSendMouseButton(JNIEnv* env, jclass clazz, jint button, jint action, jint mods) {
    if (isInputReady) {
        if (button == -1) {
            // Notify to prepare set new grab pos
            isPrepareGrabPos = true;
        } else if (GLFW_invoke_MouseButton) {
            GLFW_invoke_MouseButton(showingWindow, button, action, mods);
        }
    }
}

JNIEXPORT void JNICALL Java_org_lwjgl_glfw_CallbackBridge_nativeSendScroll(JNIEnv* env, jclass clazz, jdouble xoffset, jdouble yoffset) {
    if (GLFW_invoke_Scroll && isInputReady) {
        GLFW_invoke_Scroll(showingWindow, (double) xoffset, (double) yoffset);
    }
}

JNIEXPORT void JNICALL Java_org_lwjgl_glfw_CallbackBridge_nativeSendWindowSize(JNIEnv* env, jclass clazz, jint width, jint height) {
    if (GLFW_invoke_WindowSize && isInputReady) {
        GLFW_invoke_WindowSize(showingWindow, width, height);
    }
}

JNIEXPORT void JNICALL Java_org_lwjgl_glfw_GLFW_nglfwSetShowingWindow(JNIEnv* env, jclass clazz, jlong window) {
    showingWindow = (long) window;
}

/*
JNIEXPORT jlong JNICALL Java_org_lwjgl_glfw_GLFW_nglfwSetCharCallback(JNIEnv * env, jclass cls, jlong window, jlong callbackptr) {
    void* oldCallback = &GLFW_invoke_Char;
    GLFW_invoke_Char = (GLFW_invoke_Char_func*) (uintptr_t) callbackptr;
    return (jlong) (uintptr_t) *oldCallback;
}

JNIEXPORT jlong JNICALL Java_org_lwjgl_glfw_GLFW_nglfwSetCharModsCallback(JNIEnv * env, jclass cls, jlong window, jlong callbackptr) {
    void* oldCallback = &GLFW_invoke_CharMods;
    GLFW_invoke_CharMods = (GLFW_invoke_CharMods_func*) (uintptr_t) callbackptr;
    return (jlong) (uintptr_t) *oldCallback;
}

JNIEXPORT jlong JNICALL Java_org_lwjgl_glfw_GLFW_nglfwSetCursorEnterCallback(JNIEnv * env, jclass cls, jlong window, jlong callbackptr) {
    void* oldCallback = &GLFW_invoke_CursorEnter;
    GLFW_invoke_CursorEnter = (GLFW_invoke_CursorEnter_func*) (uintptr_t) callbackptr;
    return (jlong) (uintptr_t) *oldCallback;
}

JNIEXPORT jlong JNICALL Java_org_lwjgl_glfw_GLFW_nglfwSetCursorPosCallback(JNIEnv * env, jclass cls, jlong window, jlong callbackptr) {
    void* oldCallback = &GLFW_invoke_CursorPos;
    GLFW_invoke_CursorPos = (GLFW_invoke_CursorPos_func*) (uintptr_t) callbackptr;
    return (jlong) (uintptr_t) *oldCallback;
}

JNIEXPORT jlong JNICALL Java_org_lwjgl_glfw_GLFW_nglfwSetFramebufferSizeCallback(JNIEnv * env, jclass cls, jlong window, jlong callbackptr) {
    void* oldCallback = &GLFW_invoke_FramebufferSize;
    GLFW_invoke_FramebufferSize = (GLFW_invoke_FramebufferSize_func*) (uintptr_t) callbackptr;
    return (jlong) (uintptr_t) *oldCallback;
}

JNIEXPORT jlong JNICALL Java_org_lwjgl_glfw_GLFW_nglfwSetKeyCallback(JNIEnv * env, jclass cls, jlong window, jlong callbackptr) {
    void* oldCallback = &GLFW_invoke_Key;
    GLFW_invoke_Key = (GLFW_invoke_Key_func*) (uintptr_t) callbackptr;
    return (jlong) (uintptr_t) *oldCallback;
}

JNIEXPORT jlong JNICALL Java_org_lwjgl_glfw_GLFW_nglfwSetMouseButtonCallback(JNIEnv * env, jclass cls, jlong window, jlong callbackptr) {
    void* oldCallback = &GLFW_invoke_MouseButton;
    GLFW_invoke_MouseButton = (GLFW_invoke_MouseButton_func*) (uintptr_t) callbackptr;
    return (jlong) (uintptr_t) *oldCallback;
}

JNIEXPORT jlong JNICALL Java_org_lwjgl_glfw_GLFW_nglfwSetScrollCallback(JNIEnv * env, jclass cls, jlong window, jlong callbackptr) {
    void* oldCallback = &GLFW_invoke_Scroll;
    GLFW_invoke_Scroll = (GLFW_invoke_Scroll_func*) (uintptr_t) callbackptr;
    return (jlong) (uintptr_t) *oldCallback;
}

JNIEXPORT jlong JNICALL Java_org_lwjgl_glfw_GLFW_nglfwSetWindowSizeCallback(JNIEnv * env, jclass cls, jlong window, jlong callbackptr) {
    void* oldCallback = &GLFW_invoke_WindowSize;
    GLFW_invoke_WindowSize = (GLFW_invoke_WindowSize_func*) (uintptr_t) callbackptr;
    return (jlong) (uintptr_t) *oldCallback;
}
*/

/*
 * Class:     org_lwjgl_glfw_GLFW
 * Method:    nglfwSetInputReady
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_lwjgl_glfw_GLFW_nglfwSetInputReady(JNIEnv *env, jclass cls) {
    isInputReady = true;
}

