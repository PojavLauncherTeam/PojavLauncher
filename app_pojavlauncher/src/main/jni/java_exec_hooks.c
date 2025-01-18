//
// Created by maks on 05.01.2025.
//

#include <jni.h>
#include <libgen.h>
#include <string.h>
#include <stdlib.h>
#include <dlfcn.h>

#include <environ/environ.h>
#include <android/log.h>
#include <utils.h>

static jint (*orig_ProcessImpl_forkAndExec)(JNIEnv *env, jobject process, jint mode, jbyteArray helperpath, jbyteArray prog, jbyteArray argBlock, jint argc, jbyteArray envBlock, jint envc, jbyteArray dir, jintArray std_fds, jboolean redirectErrorStream);

// Turn a C-style string into a Java byte array
static jbyteArray stringToBytes(JNIEnv *env, const char* string) {
    const jsize string_data_len = (jsize)(strlen(string) + 1);
    jbyteArray result = (*env)->NewByteArray(env, (jsize)string_data_len);
    (*env)->SetByteArrayRegion(env, result, 0, (jsize)string_data_len, (const jbyte*) string);
    return result;
}

// Replace the env block with the one that has the desired LD_LIBRARY_PATH/PATH.
// (Due to my laziness this ignores the current contents of the block)
static void replaceLibPathInEnvBlock(JNIEnv *env, jbyteArray* envBlock, jint* envc, const char* directory) {
    static bool env_block_replacement_warning = false;
    if(*envBlock != NULL && !env_block_replacement_warning) {
        printf("exec_hooks WARN: replaceLibPathInEnvBlock does not preserve original env. Please notify PojavLauncherTeam if you need that feature\n");
        env_block_replacement_warning = true;
    }
    char envStr[1024];
    jsize new_envl = snprintf(envStr, sizeof(envStr) / sizeof(char), "LD_LIBRARY_PATH=%s%cPATH=%s", directory, 0 ,directory) + 1;
    jbyteArray newBlock = (*env)->NewByteArray(env, new_envl);
    (*env)->SetByteArrayRegion(env, newBlock, 0, new_envl, (jbyte*) envStr);
    *envBlock = newBlock;
    *envc = 2;
}

/**
 * Hooked version of java.lang.UNIXProcess.forkAndExec()
 * which is used to handle the "open" command and "ffmpeg" invocations
 */
static jint hooked_ProcessImpl_forkAndExec(JNIEnv *env, jobject process, jint mode, jbyteArray helperpath, jbyteArray prog, jbyteArray argBlock, jint argc, jbyteArray envBlock, jint envc, jbyteArray dir, jintArray std_fds, jboolean redirectErrorStream) {
    const char *pProg = (char *)((*env)->GetByteArrayElements(env, prog, NULL));
    const char* pProgBaseName = basename(pProg);
    const size_t basename_len = strlen(pProgBaseName);
    char prog_basename[basename_len];
    memcpy(&prog_basename, pProgBaseName, basename_len + 1);
    (*env)->ReleaseByteArrayElements(env, prog, (jbyte *)pProg, 0);

    if(strcmp(prog_basename, "xdg-open") == 0) {
        // When invoking xdg-open, send the open URL into Android
        Java_org_lwjgl_glfw_CallbackBridge_nativeClipboard(env, NULL, CLIPBOARD_OPEN, argBlock);
        return 0;
    }else if(strcmp(prog_basename, "ffmpeg") == 0) {
        // When invoking ffmpeg, always replace the program path with the path to ffmpeg from the plugin.
        // This allows us to replace the executable name, which is needed because android doesn't allow
        // us to put files that don't start with "lib" and end with ".so" into folders that we can execute
        // from

        // Also add LD_LIBRARY_PATH and PATH for the lib in order to override the ones from the launcher, since
        // they may interfere with ffmpeg dependencies.
        const char* ffmpeg_path = getenv("POJAV_FFMPEG_PATH");
        if(ffmpeg_path != NULL) {
            replaceLibPathInEnvBlock(env, &envBlock, &envc, dirname(ffmpeg_path));
            prog = stringToBytes(env, ffmpeg_path);
        }
    }
    return orig_ProcessImpl_forkAndExec(env, process, mode, helperpath, prog, argBlock, argc, envBlock, envc, dir, std_fds, redirectErrorStream);
}

// Hook the forkAndExec method in the Java runtime for custom executable overriding.
void hookExec() {
    jclass cls;
    orig_ProcessImpl_forkAndExec = dlsym(RTLD_DEFAULT, "Java_java_lang_UNIXProcess_forkAndExec");
    if (!orig_ProcessImpl_forkAndExec) {
        orig_ProcessImpl_forkAndExec = dlsym(RTLD_DEFAULT, "Java_java_lang_ProcessImpl_forkAndExec");
        cls = (*pojav_environ->runtimeJNIEnvPtr_JRE)->FindClass(pojav_environ->runtimeJNIEnvPtr_JRE, "java/lang/ProcessImpl");
    } else {
        cls = (*pojav_environ->runtimeJNIEnvPtr_JRE)->FindClass(pojav_environ->runtimeJNIEnvPtr_JRE, "java/lang/UNIXProcess");
    }
    JNINativeMethod methods[] = {
            {"forkAndExec", "(I[B[B[BI[BI[B[IZ)I", (void *)&hooked_ProcessImpl_forkAndExec}
    };
    (*pojav_environ->runtimeJNIEnvPtr_JRE)->RegisterNatives(pojav_environ->runtimeJNIEnvPtr_JRE, cls, methods, 1);
    printf("Registered forkAndExec\n");
}