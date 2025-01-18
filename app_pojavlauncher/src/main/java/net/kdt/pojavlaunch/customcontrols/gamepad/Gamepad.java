package net.kdt.pojavlaunch.customcontrols.gamepad;


import static android.view.MotionEvent.AXIS_HAT_X;
import static android.view.MotionEvent.AXIS_HAT_Y;
import static android.view.MotionEvent.AXIS_LTRIGGER;
import static android.view.MotionEvent.AXIS_RTRIGGER;
import static android.view.MotionEvent.AXIS_RZ;
import static android.view.MotionEvent.AXIS_X;
import static android.view.MotionEvent.AXIS_Y;
import static android.view.MotionEvent.AXIS_Z;

import android.content.Context;
import android.view.Choreographer;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.core.content.res.ResourcesCompat;
import androidx.core.math.MathUtils;

import net.kdt.pojavlaunch.GrabListener;
import net.kdt.pojavlaunch.LwjglGlfwKeycode;
import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.prefs.LauncherPreferences;
import net.kdt.pojavlaunch.utils.MCOptionUtils;

import org.lwjgl.glfw.CallbackBridge;

import static net.kdt.pojavlaunch.Tools.currentDisplayMetrics;
import static net.kdt.pojavlaunch.customcontrols.gamepad.GamepadJoystick.DIRECTION_EAST;
import static net.kdt.pojavlaunch.customcontrols.gamepad.GamepadJoystick.DIRECTION_NONE;
import static net.kdt.pojavlaunch.customcontrols.gamepad.GamepadJoystick.DIRECTION_NORTH;
import static net.kdt.pojavlaunch.customcontrols.gamepad.GamepadJoystick.DIRECTION_NORTH_EAST;
import static net.kdt.pojavlaunch.customcontrols.gamepad.GamepadJoystick.DIRECTION_NORTH_WEST;
import static net.kdt.pojavlaunch.customcontrols.gamepad.GamepadJoystick.DIRECTION_SOUTH;
import static net.kdt.pojavlaunch.customcontrols.gamepad.GamepadJoystick.DIRECTION_SOUTH_EAST;
import static net.kdt.pojavlaunch.customcontrols.gamepad.GamepadJoystick.DIRECTION_SOUTH_WEST;
import static net.kdt.pojavlaunch.customcontrols.gamepad.GamepadJoystick.DIRECTION_WEST;
import static net.kdt.pojavlaunch.customcontrols.gamepad.GamepadJoystick.isJoystickEvent;
import static net.kdt.pojavlaunch.prefs.LauncherPreferences.PREF_DEADZONE_SCALE;
import static net.kdt.pojavlaunch.prefs.LauncherPreferences.PREF_SCALE_FACTOR;
import static net.kdt.pojavlaunch.utils.MCOptionUtils.getMcScale;
import static org.lwjgl.glfw.CallbackBridge.sendKeyPress;
import static org.lwjgl.glfw.CallbackBridge.sendMouseButton;

import fr.spse.gamepad_remapper.GamepadHandler;
import fr.spse.gamepad_remapper.Settings;

public class Gamepad implements GrabListener, GamepadHandler {

    /* Sensitivity, adjusted according to screen size */
    private final double mSensitivityFactor = (1.4 * (1080f/ currentDisplayMetrics.heightPixels));

    private final ImageView mPointerImageView;

    private final GamepadJoystick mLeftJoystick;
    private int mCurrentJoystickDirection = DIRECTION_NONE;

    private final GamepadJoystick mRightJoystick;
    private float mLastHorizontalValue = 0.0f;
    private float mLastVerticalValue = 0.0f;

    private static final double MOUSE_MAX_ACCELERATION = 2f;

    private double mMouseMagnitude;
    private double mMouseAngle;
    private double mMouseSensitivity = 19;

    private GamepadMap mGameMap;
    private GamepadMap mMenuMap;
    private GamepadMap mCurrentMap;

    private boolean isGrabbing;


    /* Choreographer with time to compute delta on ticking */
    private final Choreographer mScreenChoreographer;
    private long mLastFrameTime;

    /* Listen for change in gui scale */
    @SuppressWarnings("FieldCanBeLocal") //the field is used in a WeakReference
    private final MCOptionUtils.MCOptionListener mGuiScaleListener = () -> notifyGUISizeChange(getMcScale());

    private final GamepadDataProvider mMapProvider;

    public Gamepad(View contextView, InputDevice inputDevice, GamepadDataProvider mapProvider, boolean showCursor){
        Settings.setDeadzoneScale(PREF_DEADZONE_SCALE);

        mScreenChoreographer = Choreographer.getInstance();
        Choreographer.FrameCallback frameCallback = new Choreographer.FrameCallback() {
            @Override
            public void doFrame(long frameTimeNanos) {
                tick(frameTimeNanos);
                mScreenChoreographer.postFrameCallback(this);
            }
        };
        mScreenChoreographer.postFrameCallback(frameCallback);
        mLastFrameTime = System.nanoTime();

        /* Add the listener for the cross hair */
        MCOptionUtils.addMCOptionListener(mGuiScaleListener);

        mLeftJoystick = new GamepadJoystick(AXIS_X, AXIS_Y, inputDevice);
        mRightJoystick = new GamepadJoystick(AXIS_Z, AXIS_RZ, inputDevice);


        Context ctx = contextView.getContext();
        mPointerImageView = new ImageView(contextView.getContext());
        mPointerImageView.setImageDrawable(ResourcesCompat.getDrawable(ctx.getResources(), R.drawable.ic_gamepad_pointer, ctx.getTheme()));
        mPointerImageView.getDrawable().setFilterBitmap(false);

        int size = (int) ((22 * getMcScale()) / PREF_SCALE_FACTOR);
        mPointerImageView.setLayoutParams(new FrameLayout.LayoutParams(size, size));

        mMapProvider = mapProvider;

        CallbackBridge.sendCursorPos(CallbackBridge.windowWidth/2f, CallbackBridge.windowHeight/2f);

        if(showCursor) {
            ((ViewGroup)contextView.getParent()).addView(mPointerImageView);
        }


        placePointerView(CallbackBridge.physicalWidth/2, CallbackBridge.physicalHeight/2);

        reloadGamepadMaps();
        mMapProvider.attachGrabListener(this);
    }


    public void reloadGamepadMaps() {
        if(mGameMap != null) mGameMap.resetPressedState();
        if(mMenuMap != null) mMenuMap.resetPressedState();
        GamepadMapStore.load();
        mGameMap = mMapProvider.getGameMap();
        mMenuMap = mMapProvider.getMenuMap();
        mCurrentMap = mGameMap;
        // Force state refresh
        boolean currentGrab = CallbackBridge.isGrabbing();
        isGrabbing = !currentGrab;
        onGrabState(currentGrab);
    }

    public void updateJoysticks(){
        updateDirectionalJoystick();
        updateMouseJoystick();
    }

    public void notifyGUISizeChange(int newSize){
        //Change the pointer size to match UI
        int size = (int) ((22 * newSize) / PREF_SCALE_FACTOR);
        mPointerImageView.post(() -> mPointerImageView.setLayoutParams(new FrameLayout.LayoutParams(size, size)));

    }


    public static void sendInput(short[] keycodes, boolean isDown){
        for(short keycode : keycodes){
            switch (keycode){
                case GamepadMap.MOUSE_SCROLL_DOWN:
                    if(isDown) CallbackBridge.sendScroll(0, -1);
                    break;
                case GamepadMap.MOUSE_SCROLL_UP:
                    if(isDown) CallbackBridge.sendScroll(0, 1);
                    break;
                case GamepadMap.MOUSE_LEFT:
                    sendMouseButton(LwjglGlfwKeycode.GLFW_MOUSE_BUTTON_LEFT, isDown);
                    break;
                case GamepadMap.MOUSE_MIDDLE:
                    sendMouseButton(LwjglGlfwKeycode.GLFW_MOUSE_BUTTON_MIDDLE, isDown);
                    break;
                case GamepadMap.MOUSE_RIGHT:
                    sendMouseButton(LwjglGlfwKeycode.GLFW_MOUSE_BUTTON_RIGHT, isDown);
                    break;
                case GamepadMap.UNSPECIFIED:
                    break;

                default:
                    sendKeyPress(keycode, CallbackBridge.getCurrentMods(), isDown);
                    CallbackBridge.setModifiers(keycode, isDown);
                    break;
            }
        }

    }

    public static boolean isGamepadEvent(MotionEvent event){
        return isJoystickEvent(event);
    }

    public static boolean isGamepadEvent(KeyEvent event){
        boolean isGamepad = ((event.getSource() & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD)
                || ((event.getDevice() != null) && ((event.getDevice().getSources() & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD));

        return isGamepad && GamepadDpad.isDpadEvent(event);
    }

    /**
     * Send the new mouse position, computing the delta
     * @param frameTimeNanos The time to render the frame, used to compute mouse delta
     */
    private void tick(long frameTimeNanos){
        //update mouse position
        long newFrameTime = System.nanoTime();
        if(mLastHorizontalValue != 0 || mLastVerticalValue != 0){

            double acceleration = Math.pow(mMouseMagnitude, MOUSE_MAX_ACCELERATION);
            if(acceleration > 1) acceleration = 1;

            // Compute delta since last tick time
            float deltaX = (float) (Math.cos(mMouseAngle) * acceleration * mMouseSensitivity);
            float deltaY = (float) (Math.sin(mMouseAngle) * acceleration * mMouseSensitivity);
            newFrameTime = System.nanoTime();  // More accurate delta
            float deltaTimeScale = ((newFrameTime - mLastFrameTime) / 16666666f); // Scale of 1 = 60Hz
            deltaX *= deltaTimeScale;
            deltaY *= deltaTimeScale;

            CallbackBridge.mouseX += deltaX;
            CallbackBridge.mouseY -= deltaY;

            if(!isGrabbing){
                CallbackBridge.mouseX = MathUtils.clamp(CallbackBridge.mouseX, 0, CallbackBridge.windowWidth);
                CallbackBridge.mouseY = MathUtils.clamp(CallbackBridge.mouseY, 0, CallbackBridge.windowHeight);
                placePointerView((int) (CallbackBridge.mouseX / PREF_SCALE_FACTOR), (int) (CallbackBridge.mouseY/ PREF_SCALE_FACTOR));
            }

            //Send the mouse to the game
            CallbackBridge.sendCursorPos(CallbackBridge.mouseX, CallbackBridge.mouseY);
        }

        // Update last nano time
        mLastFrameTime = newFrameTime;
    }

    private void updateMouseJoystick(){
        GamepadJoystick currentJoystick = isGrabbing ? mRightJoystick : mLeftJoystick;
        float horizontalValue = currentJoystick.getHorizontalAxis();
        float verticalValue = currentJoystick.getVerticalAxis();
        if(horizontalValue != mLastHorizontalValue || verticalValue != mLastVerticalValue){
            mLastHorizontalValue = horizontalValue;
            mLastVerticalValue = verticalValue;

            mMouseMagnitude = currentJoystick.getMagnitude();
            mMouseAngle = currentJoystick.getAngleRadian();

            tick(System.nanoTime());
            return;
        }
        mLastHorizontalValue = horizontalValue;
        mLastVerticalValue = verticalValue;

        mMouseMagnitude = currentJoystick.getMagnitude();
        mMouseAngle = currentJoystick.getAngleRadian();

    }

    private void updateDirectionalJoystick(){
        GamepadJoystick currentJoystick = isGrabbing ? mLeftJoystick : mRightJoystick;

        int lastJoystickDirection = mCurrentJoystickDirection;
        mCurrentJoystickDirection = currentJoystick.getHeightDirection();

        if(mCurrentJoystickDirection == lastJoystickDirection) return;

        sendDirectionalKeycode(lastJoystickDirection, false, getCurrentMap());
        sendDirectionalKeycode(mCurrentJoystickDirection, true, getCurrentMap());
    }


    private GamepadMap getCurrentMap(){
        return mCurrentMap;
    }

    private static void sendDirectionalKeycode(int direction, boolean isDown, GamepadMap map){
        switch (direction){
            case DIRECTION_NORTH:
                map.DIRECTION_FORWARD.update(isDown);
                break;
            case DIRECTION_NORTH_EAST:
                map.DIRECTION_FORWARD.update(isDown);
                map.DIRECTION_RIGHT.update(isDown);
                break;
            case DIRECTION_EAST:
                map.DIRECTION_RIGHT.update(isDown);
                break;
            case DIRECTION_SOUTH_EAST:
                map.DIRECTION_RIGHT.update(isDown);
                map.DIRECTION_BACKWARD.update(isDown);
                break;
            case DIRECTION_SOUTH:
                map.DIRECTION_BACKWARD.update(isDown);
                break;
            case DIRECTION_SOUTH_WEST:
                map.DIRECTION_BACKWARD.update(isDown);
                map.DIRECTION_LEFT.update(isDown);
                break;
            case DIRECTION_WEST:
                map.DIRECTION_LEFT.update(isDown);
                break;
            case DIRECTION_NORTH_WEST:
                map.DIRECTION_FORWARD.update(isDown);
                map.DIRECTION_LEFT.update(isDown);
                break;
        }
    }

    /** Place the pointer on the screen, offsetting the image size */
    private void placePointerView(int x, int y){
        mPointerImageView.setX(x - mPointerImageView.getWidth()/2f);
        mPointerImageView.setY(y - mPointerImageView.getHeight()/2f);
    }

    /** Update the grabbing state, and change the currentMap, mouse position and sensibility */
    @Override
    public void onGrabState(boolean isGrabbing) {
        boolean lastGrabbingValue = this.isGrabbing;
        this.isGrabbing = isGrabbing;
        if(lastGrabbingValue == isGrabbing) return;

        // Switch grabbing state then
        mCurrentMap.resetPressedState();
        if(isGrabbing){
            mCurrentMap = mGameMap;
            mPointerImageView.setVisibility(View.INVISIBLE);
            mMouseSensitivity = 18;
            return;
        }

        mCurrentMap = mMenuMap;
        sendDirectionalKeycode(mCurrentJoystickDirection, false, mGameMap); // removing what we were doing

        CallbackBridge.sendCursorPos(CallbackBridge.windowWidth/2f, CallbackBridge.windowHeight/2f);
        placePointerView(CallbackBridge.physicalWidth/2, CallbackBridge.physicalHeight/2);
        mPointerImageView.setVisibility(View.VISIBLE);
        // Sensitivity in menu is MC and HARDWARE resolution dependent
        mMouseSensitivity = 19 * PREF_SCALE_FACTOR / mSensitivityFactor;
    }

    @Override
    public void handleGamepadInput(int keycode, float value) {
        boolean isKeyEventDown = value == 1f;
        switch (keycode){
            case KeyEvent.KEYCODE_BUTTON_A:
                getCurrentMap().BUTTON_A.update(isKeyEventDown);
                break;
            case KeyEvent.KEYCODE_BUTTON_B:
                getCurrentMap().BUTTON_B.update(isKeyEventDown);
                break;
            case KeyEvent.KEYCODE_BUTTON_X:
                getCurrentMap().BUTTON_X.update(isKeyEventDown);
                break;
            case KeyEvent.KEYCODE_BUTTON_Y:
                getCurrentMap().BUTTON_Y.update(isKeyEventDown);
                break;

            //Shoulders
            case KeyEvent.KEYCODE_BUTTON_L1:
                getCurrentMap().SHOULDER_LEFT.update(isKeyEventDown);
                break;
            case KeyEvent.KEYCODE_BUTTON_R1:
                getCurrentMap().SHOULDER_RIGHT.update(isKeyEventDown);
                break;

            //Triggers
            case KeyEvent.KEYCODE_BUTTON_L2:
                getCurrentMap().TRIGGER_LEFT.update(isKeyEventDown);
                break;
            case KeyEvent.KEYCODE_BUTTON_R2:
                getCurrentMap().TRIGGER_RIGHT.update(isKeyEventDown);
                break;

            //L3 || R3
            case KeyEvent.KEYCODE_BUTTON_THUMBL:
                getCurrentMap().THUMBSTICK_LEFT.update(isKeyEventDown);
                break;
            case KeyEvent.KEYCODE_BUTTON_THUMBR:
                getCurrentMap().THUMBSTICK_RIGHT.update(isKeyEventDown);
                break;

            //DPAD
            case KeyEvent.KEYCODE_DPAD_UP:
                getCurrentMap().DPAD_UP.update(isKeyEventDown);
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                getCurrentMap().DPAD_DOWN.update(isKeyEventDown);
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                getCurrentMap().DPAD_LEFT.update(isKeyEventDown);
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                getCurrentMap().DPAD_RIGHT.update(isKeyEventDown);
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER:
                getCurrentMap().DPAD_RIGHT.update(false);
                getCurrentMap().DPAD_LEFT.update(false);
                getCurrentMap().DPAD_UP.update(false);
                getCurrentMap().DPAD_DOWN.update(false);
                break;

            //Start/select
            case KeyEvent.KEYCODE_BUTTON_START:
                getCurrentMap().BUTTON_START.update(isKeyEventDown);
                break;
            case KeyEvent.KEYCODE_BUTTON_SELECT:
                getCurrentMap().BUTTON_SELECT.update(isKeyEventDown);
                break;

            /* Now, it is time for motionEvents */
            case AXIS_HAT_X:
                getCurrentMap().DPAD_RIGHT.update(value > 0.85);
                getCurrentMap().DPAD_LEFT.update(value < -0.85);
                break;
            case AXIS_HAT_Y:
                getCurrentMap().DPAD_DOWN.update(value > 0.85);
                getCurrentMap().DPAD_UP.update(value < -0.85);
                break;

            // Left joystick
            case AXIS_X:
                mLeftJoystick.setXAxisValue(value);
                updateJoysticks();
                break;
            case AXIS_Y:
                mLeftJoystick.setYAxisValue(value);
                updateJoysticks();
                break;

            // Right joystick
            case AXIS_Z:
                mRightJoystick.setXAxisValue(value);
                updateJoysticks();
                break;
            case AXIS_RZ:
                mRightJoystick.setYAxisValue(value);
                updateJoysticks();
                break;

            // Triggers
            case AXIS_RTRIGGER:
                getCurrentMap().TRIGGER_RIGHT.update(value > 0.5);
                break;
            case AXIS_LTRIGGER:
                getCurrentMap().TRIGGER_LEFT.update(value > 0.5);
                break;

            default:
                sendKeyPress(LwjglGlfwKeycode.GLFW_KEY_SPACE, CallbackBridge.getCurrentMods(), isKeyEventDown);
                break;
        }
    }
}
