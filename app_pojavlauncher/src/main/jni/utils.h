#pragma once

#include <stdbool.h>

typedef struct {
    bool detach;
    JNIEnv *env;
} scope_t;

bool scopeAttach(JavaVM*, scope_t*);
void scopeDetach(JavaVM*, scope_t*);

char** convert_to_char_array(JNIEnv *env, jobjectArray jstringArray);
jobjectArray convert_from_char_array(JNIEnv *env, char **charArray, int num_rows);
void free_char_array(JNIEnv *env, jobjectArray jstringArray, const char **charArray);
jstring convertStringJVM(JNIEnv* srcEnv, JNIEnv* dstEnv, jstring srcStr);

void hookExec();
void installLinkerBugMitigation();
void installEMUIIteratorMititgation();
JNIEXPORT jstring JNICALL Java_org_lwjgl_glfw_CallbackBridge_nativeClipboard(JNIEnv* env, jclass clazz, jint action, jbyteArray copySrc);

