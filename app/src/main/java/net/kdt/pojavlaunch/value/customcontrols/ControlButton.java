package net.kdt.pojavlaunch.value.customcontrols;
import net.kdt.pojavlaunch.*;
import android.view.*;

public class ControlButton
{
	public static int pixelOf50dp;
	
	public static int SPECIALBTN_KEYBOARD = 0;
	public static int SPECIALBTN_TOGGLECTRL = 1;
	
	private static ControlButton[] SPECIAL_BUTTONS;
	
	public static ControlButton[] getSpecialButtons(){
		if (SPECIAL_BUTTONS == null) {
			ControlButton keyboardBtn = new ControlButton();
			ControlButton toggleCtrlBtn = new ControlButton();

			SPECIAL_BUTTONS = new ControlButton[]{
				keyboardBtn,
				toggleCtrlBtn
			};
		}
		
		return SPECIAL_BUTTONS;
	}
	
	// Concept...
	public String name;
	public float x;
	public float y;
	public int width = pixelOf50dp;
	public int height = pixelOf50dp;
	public int lwjglKeycode;
	public boolean holdCtrl;
	public boolean holdAlt;
	public boolean holdShift;
	public View.OnClickListener specialButtonListener;
	// public boolean hold
	
	public void execute(MainActivity act, boolean isDown) {
		act.sendKeyPress(lwjglKeycode, isDown);
	}
}
