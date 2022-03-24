package net.kdt.pojavlaunch;

public class AWTInputBridge {
    public static final int EVENT_TYPE_CHAR = 1000;
    // public static final int EVENT_TYPE_CHAR_MODS = 1001;
    // public static final int EVENT_TYPE_CURSOR_ENTER = 1002;
    public static final int EVENT_TYPE_CURSOR_POS = 1003;
    // public static final int EVENT_TYPE_FRAMEBUFFER_SIZE = 1004;
    public static final int EVENT_TYPE_KEY = 1005;
    public static final int EVENT_TYPE_MOUSE_BUTTON = 1006;
    public static final int EVENT_TYPE_SCROLL = 1007;
    // public static final int EVENT_TYPE_WINDOW_SIZE = 1008;
    
    public static void sendKey(char keychar, int keycode) {
        // TODO: Android -> AWT keycode mapping
        nativeSendData(EVENT_TYPE_KEY, (int) keychar, keycode, 0, 0);
    }

    public static void sendChar(char keychar){
        nativeSendData(EVENT_TYPE_CHAR, (int) keychar, 0, 0, 0);
    }
    
    public static void sendMousePress(int awtButtons, boolean isDown) {
        nativeSendData(EVENT_TYPE_MOUSE_BUTTON, awtButtons, isDown ? 1 : 0, 0, 0);
    }
    
    public static void sendMousePress(int awtButtons) {
        sendMousePress(awtButtons, true);
        sendMousePress(awtButtons, false);
    }
    
    public static void sendMousePos(int x, int y) {
        nativeSendData(EVENT_TYPE_CURSOR_POS, x, y, 0, 0);
    }
    
    static {
        System.loadLibrary("pojavexec_awt");
    }
    
    public static native void nativeSendData(int type, int i1, int i2, int i3, int i4);
    public static native void nativePutClipboard(String data);
}
