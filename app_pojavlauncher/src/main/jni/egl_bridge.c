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
#include "ctxbridges/osmesa_loader.h"
#include "driver_helper/nsbypass.h"

#ifdef GLES_TEST
#include <GLES2/gl2.h>
#endif

#include <android/native_window.h>
#include <android/native_window_jni.h>
#include <android/rect.h>
#include <string.h>
#include <environ/environ.h>
#include <android/dlext.h>
#include "utils.h"
#include "ctxbridges/gl_bridge.h"

#define GLFW_CLIENT_API 0x22001
/* Consider GLFW_NO_API as Vulkan API */
#define GLFW_NO_API 0
#define GLFW_OPENGL_API 0x30001


struct PotatoBridge {

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

#include "ctxbridges/egl_loader.h"
#include "ctxbridges/osmesa_loader.h"

#define RENDERER_GL4ES 1
#define RENDERER_VK_ZINK 2
#define RENDERER_VULKAN 4

void* gbuffer;

void pojavTerminate() {
    printf("EGLBridge: Terminating\n");

    switch (pojav_environ->config_renderer) {
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
    pojav_environ->pojavWindow = ANativeWindow_fromSurface(env, surface);
    if(pojav_environ->config_renderer == RENDERER_GL4ES) {
        gl_setup_window();
    }
}


JNIEXPORT void JNICALL
Java_net_kdt_pojavlaunch_utils_JREUtils_releaseBridgeWindow(JNIEnv *env, jclass clazz) {
    ANativeWindow_release(pojav_environ->pojavWindow);
}

void* pojavGetCurrentContext() {
    switch (pojav_environ->config_renderer) {
        case RENDERER_GL4ES:
            return (void *)eglGetCurrentContext_p();
        case RENDERER_VK_ZINK:
            return (void *)OSMesaGetCurrentContext_p();

        default: return NULL;
    }
}

/*void dlsym_EGL(void* dl_handle) {
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
    OSMesaPixelStore_p = dlsym(dl_handle,"OSMesaPixelStore");
    glGetString_p = dlsym(dl_handle,"glGetString");
    glClearColor_p = dlsym(dl_handle, "glClearColor");
    glClear_p = dlsym(dl_handle,"glClear");
    glFinish_p = dlsym(dl_handle,"glFinish");
    glReadPixels_p = dlsym(dl_handle,"glReadPixels");
}*/

bool loadSymbols() {
    switch (pojav_environ->config_renderer) {
        case RENDERER_VK_ZINK:
            dlsym_OSMesa();
            break;
        case RENDERER_GL4ES:
            //inside glbridge
            break;
    }
}

//#define ADRENO_POSSIBLE
#ifdef ADRENO_POSSIBLE
//Checks if your graphics are Adreno. Returns true if your graphics are Adreno, false otherwise or if there was an error
bool checkAdrenoGraphics() {
    EGLDisplay eglDisplay = eglGetDisplay(EGL_DEFAULT_DISPLAY);
    if(eglDisplay == EGL_NO_DISPLAY || eglInitialize(eglDisplay, NULL, NULL) != EGL_TRUE) return false;
    EGLint egl_attributes[] = { EGL_BLUE_SIZE, 8, EGL_GREEN_SIZE, 8, EGL_RED_SIZE, 8, EGL_ALPHA_SIZE, 8, EGL_DEPTH_SIZE, 24, EGL_SURFACE_TYPE, EGL_PBUFFER_BIT, EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT, EGL_NONE };
    EGLint num_configs = 0;
    if(eglChooseConfig(eglDisplay, egl_attributes, NULL, 0, &num_configs) != EGL_TRUE || num_configs == 0) {
        eglTerminate(eglDisplay);
        return false;
    }
    EGLConfig eglConfig;
    eglChooseConfig(eglDisplay, egl_attributes, &eglConfig, 1, &num_configs);
    const EGLint egl_context_attributes[] = { EGL_CONTEXT_CLIENT_VERSION, 3, EGL_NONE };
    EGLContext context = eglCreateContext(eglDisplay, eglConfig, EGL_NO_CONTEXT, egl_context_attributes);
    if(context == EGL_NO_CONTEXT) {
        eglTerminate(eglDisplay);
        return false;
    }
    if(eglMakeCurrent(eglDisplay, EGL_NO_SURFACE, EGL_NO_SURFACE, context) != EGL_TRUE) {
        eglDestroyContext(eglDisplay, context);
        eglTerminate(eglDisplay);
    }
    const char* vendor = glGetString(GL_VENDOR);
    const char* renderer = glGetString(GL_RENDERER);
    bool is_adreno = false;
    if(strcmp(vendor, "Qualcomm") == 0 && strstr(renderer, "Adreno") != NULL) {
        is_adreno = true; // TODO: check for Turnip support
    }
    eglMakeCurrent(eglDisplay, EGL_NO_SURFACE, EGL_NO_SURFACE, EGL_NO_CONTEXT);
    eglDestroyContext(eglDisplay, context);
    eglTerminate(eglDisplay);
    return is_adreno;
}
void* load_turnip_vulkan() {
    if(!checkAdrenoGraphics()) return NULL;
    const char* native_dir = getenv("POJAV_NATIVEDIR");
    const char* cache_dir = getenv("TMPDIR");
    linker_ns_load(native_dir);
    void* linkerhook = linker_ns_dlopen("liblinkerhook.so", RTLD_LOCAL | RTLD_NOW);
    void* turnip_driver_handle = linker_ns_dlopen("libvulkan.adr.so", RTLD_LOCAL | RTLD_NOW);
    void (*linkerhook_set_data)(void*, void*, void*) = dlsym(linkerhook, "app__pojav_linkerhook_set_data");
    linkerhook_set_data(turnip_driver_handle, &android_dlopen_ext, &ns_android_get_exported_namespace);
    void* libvulkan = linker_ns_dlopen_unique(cache_dir, "libvulkan.so", RTLD_LOCAL | RTLD_NOW);
    return libvulkan;
}
#endif

static void set_vulkan_ptr(void* ptr) {
    char envval[64];
    sprintf(envval, "%"PRIxPTR, (uintptr_t)ptr);
    setenv("VULKAN_PTR", envval, 1);
    return;
}

void load_vulkan() {
    if(getenv("POJAV_ZINK_PREFER_SYSTEM_DRIVER") == NULL) {
#ifdef ADRENO_POSSIBLE
        void* result = load_turnip_vulkan();
        if(result != NULL) {
            printf("AdrenoSupp: Loaded Turnip, loader address: %p\n", result);
            set_vulkan_ptr(result);
            return;
        }
#endif
    }
    printf("OSMDroid: loading vulkan regularly...\n");
    void* vulkan_ptr = dlopen("libvulkan.so", RTLD_LAZY | RTLD_LOCAL);
    printf("OSMDroid: loaded vulkan, ptr=%p", vulkan_ptr);
    set_vulkan_ptr(vulkan_ptr);
}

int pojavInit() {
    ANativeWindow_acquire(pojav_environ->pojavWindow);
    pojav_environ->savedWidth = ANativeWindow_getWidth(pojav_environ->pojavWindow);
    pojav_environ->savedHeight = ANativeWindow_getHeight(pojav_environ->pojavWindow);
    ANativeWindow_setBuffersGeometry(pojav_environ->pojavWindow,pojav_environ->savedWidth,pojav_environ->savedHeight,AHARDWAREBUFFER_FORMAT_R8G8B8X8_UNORM);
    return 1;
}

int pojavInitOpenGL() {
    // Only affects GL4ES as of now
    const char *forceVsync = getenv("FORCE_VSYNC");
    if (strcmp(forceVsync, "true") == 0)
        pojav_environ->force_vsync = true;

    // NOTE: Override for now.
    const char *renderer = getenv("POJAV_RENDERER");
    if (strncmp("opengles", renderer, 8) == 0) {
        pojav_environ->config_renderer = RENDERER_GL4ES;
        //loadSymbols();
    } else if (strcmp(renderer, "vulkan_zink") == 0) {
        pojav_environ->config_renderer = RENDERER_VK_ZINK;
        load_vulkan();
        setenv("GALLIUM_DRIVER","zink",1);
        loadSymbols();
    }
    if(pojav_environ->config_renderer == RENDERER_GL4ES) {
        if(gl_init()) {
            gl_setup_window(pojav_environ->pojavWindow);
            return 1;
        }
        return 0;
    }
    if (pojav_environ->config_renderer == RENDERER_VK_ZINK) {
        if(OSMesaCreateContext_p == NULL) {
            printf("OSMDroid: %s\n",dlerror());
            return 0;
        }

        printf("OSMDroid: width=%i;height=%i, reserving %i bytes for frame buffer\n", pojav_environ->savedWidth, pojav_environ->savedHeight,
               pojav_environ->savedWidth * 4 * pojav_environ->savedHeight);
        gbuffer = malloc(pojav_environ->savedWidth * 4 * pojav_environ->savedHeight+1);
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

void pojavSetWindowHint(int hint, int value) {
    if (hint != GLFW_CLIENT_API) return;
    switch (value) {
        case GLFW_NO_API:
            pojav_environ->config_renderer = RENDERER_VULKAN;
            /* Nothing to do: initialization is handled in Java-side */
            // pojavInitVulkan();
            break;
        case GLFW_OPENGL_API:
            /* Nothing to do: initialization is called in pojavCreateContext */
            // pojavInitOpenGL();
            break;
        default:
            printf("GLFW: Unimplemented API 0x%x\n", value);
            abort();
    }
}

ANativeWindow_Buffer buf;
int32_t stride;
bool stopSwapBuffers;
void pojavSwapBuffers() {
    if (stopSwapBuffers) {
        return;
    }
    switch (pojav_environ->config_renderer) {
        case RENDERER_GL4ES: {
            gl_swap_buffers();
        } break;

        case RENDERER_VK_ZINK: {
            OSMesaContext ctx = OSMesaGetCurrentContext_p();
            if(ctx == NULL) {
                printf("Zink: attempted to swap buffers without context!");
                break;
            }
            OSMesaMakeCurrent_p(ctx,buf.bits,GL_UNSIGNED_BYTE,pojav_environ->savedWidth,pojav_environ->savedHeight);
            glFinish_p();
            ANativeWindow_unlockAndPost(pojav_environ->pojavWindow);
            //OSMesaMakeCurrent_p(ctx,gbuffer,GL_UNSIGNED_BYTE,savedWidth,savedHeight);
            ANativeWindow_lock(pojav_environ->pojavWindow,&buf,NULL);
        } break;
    }
}


bool locked = false;
void pojavMakeCurrent(void* window) {
    //if(OSMesaGetCurrentContext_p() != NULL) {
    //    printf("OSMDroid: skipped context reset\n");
    //    return JNI_TRUE;
    //}
    if(pojav_environ->config_renderer == RENDERER_GL4ES) {
        gl_make_current((render_window_t*)window);
    }
    if (pojav_environ->config_renderer == RENDERER_VK_ZINK) {
        printf("OSMDroid: making current\n");
        OSMesaMakeCurrent_p((OSMesaContext)window,gbuffer,GL_UNSIGNED_BYTE,pojav_environ->savedWidth,pojav_environ->savedHeight);
        ANativeWindow_lock(pojav_environ->pojavWindow,&buf,NULL);
        OSMesaPixelStore_p(OSMESA_ROW_LENGTH,buf.stride);
        stride = buf.stride;
        //ANativeWindow_unlockAndPost(pojav_environ->pojavWindow);
        OSMesaPixelStore_p(OSMESA_Y_UP,0);

        printf("OSMDroid: vendor: %s\n",glGetString_p(GL_VENDOR));
        printf("OSMDroid: renderer: %s\n",glGetString_p(GL_RENDERER));
        glClear_p(GL_COLOR_BUFFER_BIT);
        glClearColor_p(0.4f, 0.4f, 0.4f, 1.0f);

        pojavSwapBuffers();
        return;
    }
}

/*
JNIEXPORT void JNICALL
Java_org_lwjgl_glfw_GLFW_nativeEglDetachOnCurrentThread(JNIEnv *env, jclass clazz) {
    //Obstruct the context on the current thread
    
    switch (pojav_environ->config_renderer) {
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
    if (pojav_environ->config_renderer == RENDERER_VULKAN) {
        return (void *)pojav_environ->pojavWindow;
    }

    pojavInitOpenGL();

    if (pojav_environ->config_renderer == RENDERER_GL4ES) {
        /*const EGLint ctx_attribs[] = {
            EGL_CONTEXT_CLIENT_VERSION, atoi(getenv("LIBGL_ES")),
            EGL_NONE
        };
        EGLContext* ctx = eglCreateContext_p(potatoBridge.eglDisplay, config, (void*)contextSrc, ctx_attribs);
        potatoBridge.eglContext = ctx;
        printf("EGLBridge: Created CTX pointer = %p\n",ctx);
        //(*env)->ThrowNew(env,(*env)->FindClass(env,"java/lang/Exception"),"Trace exception");
        return (long)ctx;*/
        return gl_init_context(contextSrc);
    }

    if (pojav_environ->config_renderer == RENDERER_VK_ZINK) {
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
    return (jlong) &gbuffer;
}
JNIEXPORT jintArray JNICALL
Java_org_lwjgl_opengl_GL_getNativeWidthHeight(JNIEnv *env, jobject thiz) {
    jintArray ret = (*env)->NewIntArray(env,2);
    jint arr[] = {pojav_environ->savedWidth, pojav_environ->savedHeight};
    (*env)->SetIntArrayRegion(env,ret,0,2,arr);
    return ret;
}
void pojavSwapInterval(int interval) {
    switch (pojav_environ->config_renderer) {
        case RENDERER_GL4ES: {
            gl_swap_interval(interval);
        } break;

        case RENDERER_VK_ZINK: {
            printf("eglSwapInterval: NOT IMPLEMENTED YET!\n");
            // Nothing to do here
        } break;
    }
}

