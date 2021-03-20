//
// Created by znix on 20/03/21.
//

#pragma once

#include <string>

#include <android/log.h>

#define LOGE(...) log(ANDROID_LOG_ERROR,   TAG, __VA_ARGS__)
#define LOGW(...) log(ANDROID_LOG_WARN,    TAG, __VA_ARGS__)
#define LOGI(...) log(ANDROID_LOG_INFO,    TAG, __VA_ARGS__)
#define LOGD(...) log(ANDROID_LOG_DEBUG,   TAG, __VA_ARGS__)

void log(android_LogPriority level, const char *tag, std::string message, ...);
