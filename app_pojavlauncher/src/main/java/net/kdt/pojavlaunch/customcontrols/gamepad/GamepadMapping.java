package net.kdt.pojavlaunch.customcontrols.gamepad;

import net.kdt.pojavlaunch.LWJGLGLFWKeycode;

import java.util.HashMap;

public class GamepadMapping {

    public static final int MOUSE_SCROLL_DOWN = -1;
    public static final int MOUSE_SCROLL_UP = -2;



    /*
    This class is just here to store the mapping
    can be modified to create re-mappable controls I guess

    Be warned, you should define ALL keys if you want to avoid a non defined exception

   */

    public int[] BUTTON_A;
    public int[] BUTTON_B;
    public int[] BUTTON_X;
    public int[] BUTTON_Y;
    
    public int[] BUTTON_START;
    public int[] BUTTON_SELECT;

    public int[] TRIGGER_RIGHT;         //R2
    public int[] TRIGGER_LEFT;          //L2
    
    public int[] SHOULDER_RIGHT;        //R1
    public int[] SHOULDER_LEFT;         //L1
    
    public int[] DIRECTION_FORWARD;
    public int[] DIRECTION_BACKWARD;
    public int[] DIRECTION_RIGHT;
    public int[] DIRECTION_LEFT;
    
    public int[] THUMBSTICK_RIGHT;      //R3
    public int[] THUMBSTICK_LEFT;       //L3
    
    public int[] DPAD_UP;
    public int[] DPAD_RIGHT;
    public int[] DPAD_DOWN;
    public int[] DPAD_LEFT;
    



}
