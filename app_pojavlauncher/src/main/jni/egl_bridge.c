#include <jni.h>
#include <assert.h>
#include <dlfcn.h>

#include <stdbool.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <unistd.h>

#include <EGL/egl.h>
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
EGLConfig config;
struct PotatoBridge potatoBridge;

/* OSMesa functions */
GLboolean (*OSMesaMakeCurrent_p) (OSMesaContext ctx, void *buffer, GLenum type,
                                  GLsizei width, GLsizei height);
OSMesaContext (*OSMesaGetCurrentContext_p) (void);
OSMesaContext  (*OSMesaCreateContext_p) (GLenum format, OSMesaContext sharelist);
void (*OSMesaDestroyContext_p) (OSMesaContext ctx);
void (*OSMesaFlushFrontbuffer_p) ();
void (*OSMesaPixelStore_p) ( GLint pname, GLint value );
GLubyte* (*glGetString_p) (GLenum name);
void (*glFinish_p) (void);
void (*glClearColor_p) (GLclampf red, GLclampf green, GLclampf blue, GLclampf alpha);
void (*glClear_p) (GLbitfield mask);
void (*glReadBuffer_p) (GLenum mode);
void (*glReadPixels_p) (GLint x, GLint y, GLsizei width, GLsizei height, GLenum format, GLenum type, void * data);

/*EGL functions */
EGLBoolean (*eglMakeCurrent_p) (EGLDisplay dpy, EGLSurface draw, EGLSurface read, EGLContext ctx);
EGLBoolean (*eglDestroyContext_p) (EGLDisplay dpy, EGLContext ctx);
EGLBoolean (*eglDestroySurface_p) (EGLDisplay dpy, EGLSurface surface);
EGLBoolean (*eglTerminate_p) (EGLDisplay dpy);
EGLBoolean (*eglReleaseThread_p) (void);
EGLContext (*eglGetCurrentContext_p) (void);
EGLDisplay (*eglGetDisplay_p) (NativeDisplayType display);
EGLBoolean (*eglInitialize_p) (EGLDisplay dpy, EGLint *major, EGLint *minor);
EGLBoolean (*eglChooseConfig_p) (EGLDisplay dpy, const EGLint *attrib_list, EGLConfig *configs, EGLint config_size, EGLint *num_config);
EGLBoolean (*eglGetConfigAttrib_p) (EGLDisplay dpy, EGLConfig config, EGLint attribute, EGLint *value);
EGLBoolean (*eglBindAPI_p) (EGLenum api);
EGLSurface (*eglCreatePbufferSurface_p) (EGLDisplay dpy, EGLConfig config, const EGLint *attrib_list);
EGLSurface (*eglCreateWindowSurface_p) (EGLDisplay dpy, EGLConfig config, NativeWindowType window, const EGLint *attrib_list);
EGLBoolean (*eglSwapBuffers_p) (EGLDisplay dpy, EGLSurface draw);
EGLint (*eglGetError_p) (void);
EGLContext (*eglCreateContext_p) (EGLDisplay dpy, EGLConfig config, EGLContext share_list, const EGLint *attrib_list);
EGLBoolean (*eglSwapInterval_p) (EGLDisplay dpy, EGLint interval);
EGLSurface (*eglGetCurrentSurface_p) (EGLint readdraw);
int (*vtest_main_p) (int argc, char** argv);
void (*vtest_swap_buffers_p) (void);

#define RENDERER_GL4ES 1
#define RENDERER_VK_ZINK 2
#define RENDERER_VIRGL 3

int config_renderer;
void* gbuffer;

void* egl_make_current(void* window);

void pojav_openGLOnLoad() {
}
void pojav_openGLOnUnload() {

}

void pojavTerminate() {
    printf("EGLBridge: Terminating\n");
    
    switch (config_renderer) {
        case RENDERER_GL4ES: {
            eglMakeCurrent_p(potatoBridge.eglDisplay, EGL_NO_SURFACE, EGL_NO_SURFACE, EGL_NO_CONTEXT);
            eglDestroySurface_p(potatoBridge.eglDisplay, potatoBridge.eglSurface);
            eglDestroyContext_p(potatoBridge.eglDisplay, potatoBridge.eglContext);
            eglTerminate_p(potatoBridge.eglDisplay);
            eglReleaseThread_p();
    
            potatoBridge.eglContext = EGL_NO_CONTEXT;
            potatoBridge.eglDisplay = EGL_NO_DISPLAY;
            potatoBridge.eglSurface = EGL_NO_SURFACE;
        } break;
        
        //case RENDERER_VIRGL:
        case RENDERER_VK_ZINK: {
            // Nothing to do here
        } break;
    }
}

JNIEXPORT void JNICALL Java_net_kdt_pojavlaunch_utils_JREUtils_setupBridgeWindow(JNIEnv* env, jclass clazz, jobject surface) {
    potatoBridge.androidWindow = ANativeWindow_fromSurface(env, surface);
    char *ptrStr;
    asprintf(&ptrStr, "%ld", (long) potatoBridge.androidWindow);
    setenv("POJAV_WINDOW_PTR", ptrStr, 1);
    free(ptrStr);
}


JNIEXPORT void JNICALL
Java_net_kdt_pojavlaunch_utils_JREUtils_releaseBridgeWindow(JNIEnv *env, jclass clazz) {
    ANativeWindow_release(potatoBridge.androidWindow);
}

void* pojavGetCurrentContext() {
    switch (config_renderer) {
        case RENDERER_GL4ES:
            return (void *)eglGetCurrentContext_p();
        case RENDERER_VIRGL:
        case RENDERER_VK_ZINK:
            return (void *)OSMesaGetCurrentContext_p();

        default: return NULL;
    }
}

void dlsym_EGL(void* dl_handle) {
    eglBindAPI_p = dlsym(dl_handle,"eglBindAPI");
    eglChooseConfig_p = dlsym(dl_handle, "eglChooseConfig");
    eglCreateContext_p = dlsym(dl_handle, "eglCreateContext");
    eglCreatePbufferSurface_p = dlsym(dl_handle, "eglCreatePbufferSurface");
    eglCreateWindowSurface_p = dlsym(dl_handle, "eglCreateWindowSurface");
    eglDestroyContext_p = dlsym(dl_handle, "eglDestroyContext");
    eglDestroySurface_p = dlsym(dl_handle, "eglDestroySurface");
    eglGetConfigAttrib_p = dlsym(dl_handle, "eglGetConfigAttrib");
    eglGetCurrentContext_p = dlsym(dl_handle, "eglGetCurrentContext");
    eglGetDisplay_p = dlsym(dl_handle, "eglGetDisplay");
    eglGetError_p = dlsym(dl_handle, "eglGetError");
    eglInitialize_p = dlsym(dl_handle, "eglInitialize");
    eglMakeCurrent_p = dlsym(dl_handle, "eglMakeCurrent");
    eglSwapBuffers_p = dlsym(dl_handle, "eglSwapBuffers");
    eglReleaseThread_p = dlsym(dl_handle, "eglReleaseThread");
    eglSwapInterval_p = dlsym(dl_handle, "eglSwapInterval");
    eglTerminate_p = dlsym(dl_handle, "eglTerminate");
    eglGetCurrentSurface_p = dlsym(dl_handle,"eglGetCurrentSurface");
}

void dlsym_OSMesa(void* dl_handle) {
    OSMesaMakeCurrent_p = dlsym(dl_handle,"OSMesaMakeCurrent");
    OSMesaGetCurrentContext_p = dlsym(dl_handle,"OSMesaGetCurrentContext");
    OSMesaCreateContext_p = dlsym(dl_handle, "OSMesaCreateContext");
    OSMesaDestroyContext_p = dlsym(dl_handle, "OSMesaDestroyContext");
    OSMesaFlushFrontbuffer_p = dlsym(dl_handle, "OSMesaFlushFrontbuffer");
    OSMesaPixelStore_p = dlsym(dl_handle,"OSMesaPixelStore");
    glGetString_p = dlsym(dl_handle,"glGetString");
    glClearColor_p = dlsym(dl_handle, "glClearColor");
    glClear_p = dlsym(dl_handle,"glClear");
    glFinish_p = dlsym(dl_handle,"glFinish");
    glReadBuffer_p = dlsym(dl_handle,"glReadBuffer");
    glReadPixels_p = dlsym(dl_handle,"glReadPixels");
}

bool loadSymbols() {
    char* fileName = calloc(1, 1024);
    char* fileNameExt = calloc(1, 1024);
    switch (config_renderer) {
        case RENDERER_VK_ZINK:
            sprintf(fileName, "%s/libOSMesa_8.so", getenv("POJAV_NATIVEDIR"));
            sprintf(fileNameExt, "%s/libOSMesa.so.8", getenv("POJAV_NATIVEDIR"));
            break;
        case RENDERER_GL4ES:
            sprintf(fileName, "libEGL.so");
            char* eglLib = getenv("POJAVEXEC_EGL");
            if (eglLib) {
                sprintf(fileNameExt, "%s", eglLib);
            }
            break;
    }
    void* dl_handle = dlopen(fileNameExt,RTLD_NOW|RTLD_GLOBAL|RTLD_NODELETE);
    if (!dl_handle) {
        dl_handle = dlopen(fileNameExt,RTLD_NOW|RTLD_GLOBAL);
    }
    if (!dl_handle) {
        dl_handle = dlopen(fileName,RTLD_NOW|RTLD_GLOBAL|RTLD_NODELETE);
        if (!dl_handle) {
            dl_handle = dlopen(fileName,RTLD_NOW|RTLD_GLOBAL);
        }
        printf("DlLoader: using default %s\n", fileName);
    } else {
        printf("DlLoader: using external %s\n", fileNameExt);
    }

    if(dl_handle == NULL) {
        printf("DlLoader: unable to load: %s\n",dlerror());
        return 0;
    }
    switch(config_renderer) {
        case RENDERER_VK_ZINK:
            dlsym_OSMesa(dl_handle);
            break;
        case RENDERER_GL4ES:
            dlsym_EGL(dl_handle);
            break;
    }

    free(fileName);
    free(fileNameExt);
}

bool loadSymbolsVirGL() {
    config_renderer = RENDERER_GL4ES;
    loadSymbols();
    config_renderer = RENDERER_VK_ZINK;
    loadSymbols();
    config_renderer = RENDERER_VIRGL;

    char* fileName = calloc(1, 1024);

    sprintf(fileName, "%s/libvirgl_test_server.so", getenv("POJAV_NATIVEDIR"));
    void *handle = dlopen(fileName, RTLD_LAZY);
    printf("VirGL: libvirgl_test_server = %p\n", handle);
    if (!handle) {
        printf("VirGL: %s\n", dlerror());
    }
    vtest_main_p = dlsym(handle, "vtest_main");
    vtest_swap_buffers_p = dlsym(handle, "vtest_swap_buffers");

    free(fileName);
}

int pojavInit() {
    potatoBridge.androidWindow = (void *)atol(getenv("POJAV_WINDOW_PTR"));
    ANativeWindow_acquire(potatoBridge.androidWindow);
    savedWidth = ANativeWindow_getWidth(potatoBridge.androidWindow);
    savedHeight = ANativeWindow_getHeight(potatoBridge.androidWindow);
    ANativeWindow_setBuffersGeometry(potatoBridge.androidWindow,savedWidth,savedHeight,AHARDWAREBUFFER_FORMAT_R8G8B8X8_UNORM);

    // NOTE: Override for now.
    const char *renderer = getenv("POJAV_RENDERER");
    if (strncmp("opengles3_virgl", renderer, 15) == 0) {
        config_renderer = RENDERER_VIRGL;
        setenv("GALLIUM_DRIVER","virpipe",1);
        setenv("OSMESA_NO_FLUSH_FRONTBUFFER","1",false);
        if(strcmp(getenv("OSMESA_NO_FLUSH_FRONTBUFFER"),"1") == 0) {
            printf("VirGL: OSMesa buffer flush is DISABLED!\n");
        }
        loadSymbolsVirGL();
    } else if (strncmp("opengles", renderer, 8) == 0) {
        config_renderer = RENDERER_GL4ES;
        loadSymbols();
    } else if (strcmp(renderer, "vulkan_zink") == 0) {
        config_renderer = RENDERER_VK_ZINK;
        setenv("GALLIUM_DRIVER","zink",1);
        loadSymbols();
    }

    if (config_renderer == RENDERER_GL4ES || config_renderer == RENDERER_VIRGL) {
        if (potatoBridge.eglDisplay == NULL || potatoBridge.eglDisplay == EGL_NO_DISPLAY) {
            potatoBridge.eglDisplay = eglGetDisplay_p(EGL_DEFAULT_DISPLAY);
            if (potatoBridge.eglDisplay == EGL_NO_DISPLAY) {
                printf("EGLBridge: Error eglGetDefaultDisplay() failed: %p\n", eglGetError_p());
                return 0;
            }
        }

        printf("EGLBridge: Initializing\n");
        // printf("EGLBridge: ANativeWindow pointer = %p\n", potatoBridge.androidWindow);
        //(*env)->ThrowNew(env,(*env)->FindClass(env,"java/lang/Exception"),"Trace exception");
        if (!eglInitialize_p(potatoBridge.eglDisplay, NULL, NULL)) {
            printf("EGLBridge: Error eglInitialize() failed: %s\n", eglGetError_p());
            return 0;
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

        if (!eglChooseConfig_p(potatoBridge.eglDisplay, attribs, &config, 1, &num_configs)) {
            printf("EGLBridge: Error couldn't get an EGL visual config: %s\n", eglGetError_p());
            return 0;
        }

        assert(config);
        assert(num_configs > 0);

        if (!eglGetConfigAttrib_p(potatoBridge.eglDisplay, config, EGL_NATIVE_VISUAL_ID, &vid)) {
            printf("EGLBridge: Error eglGetConfigAttrib() failed: %s\n", eglGetError_p());
            return 0;
        }

        ANativeWindow_setBuffersGeometry(potatoBridge.androidWindow, 0, 0, vid);

        eglBindAPI_p(EGL_OPENGL_ES_API);

        potatoBridge.eglSurface = eglCreateWindowSurface_p(potatoBridge.eglDisplay, config, potatoBridge.androidWindow, NULL);

        if (!potatoBridge.eglSurface) {
            printf("EGLBridge: Error eglCreateWindowSurface failed: %p\n", eglGetError_p());
            //(*env)->ThrowNew(env,(*env)->FindClass(env,"java/lang/Exception"),"Trace exception");
            return 0;
        }

        // sanity checks
        {
            EGLint val;
            assert(eglGetConfigAttrib_p(potatoBridge.eglDisplay, config, EGL_SURFACE_TYPE, &val));
            assert(val & EGL_WINDOW_BIT);
        }

        printf("EGLBridge: Initialized!\n");
        printf("EGLBridge: ThreadID=%d\n", gettid());
        printf("EGLBridge: EGLDisplay=%p, EGLSurface=%p\n",
/* window==0 ? EGL_NO_CONTEXT : */
               potatoBridge.eglDisplay,
               potatoBridge.eglSurface
        );
        if (config_renderer != RENDERER_VIRGL) {
            return 1;
        }
    }

    if (config_renderer == RENDERER_VIRGL) {
        // Init EGL context and vtest server
        const EGLint ctx_attribs[] = {
            EGL_CONTEXT_CLIENT_VERSION, 3,
            EGL_NONE
        };
        EGLContext* ctx = eglCreateContext_p(potatoBridge.eglDisplay, config, NULL, ctx_attribs);
        printf("VirGL: created EGL context %p\n", ctx);

        pthread_t t;
        pthread_create(&t, NULL, egl_make_current, (void *)ctx);
        usleep(100*1000); // need enough time for the server to init
    }

    if (config_renderer == RENDERER_VK_ZINK || config_renderer == RENDERER_VIRGL) {
        if(OSMesaCreateContext_p == NULL) {
            printf("OSMDroid: %s\n",dlerror());
            return 0;
        }
        
        printf("OSMDroid: width=%i;height=%i, reserving %i bytes for frame buffer\n", savedWidth, savedHeight,
             savedWidth * 4 * savedHeight);
        gbuffer = malloc(savedWidth * 4 * savedHeight+1);
        if (gbuffer) {
            printf("OSMDroid: created frame buffer\n");
            return 1;
        } else {
            printf("OSMDroid: can't generate frame buffer\n");
            return 0;
        }
    }
    
    return 0;
}
ANativeWindow_Buffer buf;
int32_t stride;
bool stopSwapBuffers;
void pojavSwapBuffers() {
    if (stopSwapBuffers) {
        return;
    }
    switch (config_renderer) {
        case RENDERER_GL4ES: {
            if (!eglSwapBuffers_p(potatoBridge.eglDisplay, eglGetCurrentSurface_p(EGL_DRAW))) {
                if (eglGetError_p() == EGL_BAD_SURFACE) {
                    stopSwapBuffers = true;
                    closeGLFWWindow();
                }
            }
        } break;

        case RENDERER_VIRGL: {
            glFinish_p();
            vtest_swap_buffers_p();
        } break;

        case RENDERER_VK_ZINK: {
            OSMesaFlushFrontbuffer_p();
            //OSMesaMakeCurrent_p(ctx,buf.bits,GL_UNSIGNED_BYTE,savedWidth,savedHeight);
            glFinish_p();
            //ANativeWindow_lock(potatoBridge.androidWindow,&buf,NULL);
            //glReadPixels_p(0, 0, savedWidth, savedHeight, GL_RGBA, GL_INT, buf.bits);
            //ANativeWindow_unlockAndPost(potatoBridge.androidWindow);
        } break;
    }
}

void* egl_make_current(void* window) {
    EGLBoolean success = eglMakeCurrent_p(
        potatoBridge.eglDisplay,
        window==0 ? (EGLSurface *) 0 : potatoBridge.eglSurface,
        window==0 ? (EGLSurface *) 0 : potatoBridge.eglSurface,
        /* window==0 ? EGL_NO_CONTEXT : */ (EGLContext *) window
    );

    if (success == EGL_FALSE) {
        printf("EGLBridge: Error: eglMakeCurrent() failed: %p\n", eglGetError_p());
    } else {
        printf("EGLBridge: eglMakeCurrent() succeed!\n");
    }

    if (config_renderer == RENDERER_VIRGL) {
        printf("VirGL: vtest_main = %p\n", vtest_main_p);
        printf("VirGL: Calling VTest server's main function\n");
        vtest_main_p(3, (const char*[]){"vtest", "--no-loop-or-fork", "--use-gles", NULL, NULL});
    }
}

bool locked = false;
void pojavMakeCurrent(void* window) {
    //if(OSMesaGetCurrentContext_p() != NULL) {
    //    printf("OSMDroid: skipped context reset\n");
    //    return JNI_TRUE;
    //}
    
    if (config_renderer == RENDERER_GL4ES) {
            EGLContext *currCtx = eglGetCurrentContext_p();
            printf("EGLBridge: Comparing: thr=%d, this=%p, curr=%p\n", gettid(), window, currCtx);
            if (currCtx == NULL || window == 0) {
        /*if (window != 0x0 && potatoBridge.eglContextOld != NULL && potatoBridge.eglContextOld != (void *) window) {
            // Create new pbuffer per thread
            // TODO get window size for 2nd+ window!
            int surfaceWidth, surfaceHeight;
            eglQuerySurface(potatoBridge.eglDisplay, potatoBridge.eglSurface, EGL_WIDTH, &surfaceWidth);
            eglQuerySurface(potatoBridge.eglDisplay, potatoBridge.eglSurface, EGL_HEIGHT, &surfaceHeight);
            int surfaceAttr[] = {
                EGL_WIDTH, surfaceWidth,
                EGL_HEIGHT, surfaceHeight,
                EGL_NONE
            };
            potatoBridge.eglSurface = eglCreatePbufferSurface(potatoBridge.eglDisplay, config, surfaceAttr);
            printf("EGLBridge: created pbuffer surface %p for context %p\n", potatoBridge.eglSurface, window);
        }*/
        //potatoBridge.eglContextOld = (void *) window;
        // eglMakeCurrent(potatoBridge.eglDisplay, EGL_NO_SURFACE, EGL_NO_SURFACE, EGL_NO_CONTEXT);
                printf("EGLBridge: Making current on window %p on thread %d\n", window, gettid());
                egl_make_current((void *)window);

                // Test
#ifdef GLES_TEST
                glClearColor(0.4f, 0.4f, 0.4f, 1.0f);
                glClear(GL_COLOR_BUFFER_BIT);
                eglSwapBuffers(potatoBridge.eglDisplay, potatoBridge.eglSurface);
                printf("First frame error: %p\n", eglGetError());
#endif

                // idk this should convert or just `return success;`...
                return; //success == EGL_TRUE ? JNI_TRUE : JNI_FALSE;
            } else {
                // (*env)->ThrowNew(env,(*env)->FindClass(env,"java/lang/Exception"),"Trace exception");
                return;
            }
    }

    if (config_renderer == RENDERER_VK_ZINK || config_renderer == RENDERER_VIRGL) {
            printf("OSMDroid: making current\n");
            OSMesaMakeCurrent_p((OSMesaContext)window,NULL/*potatoBridge.androidWindow*/,GL_UNSIGNED_BYTE,savedWidth,savedHeight);
            if (config_renderer == RENDERER_VK_ZINK) {
                //ANativeWindow_lock(potatoBridge.androidWindow,&buf,NULL);
                OSMesaPixelStore_p(OSMESA_ROW_LENGTH,buf.stride);
                stride = buf.stride;
                //ANativeWindow_unlockAndPost(potatoBridge.androidWindow);
                OSMesaPixelStore_p(OSMESA_Y_UP,0);
            }

            printf("OSMDroid: vendor: %s\n",glGetString_p(GL_VENDOR));
            printf("OSMDroid: renderer: %s\n",glGetString_p(GL_RENDERER));
            glReadBuffer_p(GL_FRONT);
            glClear_p(GL_COLOR_BUFFER_BIT);
            glClearColor_p(0.4f, 0.4f, 0.4f, 1.0f);
            glFinish_p();

            // Trigger a texture creation, which then set VIRGL_TEXTURE_ID
            int pixelsArr[4];
            glReadPixels_p(0, 0, 1, 1, GL_RGBA, GL_INT, &pixelsArr);
            //printf("R=%d G=%d B=%d A=%d\n", pixelsArr[0], pixelsArr[1], pixelsArr[2], pixelsArr[3]);

            pojavSwapBuffers();

            return;
    }
}

/*
JNIEXPORT void JNICALL
Java_org_lwjgl_glfw_GLFW_nativeEglDetachOnCurrentThread(JNIEnv *env, jclass clazz) {
    //Obstruct the context on the current thread
    
    switch (config_renderer) {
        case RENDERER_GL4ES: {
            eglMakeCurrent_p(potatoBridge.eglDisplay, EGL_NO_SURFACE, EGL_NO_SURFACE, EGL_NO_CONTEXT);
        } break;

        case RENDERER_VIRGL:
        case RENDERER_VK_ZINK: {
            // Nothing to do here
        } break;
    }
}
*/

void* pojavCreateContext(void* contextSrc) {
    if (config_renderer == RENDERER_GL4ES) {
            const EGLint ctx_attribs[] = {
                EGL_CONTEXT_CLIENT_VERSION, atoi(getenv("LIBGL_ES")),
                EGL_NONE
            };
            EGLContext* ctx = eglCreateContext_p(potatoBridge.eglDisplay, config, (void*)contextSrc, ctx_attribs);
            potatoBridge.eglContext = ctx;
            printf("EGLBridge: Created CTX pointer = %p\n",ctx);
            //(*env)->ThrowNew(env,(*env)->FindClass(env,"java/lang/Exception"),"Trace exception");
            return (long)ctx;
    }

    if (config_renderer == RENDERER_VK_ZINK || config_renderer == RENDERER_VIRGL) {
            printf("OSMDroid: generating context\n");
            void* ctx = OSMesaCreateContext_p(OSMESA_RGBA,contextSrc);
            printf("OSMDroid: context=%p\n",ctx);
            return ctx;
    }
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_GL_nativeRegalMakeCurrent(JNIEnv *env, jclass clazz) {
    /*printf("Regal: making current");
    
    RegalMakeCurrent_func *RegalMakeCurrent = (RegalMakeCurrent_func *) dlsym(RTLD_DEFAULT, "RegalMakeCurrent");
    RegalMakeCurrent(potatoBridge.eglContext);*/
    
    printf("regal removed\n");
    abort();
}
JNIEXPORT jlong JNICALL
Java_org_lwjgl_opengl_GL_getGraphicsBufferAddr(JNIEnv *env, jobject thiz) {
    return &gbuffer;
}
JNIEXPORT jintArray JNICALL
Java_org_lwjgl_opengl_GL_getNativeWidthHeight(JNIEnv *env, jobject thiz) {
    jintArray ret = (*env)->NewIntArray(env,2);
    jint arr[] = {savedWidth, savedHeight};
    (*env)->SetIntArrayRegion(env,ret,0,2,arr);
    return ret;
}
void pojavSwapInterval(int interval) {
    switch (config_renderer) {
        case RENDERER_GL4ES:
        case RENDERER_VIRGL: {
            eglSwapInterval_p(potatoBridge.eglDisplay, interval);
        } break;

        case RENDERER_VK_ZINK: {
            printf("eglSwapInterval: NOT IMPLEMENTED YET!\n");
            // Nothing to do here
        } break;
    }
}

