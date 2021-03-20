//
// Created by znix on 20/03/21.
//

#include "vrl_main.h"
#include "log.h"
#include "start_java.h"

#include <android/native_activity.h>
#include <EGL/egl.h>
#include <GLES3/gl32.h>
#include <dlfcn.h>
#include <fcntl.h>
#include <signal.h>
#include <unistd.h>
#include <errno.h>

#include <openxr/openxr_platform.h>

//#include <Misc/android_api.h>
#include <vector>

#include "openvr.h"
#include <oc_stub.h>

#define TAG "JRTwrap"

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
};

IMainApplication *CreateApplication() {
    return new CMainApplication;
}

CMainApplication::CMainApplication() = default;

CMainApplication::~CMainApplication() = default;

bool CMainApplication::BInit() {
    // Before we do anything else load the OpenXR runtime since it seems to choke
    // after we start openjdk
    vr::EVRInitError err;
    vr::VR_Init(&err, vr::VRApplication_Scene);
    OCWrapper_IgnoreNextVRInitCall(); // Otherwise the app will fail when it tries to also start VR
    if (err != 0) {
        LOGE("Failed to start OpenComposite: %d", err);
        return false;
    }

    // First call JREUtils.setupFUnc

    JNIEnv *env;
    int res = current_app->activity->vm->AttachCurrentThread(&env, nullptr);
    if (res) {
        LOGE("Failed to attach to ART: %d", res);
        return false;
    }

    jclass clss = env->GetObjectClass(current_app->activity->clazz);
    jmethodID setupFunc = env->GetMethodID(clss, "setup", "()V");
    if (setupFunc == nullptr) {
        LOGD("Cannot find QuestMainActivity.setup");
        return false;
    }

    env->CallVoidMethod(current_app->activity->clazz, setupFunc);

    res = current_app->activity->vm->DetachCurrentThread();
    if (res) {
        LOGE("Failed to detach from ART: %d", res);
        return false;
    }

    ////

    std::vector<std::string> args;

    // FIXME hardcoded blob, find all the jars there by calling Tools.getLWJGL3ClassPath
    std::string lwjgl = "/storage/emulated/0/games/PojavLauncher/lwjgl3/jsr305.jar:/storage/emulated/0/games/PojavLauncher/lwjgl3/lwjgl-glfw-classes.jar:/storage/emulated/0/games/PojavLauncher/lwjgl3/lwjgl-jemalloc.jar:/storage/emulated/0/games/PojavLauncher/lwjgl3/lwjgl-openal.jar:/storage/emulated/0/games/PojavLauncher/lwjgl3/lwjgl-opengl.jar:/storage/emulated/0/games/PojavLauncher/lwjgl3/lwjgl-stb.jar:/storage/emulated/0/games/PojavLauncher/lwjgl3/lwjgl-tinyfd.jar:/storage/emulated/0/games/PojavLauncher/lwjgl3/lwjgl.jar";

    args.emplace_back("java");
    args.emplace_back("-Dorg.lwjgl.opengl.libname=libgl4es_114.so");
    args.emplace_back("-cp");
    args.emplace_back(lwjgl + ":/storage/emulated/0/games/PojavLauncher/test.jar");
    args.emplace_back("xyz.znix.graphicstest.Main");

    // Create a mutable version of the args for Java to fiddle with
    int argv_size = (int) (sizeof(char *) * (args.size() + 1));
    char **argv = (char **) malloc(argv_size);
    memset(argv, 0, argv_size);
    for (int i = 0; i < args.size(); i++) {
        argv[i] = strdup(args.at(i).c_str());
    }

    // START OPENJDK
    start_java(args.size(), argv);

    // Destroy our mutable version of the args
    for (int i = 0; i < args.size(); i++) {
        free(argv[i]);
    }
    free(argv);

    return true;
}

void CMainApplication::Shutdown() {
    // TODO
}

bool CMainApplication::HandleInput() {
    return false;
}

void CMainApplication::RenderFrame() {
    // TODO
}
