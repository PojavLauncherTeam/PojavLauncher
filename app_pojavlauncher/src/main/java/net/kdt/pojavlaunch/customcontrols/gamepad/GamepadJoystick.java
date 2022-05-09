package net.kdt.pojavlaunch.customcontrols.gamepad;

import android.view.InputDevice;
import android.view.MotionEvent;


import net.kdt.pojavlaunch.utils.MathUtils;

public class GamepadJoystick {

    //Directions
    public static final int DIRECTION_NONE = -1; //GamepadJoystick at the center

    public static final int DIRECTION_EAST = 0;
    public static final int DIRECTION_NORTH_EAST = 1;
    public static final int DIRECTION_NORTH = 2;
    public static final int DIRECTION_NORTH_WEST = 3;
    public static final int DIRECTION_WEST = 4;
    public static final int DIRECTION_SOUTH_WEST = 5;
    public static final int DIRECTION_SOUTH = 6;
    public static final int DIRECTION_SOUTH_EAST = 7;

    private final InputDevice mInputDevice;
    private final int mVerticalAxis;
    private final int mHorizontalAxis;

    public GamepadJoystick(int horizontalAxis, int verticalAxis, InputDevice device){
        this.mVerticalAxis = verticalAxis;
        this.mHorizontalAxis = horizontalAxis;
        this.mInputDevice = device;
    }

    public double getAngleRadian(MotionEvent event){
        //From -PI to PI
        return -Math.atan2(getVerticalAxis(event), getHorizontalAxis(event));
    }


    public double getAngleDegree(MotionEvent event){
        //From 0 to 360 degrees
        double result = Math.toDegrees(getAngleRadian(event));
        if(result < 0) result += 360;

        return result;
    }

    public double getMagnitude(MotionEvent event){
        float x = Math.abs(event.getAxisValue(mHorizontalAxis));
        float y = Math.abs(event.getAxisValue(mVerticalAxis));

        return MathUtils.dist(0,0, x, y);
    }

    public float getVerticalAxis(MotionEvent event){
        return applyDeadzone(event, mVerticalAxis);
    }

    public float getHorizontalAxis(MotionEvent event){
        return applyDeadzone(event, mHorizontalAxis);
    }

    public static boolean isJoystickEvent(MotionEvent event){
        return (event.getSource() & InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK
                && event.getAction() == MotionEvent.ACTION_MOVE;
    }


    public int getHeightDirection(MotionEvent event){
        if(getMagnitude(event) <= getDeadzone()) return DIRECTION_NONE;
        return ((int) ((getAngleDegree(event)+22.5)/45)) % 8;
    }

    /**
     * Get the deadzone from the Input device linked to this joystick
     * Some controller aren't supported, fallback to 0.2 if that the case.
     * @return the deadzone of the joystick
     */
    public float getDeadzone() {
        try{
            return Math.max(mInputDevice.getMotionRange(mHorizontalAxis).getFlat() * 1.9f, 0.2f);
        }catch (Exception e){
            return 0.2f;
        }
    }

    private float applyDeadzone(MotionEvent event, int axis){
        //This piece of code also modifies the value
        //to make it seem like there was no deadzone in the first place

        double magnitude = getMagnitude(event);
        float deadzone = getDeadzone();
        if (magnitude < deadzone) return 0;

        return (float) ( (event.getAxisValue(axis) / magnitude) * ((magnitude - deadzone) / (1 - deadzone)) );
    }
}
