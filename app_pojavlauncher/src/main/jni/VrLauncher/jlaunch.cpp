//
// Created by znix on 20/03/21.
//

#include <android/native_activity.h>
#include <android_native_app_glue.h>

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
#include <thread>

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

    bool SleepPoll() override;

private:
    int RunJVM(std::vector<std::string> args);

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

    // First call JREUtils.setupFunc

    JNIEnv *env;
    int res = current_app->activity->vm->AttachCurrentThread(&env, nullptr);
    if (res) {
        LOGE("Failed to attach to ART: %d", res);
        return false;
    }

    jclass clss = env->GetObjectClass(current_app->activity->clazz);
    jmethodID setupFunc = env->GetMethodID(clss, "setup", "()V");
    if (setupFunc == nullptr) {
        LOGE("Cannot find QuestMainActivity.setup");
        return false;
    }

    env->CallVoidMethod(current_app->activity->clazz, setupFunc);

    // Get the Java arguments list
    jmethodID getArgsFunc = env->GetMethodID(clss, "getJavaArgs", "()[Ljava/lang/String;");
    if (getArgsFunc == nullptr) {
        LOGE("Cannot find QuestMainActivity.getJavaArgs");
        return false;
    }
    auto arr = (jobjectArray) env->CallObjectMethod(current_app->activity->clazz, getArgsFunc);

    // Read out the items
    std::vector<std::string> args(env->GetArrayLength(arr));
    LOGI("Arg count: %d", args.size());
    for (int i = 0; i < args.size(); i++) {
        auto arg = (jstring) env->GetObjectArrayElement(arr, i);
        const char *str = env->GetStringUTFChars(arg, nullptr);
        args.at(i) = str;
        env->ReleaseStringUTFChars(arg, str);
    }

    // Cleanup
    res = current_app->activity->vm->DetachCurrentThread();
    if (res) {
        LOGE("Failed to detach from ART: %d", res);
        return false;
    }

    ////

    std::thread thread([this](std::vector<std::string> args) {
        LOGI("Start JVM");
        int result = RunJVM(std::move(args));
        LOGE("JVM exited with error code: %d", result);
    }, std::move(args));
    thread.detach();

    return true;
}

int CMainApplication::RunJVM(std::vector<std::string> args) {
    // Create a mutable version of the args for Java to fiddle with
    int argv_size = (int) (sizeof(char *) * (args.size() + 1));
    char **argv = (char **) malloc(argv_size);
    memset(argv, 0, argv_size);
    for (int i = 0; i < args.size(); i++) {
        argv[i] = strdup(args.at(i).c_str());
        LOGI("launch arg: %s", argv[i]);
    }

    // START OPENJDK
    int res = start_java(args.size(), argv);

    // Destroy our mutable version of the args
    for (int i = 0; i < args.size(); i++) {
        free(argv[i]);
    }
    free(argv);

    return res;
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

bool CMainApplication::SleepPoll() {
    // TODO
    return false;
}
