package net.kdt.pojavlaunch.customcontrols.gamepad;


import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.math.MathUtils;

import net.kdt.pojavlaunch.BaseMainActivity;
import net.kdt.pojavlaunch.LWJGLGLFWKeycode;
import net.kdt.pojavlaunch.MainActivity;
import net.kdt.pojavlaunch.R;

import org.lwjgl.glfw.CallbackBridge;

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

public class Gamepad {

    private final BaseMainActivity gameActivity;
    private final ImageView pointerView;

    private final GamepadDpad gamepadDpad = new GamepadDpad(this);

    private final GamepadJoystick leftJoystick;
    private int currentJoystickDirection = DIRECTION_NONE;

    private final GamepadJoystick rightJoystick;
    private float lastHorizontalValue = 0.0f;
    private float lastVerticalValue = 0.0f;

    private final double mouseMaxAcceleration = 2f;
    private double acceleration = 0.0f;

    private double mouseMagnitude;
    private double mouseAngle;
    private double mouseSensitivity = 19;

    private final GamepadMap gameMap = GamepadMap.getDefaultGameMap();
    private final GamepadMap menuMap = GamepadMap.getDefaultMenuMap();
    private GamepadMap currentMap = gameMap;

    private boolean lastGrabbingState = true;
    private MotionEvent lastMotionEvent = null;

    private final Thread mouseThread;
    private final Runnable mouseRunnable;
    private final Runnable switchStateRunnable;
    private final Runnable motionEventRunnable;

    public Gamepad(BaseMainActivity gameActivity, InputDevice inputDevice){
        Toast.makeText(gameActivity.getApplicationContext(),"GAMEPAD CREATED", Toast.LENGTH_LONG).show();

        leftJoystick = new GamepadJoystick(MotionEvent.AXIS_X, MotionEvent.AXIS_Y, inputDevice);
        rightJoystick = new GamepadJoystick(MotionEvent.AXIS_Z, MotionEvent.AXIS_RZ, inputDevice);

        this.gameActivity = gameActivity;
        pointerView = this.gameActivity.findViewById(R.id.console_pointer);
        pointerView.getDrawable().setFilterBitmap(false);
        notifyGUISizeChange(gameActivity.getMcScale());


        mouseThread = new Thread("Gamepad Thread"){
            long lastTime = System.nanoTime();
            final double ticks = 60D;
            final double ns = 1000000000 / ticks;
            double delta = 0;

            @Override
            public void run() {

                while (!isInterrupted()) {
                    long now = System.nanoTime();
                    delta += (now - lastTime) / ns;
                    lastTime = now;

                    if(delta >= 1) {
                        updateGrabbingState();
                        tick();

                        delta--;

                        try{ sleep(Math.max((long) ( (1 - delta) * (1000/ticks) ), 0)); } catch (InterruptedException e) {e.printStackTrace();}
                    }
                }
            }

            private void tick(){
                //Update motion events, and the mouse position
                if(lastMotionEvent != null)gameActivity.runOnUiThread(motionEventRunnable);

                if(lastHorizontalValue != 0 || lastVerticalValue != 0){
                    GamepadJoystick currentJoystick = lastGrabbingState ? leftJoystick : rightJoystick;

                    acceleration = (mouseMagnitude - currentJoystick.getDeadzone())/(1 - currentJoystick.getDeadzone());
                    acceleration = Math.pow(acceleration, mouseMaxAcceleration);

                    if(acceleration > 1) acceleration = 1;


                    CallbackBridge.mouseX += Math.cos(mouseAngle) * acceleration * mouseSensitivity;
                    CallbackBridge.mouseY -= Math.sin(mouseAngle) * acceleration * mouseSensitivity;

                    gameActivity.runOnUiThread(mouseRunnable);

                    if(!lastGrabbingState){
                        CallbackBridge.mouseX = MathUtils.clamp(CallbackBridge.mouseX, 0, CallbackBridge.windowWidth);
                        CallbackBridge.mouseY = MathUtils.clamp(CallbackBridge.mouseY, 0, CallbackBridge.windowHeight);
                        placePointerView((int) (CallbackBridge.mouseX /gameActivity.scaleFactor), (int) (CallbackBridge.mouseY/gameActivity.scaleFactor));
                    }

                    gameActivity.mouse_x = CallbackBridge.mouseX;
                    gameActivity.mouse_y = CallbackBridge.mouseY;
                }

            }
        };
        mouseThread.setPriority(Thread.MAX_PRIORITY);
        mouseThread.start();


        //Initialize runnables to be used by the input system, avoiding generating one each time is better memory.
        mouseRunnable = () -> CallbackBridge.sendCursorPos(CallbackBridge.mouseX, CallbackBridge.mouseY);
        switchStateRunnable = () -> {
            currentMap.resetPressedState();
            if(lastGrabbingState){
                currentMap = gameMap;
                pointerView.setVisibility(View.INVISIBLE);
                mouseSensitivity = 22 / gameActivity.sensitivityFactor; //sensitivity in menus is resolution dependent.
                return;
            }

            currentMap = menuMap;
            sendDirectionalKeycode(currentJoystickDirection, false, gameMap); // removing what we were doing

            gameActivity.mouse_x = CallbackBridge.windowWidth/2;
            gameActivity.mouse_y = CallbackBridge.windowHeight/2;
            CallbackBridge.sendCursorPos(gameActivity.mouse_x, gameActivity.mouse_y);
            placePointerView(CallbackBridge.physicalWidth/2, CallbackBridge.physicalHeight/2);
            pointerView.setVisibility(View.VISIBLE);
            mouseSensitivity = 14; //sensitivity in game doesn't need to be resolution dependent
        };

        motionEventRunnable = () -> {
            updateDirectionalJoystick(lastMotionEvent);
            updateMouseJoystick(lastMotionEvent);
            updateAnalogTriggers(lastMotionEvent);
            gamepadDpad.update(lastMotionEvent);
        };

    }

    private void updateGrabbingState() {
        boolean lastGrabbingValue = lastGrabbingState;
        lastGrabbingState = CallbackBridge.isGrabbing();
        if(lastGrabbingValue != lastGrabbingState){
            gameActivity.runOnUiThread(switchStateRunnable);
        }
    }

    public void update(KeyEvent event){
        gamepadDpad.update(event);
        sendButton(event);
    }

    public void update(MotionEvent event){
        //Motion events are taken into account every 1/60th second
        lastMotionEvent = event;
    }

    private void updateMouseJoystick(MotionEvent event){
        GamepadJoystick currentJoystick = lastGrabbingState ? rightJoystick : leftJoystick;
        lastHorizontalValue = currentJoystick.getHorizontalAxis(event);
        lastVerticalValue = currentJoystick.getVerticalAxis(event);

        mouseMagnitude = currentJoystick.getMagnitude(event);
        mouseAngle = currentJoystick.getAngleRadian(event);
    }

    private void updateDirectionalJoystick(MotionEvent event){
        GamepadJoystick currentJoystick = lastGrabbingState ? leftJoystick : rightJoystick;

        int lastJoystickDirection = currentJoystickDirection;
        currentJoystickDirection = currentJoystick.getHeightDirection(event);

        if(currentJoystickDirection != lastJoystickDirection){
            sendDirectionalKeycode(lastJoystickDirection, false, getCurrentMap());
            sendDirectionalKeycode(currentJoystickDirection, true, getCurrentMap());
        }
    }

    private void updateAnalogTriggers(MotionEvent event){
        getCurrentMap().TRIGGER_LEFT.update(event.getAxisValue(MotionEvent.AXIS_LTRIGGER) > 0.5);
        getCurrentMap().TRIGGER_RIGHT.update(event.getAxisValue(MotionEvent.AXIS_RTRIGGER) > 0.5);
    }

    public void notifyGUISizeChange(int newSize){
        //Change the pointer size to match UI
        int size = (int) ((22 * newSize) / gameActivity.scaleFactor);
        gameActivity.runOnUiThread(() -> pointerView.setLayoutParams(new FrameLayout.LayoutParams(size, size)));
    }

    private GamepadMap getCurrentMap(){
        return currentMap;
    }

    private static void sendDirectionalKeycode(int direction, boolean isDown, GamepadMap map){
        switch (direction){
            case DIRECTION_NORTH:
                sendInput(map.DIRECTION_FORWARD, isDown);
                break;
            case DIRECTION_NORTH_EAST:
                sendInput(map.DIRECTION_FORWARD, isDown);
                sendInput(map.DIRECTION_RIGHT, isDown);
                break;
            case DIRECTION_EAST:
                sendInput(map.DIRECTION_RIGHT, isDown);
                break;
            case DIRECTION_SOUTH_EAST:
                sendInput(map.DIRECTION_RIGHT, isDown);
                sendInput(map.DIRECTION_BACKWARD, isDown);
                break;
            case DIRECTION_SOUTH:
                sendInput(map.DIRECTION_BACKWARD, isDown);
                break;
            case DIRECTION_SOUTH_WEST:
                sendInput(map.DIRECTION_BACKWARD, isDown);
                sendInput(map.DIRECTION_LEFT, isDown);
                break;
            case DIRECTION_WEST:
                sendInput(map.DIRECTION_LEFT, isDown);
                break;
            case DIRECTION_NORTH_WEST:
                sendInput(map.DIRECTION_FORWARD, isDown);
                sendInput(map.DIRECTION_LEFT, isDown);
                break;
        }
    }

    private void placePointerView(int x, int y){
        pointerView.setX(x - pointerView.getWidth()/2);
        pointerView.setY(y - pointerView.getHeight()/2);
    }


    public void sendButton(KeyEvent event){
        int keycode = event.getKeyCode();
        switch (keycode){
            case KeyEvent.KEYCODE_BUTTON_A:
                getCurrentMap().BUTTON_A.update(event);
                break;
            case KeyEvent.KEYCODE_BUTTON_B:
                getCurrentMap().BUTTON_B.update(event);
                break;
            case KeyEvent.KEYCODE_BUTTON_X:
                getCurrentMap().BUTTON_X.update(event);
                break;
            case KeyEvent.KEYCODE_BUTTON_Y:
                getCurrentMap().BUTTON_Y.update(event);
                break;

                //Shoulders
            case KeyEvent.KEYCODE_BUTTON_L1:
                getCurrentMap().SHOULDER_LEFT.update(event);
                break;
            case KeyEvent.KEYCODE_BUTTON_R1:
                getCurrentMap().SHOULDER_RIGHT.update(event);
                break;

                //Triggers
            case KeyEvent.KEYCODE_BUTTON_L2:
                getCurrentMap().TRIGGER_LEFT.update(event);
                break;
            case KeyEvent.KEYCODE_BUTTON_R2:
                getCurrentMap().TRIGGER_RIGHT.update(event);
                break;

                //L3 || R3
            case KeyEvent.KEYCODE_BUTTON_THUMBL:
                getCurrentMap().THUMBSTICK_LEFT.update(event);
                break;
            case KeyEvent.KEYCODE_BUTTON_THUMBR:
                getCurrentMap().THUMBSTICK_RIGHT.update(event);
                break;

                //DPAD
            case GamepadDpad.UP:
                getCurrentMap().DPAD_UP.update(event);
                break;
            case GamepadDpad.DOWN:
                getCurrentMap().DPAD_DOWN.update(event);
                break;
            case GamepadDpad.LEFT:
                getCurrentMap().DPAD_LEFT.update(event);
                break;
            case GamepadDpad.RIGHT:
                getCurrentMap().DPAD_RIGHT.update(event);
                break;
            case GamepadDpad.CENTER:
                getCurrentMap().DPAD_RIGHT.update(false);
                getCurrentMap().DPAD_LEFT.update(false);
                getCurrentMap().DPAD_UP.update(false);
                getCurrentMap().DPAD_DOWN.update(false);
                break;

                //Start/select
            case KeyEvent.KEYCODE_BUTTON_START:
                getCurrentMap().BUTTON_START.update(event);
                break;
            case KeyEvent.KEYCODE_BUTTON_SELECT:
                getCurrentMap().BUTTON_SELECT.update(event);
                break;


            default:
                MainActivity.sendKeyPress(LWJGLGLFWKeycode.GLFW_KEY_SPACE, CallbackBridge.getCurrentMods(), event.getAction() == KeyEvent.ACTION_DOWN);
                break;
        }
    }

    public static void sendInput(int[] keycodes, boolean isDown){
        for(int keycode : keycodes){
            switch (keycode){
                case GamepadMap.MOUSE_SCROLL_DOWN:
                    if(isDown) CallbackBridge.sendScroll(0, -1);
                    break;
                case GamepadMap.MOUSE_SCROLL_UP:
                    if(isDown) CallbackBridge.sendScroll(0, 1);
                    break;

                case LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_RIGHT:
                    //CallbackBridge.putMouseEventWithCoords(LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_RIGHT, isDown?1:0, CallbackBridge.mouseX, CallbackBridge.mouseY);
                    MainActivity.sendMouseButton(LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_RIGHT, isDown);
                    break;
                case LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_LEFT:
                    //CallbackBridge.putMouseEventWithCoords(LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_LEFT, isDown?1:0, CallbackBridge.mouseX, CallbackBridge.mouseY);
                    MainActivity.sendMouseButton(LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_LEFT, isDown);
                    break;


                default:
                    MainActivity.sendKeyPress(keycode, CallbackBridge.getCurrentMods(), isDown);
                    break;
            }
        }

    }

    public static boolean isGamepadEvent(MotionEvent event){
        return isJoystickEvent(event);
    }

    public static boolean isGamepadEvent(KeyEvent event){
        return ((event.getSource() & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD
                || GamepadDpad.isDpadEvent(event) );
    }
}
