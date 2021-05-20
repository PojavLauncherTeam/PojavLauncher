package net.kdt.pojavlaunch.customcontrols.gamepad;

import net.kdt.pojavlaunch.LWJGLGLFWKeycode;

import java.security.PublicKey;
import java.util.HashMap;

public class GamepadMapping {

    public static final int MOUSE_SCROLL_DOWN = -1;
    public static final int MOUSE_SCROLL_UP = -2;



    /*
    This class is just here to store the mapping
    can be modified to create re-mappable controls I guess

    Be warned, you should define ALL keys if you want to avoid a non defined exception

   */

    public GamepadButton BUTTON_A = new GamepadButton();
    public GamepadButton BUTTON_B = new GamepadButton();
    public GamepadButton BUTTON_X = new GamepadButton();
    public GamepadButton BUTTON_Y = new GamepadButton();
    
    public GamepadButton BUTTON_START = new GamepadButton();
    public GamepadButton BUTTON_SELECT = new GamepadButton();

    public GamepadButton TRIGGER_RIGHT = new GamepadButton();         //R2
    public GamepadButton TRIGGER_LEFT = new GamepadButton();          //L2
    
    public GamepadButton SHOULDER_RIGHT = new GamepadButton();        //R1
    public GamepadButton SHOULDER_LEFT = new GamepadButton();         //L1
    
    public int[] DIRECTION_FORWARD;
    public int[] DIRECTION_BACKWARD;
    public int[] DIRECTION_RIGHT;
    public int[] DIRECTION_LEFT;
    
    public GamepadButton THUMBSTICK_RIGHT = new GamepadButton();      //R3
    public GamepadButton THUMBSTICK_LEFT = new GamepadButton();       //L3
    
    public GamepadButton DPAD_UP = new GamepadButton();
    public GamepadButton DPAD_RIGHT = new GamepadButton();
    public GamepadButton DPAD_DOWN = new GamepadButton();
    public GamepadButton DPAD_LEFT = new GamepadButton();
    

    public void resetPressedState(){
        BUTTON_A.resetButtonState();
        BUTTON_B.resetButtonState();
        BUTTON_X.resetButtonState();
        BUTTON_Y.resetButtonState();

        BUTTON_START.resetButtonState();
        BUTTON_SELECT.resetButtonState();

        TRIGGER_LEFT.resetButtonState();
        TRIGGER_RIGHT.resetButtonState();

        SHOULDER_LEFT.resetButtonState();
        SHOULDER_RIGHT.resetButtonState();

        THUMBSTICK_LEFT.resetButtonState();
        THUMBSTICK_RIGHT.resetButtonState();

        DPAD_UP.resetButtonState();
        DPAD_RIGHT.resetButtonState();
        DPAD_DOWN.resetButtonState();
        DPAD_LEFT.resetButtonState();

    }


}
