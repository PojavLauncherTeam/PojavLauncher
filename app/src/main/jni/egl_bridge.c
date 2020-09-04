#include <jni.h>
#include <assert.h>
#include <stdint.h>
#include <stdio.h>
#include <EGL/egl.h>

#include <android/native_window.h>
#include <android/native_window_jni.h>

struct ANativeWindow* windowBridge;

struct PotatoBridge {
	void* eglContext;
	void* eglDisplay;
	void* eglSurface;
/*
	void* eglSurfaceRead;
	void* eglSurfaceDraw;
*/
};
struct PotatoBridge potatoBridge;

JNIEXPORT void JNICALL Java_net_kdt_pojavlaunch_JREUtils_setupBridgeWindow(JNIEnv* env, jclass clazz, jobject surface) {
	windowBridge = ANativeWindow_fromSurface(env, surface);
}

// Called from JNI_OnLoad of liblwjgl_opengl32
void pojav_openGLOnLoad() {
	printf("ANativeWindow pointer = %p\n", windowBridge);
	
	potatoBridge.eglDisplay = eglGetDisplay(EGL_DEFAULT_DISPLAY);
	if (potatoBridge.eglDisplay == EGL_NO_DISPLAY) {
		printf("Error: eglGetDefaultDisplay() failed: %p\n", eglGetError());
		return; // -1;
	}
	
	eglMakeCurrent(potatoBridge.eglDisplay, EGL_NO_SURFACE, EGL_NO_SURFACE, EGL_NO_CONTEXT);
	
	if (!eglInitialize(potatoBridge.eglDisplay, NULL, NULL)) {
		printf("Error: eglInitialize() failed\n");
		return; // -2;
	}
	
	static const EGLint attribs[] = {
		EGL_RED_SIZE, 1,
		EGL_GREEN_SIZE, 1,
		EGL_BLUE_SIZE, 1,
		EGL_DEPTH_SIZE, 1,
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
		printf("Error: couldn't get an EGL visual config\n");
		return; // -3;
	}
	
	assert(config);
	assert(num_configs > 0);

	if (!eglGetConfigAttrib(potatoBridge.eglDisplay, config, EGL_NATIVE_VISUAL_ID, &vid)) {
		printf("Error: eglGetConfigAttrib() failed\n");
		return; // -4;
	}

	eglBindAPI(EGL_OPENGL_ES_API);

	potatoBridge.eglContext = eglCreateContext(potatoBridge.eglDisplay, config, EGL_NO_CONTEXT, ctx_attribs);
	if (!potatoBridge.eglContext) {
		printf("Error: eglCreateContext failed\n");
		return; // -5;
	}

	// test eglQueryContext() 
	{
        EGLint val;
        eglQueryContext(potatoBridge.eglDisplay, potatoBridge.eglContext, EGL_CONTEXT_CLIENT_VERSION, &val);
        assert(val == 2);
    }
	
	potatoBridge.eglSurface = eglCreateWindowSurface(potatoBridge.eglDisplay, config, windowBridge, NULL);
	
	if (!potatoBridge.eglSurface) {
        printf("Error: eglCreateWindowSurface failed: %p\n", eglGetError());
        return; // -6;
    }
	
    // sanity checks
    {
        EGLint val;
        assert(eglGetConfigAttrib(potatoBridge.eglDisplay, config, EGL_SURFACE_TYPE, &val));
        assert(val & EGL_WINDOW_BIT);
    }
	
	printf("EGLContext=%p, EGLDisplay=%p, EGLSurface=%p\n",
		potatoBridge.eglContext,
		potatoBridge.eglDisplay,
		potatoBridge.eglSurface 
	);
	
	if (eglMakeCurrent(potatoBridge.eglDisplay, potatoBridge.eglSurface, potatoBridge.eglSurface, potatoBridge.eglContext) == EGL_FALSE) {
		printf("Error: eglMakeCurrent() failed: %p\n", eglGetError());
	}
}

void pojav_openGLOnUnload() {
	eglMakeCurrent(potatoBridge.eglDisplay, EGL_NO_SURFACE, EGL_NO_SURFACE, EGL_NO_CONTEXT);
	eglDestroySurface(potatoBridge.eglDisplay, potatoBridge.eglSurface);
	eglDestroyContext(potatoBridge.eglDisplay, potatoBridge.eglContext);
	eglTerminate(potatoBridge.eglDisplay);
	eglReleaseThread();
}

JNIEXPORT jboolean JNICALL Java_org_lwjgl_glfw_GLFW_nativeEglSwapBuffers(JNIEnv *env, jclass clazz) {
	  return eglSwapBuffers(potatoBridge.eglDisplay, potatoBridge.eglSurface);
}

JNIEXPORT jboolean JNICALL Java_org_lwjgl_glfw_GLFW_nativeEglSwapInterval(JNIEnv *env, jclass clazz, jint interval) {
	  return eglSwapInterval(potatoBridge.eglDisplay, interval);
}

