#include <jni.h>
#include <assert.h>
#include <dlfcn.h>

#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>

#include <EGL/egl.h>

#ifdef GLES_TEST
#include <GLES2/gl2.h>
#endif

#include <android/native_window.h>
#include <android/native_window_jni.h>

#include "utils.h"

struct PotatoBridge {
	ANativeWindow* androidWindow;
	
	EGLContext eglContext;
	EGLDisplay eglDisplay;
	EGLSurface eglSurface;
/*
	void* eglSurfaceRead;
	void* eglSurfaceDraw;
*/
};
struct PotatoBridge potatoBridge;

typedef jint RegalMakeCurrent_func(EGLContext context);

JNIEXPORT void JNICALL Java_net_kdt_pojavlaunch_JREUtils_setupBridgeWindow(JNIEnv* env, jclass clazz, jobject surface) {
	potatoBridge.androidWindow = ANativeWindow_fromSurface(env, surface);
}

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

JNIEXPORT jboolean JNICALL Java_org_lwjgl_glfw_GLFW_nativeEglInit(JNIEnv* env, jclass clazz) {
    return JNI_TRUE;
}

JNIEXPORT jboolean JNICALL Java_org_lwjgl_glfw_GLFW_nativeEglMakeCurrent(JNIEnv* env, jclass clazz) {
    if (potatoBridge.eglContext != EGL_NO_CONTEXT) {
        terminateEgl();
    }
    
    // dlopen("libgl04es.so", RTLD_NOW + RTLD_GLOBAL);
    
    if (potatoBridge.eglDisplay == NULL || potatoBridge.eglDisplay == EGL_NO_DISPLAY) {
        potatoBridge.eglDisplay = eglGetDisplay(EGL_DEFAULT_DISPLAY);
        if (potatoBridge.eglDisplay == EGL_NO_DISPLAY) {
            printf("EGLBridge: Error eglGetDefaultDisplay() failed: %p\n", eglGetError());
            return JNI_FALSE;
        }
    }
    
    printf("EGLBridge: Initializing\n");
    printf("EGLBridge: ANativeWindow pointer = %p\n", potatoBridge.androidWindow);
    
    if (!eglInitialize(potatoBridge.eglDisplay, NULL, NULL)) {
        printf("EGLBridge: Error eglInitialize() failed\n");
        return JNI_FALSE;
    }
   
    static const EGLint attribs[] = {
        EGL_RED_SIZE, 8,
        EGL_GREEN_SIZE, 8,
        EGL_BLUE_SIZE, 8,
        EGL_ALPHA_SIZE, 8,
        EGL_DEPTH_SIZE, 24,
        EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT,
        EGL_NONE
    };
    static const EGLint ctx_attribs[] = {
        EGL_CONTEXT_CLIENT_VERSION, 2,
        EGL_NONE
    };
   
    EGLConfig config;
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

        potatoBridge.eglContext = eglCreateContext(potatoBridge.eglDisplay, config, EGL_NO_CONTEXT, ctx_attribs);
        if (!potatoBridge.eglContext) {
            printf("EGLBridge: Error eglCreateContext failed\n");
            return JNI_FALSE;
        }

        // test eglQueryContext() 
        {
            EGLint val;
            eglQueryContext(potatoBridge.eglDisplay, potatoBridge.eglContext, EGL_CONTEXT_CLIENT_VERSION, &val);
            printf("EGLBridge: OpenGL ES from eglQueryContext: %i\n", val);
            // assert(val >= 2);
        }
    
        potatoBridge.eglSurface = eglCreateWindowSurface(potatoBridge.eglDisplay, config, potatoBridge.androidWindow, NULL);
    
        if (!potatoBridge.eglSurface) {
            printf("EGLBridge: Error eglCreateWindowSurface failed: %p\n", eglGetError());
            return JNI_FALSE;
        }
    
        // sanity checks
        {
            EGLint val;
            assert(eglGetConfigAttrib(potatoBridge.eglDisplay, config, EGL_SURFACE_TYPE, &val));
            assert(val & EGL_WINDOW_BIT);
        }
    // return JNI_TRUE;
    
	printf("EGLBridge: Making current\n");
	printf("EGLBridge: EGLContext=%p, EGLDisplay=%p, EGLSurface=%p\n",
		potatoBridge.eglContext,
		potatoBridge.eglDisplay,
		potatoBridge.eglSurface 
	);
	
	EGLBoolean success = eglMakeCurrent(potatoBridge.eglDisplay, potatoBridge.eglSurface, potatoBridge.eglSurface, potatoBridge.eglContext);
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

	return success == EGL_TRUE ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jboolean JNICALL Java_org_lwjgl_glfw_GLFW_nativeEglTerminate(JNIEnv* env, jclass clazz) {
    terminateEgl();
    return JNI_TRUE;
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_GL_nativeRegalMakeCurrent(JNIEnv *env, jclass clazz) {
    RegalMakeCurrent_func *RegalMakeCurrent = (RegalMakeCurrent_func *) dlsym(RTLD_DEFAULT, "RegalMakeCurrent");
    RegalMakeCurrent(potatoBridge.eglContext);
}

JNIEXPORT jboolean JNICALL Java_org_lwjgl_glfw_GLFW_nativeEglSwapBuffers(JNIEnv *env, jclass clazz) {
	  return eglSwapBuffers(potatoBridge.eglDisplay, potatoBridge.eglSurface);
}

JNIEXPORT jboolean JNICALL Java_org_lwjgl_glfw_GLFW_nativeEglSwapInterval(JNIEnv *env, jclass clazz, jint interval) {
	  return eglSwapInterval(potatoBridge.eglDisplay, interval);
}

