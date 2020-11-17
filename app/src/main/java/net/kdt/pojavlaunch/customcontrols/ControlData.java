package net.kdt.pojavlaunch.customcontrols;

import java.util.*;
import net.kdt.pojavlaunch.*;
import org.lwjgl.glfw.*;

public class ControlData implements Cloneable
{
	public static int pixelOf2dp;
	public static int pixelOf30dp;
	public static int pixelOf50dp;
	public static int pixelOf80dp;

	public static final int SPECIALBTN_KEYBOARD = -1;
	public static final int SPECIALBTN_TOGGLECTRL = -2;
	public static final int SPECIALBTN_MOUSEPRI = -3;
	public static final int SPECIALBTN_MOUSESEC = -4;
	public static final int SPECIALBTN_VIRTUALMOUSE = -5;
	
	private static ControlData[] SPECIAL_BUTTONS;
	private static String[] SPECIAL_BUTTON_NAME_ARRAY;

	public static ControlData[] getSpecialButtons(){
		if (SPECIAL_BUTTONS == null) {
			SPECIAL_BUTTONS = new ControlData[]{
				new DynamicControlData("Keyboard", SPECIALBTN_KEYBOARD, "${margin} * 3 + ${width} * 2", "${margin}", false),
				new DynamicControlData("GUI", SPECIALBTN_TOGGLECTRL, "${margin}", "${screen_width} - ${width} * 2 + ${margin}"),
				new DynamicControlData("PRI", SPECIALBTN_MOUSEPRI, "${margin}", "${screen_height} - ${height} * 4 + ${margin} * 2"),
				new DynamicControlData("SEC", SPECIALBTN_MOUSESEC, "${margin} * 3 + ${width} * 2", "${screen_height} - ${height} * 4 + ${margin} * 2"),
				new DynamicControlData("Mouse", SPECIALBTN_VIRTUALMOUSE, "${right}", "${margin}", false)
			};
		}

		return SPECIAL_BUTTONS;
	}

	public static String[] buildSpecialButtonArray() {
		if (SPECIAL_BUTTON_NAME_ARRAY == null) {
			List<String> nameList = new ArrayList<String>();
			for (ControlData btn : getSpecialButtons()) {
				nameList.add(btn.name);
			}
			SPECIAL_BUTTON_NAME_ARRAY = nameList.toArray(new String[0]);
		}

		return SPECIAL_BUTTON_NAME_ARRAY;
	}

	public String name;
	public float x;
	public float y;
	public int width = pixelOf50dp;
	public int height = pixelOf50dp;
	public int keycode;
	public boolean hidden;
	public boolean holdCtrl;
	public boolean holdAlt;
	public boolean holdShift;
	public /* View.OnClickListener */ Object specialButtonListener;
	// public boolean hold

	public ControlData() {
		this("", LWJGLGLFWKeycode.GLFW_KEY_UNKNOWN, 0, 0);
	}

	public ControlData(String name, int keycode) {
		this(name, keycode, 0, 0);
	}

	public ControlData(String name, int keycode, float x, float y) {
		this(name, keycode, x, y, pixelOf50dp, pixelOf50dp);
	}

	public ControlData(android.content.Context ctx, int resId, int keycode, float x, float y, boolean isSquare) {
		this(ctx.getResources().getString(resId), keycode, x, y, isSquare);
	}

	public ControlData(String name, int keycode, float x, float y, boolean isSquare) {
		this(name, keycode, x, y, isSquare ? pixelOf50dp : pixelOf80dp, isSquare ? pixelOf50dp : pixelOf30dp);
	}

	public ControlData(String name, int keycode, float x, float y, int width, int height) {
		this.name = name;
		this.keycode = keycode;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public void execute(BaseMainActivity act, boolean isDown) {
		act.sendKeyPress(keycode, 0, isDown);
	}

	public ControlData clone() {
        if (this instanceof DynamicControlData) {
            return new DynamicControlData(name, keycode, ((DynamicControlData) this).dynamicX, ((DynamicControlData) this).dynamicY, width, height);
        } else {
            return new ControlData(name, keycode, x, y, width, height);
        }
	}
}
