package org.lwjgl.glfw;
import java.io.*;
import java.util.*;
import android.widget.*;
import net.kdt.pojavlaunch.*;

public class CallbackBridge {
    public static final int JRE_TYPE_CURSOR_POS = 0;
    public static final int JRE_TYPE_CURSOR_BUTTON = 1;
    public static final int JRE_TYPE_KEYCODE_CONTROL = 2;
    public static final int JRE_TYPE_KEYCODE_CHAR = 3;
    public static final int JRE_TYPE_MOUSE_KEYCODE_CONTROL = 4;
    public static final int JRE_TYPE_WINDOW_SIZE = 5;
    public static final int JRE_TYPE_GRAB_INITIAL_POS_UNSET = 6;
    
    public static final int ANDROID_TYPE_GRAB_STATE = 0;

    public static volatile int windowWidth, windowHeight;
    public static int mouseX, mouseY;
    public static boolean mouseLeft;
    public static StringBuilder DEBUG_STRING = new StringBuilder();
    
    // volatile private static boolean isGrabbing = false;

    public static void putMouseEventWithCoords(int button, int x, int y /* , int dz, long nanos */) {
        putMouseEventWithCoords(button, 1, x, y);
        putMouseEventWithCoords(button, 0, x, y);
    }
    
    public static void putMouseEventWithCoords(int button, int state, int x, int y /* , int dz, long nanos */) {
        sendCursorPos(x, y);
        sendMouseKeycode(button, 0, state == 1);
    }

    public static void sendCursorPos(int x, int y) {
        DEBUG_STRING.append("CursorPos=" + x + ", " + y + "\n");
        mouseX = x;
        mouseY = y;
        nativeSendCursorPos(x, y);
    }
    
    public static void sendPrepareGrabInitialPos() {
        DEBUG_STRING.append("Prepare set grab initial posititon");
        sendMouseKeycode(-1, 0, false);
    }

    public static void sendKeycode(int keycode, char keychar, int modifiers, boolean isDown) {
        DEBUG_STRING.append("KeyCode=" + keycode + ", Char=" + keychar);
        // TODO CHECK: This may cause input issue, not receive input!
        if (!nativeSendCharMods(keycode, modifiers) ||!nativeSendChar(keycode)) {
            nativeSendKey(keycode, 0 /* scancode */, isDown ? 1 : 0, modifiers);
        }
        
        // sendData(JRE_TYPE_KEYCODE_CONTROL, keycode, Character.toString(keychar), Boolean.toString(isDown), modifiers);
    }

    public static void sendMouseKeycode(int button, int modifiers, boolean isDown) {
        DEBUG_STRING.append("MouseKey=" + button + ", down=" + isDown + "\n");
        // if (isGrabbing()) DEBUG_STRING.append("MouseGrabStrace: " + android.util.Log.getStackTraceString(new Throwable()) + "\n");
        nativeSendMouseButton(button, isDown ? 1 : 0, modifiers);
    }

    public static void sendMouseKeycode(int keycode) {
        sendMouseKeycode(keycode, 0, true);
        sendMouseKeycode(keycode, 0, false);
    }
    
    public static void sendScroll(double xoffset, double yoffset) {
        DEBUG_STRING.append("ScrollX=" + xoffset + ",ScrollY=" + yoffset);
        nativeSendScroll(xoffset, yoffset);
    }

    public static void sendUpdateWindowSize(int w, int h) {
        nativeSendFramebufferSize(w, h);
        nativeSendWindowSize(w, h);
    }

    public static boolean isGrabbing() {
        // return isGrabbing;
        return nativeIsGrabbing();
    }

    // Called from JRE side
    public static void receiveCallback(int type, String data) {
        switch (type) {
            case ANDROID_TYPE_GRAB_STATE:
                // isGrabbing = Boolean.parseBoolean(data);
                break;
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

    public static native void nativeAttachThreadToOther(boolean isAndroid, boolean isUsePushPoll);
    private static native boolean nativeSendChar(int codepoint);
    // GLFW: GLFWCharModsCallback deprecated, but is Minecraft still use?
    private static native boolean nativeSendCharMods(int codepoint, int mods);
    // private static native void nativeSendCursorEnter(int entered);
    private static native void nativeSendCursorPos(int x, int y);
    private static native void nativeSendFramebufferSize(int width, int height);
    private static native void nativeSendKey(int key, int scancode, int action, int mods);
    private static native void nativeSendMouseButton(int button, int action, int mods);
    private static native void nativeSendScroll(double xoffset, double yoffset);
    private static native void nativeSendWindowSize(int width, int height);
    
    public static native boolean nativeIsGrabbing();
    
    static {
        System.loadLibrary("pojavexec");
    }
}

