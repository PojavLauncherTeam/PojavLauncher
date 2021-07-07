package net.kdt.pojavlaunch.customcontrols.gamepad;

import android.view.InputDevice;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;

import java.lang.reflect.Field;

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
    KeyEvent dummyevent = new KeyEvent(KeyEvent.ACTION_DOWN, CENTER);
    Field eventCodeField;

    {
        try {
            eventCodeField = dummyevent.getClass().getDeclaredField("mKeyCode");
            eventCodeField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public GamepadDpad(Gamepad parentPad){
        this.parentPad = parentPad;
    }

    public int update(InputEvent event) {
        if (!isDpadEvent(event)) {
            return -1;
        }

        // If the input event is a MotionEvent, check its hat axis values.
        if (event instanceof MotionEvent) {

            // Use the hat axis value to find the D-pad direction
            MotionEvent motionEvent = (MotionEvent) event;
            float xaxis = motionEvent.getAxisValue(MotionEvent.AXIS_HAT_X);
            float yaxis = motionEvent.getAxisValue(MotionEvent.AXIS_HAT_Y);

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
        }

        // If the input event is a KeyEvent, check its key code.
        else if (event instanceof KeyEvent) {

            // Use the key code to find the D-pad direction.
            KeyEvent keyEvent = (KeyEvent) event;
            if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
                pressedDirection = LEFT;
            } else if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
                pressedDirection = RIGHT;
            } else if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
                pressedDirection = UP;
            } else if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
                pressedDirection = DOWN;
            } else if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER) {
                pressedDirection = CENTER;
            }
        }

        try {
            eventCodeField.setInt(dummyevent, pressedDirection);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        parentPad.sendButton(dummyevent);
        return pressedDirection;
    }

    public static boolean isDpadEvent(InputEvent event) {
        // Check that input comes from a device with directional pads.
        // And... also the joystick since it declares sometimes as a joystick.

        return (event.getSource() & InputDevice.SOURCE_DPAD) == InputDevice.SOURCE_DPAD
                || (event.getSource() & InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK;
    }
}