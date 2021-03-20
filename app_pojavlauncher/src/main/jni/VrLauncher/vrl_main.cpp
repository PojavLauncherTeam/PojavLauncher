//
// Created by znix on 20/03/21.
//

#include "vrl_main.h"

#include <exception>
#include <unistd.h>
#include <string.h>
#include <string>

#include "android_native_app_glue.h"
#include <jni.h>
#include <EGL/egl.h>

// These specifically have to come after the JNI and EGL includes
#include <openxr/openxr.h>
#include <openxr/openxr_platform.h>

#include <oc_stub.h>

// Set up our custom log tag
#include "log.h"

#define TAG "VrLauncher"

//////

android_app *current_app = nullptr;

struct AndroidAppState {
    bool Resumed;
    ANativeWindow *NativeWindow;
    IMainApplication *app;
};

static void app_handle_cmd(struct android_app *app, int32_t cmd) {
    auto *appState = (AndroidAppState *) app->userData;

    switch (cmd) {
        // There is no APP_CMD_CREATE. The ANativeActivity creates the
        // application thread from onCreate(). The application thread
        // then calls android_main().
        case APP_CMD_START: {
            LOGI("    APP_CMD_START");
            LOGI("onStart()");
            break;
        }
        case APP_CMD_RESUME: {
            LOGI("onResume()");
            LOGI("    APP_CMD_RESUME");
            appState->Resumed = true;
            break;
        }
        case APP_CMD_PAUSE: {
            LOGI("onPause()");
            LOGI("    APP_CMD_PAUSE");
            appState->Resumed = false;
            break;
        }
        case APP_CMD_STOP: {
            LOGI("onStop()");
            LOGI("    APP_CMD_STOP");

            if (appState->app) {
                LOGI("Destroying app");
                appState->app->Shutdown();
                delete appState->app;
                appState->app = nullptr;
            }

            break;
        }
        case APP_CMD_DESTROY: {
            LOGI("onDestroy()");
            LOGI("    APP_CMD_DESTROY");
            appState->NativeWindow = nullptr;
            break;
        }
        case APP_CMD_INIT_WINDOW: {
            LOGI("surfaceCreated()");
            LOGI("    APP_CMD_INIT_WINDOW");
            appState->NativeWindow = app->window;

            if (appState->app) {
                appState->app->Shutdown();
                delete appState->app;
                appState->app = nullptr;
                LOGW("Shutting down previous app");
            }

            // Start the app
            appState->app = CreateApplication();

            if (!appState->app->BInit()) {
                LOGE("Failed to BInit app");
                appState->app->Shutdown();
                delete appState->app;
                appState->app = nullptr;
                return;
            }

            break;
        }
        case APP_CMD_TERM_WINDOW: {
            LOGI("surfaceDestroyed()");
            LOGI("    APP_CMD_TERM_WINDOW");
            appState->NativeWindow = nullptr;
            break;
        }
        default: {
            LOGI("Unknown APP_CMD: " + std::to_string((int) cmd));
            break;
        }
    }
}

// Our little hack to pass setup data to OC
XrInstanceCreateInfoAndroidKHR *OpenComposite_Android_Create_Info = nullptr;

/**
 * This is the main entry point of a native application that is using
 * android_native_app_glue.  It runs in its own thread, with its own
 * event loop for receiving input events and doing other things.
 */
void android_main(struct android_app *app) {
    current_app = app;

    try {
        AndroidAppState userData = {};
        app->userData = &userData;

        app->onAppCmd = app_handle_cmd;

        JNIEnv *Env;
        app->activity->vm->AttachCurrentThread(&Env, nullptr);

        bool requestRestart = false;
        bool exitRenderLoop = false;

        // Setup the info that OpenComposite will pass to the OpenXR runtime
        OCWrapper_InitActivity(app->activity->vm, app->activity->clazz);

        // Initialize the loader for this platform
        PFN_xrInitializeLoaderKHR initializeLoader = nullptr;
        if (XR_SUCCEEDED(
                xrGetInstanceProcAddr(XR_NULL_HANDLE, "xrInitializeLoaderKHR",
                                      (PFN_xrVoidFunction *) (&initializeLoader)))) {
            XrLoaderInitInfoAndroidKHR loaderInitInfoAndroid;
            memset(&loaderInitInfoAndroid, 0, sizeof(loaderInitInfoAndroid));
            loaderInitInfoAndroid.type = XR_TYPE_LOADER_INIT_INFO_ANDROID_KHR;
            loaderInitInfoAndroid.next = nullptr;
            loaderInitInfoAndroid.applicationVM = app->activity->vm;
            loaderInitInfoAndroid.applicationContext = app->activity->clazz;
            initializeLoader((const XrLoaderInitInfoBaseHeaderKHR *) &loaderInitInfoAndroid);
        }

        while (app->destroyRequested == 0) {
            // Read all pending events.
            for (;;) {
                int events;
                struct android_poll_source *source;
                // If the timeout is zero, returns immediately without blocking.
                // If the timeout is negative, waits indefinitely until an event appears.
                // const int timeoutMilliseconds =
                //         (!appState.Resumed && !program->IsSessionRunning() &&
                //          app->destroyRequested == 0) ? -1 : 0;
                const int timeoutMilliseconds = 0;
                if (ALooper_pollAll(timeoutMilliseconds, nullptr, &events, (void **) &source) < 0) {
                    break;
                }

                // Process this event.
                if (source != nullptr) {
                    source->process(app, source);
                }
            }

            // Throttle while not displaying anything
            if (!userData.app) {
                usleep(100 * 1000);
                continue;
            }

            // Let the app render a frame, if it's active
            bool bQuit = userData.app->HandleInput();
            if (bQuit) break;

            userData.app->RenderFrame();
        }

        app->activity->vm->DetachCurrentThread();
    } catch (const std::exception &ex) {
        LOGE("caught exception in main loop: %s", ex.what());
    } catch (...) {
        LOGE("Unknown Error");
    }

    OpenComposite_Android_Create_Info = nullptr;
    current_app = nullptr;
}
