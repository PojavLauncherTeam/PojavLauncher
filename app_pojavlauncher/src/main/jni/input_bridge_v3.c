/*
 * V3 input bridge implementation.
 *
 * Status:
 * - Active development
 * - Works with some bugs:
 *  + Modded versions gives broken stuff..
 *
 * 
 * - Implements glfwSetCursorPos() to handle grab camera pos correctly.
 */
 
#include <assert.h>
#include <dlfcn.h>
#include <jni.h>
#include <libgen.h>
#include <stdlib.h>
#include <string.h>

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

jint (*orig_ProcessImpl_forkAndExec)(JNIEnv *env, jobject process, jint mode, jbyteArray helperpath, jbyteArray prog, jbyteArray argBlock, jint argc, jbyteArray envBlock, jint envc, jbyteArray dir, jintArray std_fds, jboolean redirectErrorStream);

typedef void GLFW_invoke_Char_func(void* window, unsigned int codepoint);
typedef void GLFW_invoke_CharMods_func(void* window, unsigned int codepoint, int mods);
typedef void GLFW_invoke_CursorEnter_func(void* window, int entered);
typedef void GLFW_invoke_CursorPos_func(void* window, double xpos, double ypos);
typedef void GLFW_invoke_FramebufferSize_func(void* window, int width, int height);
typedef void GLFW_invoke_Key_func(void* window, int key, int scancode, int action, int mods);
typedef void GLFW_invoke_MouseButton_func(void* window, int button, int action, int mods);
typedef void GLFW_invoke_Scroll_func(void* window, double xoffset, double yoffset);
typedef void GLFW_invoke_WindowSize_func(void* window, int width, int height);

static float grabCursorX, grabCursorY, lastCursorX, lastCursorY;

jclass inputBridgeClass_ANDROID, inputBridgeClass_JRE;
jmethodID inputBridgeMethod_ANDROID, inputBridgeMethod_JRE;
jclass bridgeClazz;
jboolean isGrabbing;

jint JNI_OnLoad(JavaVM* vm, void* reserved) {
    if (dalvikJavaVMPtr == NULL) {
        //Save dalvik global JavaVM pointer
        dalvikJavaVMPtr = vm;
        (*vm)->GetEnv(vm, (void**) &dalvikJNIEnvPtr_ANDROID, JNI_VERSION_1_4);
        bridgeClazz = (*dalvikJNIEnvPtr_ANDROID)->NewGlobalRef(dalvikJNIEnvPtr_ANDROID,(*dalvikJNIEnvPtr_ANDROID) ->FindClass(dalvikJNIEnvPtr_ANDROID,"org/lwjgl/glfw/CallbackBridge"));
        assert(bridgeClazz != NULL);
        isUseStackQueueCall = JNI_FALSE;
    } else if (dalvikJavaVMPtr != vm) {
        runtimeJavaVMPtr = vm;
        (*vm)->GetEnv(vm, (void**) &runtimeJNIEnvPtr_JRE, JNI_VERSION_1_4);
        hookExec();
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

    //dalvikJNIEnvPtr_JRE = NULL;
    runtimeJNIEnvPtr_ANDROID = NULL;
}

#define ADD_CALLBACK_WWIN(NAME) \
GLFW_invoke_##NAME##_func* GLFW_invoke_##NAME; \
JNIEXPORT jlong JNICALL Java_org_lwjgl_glfw_GLFW_nglfwSet##NAME##Callback(JNIEnv * env, jclass cls, jlong window, jlong callbackptr) { \
    void** oldCallback = (void**) &GLFW_invoke_##NAME; \
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

void sendData(int type, int i1, int i2, int i3, int i4) {
#ifdef DEBUG
    LOGD("Debug: Send data, jnienv.isNull=%d\n", runtimeJNIEnvPtr_ANDROID == NULL);
#endif
    if (runtimeJNIEnvPtr_ANDROID == NULL) {
        LOGE("BUG: Input is ready but thread is not attached yet.");
        return;
    }
    if(inputBridgeClass_ANDROID == NULL) return;
    (*runtimeJNIEnvPtr_ANDROID)->CallStaticVoidMethod(
        runtimeJNIEnvPtr_ANDROID,
        inputBridgeClass_ANDROID,
        inputBridgeMethod_ANDROID,
        type,
        i1, i2, i3, i4
    );
}

void closeGLFWWindow() {
    /*
    jclass glfwClazz = (*runtimeJNIEnvPtr_JRE)->FindClass(runtimeJNIEnvPtr_JRE, "org/lwjgl/glfw/GLFW");
    assert(glfwClazz != NULL);
    jmethodID glfwMethod = (*runtimeJNIEnvPtr_JRE)->GetStaticMethodID(runtimeJNIEnvPtr_JRE, glfwMethod, "glfwSetWindowShouldClose", "(JZ)V");
    assert(glfwMethod != NULL);
    
    (*runtimeJNIEnvPtr_JRE)->CallStaticVoidMethod(
        runtimeJNIEnvPtr_JRE,
        glfwClazz, glfwMethod,
        (jlong) showingWindow, JNI_TRUE
    );
    */
    exit(-1);
}

/**
 * Hooked version of java.lang.UNIXProcess.forkAndExec()
 * which is used to handle the "open" command.
 */
jint
hooked_ProcessImpl_forkAndExec(JNIEnv *env, jobject process, jint mode, jbyteArray helperpath, jbyteArray prog, jbyteArray argBlock, jint argc, jbyteArray envBlock, jint envc, jbyteArray dir, jintArray std_fds, jboolean redirectErrorStream) {
    char *pProg = (char *)((*env)->GetByteArrayElements(env, prog, NULL));

    // Here we only handle the "xdg-open" command
    if (strcmp(basename(pProg), "xdg-open")) {
        (*env)->ReleaseByteArrayElements(env, prog, (jbyte *)pProg, 0);
        return orig_ProcessImpl_forkAndExec(env, process, mode, helperpath, prog, argBlock, argc, envBlock, envc, dir, std_fds, redirectErrorStream);
    }
    (*env)->ReleaseByteArrayElements(env, prog, (jbyte *)pProg, 0);

    Java_org_lwjgl_glfw_CallbackBridge_nativeClipboard(env, NULL, /* CLIPBOARD_OPEN */ 2002, argBlock);
    return 0;
}

void hookExec() {
    jclass cls;
    orig_ProcessImpl_forkAndExec = dlsym(RTLD_DEFAULT, "Java_java_lang_UNIXProcess_forkAndExec");
    if (!orig_ProcessImpl_forkAndExec) {
        orig_ProcessImpl_forkAndExec = dlsym(RTLD_DEFAULT, "Java_java_lang_ProcessImpl_forkAndExec");
        cls = (*runtimeJNIEnvPtr_JRE)->FindClass(runtimeJNIEnvPtr_JRE, "java/lang/ProcessImpl");
    } else {
        cls = (*runtimeJNIEnvPtr_JRE)->FindClass(runtimeJNIEnvPtr_JRE, "java/lang/UNIXProcess");
    }
    JNINativeMethod methods[] = {
        {"forkAndExec", "(I[B[B[BI[BI[B[IZ)I", (void *)&hooked_ProcessImpl_forkAndExec}
    };
    (*runtimeJNIEnvPtr_JRE)->RegisterNatives(runtimeJNIEnvPtr_JRE, cls, methods, 1);
    printf("Registered forkAndExec\n");
}

JNIEXPORT void JNICALL
Java_org_lwjgl_glfw_CallbackBridge_nativeSetUseInputStackQueue(JNIEnv *env, jclass clazz,
                                                               jboolean use_input_stack_queue) {
    isUseStackQueueCall = (int) use_input_stack_queue;
}

JNIEXPORT jboolean JNICALL Java_org_lwjgl_glfw_CallbackBridge_nativeAttachThreadToOther(JNIEnv* env, jclass clazz, jboolean isAndroid, jboolean isUseStackQueueBool) {
#ifdef DEBUG
    LOGD("Debug: JNI attaching thread, isUseStackQueue=%d\n", isUseStackQueueBool);
#endif

    jboolean result;

    //isUseStackQueueCall = (int) isUseStackQueueBool;
    if (isAndroid) {
        result = attachThread(true, &runtimeJNIEnvPtr_ANDROID);
    } /* else {
        result = attachThread(false, &dalvikJNIEnvPtr_JRE);
        // getJavaInputBridge(&inputBridgeClass_JRE, &inputBridgeMethod_JRE);
    } */
    
    if (isUseStackQueueCall && isAndroid && result) {
        isPrepareGrabPos = true;
    }
    return result;
}

JNIEXPORT jstring JNICALL Java_org_lwjgl_glfw_CallbackBridge_nativeClipboard(JNIEnv* env, jclass clazz, jint action, jbyteArray copySrc) {
#ifdef DEBUG
    LOGD("Debug: Clipboard access is going on\n", isUseStackQueueCall);
#endif

    JNIEnv *dalvikEnv;
    (*dalvikJavaVMPtr)->AttachCurrentThread(dalvikJavaVMPtr, &dalvikEnv, NULL);
    assert(dalvikEnv != NULL);
    assert(bridgeClazz != NULL);
    LOGD("Clipboard: Obtaining method\n");
    jmethodID bridgeMethod = (*dalvikEnv)->GetStaticMethodID(dalvikEnv, bridgeClazz, "accessAndroidClipboard", "(ILjava/lang/String;)Ljava/lang/String;");
    assert(bridgeMethod != NULL);
    
    LOGD("Clipboard: Converting string\n");
    char *copySrcC = NULL;
    jstring copyDst = NULL;
    if (copySrc) {
        copySrcC = (char *)((*env)->GetByteArrayElements(env, copySrc, NULL));
        copyDst = (*dalvikEnv)->NewStringUTF(dalvikEnv, copySrcC);
    }

    LOGD("Clipboard: Calling 2nd\n");
    jstring pasteDst = convertStringJVM(dalvikEnv, env, (jstring) (*dalvikEnv)->CallStaticObjectMethod(dalvikEnv, bridgeClazz, bridgeMethod, action, copyDst));

    if (copySrc) {
        (*dalvikEnv)->DeleteLocalRef(dalvikEnv, copyDst);    
        (*env)->ReleaseByteArrayElements(env, copySrc, (jbyte *)copySrcC, 0);
    }
    (*dalvikJavaVMPtr)->DetachCurrentThread(dalvikJavaVMPtr);
    return pasteDst;
}

JNIEXPORT jboolean JNICALL Java_org_lwjgl_glfw_CallbackBridge_nativeSetInputReady(JNIEnv* env, jclass clazz, jboolean inputReady) {
#ifdef DEBUG
    LOGD("Debug: Changing input state, isReady=%d, isUseStackQueueCall=%d\n", inputReady, isUseStackQueueCall);
#endif
    isInputReady = inputReady;
    return isUseStackQueueCall;
}

JNIEXPORT void JNICALL Java_org_lwjgl_glfw_CallbackBridge_nativeSetGrabbing(JNIEnv* env, jclass clazz, jboolean grabbing, jint xset, jint yset) {
    isGrabbing = grabbing;
    if (isGrabbing == JNI_TRUE) {
        grabCursorX = xset; // savedWidth / 2;
        grabCursorY = yset; // savedHeight / 2;
        isPrepareGrabPos = true;
    }
}

JNIEXPORT jboolean JNICALL Java_org_lwjgl_glfw_CallbackBridge_nativeIsGrabbing(JNIEnv* env, jclass clazz) {
    return isGrabbing;
}

JNIEXPORT jboolean JNICALL Java_org_lwjgl_glfw_CallbackBridge_nativeSendChar(JNIEnv* env, jclass clazz, jchar codepoint /* jint codepoint */) {
    if (GLFW_invoke_Char && isInputReady) {
        if (isUseStackQueueCall) {
            sendData(EVENT_TYPE_CHAR, codepoint, 0, 0, 0);
        } else {
            GLFW_invoke_Char((void*) showingWindow, (unsigned int) codepoint);
            // return lwjgl2_triggerCharEvent(codepoint);
        }
        return JNI_TRUE;
    }
    return JNI_FALSE;
}

JNIEXPORT jboolean JNICALL Java_org_lwjgl_glfw_CallbackBridge_nativeSendCharMods(JNIEnv* env, jclass clazz, jchar codepoint, jint mods) {
    if (GLFW_invoke_CharMods && isInputReady) {
        if (isUseStackQueueCall) {
            sendData(EVENT_TYPE_CHAR_MODS, (unsigned int) codepoint, mods, 0, 0);
        } else {
            GLFW_invoke_CharMods((void*) showingWindow, codepoint, mods);
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
JNIEXPORT void JNICALL Java_org_lwjgl_glfw_CallbackBridge_nativeSendCursorPos(JNIEnv* env, jclass clazz, jfloat x, jfloat y) {
#ifdef DEBUG
    LOGD("Sending cursor position \n");
#endif
    if (GLFW_invoke_CursorPos && isInputReady) {
#ifdef DEBUG
        LOGD("GLFW_invoke_CursorPos && isInputReady \n");
#endif
        if (!isCursorEntered) {
            if (GLFW_invoke_CursorEnter) {
                isCursorEntered = true;
                if (isUseStackQueueCall) {
                    sendData(EVENT_TYPE_CURSOR_ENTER, 1, 0, 0, 0);
                } else {
                    GLFW_invoke_CursorEnter((void*) showingWindow, 1);
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
            GLFW_invoke_CursorPos((void*) showingWindow, (double) (x), (double) (y));
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
            GLFW_invoke_Key((void*) showingWindow, key, scancode, action, mods);
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
                GLFW_invoke_MouseButton((void*) showingWindow, button, action, mods);
            }
        }
    }
}

JNIEXPORT void JNICALL Java_org_lwjgl_glfw_CallbackBridge_nativeSendScreenSize(JNIEnv* env, jclass clazz, jint width, jint height) {
    savedWidth = width;
    savedHeight = height;
    
    if (isInputReady) {
        if (GLFW_invoke_FramebufferSize) {
            if (isUseStackQueueCall) {
                sendData(EVENT_TYPE_FRAMEBUFFER_SIZE, width, height, 0, 0);
            } else {
                GLFW_invoke_FramebufferSize((void*) showingWindow, width, height);
            }
        }
        
        if (GLFW_invoke_WindowSize) {
            if (isUseStackQueueCall) {
                sendData(EVENT_TYPE_WINDOW_SIZE, width, height, 0, 0);
            } else {
                GLFW_invoke_WindowSize((void*) showingWindow, width, height);
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
            GLFW_invoke_Scroll((void*) showingWindow, (double) xoffset, (double) yoffset);
        }
    }
}

JNIEXPORT void JNICALL Java_org_lwjgl_glfw_GLFW_nglfwSetShowingWindow(JNIEnv* env, jclass clazz, jlong window) {
    showingWindow = (long) window;
}

JNIEXPORT void JNICALL Java_org_lwjgl_glfw_CallbackBridge_nativeSetWindowAttrib(JNIEnv* env, jclass clazz, jint attrib, jint value) {
    if (!showingWindow || !isUseStackQueueCall) {
        // If the window is not shown, there is nothing to do yet.
        // For Minecraft < 1.13, calling to JNI functions here crashes the JVM for some reason, therefore it is skipped for now.
        return;
    }

    jclass glfwClazz = (*runtimeJNIEnvPtr_JRE)->FindClass(runtimeJNIEnvPtr_JRE, "org/lwjgl/glfw/GLFW");
    assert(glfwClazz != NULL);
    jmethodID glfwMethod = (*runtimeJNIEnvPtr_JRE)->GetStaticMethodID(runtimeJNIEnvPtr_JRE, glfwClazz, "glfwSetWindowAttrib", "(JII)V");
    assert(glfwMethod != NULL);

    (*runtimeJNIEnvPtr_JRE)->CallStaticVoidMethod(
        runtimeJNIEnvPtr_JRE,
        glfwClazz, glfwMethod,
        (jlong) showingWindow, attrib, value
    );
}

JNIEXPORT void JNICALL
Java_org_lwjgl_glfw_CallbackBridge_setClass(JNIEnv *env, jclass clazz) {
    inputBridgeMethod_ANDROID = (*env)->GetStaticMethodID(env, clazz, "receiveCallback", "(IIIII)V");
    inputBridgeClass_ANDROID = (*env)->NewGlobalRef(env, clazz);
}
