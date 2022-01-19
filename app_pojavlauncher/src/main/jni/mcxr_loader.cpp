#include <jni.h>
#include <thread>

//
// Created by Judge on 12/23/2021.
//

static jobject* context;
static jobject* app;
static JavaVM* jvm;

JNIEXPORT JNICALL
extern "C" jlong
Java_net_kdt_pojavlaunch_MCXRLoader_getContextPtr(JNIEnv *env, jclass clazz) {
    return reinterpret_cast<jlong>(&context);
}

JNIEXPORT JNICALL
extern "C" void
Java_net_kdt_pojavlaunch_MCXRLoader_setContext(JNIEnv *env, jclass clazz, jobject ctx) {
    context = reinterpret_cast<jobject*>(env->NewGlobalRef(ctx));
}

jint JNI_OnLoad(JavaVM* vm, void* reserved) {
    if (jvm == nullptr) {
        jvm = reinterpret_cast<JavaVM*>(vm);
    }
    return JNI_VERSION_1_4;
}

JNIEXPORT JNICALL
extern "C" jlong
Java_net_sorenon_mcxr_play_MCXRNativeLoad_getJVMPtr(JNIEnv *env, jclass clazz) {
    return reinterpret_cast<jlong>(&jvm);
}

JNIEXPORT JNICALL
extern "C" jlong
Java_net_sorenon_mcxr_play_MCXRNativeLoad_getCTXPtr(JNIEnv *env, jclass clazz) {
    return reinterpret_cast<jlong>(&context);
}

JNIEXPORT JNICALL
extern "C" jlong
Java_net_sorenon_mcxr_play_MCXRNativeLoad_getApplicationActivityPtr(JNIEnv *env, jclass clazz) {
    return reinterpret_cast<jlong>(&app);
}

extern "C"
JNIEXPORT void JNICALL
Java_net_kdt_pojavlaunch_MCXRLoader_setApplicationPtr(JNIEnv *env, jclass clazz, jobject activity) {
    app = reinterpret_cast<jobject *>(env->NewGlobalRef(activity));
}

extern "C"
JNIEXPORT void JNICALL
Java_net_kdt_pojavlaunch_MCXRLoader_launch(JNIEnv *env, jclass clazz, jobject main) {
    main = (*env).NewGlobalRef(main);
    jclass clazz1 = (*env).GetObjectClass(main);
    jmethodID id = (*env).GetMethodID(clazz1, "runCraft", "()V");
    std::thread thread([=]() {
        JNIEnv* threadEnv;
        jvm->AttachCurrentThread(&threadEnv, nullptr);
        threadEnv->CallVoidMethod(main, id);
    });
    thread.detach();
}