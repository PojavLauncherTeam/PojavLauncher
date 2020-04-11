package net.kdt.pojavlaunch.value.customcontrols;
import net.kdt.pojavlaunch.*;

public class ControlButton
{
	// Concept...
	public String name;
	public float x;
	public float y;
	public int lwjglKeycode;
	public boolean holdCtrl;
	public boolean holdAlt;
	public boolean holdShift;
	// public boolean hold
	
	public void execute(MainActivity act, boolean isDown) {
		act.sendKeyPress(lwjglKeycode, isDown);
	}
}
