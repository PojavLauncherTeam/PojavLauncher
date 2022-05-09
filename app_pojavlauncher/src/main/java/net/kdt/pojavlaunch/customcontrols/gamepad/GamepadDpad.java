package net.kdt.pojavlaunch.customcontrols.gamepad;

import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;

import static android.view.InputDevice.KEYBOARD_TYPE_ALPHABETIC;
import static android.view.InputDevice.KEYBOARD_TYPE_NON_ALPHABETIC;
import static android.view.InputDevice.SOURCE_GAMEPAD;
import static android.view.KeyEvent.KEYCODE_DPAD_CENTER;
import static android.view.KeyEvent.KEYCODE_DPAD_DOWN;
import static android.view.KeyEvent.KEYCODE_DPAD_LEFT;
import static android.view.KeyEvent.KEYCODE_DPAD_RIGHT;
import static android.view.KeyEvent.KEYCODE_DPAD_UP;


public class GamepadDpad {
    private int mLastKeycode = KEYCODE_DPAD_CENTER;

    /**
     * Convert the event to a 2 int array: keycode and keyAction, similar to a keyEvent
     * @param event The motion to convert
     * @return int[0] keycode, int[1] keyAction
     */
    public int[] convertEvent(MotionEvent event){
        // Use the hat axis value to find the D-pad direction
        float xaxis = event.getAxisValue(MotionEvent.AXIS_HAT_X);
        float yaxis = event.getAxisValue(MotionEvent.AXIS_HAT_Y);
        int action = KeyEvent.ACTION_DOWN;

        // Check if the AXIS_HAT_X value is -1 or 1, and set the D-pad
        // LEFT and RIGHT direction accordingly.
        if (Float.compare(xaxis, -1.0f) == 0) {
            mLastKeycode = KEYCODE_DPAD_LEFT;
        } else if (Float.compare(xaxis, 1.0f) == 0) {
            mLastKeycode = KEYCODE_DPAD_RIGHT;
        }
        // Check if the AXIS_HAT_Y value is -1 or 1, and set the D-pad
        // UP and DOWN direction accordingly.
        else if (Float.compare(yaxis, -1.0f) == 0) {
            mLastKeycode = KEYCODE_DPAD_UP;
        } else if (Float.compare(yaxis, 1.0f) == 0) {
            mLastKeycode = KEYCODE_DPAD_DOWN;
        }else {
            //No keycode change
            action = KeyEvent.ACTION_UP;
        }

        return new int[]{mLastKeycode, action};

    }

    public static boolean isDpadEvent(MotionEvent event) {
        // Check that input comes from a device with directional pads.
        // And... also the joystick since it declares sometimes as a joystick.
        return (event.getSource() & InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK;
    }

    public static boolean isDpadEvent(KeyEvent event){
        //return ((event.getSource() & InputDevice.SOURCE_DPAD) == InputDevice.SOURCE_DPAD) && (event.getDevice().getKeyboardType() == KEYBOARD_TYPE_NON_ALPHABETIC);
        return event.isFromSource(SOURCE_GAMEPAD) && event.getDevice().getKeyboardType() != KEYBOARD_TYPE_ALPHABETIC;
    }
}