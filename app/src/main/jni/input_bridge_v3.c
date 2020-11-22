/*
 * V3 input bridge implementation.
 *
 * Status:
 * - Active development
 * - Works with some bugs:
 *  + Modded versions gives broken stuff.
 *  + Broken text input on MC 1.12.2 and below.
 */
 
#include <jni.h>
#include <assert.h>

#include "log.h"
#include "utils.h"

#define EVENT_TYPE_CHAR 1000
#define EVENT_TYPE_CHAR_MODS 1001
#define EVENT_TYPE_CURSOR_ENTER 1002
#define EVENT_TYPE_CURSOR_POS 1003
#define EVENT_TYPE_FRAMEBUFFER_SIZE 1004
#define EVENT_TYPE_KEY 1005
#define EVENT_TYPE_MOUSE_BUTTON 1006
#define EVENT_TYPE_SCROLL 1007
#define EVENT_TYPE_WINDOW_SIZE 1008

typedef void GLFW_invoke_Char_func(void* window, unsigned int codepoint);
typedef void GLFW_invoke_CharMods_func(void* window, unsigned int codepoint, int mods);
typedef void GLFW_invoke_CursorEnter_func(void* window, int entered);
typedef void GLFW_invoke_CursorPos_func(void* window, double xpos, double ypos);
typedef void GLFW_invoke_FramebufferSize_func(void* window, int width, int height);
typedef void GLFW_invoke_Key_func(void* window, int key, int scancode, int action, int mods);
typedef void GLFW_invoke_MouseButton_func(void* window, int button, int action, int mods);
typedef void GLFW_invoke_Scroll_func(void* window, double xoffset, double yoffset);
typedef void GLFW_invoke_WindowSize_func(void* window, int width, int height);

int grabCursorX, grabCursorY, lastCursorX, lastCursorY;

jclass inputBridgeClass_ANDROID, inputBridgeClass_JRE;
jmethodID inputBridgeMethod_ANDROID, inputBridgeMethod_JRE;

jboolean isGrabbing;

jint JNI_OnLoad(JavaVM* vm, void* reserved) {
    if (dalvikJavaVMPtr == NULL) {
        //Save dalvik global JavaVM pointer
        dalvikJavaVMPtr = vm;
        (*vm)->GetEnv(vm, (void**) &dalvikJNIEnvPtr_ANDROID, JNI_VERSION_1_4);
        
        isUseStackQueueCall = JNI_FALSE;
    } else if (dalvikJavaVMPtr != vm) {
        runtimeJavaVMPtr = vm;
        (*vm)->GetEnv(vm, (void**) &runtimeJNIEnvPtr_JRE, JNI_VERSION_1_4);
    }
    
    isGrabbing = JNI_FALSE;
    
    return JNI_VERSION_1_4;
}

// Should be?
void JNI_OnUnload(JavaVM* vm, void* reserved) {
/*
    if (dalvikJavaVMPtr == vm) {
    } else {
    }
    
    DetachCurrentThread(vm);
*/

    dalvikJNIEnvPtr_JRE = NULL;
    runtimeJNIEnvPtr_ANDROID = NULL;
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

jboolean attachThread(bool isAndroid, JNIEnv** secondJNIEnvPtr) {
#ifdef DEBUG
    LOGD("Debug: Attaching %s thread to %s, javavm.isNull=%d\n", isAndroid ? "Android" : "JRE", isAndroid ? "JRE" : "Android", (isAndroid ? runtimeJavaVMPtr : dalvikJavaVMPtr) == NULL);
#endif

    if (*secondJNIEnvPtr != NULL || (!isUseStackQueueCall)) return JNI_TRUE;

    if (isAndroid && runtimeJavaVMPtr) {
        (*runtimeJavaVMPtr)->AttachCurrentThread(runtimeJavaVMPtr, secondJNIEnvPtr, NULL);
        return JNI_TRUE;
    } else if (!isAndroid && dalvikJavaVMPtr) {
        (*dalvikJavaVMPtr)->AttachCurrentThread(dalvikJavaVMPtr, secondJNIEnvPtr, NULL);
        return JNI_TRUE;
    }
    
    return JNI_FALSE;
}

void getJavaInputBridge(jclass* clazz, jmethodID* method) {
#ifdef DEBUG
    LOGD("Debug: Initializing input bridge, method.isNull=%d, jnienv.isNull=%d\n", *method == NULL, runtimeJNIEnvPtr_ANDROID == NULL);
#endif
    if (*method == NULL && runtimeJNIEnvPtr_ANDROID != NULL) {
        *clazz = (*runtimeJNIEnvPtr_ANDROID)->FindClass(runtimeJNIEnvPtr_ANDROID, "org/lwjgl/glfw/CallbackBridge");
        assert(*clazz != NULL);
        *method = (*runtimeJNIEnvPtr_ANDROID)->GetStaticMethodID(runtimeJNIEnvPtr_ANDROID, *clazz, "receiveCallback", "(IIIII)V");
        assert(*method != NULL);
    }
}

void sendData(int type, int i1, int i2, int i3, int i4) {
#ifdef DEBUG
    LOGD("Debug: Send data, jnienv.isNull=%d\n", runtimeJNIEnvPtr_ANDROID == NULL);
#endif
    if (runtimeJNIEnvPtr_ANDROID == NULL) {
        LOGE("BUG: Input is ready but thread is not attached yet.");
        return;
    }
    (*runtimeJNIEnvPtr_ANDROID)->CallStaticVoidMethod(
        runtimeJNIEnvPtr_ANDROID,
        inputBridgeClass_ANDROID,
        inputBridgeMethod_ANDROID,
        type,
        i1, i2, i3, i4
    );
}

JNIEXPORT jboolean JNICALL Java_org_lwjgl_glfw_CallbackBridge_nativeAttachThreadToOther(JNIEnv* env, jclass clazz, jboolean isAndroid, jboolean isUseStackQueueBool) {
#ifdef DEBUG
    LOGD("Debug: JNI attaching thread, isUseStackQueue=%d\n", isUseStackQueue);
#endif

    jboolean result;

    isUseStackQueueCall = (int) isUseStackQueueBool;
    if (isAndroid) {
        result = attachThread(true, &runtimeJNIEnvPtr_ANDROID);
    } else {
        result = attachThread(false, &dalvikJNIEnvPtr_JRE);
        // getJavaInputBridge(&inputBridgeClass_JRE, &inputBridgeMethod_JRE);
    }
    
    if (isUseStackQueueCall && isAndroid && result) {
        isPrepareGrabPos = true;
        getJavaInputBridge(&inputBridgeClass_ANDROID, &inputBridgeMethod_ANDROID);
    }
    
    return result;
}

JNIEXPORT jstring JNICALL Java_org_lwjgl_glfw_CallbackBridge_nativeClipboard(JNIEnv* env, jclass clazz, jint action, jstring copy) {
#ifdef DEBUG
    LOGD("Debug: Clipboard access is going on\n", isUseStackQueueCall);
#endif
    // TODO: if crash here, then convert jstring to jstring (diff JVM)
    jclass bridgeClazz = (*dalvikJNIEnvPtr_JRE)->FindClass(dalvikJNIEnvPtr_JRE, "org/lwjgl/glfw/CallbackBridge");
    assert(bridgeClazz != NULL);
    jmethodID bridgeMethod = (*dalvikJNIEnvPtr_JRE)->GetStaticMethodID(dalvikJNIEnvPtr_JRE, bridgeClazz, "accessAndroidClipboard", "(ILjava/lang/String;)Ljava/lang/String;");
    assert(bridgeMethod != NULL);
    
    return (jstring) (*dalvikJNIEnvPtr_JRE)->CallStaticObjectMethod(dalvikJNIEnvPtr_JRE, bridgeClazz, bridgeMethod, action, copy);
}

JNIEXPORT jboolean JNICALL Java_org_lwjgl_glfw_CallbackBridge_nativeSetInputReady(JNIEnv* env, jclass clazz, jboolean inputReady) {
#ifdef DEBUG
    LOGD("Debug: Changing input state, isReady=%d, isUseStackQueueCall=%d\n", inputReady, isUseStackQueueCall);
#endif
    isInputReady = inputReady;
    return isUseStackQueueCall;
}

JNIEXPORT void JNICALL Java_org_lwjgl_glfw_CallbackBridge_nativeSetGrabbing(JNIEnv* env, jclass clazz, jboolean grabbing) {
    isGrabbing = grabbing;
}

JNIEXPORT jboolean JNICALL Java_org_lwjgl_glfw_CallbackBridge_nativeIsGrabbing(JNIEnv* env, jclass clazz) {
    return isGrabbing;
}

JNIEXPORT jboolean JNICALL Java_org_lwjgl_glfw_CallbackBridge_nativeSendChar(JNIEnv* env, jclass clazz, jint codepoint) {
    if (GLFW_invoke_Char && isInputReady) {
        if (isUseStackQueueCall) {
            sendData(EVENT_TYPE_CHAR, codepoint, 0, 0, 0);
        } else {
            GLFW_invoke_Char(showingWindow, (unsigned int) codepoint);
        }
        return JNI_TRUE;
    }
    return JNI_FALSE;
}

JNIEXPORT jboolean JNICALL Java_org_lwjgl_glfw_CallbackBridge_nativeSendCharMods(JNIEnv* env, jclass clazz, jint codepoint, jint mods) {
    if (GLFW_invoke_CharMods && isInputReady) {
        if (isUseStackQueueCall) {
            sendData(EVENT_TYPE_CHAR_MODS, (unsigned int) codepoint, mods, 0, 0);
        } else {
            GLFW_invoke_CharMods(showingWindow, codepoint, mods);
        }
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
        if (!isCursorEntered) {
            if (GLFW_invoke_CursorEnter) {
                isCursorEntered = true;
                if (isUseStackQueueCall) {
                    sendData(EVENT_TYPE_CURSOR_ENTER, 1, 0, 0, 0);
                } else {
                    GLFW_invoke_CursorEnter(showingWindow, 1);
                }
            } else if (isGrabbing) {
                // Some Minecraft versions does not use GLFWCursorEnterCallback
                // This is a smart check, as Minecraft will not in grab mode if already not.
                isCursorEntered = true;
            }
        }
        
        if (isGrabbing) {
            if (!isPrepareGrabPos) {
                grabCursorX += x - lastCursorX;
                grabCursorY += y - lastCursorY;
            }
            
            lastCursorX = x;
            lastCursorY = y;
            
            if (isPrepareGrabPos) {
                isPrepareGrabPos = false;
                return;
            }
        }
        
        if (!isUseStackQueueCall) {
            GLFW_invoke_CursorPos(showingWindow, (double) (isGrabbing ? grabCursorX : x), (double) (isGrabbing ? grabCursorY : y));
        } else {
            sendData(EVENT_TYPE_CURSOR_POS, (isGrabbing ? grabCursorX : x), (isGrabbing ? grabCursorY : y), 0, 0);
        }
        
        lastCursorX = x;
        lastCursorY = y;
    }
}

JNIEXPORT void JNICALL Java_org_lwjgl_glfw_CallbackBridge_nativeSendKey(JNIEnv* env, jclass clazz, jint key, jint scancode, jint action, jint mods) {
    if (GLFW_invoke_Key && isInputReady) {
        if (isUseStackQueueCall) {
            sendData(EVENT_TYPE_KEY, key, scancode, action, mods);
        } else {
            GLFW_invoke_Key(showingWindow, key, scancode, action, mods);
        }
    }
}

JNIEXPORT void JNICALL Java_org_lwjgl_glfw_CallbackBridge_nativeSendMouseButton(JNIEnv* env, jclass clazz, jint button, jint action, jint mods) {
    if (isInputReady) {
        if (button == -1) {
            // Notify to prepare set new grab pos
            isPrepareGrabPos = true;
        } else if (GLFW_invoke_MouseButton) {
            if (isUseStackQueueCall) {
                sendData(EVENT_TYPE_MOUSE_BUTTON, button, action, mods, 0);
            } else {
                GLFW_invoke_MouseButton(showingWindow, button, action, mods);
            }
        }
    }
}

JNIEXPORT void JNICALL Java_org_lwjgl_glfw_CallbackBridge_nativeSendScreenSize(JNIEnv* env, jclass clazz, jint width, jint height) {
    if (isInputReady) {
        if (GLFW_invoke_FramebufferSize) {
            if (isUseStackQueueCall) {
                sendData(EVENT_TYPE_FRAMEBUFFER_SIZE, width, height, 0, 0);
            } else {
                GLFW_invoke_FramebufferSize(showingWindow, width, height);
            }
        }
        
        if (GLFW_invoke_WindowSize) {
            if (isUseStackQueueCall) {
                sendData(EVENT_TYPE_WINDOW_SIZE, width, height, 0, 0);
            } else {
                GLFW_invoke_WindowSize(showingWindow, width, height);
            }
        }
    }
    
    // return (isInputReady && (GLFW_invoke_FramebufferSize || GLFW_invoke_WindowSize));
}

JNIEXPORT void JNICALL Java_org_lwjgl_glfw_CallbackBridge_nativeSendScroll(JNIEnv* env, jclass clazz, jdouble xoffset, jdouble yoffset) {
    if (GLFW_invoke_Scroll && isInputReady) {
        if (isUseStackQueueCall) {
            sendData(EVENT_TYPE_SCROLL, xoffset, yoffset, 0, 0);
        } else {
            GLFW_invoke_Scroll(showingWindow, (double) xoffset, (double) yoffset);
        }
    }
}

JNIEXPORT void JNICALL Java_org_lwjgl_glfw_GLFW_nglfwSetShowingWindow(JNIEnv* env, jclass clazz, jlong window) {
    showingWindow = (long) window;
}

