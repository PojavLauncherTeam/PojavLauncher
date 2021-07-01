package net.kdt.pojavlaunch.customcontrols.gamepad;

import android.os.Handler;
import android.os.Looper;
import android.view.InputDevice;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

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

    private BaseMainActivity gameActivity;
    private ImageView pointerView;

    private GamepadDpad gamepadDpad = new GamepadDpad();

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

    private final GamepadMapping gameMap = new GamepadMapping();
    private final GamepadMapping menuMap = new GamepadMapping();
    private GamepadMapping currentMap = gameMap;

    private boolean lastGrabbingState = true;


    private final Thread mouseThread;
    private final Runnable mouseRunnable;
    private final Runnable switchStateRunnable;

    public Gamepad(BaseMainActivity gameActivity, InputDevice inputDevice){
        leftJoystick = new GamepadJoystick(MotionEvent.AXIS_X, MotionEvent.AXIS_Y, inputDevice);
        rightJoystick = new GamepadJoystick(MotionEvent.AXIS_Z, MotionEvent.AXIS_RZ, inputDevice);


        this.gameActivity = gameActivity;
        pointerView = this.gameActivity.findViewById(R.id.console_pointer);
        createMapping();

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


                        try {
                            sleep(Math.max((long) ( (1 - delta) * (1000/ticks) ), 0));

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }

            private void tick(){
                if(lastHorizontalValue != 0 || lastVerticalValue != 0){
                    GamepadJoystick currentJoystick = CallbackBridge.isGrabbing() ? leftJoystick : rightJoystick;

                    acceleration = (mouseMagnitude - currentJoystick.getDeadzone())/(1 - currentJoystick.getDeadzone());
                    acceleration = Math.pow(acceleration, mouseMaxAcceleration);

                    if(acceleration > 1) acceleration = 1;


                    CallbackBridge.mouseX += Math.cos(mouseAngle) * acceleration * mouseSensitivity;
                    CallbackBridge.mouseY -= Math.sin(mouseAngle) * acceleration * mouseSensitivity;
                    gameActivity.mouse_x = CallbackBridge.mouseX;
                    gameActivity.mouse_y = CallbackBridge.mouseY;

                    gameActivity.runOnUiThread(mouseRunnable);

                    if(!CallbackBridge.isGrabbing()){
                        placePointerView((int)(CallbackBridge.mouseX  / gameActivity.scaleFactor), (int) (CallbackBridge.mouseY  / gameActivity.scaleFactor));
                    }
                }

            }
        };
        mouseThread.setPriority(Thread.MAX_PRIORITY);
        mouseThread.start();


        //Initialize runnables to be used by the input system, avoiding generating one each time is better memory.
        mouseRunnable = () -> CallbackBridge.sendCursorPos(gameActivity.mouse_x, gameActivity.mouse_y);
        switchStateRunnable = () -> {
            currentMap.resetPressedState();
            if(lastGrabbingState){
                currentMap = gameMap;
                pointerView.setVisibility(View.INVISIBLE);
                mouseSensitivity = 26 / gameActivity.sensitivityFactor; //sensitivity in menus is resolution dependent.
                return;
            }

            currentMap = menuMap;
            sendDirectionalKeycode(currentJoystickDirection, false, gameMap); // removing what we were doing

            gameActivity.mouse_x = CallbackBridge.windowWidth/2;
            gameActivity.mouse_y = CallbackBridge.windowHeight/2;
            CallbackBridge.sendCursorPos(gameActivity.mouse_x, gameActivity.mouse_y);
            placePointerView(CallbackBridge.physicalWidth/2, CallbackBridge.physicalHeight/2);
            pointerView.setVisibility(View.VISIBLE);
            mouseSensitivity = 15; //sensitivity in game doesn't need to be resolution dependent
        };
    }

    private void updateGrabbingState() {
        boolean lastGrabbingValue = lastGrabbingState;
        lastGrabbingState = CallbackBridge.isGrabbing();
        if(lastGrabbingValue != lastGrabbingState){
            gameActivity.runOnUiThread(switchStateRunnable);
        }
    }

    private void createMapping(){
        //create mappings to fit our needs

        //GAMEMAP
        gameMap.BUTTON_A.keycodes = new int[]{LWJGLGLFWKeycode.GLFW_KEY_SPACE};
        gameMap.BUTTON_B.keycodes = new int[]{LWJGLGLFWKeycode.GLFW_KEY_Q};
        gameMap.BUTTON_X.keycodes = new int[]{LWJGLGLFWKeycode.GLFW_KEY_F};
        gameMap.BUTTON_Y.keycodes = new int[]{LWJGLGLFWKeycode.GLFW_KEY_E};

        gameMap.DIRECTION_FORWARD = new int[]{LWJGLGLFWKeycode.GLFW_KEY_W};
        gameMap.DIRECTION_BACKWARD = new int[]{LWJGLGLFWKeycode.GLFW_KEY_S};
        gameMap.DIRECTION_RIGHT = new int[]{LWJGLGLFWKeycode.GLFW_KEY_D};
        gameMap.DIRECTION_LEFT = new int[]{LWJGLGLFWKeycode.GLFW_KEY_A};

        gameMap.DPAD_UP.keycodes = new int[]{};
        gameMap.DPAD_DOWN.keycodes = new int[]{};
        gameMap.DPAD_RIGHT.keycodes = new int[]{};
        gameMap.DPAD_LEFT.keycodes = new int[]{};

        gameMap.SHOULDER_LEFT.keycodes = new int[]{GamepadMapping.MOUSE_SCROLL_UP};
        gameMap.SHOULDER_RIGHT.keycodes = new int[]{GamepadMapping.MOUSE_SCROLL_DOWN};

        gameMap.TRIGGER_LEFT.keycodes = new int[]{LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_RIGHT};
        gameMap.TRIGGER_RIGHT.keycodes = new int[]{LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_LEFT};

        gameMap.THUMBSTICK_LEFT.keycodes = new int[]{LWJGLGLFWKeycode.GLFW_KEY_LEFT_SHIFT};
        gameMap.THUMBSTICK_LEFT.isToggleable = true;
        gameMap.THUMBSTICK_RIGHT.keycodes = new int[]{LWJGLGLFWKeycode.GLFW_KEY_F5};

        gameMap.BUTTON_START.keycodes = new int[]{LWJGLGLFWKeycode.GLFW_KEY_ESCAPE};
        gameMap.BUTTON_SELECT.keycodes = new int[]{};


        //MENU MAP
        menuMap.BUTTON_A.keycodes = new int[]{LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_LEFT};
        menuMap.BUTTON_B.keycodes = new int[]{LWJGLGLFWKeycode.GLFW_KEY_ESCAPE};
        menuMap.BUTTON_X.keycodes = new int[]{LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_RIGHT};
        menuMap.BUTTON_Y.keycodes = new int[]{LWJGLGLFWKeycode.GLFW_KEY_LEFT_SHIFT, LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_RIGHT}; //Oops, doesn't work since left shift isn't properly applied.

        menuMap.DIRECTION_FORWARD = new int[]{GamepadMapping.MOUSE_SCROLL_UP, GamepadMapping.MOUSE_SCROLL_UP, GamepadMapping.MOUSE_SCROLL_UP,GamepadMapping.MOUSE_SCROLL_UP,GamepadMapping.MOUSE_SCROLL_UP};
        menuMap.DIRECTION_BACKWARD = new int[]{GamepadMapping.MOUSE_SCROLL_DOWN, GamepadMapping.MOUSE_SCROLL_DOWN, GamepadMapping.MOUSE_SCROLL_DOWN,GamepadMapping.MOUSE_SCROLL_DOWN,GamepadMapping.MOUSE_SCROLL_DOWN};
        menuMap.DIRECTION_RIGHT = new int[]{};
        menuMap.DIRECTION_LEFT = new int[]{};

        menuMap.DPAD_UP.keycodes = new int[]{};
        menuMap.DPAD_DOWN.keycodes = new int[]{};
        menuMap.DPAD_RIGHT.keycodes = new int[]{};
        menuMap.DPAD_LEFT.keycodes = new int[]{};

        menuMap.SHOULDER_LEFT.keycodes = new int[]{GamepadMapping.MOUSE_SCROLL_UP};
        menuMap.SHOULDER_RIGHT.keycodes = new int[]{GamepadMapping.MOUSE_SCROLL_DOWN};

        menuMap.TRIGGER_LEFT.keycodes = new int[]{};
        menuMap.TRIGGER_RIGHT.keycodes = new int[]{};

        menuMap.THUMBSTICK_LEFT.keycodes = new int[]{};
        menuMap.THUMBSTICK_RIGHT.keycodes = new int[]{};

        menuMap.BUTTON_START.keycodes = new int[]{LWJGLGLFWKeycode.GLFW_KEY_ESCAPE};
        menuMap.BUTTON_SELECT.keycodes = new int[]{};


    }

    public void update(KeyEvent event){
        sendButton(event);
    }

    public void update(MotionEvent event){
        updateDirectionalJoystick(event);
        updateMouseJoystick(event);
        updateAnalogTriggers(event);
    }

    private void updateMouseJoystick(MotionEvent event){
        GamepadJoystick currentJoystick = CallbackBridge.isGrabbing() ? rightJoystick : leftJoystick;
        lastHorizontalValue = currentJoystick.getHorizontalAxis(event);
        lastVerticalValue = currentJoystick.getVerticalAxis(event);

        mouseMagnitude = currentJoystick.getMagnitude(event);
        mouseAngle = currentJoystick.getAngleRadian(event);
    }

    private void updateDirectionalJoystick(MotionEvent event){
        GamepadJoystick currentJoystick = CallbackBridge.isGrabbing() ? leftJoystick : rightJoystick;

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

    private GamepadMapping getCurrentMap(){
        return currentMap;
    }

    private static void sendDirectionalKeycode(int direction, boolean isDown, GamepadMapping map){
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
        pointerView.setX(x-32);
        pointerView.setY(y-32);
    }


    private void sendButton(KeyEvent event){
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
                case GamepadMapping.MOUSE_SCROLL_DOWN:
                    if(isDown) CallbackBridge.sendScroll(0, -1);
                    break;
                case GamepadMapping.MOUSE_SCROLL_UP:
                    if(isDown) CallbackBridge.sendScroll(0, 1);
                    break;

                case LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_RIGHT:
                    CallbackBridge.putMouseEventWithCoords(LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_RIGHT, isDown?1:0, CallbackBridge.mouseX, CallbackBridge.mouseY);
                    //MainActivity.sendMouseButton(LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_RIGHT, isDown);
                    break;
                case LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_LEFT:
                    CallbackBridge.putMouseEventWithCoords(LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_LEFT, isDown?1:0, CallbackBridge.mouseX, CallbackBridge.mouseY);
                    //MainActivity.sendMouseButton(LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_LEFT, isDown);
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
                || GamepadDpad.isDpadEvent(event));
    }

}
