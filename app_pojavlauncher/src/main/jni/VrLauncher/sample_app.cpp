//
// Created by znix on 20/03/21.
//

#include "vrl_main.h"
#include "log.h"

#include <android/native_activity.h>
#include <EGL/egl.h>

#include <openxr/openxr_platform.h>

//#include <Misc/android_api.h>

#include <GLES3/gl32.h>

#include "openvr.h"
#include "oc_stub.h"

#define TAG "testapp"

class EyeTarget;

class CMainApplication : public IMainApplication {
public:
    CMainApplication();

    ~CMainApplication() override;

    bool BInit() override;

    void Shutdown() override;

    bool HandleInput() override;

    void RenderFrame() override;

private:
    vr::IVRSystem *openvr = nullptr;
    EGLContext context = nullptr;
    EGLDisplay display = nullptr;
    EGLSurface surface = nullptr;

    std::unique_ptr<EyeTarget> eyes[2];
};

class EyeTarget {
public:
    EyeTarget(int width, int height);

    ~EyeTarget();

    GLuint tex = 0;
    GLuint depth = 0;
    GLuint fb = 0;
};

IMainApplication *CreateApplication() {
    return new CMainApplication;
}

// Not going to implement the former
// std::string (*OpenComposite_Android_Load_Input_File)(const char *path) = nullptr;
// XrGraphicsBindingOpenGLESAndroidKHR *OpenComposite_Android_GLES_Binding_Info = nullptr;

////

CMainApplication::CMainApplication() = default;

CMainApplication::~CMainApplication() = default;

bool CMainApplication::BInit() {

    // Start OpenComposite
    vr::EVRInitError err;
    openvr = vr::VR_Init(&err, vr::VRApplication_Scene, nullptr);

    if (err != vr::VRInitError_None) {
        LOGE("Failed to init openvr: %d", err);
        return false;
    }


    // EGL setup stuff from https://github.com/tsaarni/android-native-egl-example/blob/master/src/main/jni/renderer.cpp
    // Apache-2 licenced
    if ((display = eglGetDisplay(EGL_DEFAULT_DISPLAY)) == EGL_NO_DISPLAY) {
        LOGE("eglGetDisplay() returned error %d", eglGetError());
        return false;
    }
    if (!eglInitialize(display, nullptr, nullptr)) {
        LOGE("eglInitialize() returned error %d", eglGetError());
        return false;
    }
    if (!eglBindAPI(EGL_OPENGL_ES_API)) {
        LOGE("Failed to bind GL API: %d", eglGetError());
        return false;
    }

    const EGLint attribs[] = {
            EGL_SURFACE_TYPE, EGL_WINDOW_BIT,
            EGL_BLUE_SIZE, 8,
            EGL_GREEN_SIZE, 8,
            EGL_RED_SIZE, 8,
            EGL_DEPTH_SIZE, 16,
            EGL_NONE
    };
    EGLConfig configs[10];
    EGLint numConfigs;
    if (!eglChooseConfig(display, attribs, configs, 10, &numConfigs)) {
        LOGE("eglChooseConfig() returned error %d", eglGetError());
        return false;
    }
    EGLConfig config = configs[0];
    EGLint format;
    if (!eglGetConfigAttrib(display, config, EGL_NATIVE_VISUAL_ID, &format)) {
        LOGE("eglGetConfigAttrib() returned error %d", eglGetError());
        return false;
    }
    // TODO do we need this?
    ANativeWindow_setBuffersGeometry(current_app->window, 0, 0, format);

    if (!(surface = eglCreateWindowSurface(display, config, current_app->window, 0))) {
        LOGE("eglCreateWindowSurface() returned error %d", eglGetError());
        return false;
    }
    EGLint attrs[] = {EGL_CONTEXT_CLIENT_VERSION, 3, EGL_NONE};
    if (!(context = eglCreateContext(display, config, 0, attrs))) {
        LOGE("eglCreateContext() returned error %d", eglGetError());
        return false;
    }
    if (!eglMakeCurrent(display, surface, surface, context)) {
        LOGE("eglMakeCurrent() returned error %d", eglGetError());
        return false;
    }

    // Create the eye textures
    uint32_t width, height;
    openvr->GetRecommendedRenderTargetSize(&width, &height);
    eyes[0] = std::make_unique<EyeTarget>(width, height);
    eyes[1] = std::make_unique<EyeTarget>(width, height);

    // Make the GLES data available to OpenComposite
    OCWrapper_EGLInitInfo info = {};
    info.display = display;
    info.context = context;
    info.config = config;
    OCWrapper_InitEGL(&info);

    return true;
}

void CMainApplication::Shutdown() {
    eglMakeCurrent(display, EGL_NO_SURFACE, EGL_NO_SURFACE, EGL_NO_CONTEXT);
    eglDestroyContext(display, context);
    eglDestroySurface(display, surface);
    // AFAIK that's everything?

    OCWrapper_Cleanup();
}

bool CMainApplication::HandleInput() {
    return false;
}

void CMainApplication::RenderFrame() {
    vr::VRCompositor()->WaitGetPoses(nullptr, 0, nullptr, 0);

    glBindFramebuffer(GL_FRAMEBUFFER, eyes[0]->fb);
    glClearColor(0, 1, 1, 1);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    glBindFramebuffer(GL_FRAMEBUFFER, 0);

    for (int i = 0; i < 2; i++) {
        vr::Texture_t tex = {};
        tex.eColorSpace = vr::ColorSpace_Gamma;
        tex.eType = vr::TextureType_OpenGL;
        tex.handle = (void *) (uint64_t) (eyes[i]->tex);
        vr::VRCompositor()->Submit((vr::EVREye) i, &tex);
    }
}

// Eye targets

EyeTarget::EyeTarget(int width, int height) {
    // Create the framebuffer
    glGenFramebuffers(1, &fb);
    glBindFramebuffer(GL_FRAMEBUFFER, fb);

    // For some reason we have to use a depth/stencil texture rather than a depth buffer? why?
    //  is this due to it needing a stencil, or needing it to be a texture?
    glGenTextures(1, &depth);
    glBindTexture(GL_TEXTURE_2D, depth);
    glTexStorage2D(GL_TEXTURE_2D, 1, GL_DEPTH24_STENCIL8, width, height);
    glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_TEXTURE_2D, depth, 0);

    glGenTextures(1, &tex);
    glBindTexture(GL_TEXTURE_2D, tex);
    glTexStorage2D(GL_TEXTURE_2D, 1, GL_RGBA8, width, height);
    glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, tex, 0);

    // check FBO status
    GLenum status = glCheckFramebufferStatus(GL_FRAMEBUFFER);
    if (status != GL_FRAMEBUFFER_COMPLETE) {
        LOGE("Failed to create framebuffer!");
    }

    glBindFramebuffer(GL_FRAMEBUFFER, 0);
}

EyeTarget::~EyeTarget() {
    glDeleteFramebuffers(1, &fb);
    glDeleteTextures(1, &tex);
    glDeleteTextures(1, &depth);
}
