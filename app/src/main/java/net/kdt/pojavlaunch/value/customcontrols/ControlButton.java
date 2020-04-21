package net.kdt.pojavlaunch.value.customcontrols;
import net.kdt.pojavlaunch.*;
import android.view.*;
import java.util.*;
import android.content.*;

public class ControlButton
{
	public static int pixelOf50dp;
	
	public static int SPECIALBTN_KEYBOARD = 0;
	public static int SPECIALBTN_TOGGLECTRL = 1;
	
	private static ControlButton[] SPECIAL_BUTTONS;
	private static String[] SPECIAL_BUTTON_NAME_ARRAY;
	
	public static ControlButton[] getSpecialButtons(){
		if (SPECIAL_BUTTONS == null) {
			ControlButton keyboardBtn = new ControlButton();
			keyboardBtn.lwjglKeycode = -1;
			
			ControlButton toggleCtrlBtn = new ControlButton();
			toggleCtrlBtn.lwjglKeycode = -2;

			SPECIAL_BUTTONS = new ControlButton[]{
				keyboardBtn,
				toggleCtrlBtn
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
	
	// Concept...
	public String name;
	public float x;
	public float y;
	public int width = pixelOf50dp;
	public int height = pixelOf50dp;
	public int lwjglKeycode;
	public boolean hidden;
	public boolean holdCtrl;
	public boolean holdAlt;
	public boolean holdShift;
	public View.OnClickListener specialButtonListener;
	// public boolean hold
	
	public void execute(MainActivity act, boolean isDown) {
		act.sendKeyPress(lwjglKeycode, isDown);
	}
}
