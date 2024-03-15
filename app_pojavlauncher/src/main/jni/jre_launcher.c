/*
 * Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
#include <android/log.h>
#include <dlfcn.h>
#include <errno.h>
#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <unistd.h>
// Boardwalk: missing include
#include <string.h>

#include "log.h"
#include "utils.h"
#include "environ/environ.h"

typedef jint (*JNI_CreateJavaVM_t)(JavaVM**, JNIEnv**, void*);

typedef struct {
    void* handle;
    JNI_CreateJavaVM_t JNI_CreateJavaVM;
} jvm_library_t;

bool load_vm_library(JNIEnv *env, jstring path, jvm_library_t* library) {
    void* handle;
    if(path) {
        const char* jvm_path = (*env)->GetStringUTFChars(env, path, NULL);
        if(jvm_path == NULL) {
            printf("failed to get UTF contents of jvm_path\n");
            return false;
        }
        handle = dlopen(jvm_path, RTLD_NOW);
        (*env)->ReleaseStringUTFChars(env, path, jvm_path);
    } else {
        handle = dlopen("libjvm.so", RTLD_NOW);
    }
    if(!handle) {
        printf("failed to load JVM: %s\n", dlerror());
        return false;
    }
    library->JNI_CreateJavaVM = dlsym(handle, "JNI_CreateJavaVM");
    if(!library->JNI_CreateJavaVM) {
        printf("failed to load JVM: %s\n", dlerror());
        dlclose(handle);
        return false;
    }
    library->handle = handle;
    return true;
}

void unload_vm_library(jvm_library_t* library) {
    dlclose(library->handle);
}

bool transfer_arg(JNIEnv* env, jstring argJString, const char** argStringp) {
    jsize strlen = (*env)->GetStringUTFLength(env, argJString);
    char* argString = malloc((strlen + 1) * sizeof(char));
    if(argString == NULL) return false;
    (*env)->GetStringUTFRegion(env, argJString, 0, strlen, argString);
    // Add null terminator just in case if the implementation does not null terminate strings
    argString[strlen] = 0;
    *argStringp = argString;
    return true;
}

void free_vm_args(JavaVMOption* args, jsize length) {
    for(jsize i = 0; i < length; i++) {
        if(args[i].optionString != NULL) free((void*)args[i].optionString);
    }
}

static void exceptionCheck(JNIEnv *env) {
    if(!(*env)->ExceptionCheck(env)) return;
    (*env)->ExceptionDescribe(env);
    (*env)->ExceptionClear(env);
}

bool initialize_vm_args(JNIEnv* env, jobjectArray vmArgs, jsize length, JavaVMOption* args) {
    char* failureReason;
    if(args == NULL) {
        failureReason = "Out of memory (JavaVMOption* allocation)";
        goto fail;
    }
    if((*env)->PushLocalFrame(env, length) < 0) {
        exceptionCheck(env);
        failureReason = "Out of memory (local frame allocation)";
        goto fail;
    }

    for(jsize i = 0; i < length; i++) {
        jstring argJString = (*env)->GetObjectArrayElement(env, vmArgs, i);
        if(argJString == NULL) {
            exceptionCheck(env);
            failureReason = "One of the argument strings is NULL";
            goto fail_frame;
        }
        if(!transfer_arg(env, argJString, &(args[i].optionString))) {
            exceptionCheck(env);
            failureReason = "Argument string transfer failed";
            goto fail_frame;
        }
    }
    (*env)->PopLocalFrame(env, NULL);
    return true;
    fail_frame:
    (*env)->PopLocalFrame(env, NULL);
    fail:
    free_vm_args(args, length);
    printf("%s\n",failureReason);
    return false;
}

bool transfer_app_args(JNIEnv* host_env, JNIEnv* guest_env, jobjectArray hostArray, jobjectArray guestArray, jsize length) {
    char* failureReason;
    if((*host_env)->PushLocalFrame(host_env, length) < 0) {
        failureReason = "Out of memory (host local frame allocation)";
        goto fail;
    }
    if((*guest_env)->PushLocalFrame(guest_env, length) < 0) {
        failureReason = "Out of memory (guest local frame allocation)";
        goto fail1;
    }
    for(jsize i = 0; i < length; i++) {
        jstring hostEntry = (*host_env)->GetObjectArrayElement(host_env, hostArray, i);
        // All entries are initialized to null by default, so we can just skip null host entries
        if(hostEntry == NULL) continue;
        const char* hostEntryUTF = (*host_env)->GetStringUTFChars(host_env, hostEntry, NULL);
        if(hostEntryUTF == NULL) {
            failureReason = "Unable to get UTF contents of application argument string";
            goto fail2;
        }
        jstring guestEntry = (*guest_env)->NewStringUTF(guest_env, hostEntryUTF);
        if(guestEntry == NULL) {
            failureReason = "Out of memory (guest argument string allocation)";
            goto fail2;
        }
        (*guest_env)->SetObjectArrayElement(guest_env, guestArray, i, guestEntry);
    }
    (*guest_env)->PopLocalFrame(guest_env, NULL);
    (*host_env)->PopLocalFrame(host_env, NULL);
    return true;
    fail2:
    exceptionCheck(guest_env);
    (*guest_env)->PopLocalFrame(guest_env, NULL);
    fail1:
    exceptionCheck(guest_env);
    (*host_env)->PopLocalFrame(host_env, NULL);
    fail:
    exceptionCheck(guest_env);
    printf("%s\n", failureReason);
    return false;
}

#define CLASSPATH_ARG "-Djava.class.path="
#define CLASSPATH_ARG_LEN 18

void setup_classpath(JNIEnv* env, jstring jclasspath, jsize length, char* classpath) {
    memcpy(classpath, CLASSPATH_ARG, CLASSPATH_ARG_LEN);
    (*env)->GetStringUTFRegion(env, jclasspath, 0, length, &classpath[CLASSPATH_ARG_LEN]);
}

void vmh_exit(int result) {
    printf("VM_EXIT %i\n", result);
}

#define EXIT(x) {    \
printf("%s\n", x);   \
goto exit;           \
}

#define EXITVM(x) {                           \
printf("%s\n", x);                            \
exceptionCheck(guest_env);                    \
goto exit_destroyvm;                          \
}

JNIEXPORT void JNICALL Java_com_oracle_dalvik_VMLauncher_launchJVM(JNIEnv *env,
                                                                   __attribute__((unused)) jclass clazz,
                                                                   jstring vmPath,
                                                                   jobjectArray vmArgs,
                                                                   jstring classpath,
                                                                   jstring mainClass,
                                                                   jobjectArray appArgs) {
    jvm_library_t library;
    if(!load_vm_library(env, vmPath, &library)) return;



    jsize inSize = (*env)->GetArrayLength(env, vmArgs);
    jsize realSize = inSize + 2;
    JavaVMOption options[realSize];

    jsize classpathLength = (*env)->GetStringUTFLength(env, classpath);
    size_t totalLength = CLASSPATH_ARG_LEN + classpathLength + 1;
    char classpath_c[totalLength];
    setup_classpath(env, classpath, classpathLength, classpath_c);
    classpath_c[totalLength - 1] = 0; // Null terminate in case if setup_classpath doesn't do it.
    options[inSize].optionString = classpath_c;

    const char* cmainClass = (*env)->GetStringUTFChars(env, mainClass, NULL);
    if(cmainClass == NULL) EXIT("Failed to get classname string")

    if(!initialize_vm_args(env, vmArgs, inSize, options)) goto exit;

    options[inSize+1].optionString = "exit";
    options[inSize+1].extraInfo = vmh_exit;

    JavaVMInitArgs initArgs;
    initArgs.options = options;
    initArgs.nOptions = realSize;
    initArgs.ignoreUnrecognized = JNI_FALSE;
    initArgs.version = JNI_VERSION_1_6;

    for(jsize i = 0; i < initArgs.nOptions; i++) {
        printf("%s\n", initArgs.options[i].optionString);
    }

    JavaVM *guest_jvm;
    JNIEnv *guest_env;
    jint res = library.JNI_CreateJavaVM(&guest_jvm, &guest_env, (void*)&initArgs);
    free_vm_args(options, inSize);
    if(res < 0) EXIT("Failed to initialize the Java VM")

    jclass guest_main_class = (*guest_env)->FindClass(guest_env, cmainClass);
    (*env)->ReleaseStringUTFChars(env, mainClass, cmainClass);
    if(guest_main_class == NULL) EXITVM("Failed to locate main class")

    jmethodID guest_main_method = (*guest_env)->GetStaticMethodID(guest_env, guest_main_class, "main", "([Ljava/lang/String;)V");
    if(guest_main_method == NULL) EXITVM("Failed to locate main method")

    jclass guest_string_class = (*guest_env)->FindClass(guest_env, "java/lang/String");
    if(guest_string_class == NULL) EXITVM("Failed to locate String class")

    jsize appArgsCount = (*env)->GetArrayLength(env, appArgs);
    jobjectArray guestArgsArray = (*guest_env)->NewObjectArray(guest_env, appArgsCount, guest_string_class, NULL);
    if(!transfer_app_args(env, guest_env, appArgs, guestArgsArray, appArgsCount)) goto exit_destroyvm;

    (*guest_env)->CallStaticVoidMethod(guest_env, guest_main_class, guest_main_method, guestArgsArray);
    exceptionCheck(guest_env);
    exit_destroyvm:
    (*guest_jvm)->DetachCurrentThread(guest_jvm);
    (*guest_jvm)->DestroyJavaVM(guest_jvm);
    exit:
    unload_vm_library(&library);
}
