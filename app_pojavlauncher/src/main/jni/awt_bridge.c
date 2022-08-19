#include <jni.h>
#include <assert.h>

static JavaVM* dalvikJavaVMPtr;

static JavaVM* runtimeJavaVMPtr;
static JNIEnv* runtimeJNIEnvPtr_GRAPHICS;
static JNIEnv* runtimeJNIEnvPtr_INPUT;
static JNIEnv* runtimeJNIEnvPtr_CLIPBOARD;
jclass class_CTCScreen;
jmethodID method_GetRGB;

jclass class_CTCAndroidInput;
jmethodID method_ReceiveInput;

jint JNI_OnLoad(JavaVM* vm, void* reserved) {
    if (dalvikJavaVMPtr == NULL) {
        //Save dalvik global JavaVM pointer
        dalvikJavaVMPtr = vm;
    } else if (dalvikJavaVMPtr != vm) {
        runtimeJavaVMPtr = vm;
    }

    return JNI_VERSION_1_4;
}

JNIEXPORT void JNICALL Java_net_kdt_pojavlaunch_AWTInputBridge_nativeSendData(JNIEnv* env, jclass clazz, jint type, jint i1, jint i2, jint i3, jint i4) {
    if (runtimeJNIEnvPtr_INPUT == NULL) {
        if (runtimeJavaVMPtr == NULL) {
            return;
        } else {
            (*runtimeJavaVMPtr)->AttachCurrentThread(runtimeJavaVMPtr, &runtimeJNIEnvPtr_INPUT, NULL);
        }
    }

    if (method_ReceiveInput == NULL) {
        class_CTCAndroidInput = (*runtimeJNIEnvPtr_INPUT)->FindClass(runtimeJNIEnvPtr_INPUT, "net/java/openjdk/cacio/ctc/CTCAndroidInput");
        if ((*runtimeJNIEnvPtr_INPUT)->ExceptionCheck(runtimeJNIEnvPtr_INPUT) == JNI_TRUE) {
            (*runtimeJNIEnvPtr_INPUT)->ExceptionClear(runtimeJNIEnvPtr_INPUT);
            class_CTCAndroidInput = (*runtimeJNIEnvPtr_INPUT)->FindClass(runtimeJNIEnvPtr_INPUT, "com/github/caciocavallosilano/cacio/ctc/CTCAndroidInput");
        }
        assert(class_CTCAndroidInput != NULL);
        method_ReceiveInput = (*runtimeJNIEnvPtr_INPUT)->GetStaticMethodID(runtimeJNIEnvPtr_INPUT, class_CTCAndroidInput, "receiveData", "(IIIII)V");
        assert(method_ReceiveInput != NULL);
    }
    (*runtimeJNIEnvPtr_INPUT)->CallStaticVoidMethod(
        runtimeJNIEnvPtr_INPUT,
        class_CTCAndroidInput,
        method_ReceiveInput,
        type, i1, i2, i3, i4
    );
}

// TODO: check for memory leaks
// int printed = 0;
int threadAttached = 0;
JNIEXPORT jintArray JNICALL Java_net_kdt_pojavlaunch_utils_JREUtils_renderAWTScreenFrame(JNIEnv* env, jclass clazz /*, jobject canvas, jint width, jint height */) {
    if (runtimeJNIEnvPtr_GRAPHICS == NULL) {
        if (runtimeJavaVMPtr == NULL) {
            return NULL;
        } else {
            (*runtimeJavaVMPtr)->AttachCurrentThread(runtimeJavaVMPtr, &runtimeJNIEnvPtr_GRAPHICS, NULL);
        }
    }

    int *rgbArray;
    jintArray jreRgbArray, androidRgbArray;
  
    if (method_GetRGB == NULL) {
        class_CTCScreen = (*runtimeJNIEnvPtr_GRAPHICS)->FindClass(runtimeJNIEnvPtr_GRAPHICS, "net/java/openjdk/cacio/ctc/CTCScreen");
        if ((*runtimeJNIEnvPtr_GRAPHICS)->ExceptionCheck(runtimeJNIEnvPtr_GRAPHICS) == JNI_TRUE) {
            (*runtimeJNIEnvPtr_GRAPHICS)->ExceptionClear(runtimeJNIEnvPtr_GRAPHICS);
            class_CTCScreen = (*runtimeJNIEnvPtr_GRAPHICS)->FindClass(runtimeJNIEnvPtr_GRAPHICS, "com/github/caciocavallosilano/cacio/ctc/CTCScreen");
        }
        assert(class_CTCScreen != NULL);
        method_GetRGB = (*runtimeJNIEnvPtr_GRAPHICS)->GetStaticMethodID(runtimeJNIEnvPtr_GRAPHICS, class_CTCScreen, "getCurrentScreenRGB", "()[I");
        assert(method_GetRGB != NULL);
    }
    jreRgbArray = (jintArray) (*runtimeJNIEnvPtr_GRAPHICS)->CallStaticObjectMethod(
        runtimeJNIEnvPtr_GRAPHICS,
        class_CTCScreen,
        method_GetRGB
    );
    if (jreRgbArray == NULL) {
        return NULL;
    }
    
    // Copy JRE RGB array memory to Android.
    int arrayLength = (*runtimeJNIEnvPtr_GRAPHICS)->GetArrayLength(runtimeJNIEnvPtr_GRAPHICS, jreRgbArray);
    rgbArray = (*runtimeJNIEnvPtr_GRAPHICS)->GetIntArrayElements(runtimeJNIEnvPtr_GRAPHICS, jreRgbArray, 0);
    androidRgbArray = (*env)->NewIntArray(env, arrayLength);
    (*env)->SetIntArrayRegion(env, androidRgbArray, 0, arrayLength, rgbArray);

    (*runtimeJNIEnvPtr_GRAPHICS)->ReleaseIntArrayElements(runtimeJNIEnvPtr_GRAPHICS, jreRgbArray, rgbArray, NULL);
    // (*env)->DeleteLocalRef(env, androidRgbArray);
    // free(rgbArray);
    
    return androidRgbArray;
}

jobject clipboard = NULL;
jclass clipboardClass;
static jobject obtainClipboard(JNIEnv *env) {
    jclass toolkitClass = (*runtimeJNIEnvPtr_CLIPBOARD)->FindClass(runtimeJNIEnvPtr_CLIPBOARD,"java/awt/Toolkit");
    jobject toolkit = (*runtimeJNIEnvPtr_CLIPBOARD)->CallStaticObjectMethod(runtimeJNIEnvPtr_CLIPBOARD,toolkitClass,(*runtimeJNIEnvPtr_CLIPBOARD)->GetStaticMethodID(runtimeJNIEnvPtr_CLIPBOARD,toolkitClass,"getDefaultToolkit", "()Ljava/awt/Toolkit;"));
    clipboardClass = (*runtimeJNIEnvPtr_CLIPBOARD)->NewGlobalRef(runtimeJNIEnvPtr_CLIPBOARD,(*runtimeJNIEnvPtr_CLIPBOARD)->FindClass(runtimeJNIEnvPtr_CLIPBOARD,"java/awt/datatransfer/Clipboard"));
    clipboard = (*runtimeJNIEnvPtr_CLIPBOARD)->NewGlobalRef(runtimeJNIEnvPtr_CLIPBOARD,(*runtimeJNIEnvPtr_CLIPBOARD)->CallObjectMethod(runtimeJNIEnvPtr_CLIPBOARD,toolkit,(*runtimeJNIEnvPtr_CLIPBOARD)->GetMethodID(runtimeJNIEnvPtr_CLIPBOARD,toolkitClass,"getSystemClipboard", "()Ljava/awt/datatransfer/Clipboard;")));
    (*runtimeJNIEnvPtr_CLIPBOARD)->DeleteLocalRef(runtimeJNIEnvPtr_CLIPBOARD,toolkitClass);
    (*runtimeJNIEnvPtr_CLIPBOARD)->DeleteLocalRef(runtimeJNIEnvPtr_CLIPBOARD,toolkit);
}

JNIEXPORT void JNICALL
Java_net_kdt_pojavlaunch_AWTInputBridge_nativePutClipboard(JNIEnv *env, jclass clazz,
                                                           jstring data) {
    if (runtimeJNIEnvPtr_CLIPBOARD == NULL) {
        if (runtimeJavaVMPtr != NULL) {
            (*runtimeJavaVMPtr)->AttachCurrentThread(runtimeJavaVMPtr, &runtimeJNIEnvPtr_CLIPBOARD, NULL);
        }else{
            return;
        }
    }
    if(clipboard == NULL) obtainClipboard(runtimeJNIEnvPtr_CLIPBOARD);
    jclass stringSelection = (*runtimeJNIEnvPtr_CLIPBOARD)->FindClass(runtimeJNIEnvPtr_CLIPBOARD,"java/awt/datatransfer/StringSelection");
    jobject o_stringSelection = (*runtimeJNIEnvPtr_CLIPBOARD)->NewObject(runtimeJNIEnvPtr_CLIPBOARD,stringSelection,(*runtimeJNIEnvPtr_CLIPBOARD)->GetMethodID(runtimeJNIEnvPtr_CLIPBOARD,stringSelection,"<init>", "(Ljava/lang/String;)V"),NULL);
    (*runtimeJNIEnvPtr_CLIPBOARD)->CallVoidMethod(runtimeJNIEnvPtr_CLIPBOARD,clipboard,(*runtimeJNIEnvPtr_CLIPBOARD)->GetMethodID(env,clipboardClass,"setContents", "(Ljava/awt/datatransfer/Transferable;Ljava/awt/datatransfer/ClipboardOwner;)V"),o_stringSelection,NULL);
    (*runtimeJNIEnvPtr_CLIPBOARD)->DeleteLocalRef(runtimeJNIEnvPtr_CLIPBOARD,stringSelection);
    (*runtimeJNIEnvPtr_CLIPBOARD)->DeleteLocalRef(runtimeJNIEnvPtr_CLIPBOARD,o_stringSelection);
}
