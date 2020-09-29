package net.kdt.pojavlaunch.customcontrolsv2;

import java.util.*;
import net.kdt.pojavlaunch.*;
import org.lwjgl.glfw.*;

public class V2ControlButton implements Cloneable {
    /*
     * The v2 custom controls support for these value format use ${value}:
     * This will provive autoscale when import/export
     * - dp=N : define N dp size
     * - width_window: replace by width screen
     * - height_window: replace by height screen
     * - at_corner=(left/right)-(top/bottom)
     * - at_target=(name)
     */
/*
    public static final int SPECIALBTN_KEYBOARD = -1;
    public static final int SPECIALBTN_TOGGLECTRL = -2;
    public static final int SPECIALBTN_MOUSEPRI = -3;
    public static final int SPECIALBTN_MOUSESEC = -4;
    public static final int SPECIALBTN_VIRTUALMOUSE = -5;

    private static V2ControlButton[] SPECIAL_BUTTONS;
    private static String[] SPECIAL_BUTTON_NAME_ARRAY;

    public static V2ControlButton[] getSpecialButtons(){
        if (SPECIAL_BUTTONS == null) {
            SPECIAL_BUTTONS = new V2ControlButton[]{
                new V2ControlButton("Keyboard", SPECIALBTN_KEYBOARD, "${dp=2} * 3 + ${dp=80} * 2", "${dp=2}", false),
                new V2ControlButton("GUI", SPECIALBTN_TOGGLECTRL, "${dp=2}", "${width_window} - ${dp=50} * 2 + ${dp=2} * 4"),
                new V2ControlButton("PRI", SPECIALBTN_MOUSEPRI, "${dp=2}", "${width_window} - ${dp=50} * 4 + ${dp=2} * 2"),
                new V2ControlButton("SEC", SPECIALBTN_MOUSESEC, "${dp=2} * 3 + ${dp=50} * 2", "${height_window} - ${dp=50} * 4 + ${dp=2} * 2"),
                new V2ControlButton("Mouse", SPECIALBTN_VIRTUALMOUSE, "${width_window} - ${dp=80}", "${dp=2}", false)
            };
        }

        return SPECIAL_BUTTONS;
    }

    public static String[] buildSpecialButtonArray() {
        if (SPECIAL_BUTTON_NAME_ARRAY == null) {
            List<String> nameList = new ArrayList<String>();
            for (V2ControlButton btn : getSpecialButtons()) {
                nameList.add(btn.name);
            }
            SPECIAL_BUTTON_NAME_ARRAY = nameList.toArray(new String[0]);
        }

        return SPECIAL_BUTTON_NAME_ARRAY;
    }

    public String name;
    public float x;
    public float y;
    public String width = "${dp=50}";
    public String height = "${dp=50}";
    public int keycode;
    public int keyindex;
    public boolean hidden;
    public int mods;
    public Object specialButtonListener;
    // public boolean hold

    public V2ControlButton() {
        this("", LWJGLGLFWKeycode.GLFW_KEY_UNKNOWN, 0, 0);
    }

    public V2ControlButton(String name, int keycode) {
        this(name, keycode, 0, 0);
    }

    public V2ControlButton(String name, int keycode, String x, String y) {
        this(name, keycode, x, y, "${dp=50}", "${dp=50}");
    }

    public V2ControlButton(android.content.Context ctx, int resId, int keycode, String x, String y, boolean isSquare) {
        this(ctx.getResources().getString(resId), keycode, x, y, isSquare);
    }

    public V2ControlButton(String name, int keycode, String x, String y, boolean isSquare) {
        this(name, keycode, x, y, isSquare ? "${dp=50}" : pixelOf80dp, isSquare ? ${dp=50} : pixelOf30dp);
    }

    public V2ControlButton(String name, int keycode, String x, String y, int width, int height) {
        this.name = name;
        this.keycode = keycode;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void execute(MainActivity act, boolean isDown) {
        act.sendKeyPress(keycode, 0, isDown);
    }

    public V2ControlButton clone() {
        return new V2ControlButton(name, keycode, x, y, width, height);
    }
*/
}
