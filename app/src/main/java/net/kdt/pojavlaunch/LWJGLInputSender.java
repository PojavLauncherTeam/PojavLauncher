package net.kdt.pojavlaunch;

public class LWJGLInputSender
{
	public static final int TYPE_CURSOR_POS = 0;
	public static final int TYPE_CURSOR_BUTTON = 1;
	public static final int TYPE_KEYCODE_CONTROL = 2;
	public static final int TYPE_KEYCODE_CHAR = 3;
	public static final int TYPE_MOUSE_KEYCODE_CONTROL = 4;
	public static final int TYPE_WINDOW_SIZE = 5;
    
    public static int windowWidth, windowHeight;
    public static int mouseX, mouseY;
    public static boolean mouseLeft;
    public static StringBuilder DEBUG_STRING = new StringBuilder();
    
    public static void sendMouseEvent(int x, int y, int keycode, boolean isDown) {
        sendCursorPos(x, y);
        sendKeycode(keycode, isDown);
    }
    
	public static void sendCursorPos(int x, int y) {
        DEBUG_STRING.append("CursorPos=" + x + ", " + y + "\n");
        mouseX = x;
        mouseY = y;
		sendDataToJRE(TYPE_CURSOR_POS, x + ":" + y);
	}
    
    public static void sendKeycode(int keycode, boolean isDown) {
        sendDataToJRE(TYPE_KEYCODE_CONTROL, keycode + ":" + Boolean.toString(isDown));
	}
    
    public static void sendMouseKeycode(int keycode, boolean isDown) {
        DEBUG_STRING.append("MouseKey=" + keycode + ", down=" + isDown + "\n");
        sendDataToJRE(TYPE_MOUSE_KEYCODE_CONTROL, keycode + ":" + Boolean.toString(isDown));
	}
    
    public static void sendMouseKeycode(int keycode) {
        sendMouseKeycode(keycode, true);
        sendMouseKeycode(keycode, false);
	}
    
	public static void sendUpdateWindowSize(int w, int h) {
		sendDataToJRE(TYPE_WINDOW_SIZE, w + ":" + h);
	}
    
    public static boolean isGrabbing() {
        // TODO implement this method!!!
        return false;
    }
	
	public static native void sendDataToJRE(int type, String data);
}
