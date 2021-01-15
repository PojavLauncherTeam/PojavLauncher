#include <jni.h>
#include <assert.h>

static JavaVM* dalvikJavaVMPtr;
static JNIEnv* dalvikJNIEnvPtr_ANDROID;
static JNIEnv* dalvikJNIEnvPtr_JRE;

static JavaVM* runtimeJavaVMPtr;
static JNIEnv* runtimeJNIEnvPtr_ANDROID;
static JNIEnv* runtimeJNIEnvPtr_JRE;

jint JNI_OnLoad(JavaVM* vm, void* reserved) {
    if (dalvikJavaVMPtr == NULL) {
        //Save dalvik global JavaVM pointer
        dalvikJavaVMPtr = vm;
        (*vm)->GetEnv(vm, (void**) &dalvikJNIEnvPtr_ANDROID, JNI_VERSION_1_4);
    } else if (dalvikJavaVMPtr != vm) {
        runtimeJavaVMPtr = vm;
        (*vm)->GetEnv(vm, (void**) &runtimeJNIEnvPtr_JRE, JNI_VERSION_1_4);
    }

    return JNI_VERSION_1_4;
}

// TODO: check for memory leaks
// int printed = 0;
int threadAttached = 0;
JNIEXPORT jintArray JNICALL Java_net_kdt_pojavlaunch_utils_JREUtils_renderAWTScreenFrame(JNIEnv* env, jclass clazz /*, jobject canvas, jint width, jint height */) {
    if (runtimeJNIEnvPtr_ANDROID == NULL) {
        if (runtimeJavaVMPtr == NULL) {
            return NULL;
        } else {
            if (threadAttached == 0) {
                (*runtimeJavaVMPtr)->AttachCurrentThread(runtimeJavaVMPtr, &runtimeJNIEnvPtr_ANDROID, NULL);
                threadAttached = 1;
            }
        }
    }

    int *rgbArray;
    jintArray jreRgbArray, androidRgbArray;
  
    jclass class_awt = (*runtimeJNIEnvPtr_ANDROID)->FindClass(runtimeJNIEnvPtr_ANDROID, "net/java/openjdk/cacio/ctc/CTCScreen");
    assert(class_awt != NULL);
    jmethodID method_awt = (*runtimeJNIEnvPtr_ANDROID)->GetStaticMethodID(runtimeJNIEnvPtr_ANDROID, class_awt, "getCurrentScreenRGB", "()[I");
    assert(class_awt != NULL);
    jreRgbArray = (jintArray) (*runtimeJNIEnvPtr_ANDROID)->CallStaticObjectMethod(
        runtimeJNIEnvPtr_ANDROID,
        class_awt,
        method_awt
    );
    if (jreRgbArray == NULL) {
        return NULL;
    }
    
    // Copy JRE RGB array memory to Android.
    int arrayLength = (*runtimeJNIEnvPtr_ANDROID)->GetArrayLength(runtimeJNIEnvPtr_ANDROID, jreRgbArray);
    rgbArray = (*runtimeJNIEnvPtr_ANDROID)->GetIntArrayElements(runtimeJNIEnvPtr_ANDROID, jreRgbArray, 0);
    androidRgbArray = (*env)->NewIntArray(env, arrayLength);
    (*env)->SetIntArrayRegion(env, androidRgbArray, 0, arrayLength, rgbArray);

    (*runtimeJNIEnvPtr_ANDROID)->ReleaseIntArrayElements(runtimeJNIEnvPtr_ANDROID, jreRgbArray, rgbArray, NULL);
    // (*env)->DeleteLocalRef(env, androidRgbArray);
    // free(rgbArray);
    
    return androidRgbArray;
}

