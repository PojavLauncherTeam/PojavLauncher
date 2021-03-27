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
#define OSMESA_UNSAFE
#ifdef OSMESA_UNSAFE
typedef struct native_handle
{
    int version;        /* sizeof(native_handle_t) */
    int numFds;         /* number of file-descriptors at &data[0] */
    int numInts;        /* number of ints at &data[numFds] */
#if defined(__clang__)
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wzero-length-array"
#endif
    int data[0];        /* numFds + numInts ints */
#if defined(__clang__)
#pragma clang diagnostic pop
#endif
} native_handle_t;
typedef const native_handle_t* buffer_handle_t;
typedef struct android_native_base_t
{
    /* a magic value defined by the actual EGL native type */
    int magic;
    /* the sizeof() of the actual EGL native type */
    int version;
    void* reserved[4];
    /* reference-counting interface */
    void (*incRef)(struct android_native_base_t* base);
    void (*decRef)(struct android_native_base_t* base);
} android_native_base_t;
typedef struct ANativeWindowBuffer
{
    struct android_native_base_t common;
    int width;
    int height;
    int stride;
    int format;
    int usage;
    void* reserved[2];
    buffer_handle_t handle;
    void* reserved_proc[8];
} ANativeWindowBuffer_t;
struct ANativeWindow_sys
{
    struct android_native_base_t common;
    /* flags describing some attributes of this surface or its updater */
    const uint32_t flags;
    /* min swap interval supported by this updated */
    const int   minSwapInterval;
    /* max swap interval supported by this updated */
    const int   maxSwapInterval;
    /* horizontal and vertical resolution in DPI */
    const float xdpi;
    const float ydpi;
    /* Some storage reserved for the OEM's driver. */
    intptr_t    oem[4];
    /*
     * Set the swap interval for this surface.
     *
     * Returns 0 on success or -errno on error.
     */
    int     (*setSwapInterval)(struct ANativeWindow* window,
                int interval);
    /*
     * Hook called by EGL to acquire a buffer. After this call, the buffer
     * is not locked, so its content cannot be modified. This call may block if
     * no buffers are available.
     *
     * The window holds a reference to the buffer between dequeueBuffer and
     * either queueBuffer or cancelBuffer, so clients only need their own
     * reference if they might use the buffer after queueing or canceling it.
     * Holding a reference to a buffer after queueing or canceling it is only
     * allowed if a specific buffer count has been set.
     *
     * Returns 0 on success or -errno on error.
     */
    int     (*dequeueBuffer)(struct ANativeWindow* window,
                struct ANativeWindowBuffer** buffer);
    /*
     * hook called by EGL to lock a buffer. This MUST be called before modifying
     * the content of a buffer. The buffer must have been acquired with
     * dequeueBuffer first.
     *
     * Returns 0 on success or -errno on error.
     */
    int     (*lockBuffer)(struct ANativeWindow* window,
                struct ANativeWindowBuffer* buffer);
    /*
     * Hook called by EGL when modifications to the render buffer are done.
     * This unlocks and post the buffer.
     *
     * The window holds a reference to the buffer between dequeueBuffer and
     * either queueBuffer or cancelBuffer, so clients only need their own
     * reference if they might use the buffer after queueing or canceling it.
     * Holding a reference to a buffer after queueing or canceling it is only
     * allowed if a specific buffer count has been set.
     *
     * Buffers MUST be queued in the same order than they were dequeued.
     *
     * Returns 0 on success or -errno on error.
     */
    int     (*queueBuffer)(struct ANativeWindow* window,
                struct ANativeWindowBuffer* buffer);
    /*
     * hook used to retrieve information about the native window.
     *
     * Returns 0 on success or -errno on error.
     */
    int     (*query)(const struct ANativeWindow* window,
                int what, int* value);
    /*
     * hook used to perform various operations on the surface.
     * (*perform)() is a generic mechanism to add functionality to
     * ANativeWindow while keeping backward binary compatibility.
     *
     * DO NOT CALL THIS HOOK DIRECTLY.  Instead, use the helper functions
     * defined below.
     *
     *  (*perform)() returns -ENOENT if the 'what' parameter is not supported
     *  by the surface's implementation.
     *
     * The valid operations are:
     *     NATIVE_WINDOW_SET_USAGE
     *     NATIVE_WINDOW_CONNECT               (deprecated)
     *     NATIVE_WINDOW_DISCONNECT            (deprecated)
     *     NATIVE_WINDOW_SET_CROP
     *     NATIVE_WINDOW_SET_BUFFER_COUNT
     *     NATIVE_WINDOW_SET_BUFFERS_GEOMETRY  (deprecated)
     *     NATIVE_WINDOW_SET_BUFFERS_TRANSFORM
     *     NATIVE_WINDOW_SET_BUFFERS_TIMESTAMP
     *     NATIVE_WINDOW_SET_BUFFERS_DIMENSIONS
     *     NATIVE_WINDOW_SET_BUFFERS_FORMAT
     *     NATIVE_WINDOW_SET_SCALING_MODE
     *     NATIVE_WINDOW_LOCK                   (private)
     *     NATIVE_WINDOW_UNLOCK_AND_POST        (private)
     *     NATIVE_WINDOW_API_CONNECT            (private)
     *     NATIVE_WINDOW_API_DISCONNECT         (private)
     *
     */
    int     (*perform)(struct ANativeWindow* window,
                int operation, ... );
    /*
     * Hook used to cancel a buffer that has been dequeued.
     * No synchronization is performed between dequeue() and cancel(), so
     * either external synchronization is needed, or these functions must be
     * called from the same thread.
     *
     * The window holds a reference to the buffer between dequeueBuffer and
     * either queueBuffer or cancelBuffer, so clients only need their own
     * reference if they might use the buffer after queueing or canceling it.
     * Holding a reference to a buffer after queueing or canceling it is only
     * allowed if a specific buffer count has been set.
     */
    int     (*cancelBuffer)(struct ANativeWindow* window,
                struct ANativeWindowBuffer* buffer);
    void* reserved_proc[2];
};
typedef struct ANativeWindow_sys _ANativeWindow_sys2;
#endif
struct PotatoBridge {
	 /*ANativeWindow */ void* androidWindow;
	    
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
    if (!dl_handle) {
        dl_handle = dlopen("libOSMesa_8.so",RTLD_NOLOAD|RTLD_NOW|RTLD_GLOBAL|RTLD_NODELETE);
        printf("OSMDroid: using built-in libOSMesa_8.so\n");
    } else {
        printf("OSMDroid: using developer libOSMesa.so.8 instead of built-in\n");
    }

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
    _ANativeWindow_sys2 window = *(_ANativeWindow_sys2*)potatoBridge.androidWindow;
    printf("OSMDroid: internal dequeueBuffer ptr=%p",window.dequeueBuffer);
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
int32_t stride;
void flipFrame() {
    ANativeWindow_lock(potatoBridge.androidWindow,&buf,NULL);

    if(stride != buf.stride) {
        stride = buf.stride;
        printf("OSMDroid: stride reset\n");

    }
    glFinish_p();
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
    OSMesaPixelStore_p(OSMESA_Y_UP,0);
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

