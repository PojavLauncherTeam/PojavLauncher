#include <jni.h>
#include <assert.h>

#include "log.h"
#include "utils.h"

int grabCursorX, grabCursorY, lastCursorX, lastCursorY;

JavaVM* firstJavaVM;
JavaVM* secondJavaVM;

JNIEnv* firstJNIEnv;
JNIEnv* secondJNIEnv;

jclass inputBridgeClass_ANDROID, inputBridgeClass_JRE;
jmethodID inputBridgeMethod_ANDROID, inputBridgeMethod_JRE;

jboolean isGrabbing, isUseStackQueueCall;

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
void JNI_OnUnload(JavaVM* vm, void* reserved) {
/*
    if (dalvikJavaVMPtr == vm) {
    } else {
    }
    
    DetachCurrentThread(vm);
*/

    free(glfwInputEventArr);
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

void invokeCursorPos(int x, int y) {
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
    if (!isUseStackQueueCall && GLFW_invoke_CursorPos) {
        GLFW_invoke_CursorPos(showingWindow, (double) (isGrabbing ? grabCursorX : x), (double) (isGrabbing ? grabCursorY : y));
    }
    lastCursorX = x;
    lastCursorY = y;
}

void addInputToQueue(GLFWInputEvent event) {
    if (glfwInputEventIndex++ >= 100) {
        // player type too fast? or fps lower than player tps?
        glfwInputEventIndex = 0;
    }
    glfwInputEventArr[glfwInputEventIndex] = &event;
}

JNIEXPORT void JNICALL Java_org_lwjgl_glfw_CallbackBridge_nativeAttachThreadToOther(JNIEnv* env, jclass clazz, jboolean isAndroid, jboolean isUseStackQueue) {
    glfwInputEventIndex = -1;
    // isUseStackQueueCall = 1;
    isUseStackQueueCall = (int) isUseStackQueue;
    if (isUseStackQueue) {
        isPrepareGrabPos = true;
    } else if (isAndroid) {
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

JNIEXPORT void JNICALL Java_org_lwjgl_glfw_CallbackBridge_nativeSetGrabbing(JNIEnv* env, jclass clazz, jboolean grabbing) {
    isGrabbing = grabbing;
}

JNIEXPORT jboolean JNICALL Java_org_lwjgl_glfw_CallbackBridge_nativeIsGrabbing(JNIEnv* env, jclass clazz) {
    return isGrabbing;
}

int diffX, diffY, diffGrabX, diffGrabY, debugTimes;
JNIEXPORT void JNICALL Java_org_lwjgl_glfw_GLFW_nglfwPollEvents(JNIEnv* env, jclass clazz) {
    if (!isInputReady) isInputReady = true;
/*
    if (debugTimes < 1000) {
        debugTimes++;
        LOGI("INPUT: IsUseStackQueue=%d, CurrentInputLength=%d, CursorX=%d, CursorY=%d", isUseStackQueueCall, glfwInputEventIndex, lastCursorX, lastCursorY);
    }
*/
    if (isUseStackQueueCall) {
        if (diffX != lastCursorX || diffY != lastCursorY) {
            diffX = lastCursorX;
            diffY = lastCursorY;
            
            if (GLFW_invoke_CursorPos) {
                GLFW_invoke_CursorPos(showingWindow,
                    isGrabbing ? grabCursorX : lastCursorX,
                    isGrabbing ? grabCursorY : lastCursorY);
            }
        }
        
        for (int i = 0; i <= glfwInputEventIndex; i++) {
            GLFWInputEvent curr = *glfwInputEventArr[i];
            ((GLFW_invoke_callback*) curr.trigger)(curr);
            
//            if (debugTimes < 1000) {
//                LOGI("INPUT: Got input event %d", curr.type);
//            }
/*
            switch (curr.type) {
                case EVENT_TYPE_CHAR:
                    if (GLFW_invoke_Char) {
                        GLFW_invoke_Char(showingWindow, curr.ui1);
                    }
                    break;
                case EVENT_TYPE_CHAR_MODS:
                    if (GLFW_invoke_CharMods) {
                        GLFW_invoke_CharMods(showingWindow, curr.ui1, curr.i2);
                    }
                    break;
                case EVENT_TYPE_CURSOR_ENTER:
                    if (GLFW_invoke_CursorEnter) {
                        GLFW_invoke_CursorEnter(showingWindow, curr.i1);
                    }
                    break;
                case EVENT_TYPE_FRAMEBUFFER_SIZE:
                    if (GLFW_invoke_FramebufferSize) {
                        GLFW_invoke_FramebufferSize(showingWindow, curr.i1, curr.i2);
                    }
                    break;
                case EVENT_TYPE_KEY:
                    if (GLFW_invoke_Key) {
                        GLFW_invoke_Key(showingWindow, curr.i1, curr.i2, curr.i3, curr.i4);
                    }
                    break;
                case EVENT_TYPE_MOUSE_BUTTON:
                    if (GLFW_invoke_MouseButton) {
                        GLFW_invoke_MouseButton(showingWindow, curr.i1, curr.i2, curr.i3);
                    }
                    break;
                case EVENT_TYPE_SCROLL:
                    if (GLFW_invoke_Scroll) {
                        GLFW_invoke_Scroll(showingWindow, curr.d1, curr.d2);
                    }
                    break;
                case EVENT_TYPE_WINDOW_SIZE:
                    if (GLFW_invoke_WindowSize) {
                        GLFW_invoke_WindowSize(showingWindow, curr.i1, curr.i2);
                    }
                    break;
                default:
                    LOGW("Unknown GLFW input event: %d", curr.type);
                    break;
            }
*/
        }
        glfwInputEventIndex = -1;
    }
}

JNIEXPORT jboolean JNICALL Java_org_lwjgl_glfw_CallbackBridge_nativeSendChar(JNIEnv* env, jclass clazz, jint codepoint) {
    if (GLFW_invoke_Char && isInputReady) {
        if (isUseStackQueueCall) {
            GLFWInputEvent curr;
            curr.trigger = triggerChar;
            curr.ui1 = (unsigned int) codepoint;
            addInputToQueue(curr);
        } else
            GLFW_invoke_Char(showingWindow, codepoint);
        return JNI_TRUE;
    }
    return JNI_FALSE;
}

JNIEXPORT jboolean JNICALL Java_org_lwjgl_glfw_CallbackBridge_nativeSendCharMods(JNIEnv* env, jclass clazz, jint codepoint, jint mods) {
    if (GLFW_invoke_CharMods && isInputReady) {
        if (isUseStackQueueCall) {
            GLFWInputEvent curr;
            curr.trigger = triggerCharMods;
            curr.ui1 = (unsigned int) codepoint;
            curr.i2 = mods;
            addInputToQueue(curr);
        } else
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
        if (!isCursorEntered) {
            if (GLFW_invoke_CursorEnter) {
                isCursorEntered = true;
                if (isUseStackQueueCall) {
                    GLFWInputEvent curr;
                    curr.trigger = triggerCursorEnter;
                    curr.i1 = 1;
                    addInputToQueue(curr);
                } else
                    GLFW_invoke_CursorEnter(showingWindow, 1);
            } else if (isGrabbing) {
                // Some Minecraft versions does not use GLFWCursorEnterCallback
                // This is a smart check, as Minecraft will not in grab mode if already not.
                isCursorEntered = true;
            }
        }
        
        invokeCursorPos(x, y);
    }
}

JNIEXPORT void JNICALL Java_org_lwjgl_glfw_CallbackBridge_nativeSendFramebufferSize(JNIEnv* env, jclass clazz, jint width, jint height) {
    if (GLFW_invoke_FramebufferSize && isInputReady) {
        if (isUseStackQueueCall) {
            GLFWInputEvent curr;
            curr.trigger = triggerFramebufferSize;
            curr.i1 = width;
            curr.i2 = height;
            addInputToQueue(curr);
        } else
            GLFW_invoke_FramebufferSize(showingWindow, width, height);
    }
}

JNIEXPORT void JNICALL Java_org_lwjgl_glfw_CallbackBridge_nativeSendKey(JNIEnv* env, jclass clazz, jint key, jint scancode, jint action, jint mods) {
    if (GLFW_invoke_Key && isInputReady) {
        if (isUseStackQueueCall) {
            GLFWInputEvent curr;
            curr.trigger = triggerKey;
            curr.i1 = key;
            curr.i2 = scancode;
            curr.i3 = action;
            curr.i4 = mods;
            addInputToQueue(curr);
        } else
            GLFW_invoke_Key(showingWindow, key, scancode, action, mods);
    }
}

JNIEXPORT void JNICALL Java_org_lwjgl_glfw_CallbackBridge_nativeSendMouseButton(JNIEnv* env, jclass clazz, jint button, jint action, jint mods) {
    if (isInputReady) {
        if (button == -1) {
            // Notify to prepare set new grab pos
            isPrepareGrabPos = true;
        } else if (GLFW_invoke_MouseButton) {
            if (isUseStackQueueCall) {
                GLFWInputEvent curr;
                curr.trigger = triggerMouseButton;
                curr.i1 = button;
                curr.i2 = action;
                curr.i3 = mods;
                addInputToQueue(curr);
            } else
                GLFW_invoke_MouseButton(showingWindow, button, action, mods);
        }
    }
}

JNIEXPORT void JNICALL Java_org_lwjgl_glfw_CallbackBridge_nativeSendScroll(JNIEnv* env, jclass clazz, jdouble xoffset, jdouble yoffset) {
    if (GLFW_invoke_Scroll && isInputReady) {
        if (isUseStackQueueCall) {
            GLFWInputEvent curr;
            curr.trigger = triggerScroll;
            curr.d1 = (double) xoffset;
            curr.d2 = (double) yoffset;
            addInputToQueue(curr);
        } else
            GLFW_invoke_Scroll(showingWindow, (double) xoffset, (double) yoffset);
    }
}

JNIEXPORT void JNICALL Java_org_lwjgl_glfw_CallbackBridge_nativeSendWindowSize(JNIEnv* env, jclass clazz, jint width, jint height) {
    if (GLFW_invoke_WindowSize && isInputReady) {
        if (isUseStackQueueCall) {
            GLFWInputEvent curr;
            curr.trigger = triggerWindowSize;
            curr.i1 = width;
            curr.i2 = height;
            addInputToQueue(curr);
        } else
            GLFW_invoke_WindowSize(showingWindow, width, height);
    }
}

JNIEXPORT void JNICALL Java_org_lwjgl_glfw_GLFW_nglfwSetShowingWindow(JNIEnv* env, jclass clazz, jlong window) {
    showingWindow = (long) window;
}

