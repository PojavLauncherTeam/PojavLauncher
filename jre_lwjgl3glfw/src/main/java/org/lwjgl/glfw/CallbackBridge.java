package org.lwjgl.glfw;
import java.util.*;

public class CallbackBridge {
    public static final int CLIPBOARD_COPY = 2000;
    public static final int CLIPBOARD_PASTE = 2001;
    
    public static final int EVENT_TYPE_CHAR = 1000;
    public static final int EVENT_TYPE_CHAR_MODS = 1001;
    public static final int EVENT_TYPE_CURSOR_ENTER = 1002;
    public static final int EVENT_TYPE_CURSOR_POS = 1003;
    public static final int EVENT_TYPE_FRAMEBUFFER_SIZE = 1004;
    public static final int EVENT_TYPE_KEY = 1005;
    public static final int EVENT_TYPE_MOUSE_BUTTON = 1006;
    public static final int EVENT_TYPE_SCROLL = 1007;
    public static final int EVENT_TYPE_WINDOW_SIZE = 1008;
    
    public static final int ANDROID_TYPE_GRAB_STATE = 0;
    
    public static final boolean INPUT_DEBUG_ENABLED;
    
    // TODO send grab state event to Android
    
    static {
        INPUT_DEBUG_ENABLED = Boolean.parseBoolean(System.getProperty("glfwstub.debugInput", "false"));

        
/*
        if (isDebugEnabled) {
            //try {
                //debugEventStream = new PrintStream(new File(System.getProperty("user.dir"), "glfwstub_inputeventlog.txt"));
		    debugEventStream = System.out;
            //} catch (FileNotFoundException e) {
            //    e.printStackTrace();
            //}
        }
	
	    //Quick and dirty: debul all key inputs to System.out
*/
    }


    public static void sendData(int type, String data) {
        nativeSendData(false, type, data);
    }
    public static native void nativeSendData(boolean isAndroid, int type, String data);
    public static native boolean nativeSetInputReady(boolean ready);
    public static native String nativeClipboard(int action, byte[] copy);
    public static native void nativeSetGrabbing(boolean grab);
}

