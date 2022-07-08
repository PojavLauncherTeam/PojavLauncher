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

    // Should pending events be limited?
    volatile public static List<Integer[]> PENDING_EVENT_LIST = new ArrayList<>();
    volatile public static boolean PENDING_EVENT_READY = false;
    
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
    
    public static void sendGrabbing(boolean grab, int xset, int yset) {
        // sendData(ANDROID_TYPE_GRAB_STATE, Boolean.toString(grab));
        
        GLFW.mGLFWIsGrabbing = grab;
        nativeSetGrabbing(grab, xset, yset);
    }
	// Called from Android side
    public static void receiveCallback(int type, int i1, int i2, int i3, int i4) {
       /*
        if (INPUT_DEBUG_ENABLED) {
            System.out.println("LWJGL GLFW Callback received type=" + Integer.toString(type) + ", data=" + i1 + ", " + i2 + ", " + i3 + ", " + i4);
        }
        */
        if (PENDING_EVENT_READY) {
            if (type == EVENT_TYPE_CURSOR_POS) {
                GLFW.mGLFWCursorX = (double) i1;
                GLFW.mGLFWCursorY = (double) i2;
            } else {
                PENDING_EVENT_LIST.add(new Integer[]{type, (int) i1, (int)i2, i3, i4});
            }
        } // else System.out.println("Event input is not ready yet!");
    }
    
    public static void sendData(int type, String data) {
        nativeSendData(false, type, data);
    }
    public static native void nativeSendData(boolean isAndroid, int type, String data);
    public static native boolean nativeSetInputReady(boolean ready);
    public static native String nativeClipboard(int action, byte[] copy);
    public static native void nativeAttachThreadToOther(boolean isAndroid, boolean isUseStackQueueBool);
    private static native void nativeSetGrabbing(boolean grab, int xset, int yset);
    public static native void setClass();
}

