//
// Created by znix on 20/03/21.
//

#pragma once

#include <android/native_activity.h>
#include <android_native_app_glue.h>

class IMainApplication {
public:
    virtual ~IMainApplication() = default;

    virtual void Shutdown() = 0;

    virtual bool BInit() = 0;

    virtual bool HandleInput() = 0;

    virtual void RenderFrame() = 0;

    virtual bool SleepPoll() = 0;
};

IMainApplication *CreateApplication();

extern android_app *current_app;

