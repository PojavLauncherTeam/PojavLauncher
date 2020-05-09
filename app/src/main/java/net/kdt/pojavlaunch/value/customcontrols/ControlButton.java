package net.kdt.pojavlaunch.value.customcontrols;

import net.kdt.pojavlaunch.*;
import android.view.*;
import java.util.*;
import android.content.*;
import org.lwjgl.input.*;
import org.lwjgl.opengl.*;

public class ControlButton implements Cloneable
{
	public static int pixelOf2dp;
	public static int pixelOf30dp;
	public static int pixelOf50dp;
	public static int pixelOf80dp;
	
	public static final int SPECIALBTN_KEYBOARD = -1;
	public static final int SPECIALBTN_TOGGLECTRL = -2;
	public static final int SPECIALBTN_MOUSEPRI = -3;
	public static final int SPECIALBTN_MOUSESEC = -4;
	
	private static ControlButton[] SPECIAL_BUTTONS;
	private static String[] SPECIAL_BUTTON_NAME_ARRAY;
	
	public static ControlButton[] getSpecialButtons(){
		if (SPECIAL_BUTTONS == null) {
			SPECIAL_BUTTONS = new ControlButton[]{
				new ControlButton("Keyboard", SPECIALBTN_KEYBOARD, pixelOf2dp * 3 + pixelOf80dp * 2, pixelOf2dp, pixelOf80dp, pixelOf30dp),
				new ControlButton("GUI", SPECIALBTN_TOGGLECTRL, pixelOf2dp, AndroidDisplay.windowHeight - pixelOf50dp * 2 + pixelOf2dp * 4),
				new ControlButton("PRI", SPECIALBTN_MOUSEPRI, pixelOf2dp, AndroidDisplay.windowHeight - pixelOf50dp * 4 + pixelOf2dp * 2),
				new ControlButton("SEC", SPECIALBTN_MOUSESEC, pixelOf2dp * 3 + pixelOf50dp * 2, AndroidDisplay.windowHeight - pixelOf50dp * 4 + pixelOf2dp * 2)
			};
		}
		
		return SPECIAL_BUTTONS;
	}
	
	public static String[] buildSpecialButtonArray() {
		if (SPECIAL_BUTTON_NAME_ARRAY == null) {
			List<String> nameList = new ArrayList<String>();
			for (ControlButton btn : getSpecialButtons()) {
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
	public int keyindex;
	public boolean hidden;
	/*
	public boolean holdCtrl;
	public boolean holdAlt;
	public boolean holdShift;
	*/
	public Object specialButtonListener;
	// public boolean hold
	
	public ControlButton() {
		this("", Keyboard.CHAR_NONE, 0, 0);
	}

	public ControlButton(String name, int keycode) {
		this(name, keycode, 0, 0);
	}
	
	public ControlButton(String name, int keycode, float x, float y) {
		this(name, keycode, x, y, pixelOf50dp, pixelOf50dp);
	}

	public ControlButton(android.content.Context ctx, int resId, int keycode, float x, float y, boolean isSquare) {
		this(ctx.getResources().getString(resId), keycode, x, y, isSquare ? pixelOf50dp : pixelOf80dp, isSquare ? pixelOf50dp : pixelOf30dp);
	}
	
	public ControlButton(String name, int keycode, float x, float y, boolean isSquare) {
		this(name, keycode, x, y, isSquare ? pixelOf50dp : pixelOf80dp, isSquare ? pixelOf50dp : pixelOf30dp);
	}
	
	public ControlButton(String name, int keycode, float x, float y, int width, int height) {
		this.name = name;
		this.keycode = keycode;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public ControlButton(String name, int keycode, int keyindex, boolean hidden, float x, float y, int width, int height) {
		this.name = name;
		this.keycode = keycode;
		this.keyindex = keyindex;
		this.hidden = hidden;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public void execute(MainActivity act, boolean isDown) {
		act.sendKeyPress(keycode, isDown);
	}
	
	public ControlButton clone() {
		return new ControlButton(name, keycode, keyindex, hidden, x, y, width, height);
	}
	
	public static interface TouchListener {
		public void onTouch(boolean down);
	}
}
