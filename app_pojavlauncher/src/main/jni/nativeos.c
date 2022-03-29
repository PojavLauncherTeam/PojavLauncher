#include <jni.h>
#include <errno.h>
#include <unistd.h>
#include <assert.h>
#include <stdio.h>
#include <stdlib.h>
//
// Created by maks on 28.03.2022.
//
JNIEXPORT jstring JNICALL
Java_net_kdt_pojavlaunch_NativeOs_getenv(JNIEnv *env, jclass clazz, jstring name) {
    // TODO: implement getenv()
    jclass exception = (*env)->FindClass(env,"java/lang/Exception");
    if(name == NULL) {
        (*env)->ThrowNew(env,exception,"Name is null");
        (*env)->DeleteLocalRef(env, exception);
        return NULL;
    }
    const char* c_name = (*env)->GetStringUTFChars(env, name, NULL);
    char* value = getenv(c_name);
    jstring str;
    if(value != NULL) {
        str = (*env)->NewStringUTF(env, value);
    }else{
        str = NULL;
    }
    (*env)->DeleteLocalRef(env, exception);
    (*env)->ReleaseStringUTFChars(env, name, c_name);
    return str;
}

JNIEXPORT void JNICALL
Java_net_kdt_pojavlaunch_NativeOs_setenv(JNIEnv *env, jclass clazz, jstring name, jstring value,
                                         jboolean force) {
    // TODO: implement setenv()
    jclass exception = (*env)->FindClass(env,"java/lang/Exception");
    if(name == NULL || value == NULL) {
        (*env)->ThrowNew(env,exception,"Setenv name or value is null");
        (*env)->DeleteLocalRef(env, exception);
        return;
    }
    const char* c_name = (*env)->GetStringUTFChars(env, name, NULL);
    const char* c_value = (*env)->GetStringUTFChars(env, value, NULL);
    if(setenv(c_name, c_value, force)) {
        (*env)->ThrowNew(env,exception,"Can't setenv");
    }
    (*env)->DeleteLocalRef(env, exception);
    (*env)->ReleaseStringUTFChars(env, name, c_name);
    (*env)->ReleaseStringUTFChars(env, value, c_value);
}

JNIEXPORT void JNICALL
Java_net_kdt_pojavlaunch_NativeOs_symlink(JNIEnv *env, jclass clazz, jstring name1, jstring name2) {
    // TODO: implement symlink()
    jclass exception = (*env)->FindClass(env,"java/lang/Exception");
    assert(exception != NULL);
    if(name1 == NULL || name2 == NULL) {
        (*env)->ThrowNew(env,exception,"Symlink source or destination is null");
        (*env)->DeleteLocalRef(env, exception);
        return;
    }
    const char* c_name1 = (*env)->GetStringUTFChars(env, name1, NULL);
    const char* c_name2 = (*env)->GetStringUTFChars(env, name2, NULL);
    if(symlink(c_name1, c_name2)) {
        char* strp;
        if(asprintf(&strp, "errno %d", errno) != -1) {
            (*env)->ThrowNew(env, exception, strp);
        }else{
            (*env)->ThrowNew(env, exception, "unable to print errno");

        }
    }
    (*env)->DeleteLocalRef(env, exception);
    (*env)->ReleaseStringUTFChars(env, name1, c_name1);
    (*env)->ReleaseStringUTFChars(env, name2, c_name2);
}
