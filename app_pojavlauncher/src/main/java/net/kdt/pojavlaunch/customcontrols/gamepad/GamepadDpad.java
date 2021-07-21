package net.kdt.pojavlaunch.customcontrols.gamepad;

import android.view.InputDevice;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;

import java.lang.reflect.Field;

import static android.view.InputDevice.KEYBOARD_TYPE_NON_ALPHABETIC;

/*
    Code from the android documentation
 */

public class GamepadDpad {
    final static int UP       = 999;
    final static int LEFT     = 9999;
    final static int RIGHT    = 99999;
    final static int DOWN     = 999999;
    final static int CENTER   = 9999999;

    int pressedDirection = -1;
    Gamepad parentPad;
    KeyEvent dummyEvent = new KeyEvent(KeyEvent.ACTION_DOWN, CENTER);
    Field eventCodeField;

    {
        try {
            eventCodeField = dummyEvent.getClass().getDeclaredField("mKeyCode");
            eventCodeField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public GamepadDpad(Gamepad parentPad){
        this.parentPad = parentPad;
    }

    public void update(KeyEvent event){

        //TODO check if the event is valid
        if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
            pressedDirection = LEFT;
        } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
            pressedDirection = RIGHT;
        } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
            pressedDirection = UP;
        } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
            pressedDirection = DOWN;
        } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER) {
            pressedDirection = CENTER;
        }

        setDummyEventKeyCode(pressedDirection);
        parentPad.sendButton(dummyEvent);
    }

    public void update(MotionEvent event){
        //TODO check if the event is valid

        // Use the hat axis value to find the D-pad direction
        float xaxis = event.getAxisValue(MotionEvent.AXIS_HAT_X);
        float yaxis = event.getAxisValue(MotionEvent.AXIS_HAT_Y);

        // Check if the AXIS_HAT_X value is -1 or 1, and set the D-pad
        // LEFT and RIGHT direction accordingly.
        if (Float.compare(xaxis, -1.0f) == 0) {
            pressedDirection = LEFT;
        } else if (Float.compare(xaxis, 1.0f) == 0) {
            pressedDirection = RIGHT;
        }
        // Check if the AXIS_HAT_Y value is -1 or 1, and set the D-pad
        // UP and DOWN direction accordingly.
        else if (Float.compare(yaxis, -1.0f) == 0) {
            pressedDirection =  UP;
        } else if (Float.compare(yaxis, 1.0f) == 0) {
            pressedDirection =  DOWN;
        }else {
            pressedDirection = CENTER;
        }

        setDummyEventKeyCode(pressedDirection);
        parentPad.sendButton(dummyEvent);
    }

    private void setDummyEventKeyCode(int fakeKeycode){
        try {
            eventCodeField.setInt(dummyEvent, fakeKeycode);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static boolean isDpadEvent(MotionEvent event) {
        // Check that input comes from a device with directional pads.
        // And... also the joystick since it declares sometimes as a joystick.
        return (event.getSource() & InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK;
    }

    public static boolean isDpadEvent(KeyEvent event){
        return ((event.getSource() & InputDevice.SOURCE_DPAD) == InputDevice.SOURCE_DPAD) && (event.getDevice().getKeyboardType() == KEYBOARD_TYPE_NON_ALPHABETIC);
    }
}