#include <jni.h>
#include <assert.h>
#include <dlfcn.h>

#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <unistd.h>

//#include <EGL/egl.h>
#include <GL/osmesa.h>

#ifdef GLES_TEST
#include <GLES2/gl2.h>
#endif

#include <android/native_window.h>
#include <android/native_window_jni.h>
#include <android/rect.h>
#include <string.h>
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
GLboolean (*OSMesaMakeCurrent_p) (OSMesaContext ctx, void *buffer, GLenum type,
                                  GLsizei width, GLsizei height);
OSMesaContext (*OSMesaGetCurrentContext_p) (void);
OSMesaContext  (*OSMesaCreateContext_p) (GLenum format, OSMesaContext sharelist);
void (*OSMesaPixelStore_p) ( GLint pname, GLint value );
GLubyte* (*glGetString_p) (GLenum name);
void (*glFinish_p) (void);
void (*glClearColor_p) (GLclampf red, GLclampf green, GLclampf blue, GLclampf alpha);
void (*glClear_p) (GLbitfield mask);

void pojav_openGLOnLoad() {
}
void pojav_openGLOnUnload() {

}
void* gbuffer;
int width;
int height;
void terminateEgl() {
    printf("EGLBridge: Terminating\n");
    //OSMesaDestroyContext(potatoBridge.eglContext);
}

JNIEXPORT void JNICALL Java_net_kdt_pojavlaunch_utils_JREUtils_setupBridgeWindow(JNIEnv* env, jclass clazz, jobject surface) {
    potatoBridge.androidWindow = ANativeWindow_fromSurface(env, surface);
}

JNIEXPORT jlong JNICALL Java_org_lwjgl_glfw_GLFW_nativeEglGetCurrentContext(JNIEnv* env, jclass clazz) {
    return (jlong) OSMesaGetCurrentContext_p();
}

JNIEXPORT jboolean JNICALL Java_org_lwjgl_glfw_GLFW_nativeEglInit(JNIEnv* env, jclass clazz) {
    setenv("GALLIUM_DRIVER","zink",1);
    void* dl_handle = dlopen("libOSMesa.so.8",RTLD_NOLOAD|RTLD_NOW|RTLD_GLOBAL|RTLD_NODELETE);
    if(dl_handle == NULL) {
        printf("OSMDroid: unable to load: %s\n",dlerror());
        return JNI_FALSE;
    }
    OSMesaMakeCurrent_p = dlsym(dl_handle,"OSMesaMakeCurrent");
    OSMesaGetCurrentContext_p = dlsym(dl_handle,"OSMesaGetCurrentContext");
    OSMesaCreateContext_p = dlsym(dl_handle, "OSMesaCreateContext");
    OSMesaPixelStore_p = dlsym(dl_handle,"OSMesaPixelStore");
    glGetString_p = dlsym(dl_handle,"glGetString");
    glClearColor_p = dlsym(dl_handle, "glClearColor");
    glClear_p = dlsym(dl_handle,"glClear");
    glFinish_p = dlsym(dl_handle,"glFinish");
    if(OSMesaCreateContext_p == NULL) {
        printf("OSMDroid: %s\n",dlerror());
        return JNI_FALSE;
    }
    ANativeWindow_acquire(potatoBridge.androidWindow);
    width = ANativeWindow_getWidth(potatoBridge.androidWindow);
    height = ANativeWindow_getHeight(potatoBridge.androidWindow);
    ANativeWindow_setBuffersGeometry(potatoBridge.androidWindow,width,height,AHARDWAREBUFFER_FORMAT_R8G8B8X8_UNORM);
    printf("OSMDroid: width=%i;height=%i, reserving %i bytes for frame buffer\n", width, height,
           width * 4 * height);
    gbuffer = malloc(width * 4 * height+1);
    if (gbuffer) {
        printf("OSMDroid: created frame buffer\n");
        return JNI_TRUE;
    }else{
        printf("OSMDroid: can't generate frame buffer\n");
        return JNI_FALSE;
    }

}
ANativeWindow_Buffer buf;
void flipFrame() {
    glFinish_p();
    ANativeWindow_lock(potatoBridge.androidWindow,&buf,NULL);
    memcpy(buf.bits,gbuffer,width*4*height);
    ANativeWindow_unlockAndPost(potatoBridge.androidWindow);
}
bool stopSwapBuffers;
JNIEXPORT jboolean JNICALL Java_org_lwjgl_glfw_GLFW_nativeEglSwapBuffers(JNIEnv *env, jclass clazz) {
    if (stopSwapBuffers) {
        return JNI_FALSE;
    }
    flipFrame();
}
bool locked = false;
JNIEXPORT jboolean JNICALL Java_org_lwjgl_glfw_GLFW_nativeEglMakeCurrent(JNIEnv* env, jclass clazz, jlong window) {
    //if(OSMesaGetCurrentContext_p() != NULL) {
    //    printf("OSMDroid: skipped context reset\n");
    //    return JNI_TRUE;
    //}
    printf("OSMDroid: making current\n");
    OSMesaMakeCurrent_p((OSMesaContext)window,gbuffer,GL_UNSIGNED_BYTE,width,height);
    OSMesaPixelStore_p(OSMESA_ROW_LENGTH,atoi(getenv("OSMESA_IMAGE_OFFSET")));

    printf("OSMDroid: vendor: %s\n",glGetString_p(GL_VENDOR));
    printf("OSMDroid: renderer: %s\n",glGetString_p(GL_RENDERER));
    glClear_p(GL_COLOR_BUFFER_BIT);
    glClearColor_p(0.4f, 0.4f, 0.4f, 1.0f);
    flipFrame();
    return JNI_TRUE;
}

JNIEXPORT void JNICALL
Java_org_lwjgl_glfw_GLFW_nativeEglDetachOnCurrentThread(JNIEnv *env, jclass clazz) {
    //Obstruct the context on the current thread
    //
}

JNIEXPORT jlong JNICALL
Java_org_lwjgl_glfw_GLFW_nativeEglCreateContext(JNIEnv *env, jclass clazz, jlong contextSrc) {
    printf("OSMDroid: generating context\n");
    void* ctx = OSMesaCreateContext_p(OSMESA_RGBA,contextSrc);
    printf("OSMDroid: context=%p",ctx);
    return ctx;
}

JNIEXPORT jboolean JNICALL Java_org_lwjgl_glfw_GLFW_nativeEglTerminate(JNIEnv* env, jclass clazz) {
    terminateEgl();
    return JNI_TRUE;
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_GL_nativeRegalMakeCurrent(JNIEnv *env, jclass clazz) {
    /*printf("Regal: making current");
    
    RegalMakeCurrent_func *RegalMakeCurrent = (RegalMakeCurrent_func *) dlsym(RTLD_DEFAULT, "RegalMakeCurrent");
    RegalMakeCurrent(potatoBridge.eglContext);*/
}
JNIEXPORT jlong JNICALL
Java_org_lwjgl_opengl_GL_getGraphicsBufferAddr(JNIEnv *env, jobject thiz) {
    return &gbuffer;
}
JNIEXPORT jintArray JNICALL
Java_org_lwjgl_opengl_GL_getNativeWidthHeight(JNIEnv *env, jobject thiz) {
    jintArray ret = (*env)->NewIntArray(env,2);
    jint arr[] = {width,height};
    (*env)->SetIntArrayRegion(env,ret,0,2,arr);
    return ret;
}
JNIEXPORT jboolean JNICALL Java_org_lwjgl_glfw_GLFW_nativeEglSwapInterval(JNIEnv *env, jclass clazz, jint interval) {
    printf("eglSwapInterval: NOT IMPLEMENTED YET!\n");
    //return eglSwapInterval(potatoBridge.eglDisplay, interval);
}

