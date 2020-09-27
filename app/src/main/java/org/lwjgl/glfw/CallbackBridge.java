package org.lwjgl.glfw;
import java.io.*;
import java.util.*;
import android.widget.*;

public class CallbackBridge {
    public static final int JRE_TYPE_CURSOR_POS = 0;
    public static final int JRE_TYPE_CURSOR_BUTTON = 1;
    public static final int JRE_TYPE_KEYCODE_CONTROL = 2;
    public static final int JRE_TYPE_KEYCODE_CHAR = 3;
    public static final int JRE_TYPE_MOUSE_KEYCODE_CONTROL = 4;
    public static final int JRE_TYPE_WINDOW_SIZE = 5;
    
    public static final int ANDROID_TYPE_GRAB_STATE = 0;

    public static volatile int windowWidth, windowHeight;
    public static int mouseX, mouseY;
    public static boolean mouseLeft;
    public static StringBuilder DEBUG_STRING = new StringBuilder();
    
    // volatile private static boolean isGrabbing = false;

    public static void putMouseEventWithCoords(int button, int state, int x, int y, int dz, long nanos) {
        sendCursorPos(x, y);
        sendMouseKeycode(button, 0, state == 1);
    }

    public static void sendCursorPos(int x, int y) {
        DEBUG_STRING.append("CursorPos=" + x + ", " + y + "\n");
        mouseX = x;
        mouseY = y;
        sendData(JRE_TYPE_CURSOR_POS, x, y);
    }

    public static void sendKeycode(int keycode, char keychar, int modifiers, boolean isDown) {
        DEBUG_STRING.append("KeyCode=" + keycode + ", Char=" + keychar);
        sendData(JRE_TYPE_KEYCODE_CONTROL, keycode, Character.toString(keychar), Boolean.toString(isDown), modifiers);
    }

    public static void sendMouseKeycode(int keycode, int modifiers, boolean isDown) {
        DEBUG_STRING.append("MouseKey=" + keycode + ", down=" + isDown + "\n");
        if (isGrabbing()) DEBUG_STRING.append("MouseGrabStrace: " + android.util.Log.getStackTraceString(new Throwable()) + "\n");
        sendData(JRE_TYPE_MOUSE_KEYCODE_CONTROL, keycode, Boolean.toString(isDown), modifiers);
    }

    public static void sendMouseKeycode(int keycode) {
        sendMouseKeycode(keycode, 0, true);
        sendMouseKeycode(keycode, 0, false);
    }

    public static void sendUpdateWindowSize(int w, int h) {
        sendData(JRE_TYPE_WINDOW_SIZE, w, h);
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
    public static native boolean nativeIsGrabbing();
    
    static {
        System.loadLibrary("pojavexec");
    }
}

