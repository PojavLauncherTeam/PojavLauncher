#ifndef _BINARY_UTILS_H_
#define _BINARY_UTILS_H_

JavaVM *runtimeJavaVMPtr;
JNIEnv *runtimeJNIEnvPtr;

JavaVM *dalvikJavaVMPtr;
JNIEnv *dalvikJNIEnvPtr;

int isEglMakeCurrentCalled;

char** convert_to_char_array(JNIEnv *env, jobjectArray jstringArray);
jobjectArray convert_from_char_array(JNIEnv *env, char **charArray, int num_rows);
void free_char_array(JNIEnv *env, jobjectArray jstringArray, const char **charArray);

#endif // _BINARY_UTILS_H_

