#include <jni.h>
#include <assert.h>
#include <dlfcn.h>

#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>

#include <EGL/egl.h>

#ifdef GLES_TEST
#include <GLES2/gl2.h>
#endif

#include <android/native_window.h>
#include <android/native_window_jni.h>

#include "utils.h"

struct PotatoBridge {
	/* ANativeWindow */ void* androidWindow;
	    
	/* EGLContext */ void* eglContextOld;
	/* EGLContext */ void* eglContext;
	/* EGLDisplay */ void* eglDisplay;
	/* EGLSurface */ void* eglSurface;
/*
	void* eglSurfaceRead;
	void* eglSurfaceDraw;
*/
};
struct PotatoBridge potatoBridge;
EGLConfig config;

typedef jint RegalMakeCurrent_func(EGLContext context);

// Called from JNI_OnLoad of liblwjgl_opengl
void pojav_openGLOnLoad() {
	
}
void pojav_openGLOnUnload() {

}

void terminateEgl() {
    printf("EGLBridge: Terminating\n");
    eglMakeCurrent(potatoBridge.eglDisplay, EGL_NO_SURFACE, EGL_NO_SURFACE, EGL_NO_CONTEXT);
    eglDestroySurface(potatoBridge.eglDisplay, potatoBridge.eglSurface);
    eglDestroyContext(potatoBridge.eglDisplay, potatoBridge.eglContext);
    eglTerminate(potatoBridge.eglDisplay);
    eglReleaseThread();
    
    potatoBridge.eglContext = EGL_NO_CONTEXT;
    potatoBridge.eglDisplay = EGL_NO_DISPLAY;
    potatoBridge.eglSurface = EGL_NO_SURFACE;
}

JNIEXPORT void JNICALL Java_net_kdt_pojavlaunch_utils_JREUtils_setupBridgeWindow(JNIEnv* env, jclass clazz, jobject surface) {    
    potatoBridge.androidWindow = ANativeWindow_fromSurface(env, surface);   
}

JNIEXPORT jlong JNICALL Java_org_lwjgl_glfw_GLFW_nativeEglGetCurrentContext(JNIEnv* env, jclass clazz) {
    return eglGetCurrentContext();
}
static const EGLint ctx_attribs[] = {
        EGL_CONTEXT_CLIENT_VERSION, 2,
        EGL_NONE
};
JNIEXPORT jboolean JNICALL Java_org_lwjgl_glfw_GLFW_nativeEglInit(JNIEnv* env, jclass clazz) {
    if (potatoBridge.eglDisplay == NULL || potatoBridge.eglDisplay == EGL_NO_DISPLAY) {
        potatoBridge.eglDisplay = eglGetDisplay(EGL_DEFAULT_DISPLAY);
        if (potatoBridge.eglDisplay == EGL_NO_DISPLAY) {
            printf("EGLBridge: Error eglGetDefaultDisplay() failed: %p\n", eglGetError());
            return JNI_FALSE;
        }
    }

    printf("EGLBridge: Initializing\n");
    // printf("EGLBridge: ANativeWindow pointer = %p\n", potatoBridge.androidWindow);
    //(*env)->ThrowNew(env,(*env)->FindClass(env,"java/lang/Exception"),"Trace exception");
    if (!eglInitialize(potatoBridge.eglDisplay, NULL, NULL)) {
        printf("EGLBridge: Error eglInitialize() failed\n");
        return JNI_FALSE;
    }

    static const EGLint attribs[] = {
            EGL_RED_SIZE, 8,
            EGL_GREEN_SIZE, 8,
            EGL_BLUE_SIZE, 8,
            EGL_ALPHA_SIZE, 8,
            // Minecraft required on initial 24
            EGL_DEPTH_SIZE, 24,
            EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT,
            EGL_NONE
    };

    EGLint num_configs;
    EGLint vid;

    if (!eglChooseConfig(potatoBridge.eglDisplay, attribs, &config, 1, &num_configs)) {
        printf("EGLBridge: Error couldn't get an EGL visual config\n");
        return JNI_FALSE;
    }

    assert(config);
    assert(num_configs > 0);

    if (!eglGetConfigAttrib(potatoBridge.eglDisplay, config, EGL_NATIVE_VISUAL_ID, &vid)) {
        printf("EGLBridge: Error eglGetConfigAttrib() failed\n");
        return JNI_FALSE;
    }

    ANativeWindow_setBuffersGeometry(potatoBridge.androidWindow, 0, 0, vid);

    eglBindAPI(EGL_OPENGL_ES_API);

    potatoBridge.eglSurface = eglCreateWindowSurface(potatoBridge.eglDisplay, config, potatoBridge.androidWindow, NULL);

    if (!potatoBridge.eglSurface) {
        printf("EGLBridge: Error eglCreateWindowSurface failed: %p\n", eglGetError());
        //(*env)->ThrowNew(env,(*env)->FindClass(env,"java/lang/Exception"),"Trace exception");
        return JNI_FALSE;
    }

    // sanity checks
    {
        EGLint val;
        assert(eglGetConfigAttrib(potatoBridge.eglDisplay, config, EGL_SURFACE_TYPE, &val));
        assert(val & EGL_WINDOW_BIT);
    }


    printf("EGLBridge: Initialized!\n");
    printf("EGLBridge: ThreadID=%d\n", gettid());
    printf("EGLBridge: EGLDisplay=%p, EGLSurface=%p\n",
/* window==0 ? EGL_NO_CONTEXT : */
           potatoBridge.eglDisplay,
           potatoBridge.eglSurface
    );
    return JNI_TRUE;
}


JNIEXPORT jboolean JNICALL Java_org_lwjgl_glfw_GLFW_nativeEglMakeCurrent(JNIEnv* env, jclass clazz, jlong window) {

    if (window != 0x1) {
        printf("Making current on window %p\n", window);
    EGLBoolean success = eglMakeCurrent(
            potatoBridge.eglDisplay,
            potatoBridge.eglSurface,
            potatoBridge.eglSurface,
            /* window==0 ? EGL_NO_CONTEXT : */ (EGLContext *) window
    );
    if (success == EGL_FALSE) {
        printf("Error: eglMakeCurrent() failed: %p\n", eglGetError());
    }

    // Test
#ifdef GLES_TEST
    glClearColor(0.4f, 0.4f, 0.4f, 1.0f);
    glClear(GL_COLOR_BUFFER_BIT);
    eglSwapBuffers(potatoBridge.eglDisplay, potatoBridge.eglSurface);
    printf("First frame error: %p\n", eglGetError());
#endif

    // idk this should convert or just `return success;`...
    return success == EGL_TRUE ? JNI_TRUE : JNI_FALSE;
    }else{
        (*env)->ThrowNew(env,(*env)->FindClass(env,"java/lang/Exception"),"Trace exception");
        //return JNI_TRUE;
    }
    }

JNIEXPORT void JNICALL
Java_org_lwjgl_glfw_GLFW_nativeEglDetachOnCurrentThread(JNIEnv *env, jclass clazz) {
    //Obstruct the context on the current thread
    eglMakeCurrent(potatoBridge.eglDisplay, EGL_NO_SURFACE, EGL_NO_SURFACE, EGL_NO_CONTEXT);
}
JNIEXPORT jlong JNICALL
Java_org_lwjgl_glfw_GLFW_nativeEglCreateContext(JNIEnv *env, jclass clazz, jlong contextSrc) {
    EGLContext* ctx = eglCreateContext(potatoBridge.eglDisplay,config,(void*)contextSrc,ctx_attribs);
    printf("Created CTX pointer = %p\n",ctx);
    //(*env)->ThrowNew(env,(*env)->FindClass(env,"java/lang/Exception"),"Trace exception");
    return (long)ctx;
}
JNIEXPORT jboolean JNICALL Java_org_lwjgl_glfw_GLFW_nativeEglTerminate(JNIEnv* env, jclass clazz) {
    terminateEgl();
    return JNI_TRUE;
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_GL_nativeRegalMakeCurrent(JNIEnv *env, jclass clazz) {
    printf("Regal: making current");
    
    RegalMakeCurrent_func *RegalMakeCurrent = (RegalMakeCurrent_func *) dlsym(RTLD_DEFAULT, "RegalMakeCurrent");
    RegalMakeCurrent(potatoBridge.eglContext);
}

bool stopMakeCurrent;
JNIEXPORT jboolean JNICALL Java_org_lwjgl_glfw_GLFW_nativeEglSwapBuffers(JNIEnv *env, jclass clazz) {
    if (stopMakeCurrent) {
        return JNI_FALSE;
    }
    
    jboolean result = (jboolean) eglSwapBuffers(potatoBridge.eglDisplay, potatoBridge.eglSurface);
    if (!result) {
        if (eglGetError() == EGL_BAD_SURFACE) {
            stopMakeCurrent = true;
            closeGLFWWindow();
        }
    }
    return result;
}

JNIEXPORT jboolean JNICALL Java_org_lwjgl_glfw_GLFW_nativeEglSwapInterval(JNIEnv *env, jclass clazz, jint interval) {
	  return eglSwapInterval(potatoBridge.eglDisplay, interval);
}

