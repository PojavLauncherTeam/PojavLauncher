#ifndef __ANDROID_DEBUG_H__
#define __ANDROID_DEBUG_H__

// Redirect printf() to Android log
// Put this file into CFLAGS: "-include ../android_debug.h"

#include <stdio.h>
#include <stdarg.h>
#include <android/log.h>

//#define printf(...) __android_log_print(ANDROID_LOG_INFO, "LIBGL", __VA_ARGS__)

#endif

