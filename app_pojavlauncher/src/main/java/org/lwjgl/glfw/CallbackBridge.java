package org.lwjgl.glfw;

import android.os.Handler;
import android.os.Looper;

import net.kdt.pojavlaunch.*;
import android.content.*;
import android.view.Choreographer;

public class CallbackBridge {
    public static Choreographer sChoreographer = Choreographer.getInstance();
    private static boolean isGrabbing = false;
    private static long lastGrabTime = System.currentTimeMillis();
    public static final int ANDROID_TYPE_GRAB_STATE = 0;
    
    public static final int CLIPBOARD_COPY = 2000;
    public static final int CLIPBOARD_PASTE = 2001;
    public static final int CLIPBOARD_OPEN = 2002;
    
    public static volatile int windowWidth, windowHeight;
    public static volatile int physicalWidth, physicalHeight;
    public static float mouseX, mouseY;
    public static StringBuilder DEBUG_STRING = new StringBuilder();
    private static boolean threadAttached;
    public volatile static boolean holdingAlt, holdingCapslock, holdingCtrl,
            holdingNumlock, holdingShift;

    public static void putMouseEventWithCoords(int button, float x, float y) {
        putMouseEventWithCoords(button, true, x, y);
        sChoreographer.postFrameCallbackDelayed(l -> putMouseEventWithCoords(button, false, x, y), 33);
    }
    
    public static void putMouseEventWithCoords(int button, boolean isDown, float x, float y /* , int dz, long nanos */) {
        sendCursorPos(x, y);
        sendMouseKeycode(button, CallbackBridge.getCurrentMods(), isDown);
    }


    public static void sendCursorPos(float x, float y) {
        if (!threadAttached) {
            nativeSetUseInputStackQueue(BaseMainActivity.isInputStackCall);
            threadAttached = CallbackBridge.nativeAttachThreadToOther(true, BaseMainActivity.isInputStackCall);
        }

        DEBUG_STRING.append("CursorPos=").append(x).append(", ").append(y).append("\n");
        mouseX = x;
        mouseY = y;
        nativeSendCursorPos(mouseX, mouseY);
    }
    
    public static void sendPrepareGrabInitialPos() {
        DEBUG_STRING.append("Prepare set grab initial posititon: ignored");
        //sendMouseKeycode(-1, CallbackBridge.getCurrentMods(), false);
    }

    public static void sendKeycode(int keycode, char keychar, int scancode, int modifiers, boolean isDown) {
        DEBUG_STRING.append("KeyCode=").append(keycode).append(", Char=").append(keychar);
        // TODO CHECK: This may cause input issue, not receive input!
/*
        if (!nativeSendCharMods((int) keychar, modifiers) || !nativeSendChar(keychar)) {
            nativeSendKey(keycode, 0, isDown ? 1 : 0, modifiers);
        }
*/

        //nativeSendKeycode(keycode, keychar, scancode, isDown ? 1 : 0, modifiers);
        if(keycode != 0)  nativeSendKey(keycode,scancode,isDown ? 1 : 0, modifiers);
        //else nativeSendKey(32,scancode,isDown ? 1 : 0, modifiers);
        if(isDown && keychar != '\u0000') {
            nativeSendCharMods(keychar,modifiers);
            nativeSendChar(keychar);
        }
        //throw new IllegalStateException("Tracing call");
        // sendData(JRE_TYPE_KEYCODE_CONTROL, keycode, Character.toString(keychar), Boolean.toString(isDown), modifiers);
    }

    public static void sendChar(char keychar, int modifiers){
        nativeSendCharMods(keychar,modifiers);
        nativeSendChar(keychar);
    }

    public static void sendKeyPress(int keyCode, int modifiers, boolean status) {
        sendKeyPress(keyCode, 0, modifiers, status);
    }

    public static void sendKeyPress(int keyCode, int scancode, int modifiers, boolean status) {
        sendKeyPress(keyCode, '\u0000', scancode, modifiers, status);
    }

    public static void sendKeyPress(int keyCode, char keyChar, int scancode, int modifiers, boolean status) {
        CallbackBridge.sendKeycode(keyCode, keyChar, scancode, modifiers, status);
    }

    public static void sendKeyPress(int keyCode) {
        sendKeyPress(keyCode, CallbackBridge.getCurrentMods(), true);
        sendKeyPress(keyCode, CallbackBridge.getCurrentMods(), false);
    }

    public static void sendMouseButton(int button, boolean status) {
        CallbackBridge.sendMouseKeycode(button, CallbackBridge.getCurrentMods(), status);
    }

    public static void sendMouseKeycode(int button, int modifiers, boolean isDown) {
        DEBUG_STRING.append("MouseKey=").append(button).append(", down=").append(isDown).append("\n");
        // if (isGrabbing()) DEBUG_STRING.append("MouseGrabStrace: " + android.util.Log.getStackTraceString(new Throwable()) + "\n");
        nativeSendMouseButton(button, isDown ? 1 : 0, modifiers);
    }

    public static void sendMouseKeycode(int keycode) {
        sendMouseKeycode(keycode, CallbackBridge.getCurrentMods(), true);
        sendMouseKeycode(keycode, CallbackBridge.getCurrentMods(), false);
    }
    
    public static void sendScroll(double xoffset, double yoffset) {
        DEBUG_STRING.append("ScrollX=").append(xoffset).append(",ScrollY=").append(yoffset);
        nativeSendScroll(xoffset, yoffset);
    }

    public static void sendUpdateWindowSize(int w, int h) {
        nativeSendScreenSize(w, h);
    }

    public static boolean isGrabbing() {
        // Avoid going through the JNI each time.
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastGrabTime > 250){
            isGrabbing = nativeIsGrabbing();
            lastGrabTime = currentTime;
        }
        return isGrabbing;
    }

    // Called from JRE side
    public static String accessAndroidClipboard(int type, String copy) {
        switch (type) {
            case CLIPBOARD_COPY:
                BaseMainActivity.GLOBAL_CLIPBOARD.setPrimaryClip(ClipData.newPlainText("Copy", copy));
                return null;

            case CLIPBOARD_PASTE:
                if (BaseMainActivity.GLOBAL_CLIPBOARD.hasPrimaryClip() && BaseMainActivity.GLOBAL_CLIPBOARD.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                    return BaseMainActivity.GLOBAL_CLIPBOARD.getPrimaryClip().getItemAt(0).getText().toString();
                } else {
                    return "";
                }

            case CLIPBOARD_OPEN:
                BaseMainActivity.openLink(copy);
                return null;
            default: return null;
        }
    }
/*
    private static String currData;
    public static void sendData(int type, Object... dataArr) {
        currData = "";
        for (int i = 0; i < dataArr.length; i++) {
            if (dataArr[i] instanceof Integer) {
                currData += Integer.toString((int) dataArr[i]);
            } else if (dataArr[i] instanceof String) {
                currData += (String) dataArr[i];
            } else {
                currData += dataArr[i].toString();
            }
            currData += (i + 1 < dataArr.length ? ":" : "");
        }
        nativeSendData(true, type, currData);
    }
    private static native void nativeSendData(boolean isAndroid, int type, String data);
*/


    public static int getCurrentMods() {
        int currMods = 0;
        if (holdingAlt) {
            currMods |= LwjglGlfwKeycode.GLFW_MOD_ALT;
        } if (holdingCapslock) {
            currMods |= LwjglGlfwKeycode.GLFW_MOD_CAPS_LOCK;
        } if (holdingCtrl) {
            currMods |= LwjglGlfwKeycode.GLFW_MOD_CONTROL;
        } if (holdingNumlock) {
            currMods |= LwjglGlfwKeycode.GLFW_MOD_NUM_LOCK;
        } if (holdingShift) {
            currMods |= LwjglGlfwKeycode.GLFW_MOD_SHIFT;
        }
        return currMods;
    }

    public static void setModifiers(int keyCode, boolean isDown){
        switch (keyCode){
            case LwjglGlfwKeycode.GLFW_KEY_LEFT_SHIFT:
                CallbackBridge.holdingShift = isDown;
                return;

            case LwjglGlfwKeycode.GLFW_KEY_LEFT_CONTROL:
                CallbackBridge.holdingCtrl = isDown;
                return;

            case LwjglGlfwKeycode.GLFW_KEY_LEFT_ALT:
                CallbackBridge.holdingAlt = isDown;
                return;

            case LwjglGlfwKeycode.GLFW_KEY_CAPS_LOCK:
                CallbackBridge.holdingCapslock = isDown;
                return;

            case LwjglGlfwKeycode.GLFW_KEY_NUM_LOCK:
                CallbackBridge.holdingNumlock = isDown;
                return;
        }
    }

    public static native void nativeSetUseInputStackQueue(boolean useInputStackQueue);
    public static native boolean nativeAttachThreadToOther(boolean isAndroid, boolean isUsePushPoll);

    private static native boolean nativeSendChar(char codepoint);
    // GLFW: GLFWCharModsCallback deprecated, but is Minecraft still use?
    private static native boolean nativeSendCharMods(char codepoint, int mods);
    private static native void nativeSendKey(int key, int scancode, int action, int mods);
    // private static native void nativeSendCursorEnter(int entered);
    private static native void nativeSendCursorPos(float x, float y);
    private static native void nativeSendMouseButton(int button, int action, int mods);
    private static native void nativeSendScroll(double xoffset, double yoffset);
    private static native void nativeSendScreenSize(int width, int height);
    public static native void nativeSetWindowAttrib(int attrib, int value);

    public static native boolean nativeIsGrabbing();
    static {
        System.loadLibrary("pojavexec");
    }
}

