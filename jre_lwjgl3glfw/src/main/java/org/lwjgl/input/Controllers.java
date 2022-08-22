package org.lwjgl.input;

import org.lwjgl.Sys;
import org.lwjgl.glfw.GLFWJoystickCallback;

public class Controllers {
    static GLFWController ctrlr;
public static void create() {
    ctrlr = new GLFWController();
    ctrlr.jid = 0;
}
public static Controller getController(int ctrl) {
    return ctrlr;
}
public static int getControllerCount() {
    return 1;
}
    public static void poll() {
    ctrlr.poll();
    }
    public static boolean next() {
        return false;
    }
    public static boolean isCreated() {
    return true;
    }
    public static void destroy() {

    }
    public static void clearEvents() {}
    public static Controller getEventSource() {
        return ctrlr;
    }

    public static int getEventControlIndex() {
        return 0;
    }

    public static boolean isEventButton() {
        return true;
    }

    public static boolean isEventAxis() {
        return true;
    }

    public static boolean isEventXAxis() {
        return true;
    }

    public static boolean isEventYAxis() {
        return true;
    }

    public static boolean isEventPovX() {
        return true;
    }

    public static boolean isEventPovY() {
        return true;
    }

    public static long getEventNanoseconds() {
        return Sys.getNanoTime();
    }

    public static boolean getEventButtonState() {
        return true;
    }

    public static float getEventXAxisValue() {
        return ctrlr.getXAxisValue();
    }

    public static float getEventYAxisValue() {
        return ctrlr.getYAxisValue();
    }
}
