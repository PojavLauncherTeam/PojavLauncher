package net.kdt.pojavlaunch.customcontrols.gamepad;

import android.view.InputDevice;
import android.view.MotionEvent;

import com.google.android.material.math.MathUtils;

public class GamepadJoystick {

    //Directions
    public static final int DIRECTION_NONE = -1; //GamepadJoystick at the center
    public static final int DIRECTION_NORTH = 0;
    public static final int DIRECTION_NORTH_EAST = 7;
    public static final int DIRECTION_EAST = 6;
    public static final int DIRECTION_SOUTH_EAST = 5;
    public static final int DIRECTION_SOUTH = 4;
    public static final int DIRECTION_SOUTH_WEST = 3;
    public static final int DIRECTION_WEST = 2;
    public static final int DIRECTION_NORTH_WEST = 1;

    private float deadzone;

    private final int verticalAxis;
    private final int horizontalAxis;

    public GamepadJoystick(int horizontalAxis, int verticalAxis, InputDevice device){
        this.verticalAxis = verticalAxis;
        this.horizontalAxis = horizontalAxis;

        /*
            Some controllers aren't recognized as such by android, so we fallback to a default value of 0.2
            And some others don't report their MotionRange. This was the case with the xbox one series S controller.
         */
        try { deadzone = Math.max(device.getMotionRange(verticalAxis).getFlat(), device.getMotionRange(horizontalAxis).getFlat()) * 1.9f; }
        catch (NullPointerException e){ deadzone = 0.2f; }

        if(deadzone < 0.15) deadzone = 0.15f;
    }

    public double getAngleRadian(MotionEvent event){
        float x = getHorizontalAxis(event);
        float y = getVerticalAxis(event);
        if(x == y && x == 0)
            return 0.00; //atan2 don't like when x and y == 0

        return -Math.atan2(y, x);
    }

    public double getAngle(MotionEvent event){
        float x = getHorizontalAxis(event);
        float y = getVerticalAxis(event);
        if(x == y && x == 0)
            return 0.00; //atan2 don't like when x and y == 0

        return 180+(Math.atan2(x, y)*57);
    }

    public double getMagnitude(MotionEvent event){
        float x = Math.abs(event.getAxisValue(horizontalAxis));
        float y = Math.abs(event.getAxisValue(verticalAxis));

        return MathUtils.dist(0,0, x, y);
    }

    public float getVerticalAxis(MotionEvent event){
        return applyDeadzone(event, verticalAxis);
    }

    public float getHorizontalAxis(MotionEvent event){
        return applyDeadzone(event, horizontalAxis);
    }

    private float applyDeadzone(MotionEvent event, int axis){
        /*
            This piece of code also modifies the value
            to make it seem like there was no deadzone in the first place
         */

        double magnitude = getMagnitude(event);
        if (magnitude < deadzone){
            return 0;
        }

        return (float) ((event.getAxisValue(axis) / magnitude) * ((magnitude - deadzone) / (1 - deadzone)));
    }

    public static boolean isJoystickEvent(MotionEvent event){
        return (event.getSource() & InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK
                && event.getAction() == MotionEvent.ACTION_MOVE;
    }


    public int getHeightDirection(MotionEvent event){
        if(getMagnitude(event) <= deadzone) return DIRECTION_NONE;
        return ((int) ((getAngle(event)+22.5)/45)) % 8;
    }


    public float getDeadzone() {
        return deadzone;
    }
}
