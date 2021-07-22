package net.kdt.pojavlaunch.customcontrols.gamepad;

import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;

import net.kdt.pojavlaunch.LWJGLGLFWKeycode;

import java.lang.reflect.Field;

import static android.view.InputDevice.KEYBOARD_TYPE_NON_ALPHABETIC;
import static android.view.InputDevice.SOURCE_DPAD;
import static android.view.KeyEvent.KEYCODE_DPAD_CENTER;
import static android.view.KeyEvent.KEYCODE_DPAD_DOWN;
import static android.view.KeyEvent.KEYCODE_DPAD_LEFT;
import static android.view.KeyEvent.KEYCODE_DPAD_RIGHT;
import static android.view.KeyEvent.KEYCODE_DPAD_UP;

/*
    Reflection is used to avoid memory churning, and only has an negative impact at start
 */

public class GamepadDpad {


    private int lastKeycode = KEYCODE_DPAD_CENTER;
    private KeyEvent dummyEvent = new KeyEvent(KeyEvent.ACTION_DOWN, lastKeycode);
    private Field eventCodeField;
    private Field eventActionField;

    {
        try {
            eventCodeField = dummyEvent.getClass().getDeclaredField("mKeyCode");
            eventCodeField.setAccessible(true);

            eventActionField = dummyEvent.getClass().getDeclaredField("mAction");
            eventActionField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public KeyEvent convertEvent(MotionEvent event){
        // Use the hat axis value to find the D-pad direction
        float xaxis = event.getAxisValue(MotionEvent.AXIS_HAT_X);
        float yaxis = event.getAxisValue(MotionEvent.AXIS_HAT_Y);
        int action = KeyEvent.ACTION_DOWN;

        // Check if the AXIS_HAT_X value is -1 or 1, and set the D-pad
        // LEFT and RIGHT direction accordingly.
        if (Float.compare(xaxis, -1.0f) == 0) {
            lastKeycode = KEYCODE_DPAD_LEFT;
        } else if (Float.compare(xaxis, 1.0f) == 0) {
            lastKeycode = KEYCODE_DPAD_RIGHT;
        }
        // Check if the AXIS_HAT_Y value is -1 or 1, and set the D-pad
        // UP and DOWN direction accordingly.
        else if (Float.compare(yaxis, -1.0f) == 0) {
            lastKeycode = KEYCODE_DPAD_UP;
        } else if (Float.compare(yaxis, 1.0f) == 0) {
            lastKeycode = KEYCODE_DPAD_DOWN;
        }else {
            //No keycode change
            action = KeyEvent.ACTION_UP;
        }

        setDummyEventKeycode(lastKeycode);
        setDummyEventAction(action);
        dummyEvent.setSource(SOURCE_DPAD);
        return dummyEvent;

    }

    private void setDummyEventKeycode(int fakeKeycode){
        try {
            eventCodeField.setInt(dummyEvent, fakeKeycode);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void setDummyEventAction(int action){
        try {
            eventActionField.setInt(dummyEvent, action);
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