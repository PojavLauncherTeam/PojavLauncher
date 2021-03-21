#include "oc_stub.h"

#include <string>

#include <openxr/openxr.h>
#include <openxr/openxr_platform.h>

#include "../log.h"

#include "Misc/android_api.h"
#include "steamvr_abi.h" // thanks for the convenient header, OpenComposite

XrInstanceCreateInfoAndroidKHR *OpenComposite_Android_Create_Info = nullptr;
XrGraphicsBindingOpenGLESAndroidKHR *OpenComposite_Android_GLES_Binding_Info = nullptr;

std::string (*OpenComposite_Android_Load_Input_File)(const char *path);

static std::string load_file(const char *path);

void OCWrapper_InitEGL(const OCWrapper_EGLInitInfo *info) {
    OpenComposite_Android_Load_Input_File = load_file;

    // Tiny memory leak, doesn't matter
    auto *eglInfo = new XrGraphicsBindingOpenGLESAndroidKHR;
    *eglInfo = {XR_TYPE_GRAPHICS_BINDING_OPENGL_ES_ANDROID_KHR};
    eglInfo->display = info->display;
    eglInfo->config = info->config;
    eglInfo->context = info->context;
    OpenComposite_Android_GLES_Binding_Info = eglInfo;

    LOGI("Setup OpenComposite variables");
}

extern "C" JNIEXPORT void JNICALL
Java_net_kdt_pojavlaunch_OpenComposite_bindActivity(JNIEnv *env, jclass type, jobject activity) {
    JavaVM *vm = nullptr;
    int err = (*env).GetJavaVM(&vm);
    if (err) {
        LOGE("bindActivity: Failed to get Java VM");
    }

    // Yeah this leaks a reference, but afaik this activity lasts as long as the VR app does.
    activity = env->NewGlobalRef(activity);

    // Now setup the OpenXR loader, if it hasn't already been
    static bool loaderInitialised = false;
    if (!loaderInitialised) {
        PFN_xrInitializeLoaderKHR initializeLoader = nullptr;
        XrResult res;

        res = xrGetInstanceProcAddr(XR_NULL_HANDLE, "xrInitializeLoaderKHR",
                                    (PFN_xrVoidFunction *) (&initializeLoader));
        if (!XR_SUCCEEDED(res)) {
            jclass exClass = env->FindClass("java/lang/NoClassDefFoundError");
            env->ThrowNew(exClass, "Could not find address of xrInitializeLoaderKHR");
            return;
        }

        XrLoaderInitInfoAndroidKHR loaderInitInfoAndroid;
        memset(&loaderInitInfoAndroid, 0, sizeof(loaderInitInfoAndroid));
        loaderInitInfoAndroid.type = XR_TYPE_LOADER_INIT_INFO_ANDROID_KHR;
        loaderInitInfoAndroid.next = nullptr;
        loaderInitInfoAndroid.applicationVM = vm;
        loaderInitInfoAndroid.applicationContext = activity; // An activity is a context - this is correct?
        res = initializeLoader((const XrLoaderInitInfoBaseHeaderKHR *) &loaderInitInfoAndroid);

        LOGD("Init loader with VM %p and activity %p", vm, activity);

        if (!XR_SUCCEEDED(res)) {
            jclass exClass = env->FindClass("java/lang/NoClassDefFoundError");
            env->ThrowNew(exClass, "Could not call xrInitializeLoaderKHR");
            return;
        }

        loaderInitialised = true;
    }

    // Tiny memory leak, doesn't matter
    auto *info = new XrInstanceCreateInfoAndroidKHR;
    *info = {XR_TYPE_INSTANCE_CREATE_INFO_ANDROID_KHR};
    info->applicationVM = vm;
    info->applicationActivity = activity;

    OpenComposite_Android_Create_Info = info;

    LOGI("Set OpenXR create android-specific info");
}

void OCWrapper_InitActivity(JavaVM *vm, jobject activity) {
    auto *createInfo = new XrInstanceCreateInfoAndroidKHR;
    *createInfo = {XR_TYPE_INSTANCE_CREATE_INFO_ANDROID_KHR};
    createInfo->applicationVM = vm;
    createInfo->applicationActivity = activity;
    OpenComposite_Android_Create_Info = createInfo;
}

void OCWrapper_Cleanup() {
    OpenComposite_Android_Create_Info = nullptr;
    OpenComposite_Android_GLES_Binding_Info = nullptr;

    LOGI("Unset OpenComposite variables");
}

static std::string load_file(const char *path) {
    LOGE("questcraft stub: Cannot load input manifest file '%s', OpenComposite_Android_Load_Input_File not implemented",
         path);
    abort();
}

// The original implementation that we renamed with a sneaky define :) - see our cmakelists file
VR_INTERFACE uint32_t VR_CALLTYPE
VR_InitInternal2_orig(EVRInitError *err, EVRApplicationType appType, const char *unused);

static bool ignoreNextInitCall = false;

VR_INTERFACE uint32_t VR_CALLTYPE
VR_InitInternal2(EVRInitError *err, EVRApplicationType appType, const char *unused) {
    LOGI("VR_InitInternal2 hook called, ignoring: %d", ignoreNextInitCall);

    if (ignoreNextInitCall) {
        // Hacking this in: while we're here, bind this thread if necessary as an Android Java thread
        // Otherwise the Oculus runtime will spam this multiple times per frame:
        // I/JniUtils: Creating temporary JNIEnv. This is a heavy operation and should be infrequent. To optimize, use JNI AttachCurrentThread on calling thread
        auto *vm = (JavaVM *) OpenComposite_Android_Create_Info->applicationVM;
        JNIEnv *env = nullptr;
        if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
            LOGE("Failed to get JNIEnv, attaching as Android JNI thread");
            vm->AttachCurrentThread(&env, nullptr);
        }

        ignoreNextInitCall = false;
        return VR_GetInitToken();
    }

    return VR_InitInternal2_orig(err, appType, unused);
}

VR_INTERFACE uint32_t VR_CALLTYPE VR_InitInternal(EVRInitError *err, EVRApplicationType appType) {
    return VR_InitInternal2(err, appType, nullptr);
}

void OCWrapper_IgnoreNextVRInitCall() {
    ignoreNextInitCall = true;
}

void OCWrapper_PollEventsXR() {
    OpenComposite_Android_EventPoll();
}
