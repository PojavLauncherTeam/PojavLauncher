package org.lwjgl.glfw;
import java.io.*;
import java.util.*;

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
    
    volatile private static boolean isGrabbing = false;

    public static void sendMouseEvent(int x, int y, int keycode) {
        sendCursorPos(x, y);
        sendMouseKeycode(keycode);
    }
    
    public static void sendMouseEvent(int x, int y, int keycode, boolean isDown) {
        sendCursorPos(x, y);
        sendMouseKeycode(keycode, isDown);
    }

    public static void sendCursorPos(int x, int y) {
        DEBUG_STRING.append("CursorPos=" + x + ", " + y + "\n");
        mouseX = x;
        mouseY = y;
        sendData(JRE_TYPE_CURSOR_POS, x + ":" + y);
    }

    public static void sendKeycode(int keycode, boolean isDown) {
        sendData(JRE_TYPE_KEYCODE_CONTROL, keycode + ":" + Boolean.toString(isDown));
    }

    public static void sendMouseKeycode(int keycode, boolean isDown) {
        DEBUG_STRING.append("MouseKey=" + keycode + ", down=" + isDown + "\n");
        sendData(JRE_TYPE_MOUSE_KEYCODE_CONTROL, keycode + ":" + Boolean.toString(isDown));
    }

    public static void sendMouseKeycode(int keycode) {
        sendMouseKeycode(keycode, true);
        sendMouseKeycode(keycode, false);
    }

    public static void sendUpdateWindowSize(int w, int h) {
        sendData(JRE_TYPE_WINDOW_SIZE, w + ":" + h);
    }

    public static boolean isGrabbing() {
        // return isGrabbing;
        return nativeIsGrabbing();
    }

    // Called from JRE side
    public static void receiveCallback(int type, String data) {
        switch (type) {
            case ANDROID_TYPE_GRAB_STATE:
                isGrabbing = Boolean.parseBoolean(data);
                break;
        }
    }
    
    public static void sendData(int type, String data) {
        nativeSendData(true, type, data);
    }
    
    private static native void nativeSendData(boolean isAndroid, int type, String data);
    public static native boolean nativeIsGrabbing();
    
    static {
        System.loadLibrary("pojavexec");
    }
}

