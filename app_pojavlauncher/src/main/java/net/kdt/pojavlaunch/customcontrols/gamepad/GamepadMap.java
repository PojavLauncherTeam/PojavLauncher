package net.kdt.pojavlaunch.customcontrols.gamepad;

import net.kdt.pojavlaunch.LWJGLGLFWKeycode;

public class GamepadMap {

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


    /*
     * Sets all buttons to a not pressed state, sending an input if needed
     */
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

    /*
     * Returns a pre-done mapping used when the mouse is grabbed by the game.
     */
    public static GamepadMap getDefaultGameMap(){
        GamepadMap gameMap = new GamepadMap();

        gameMap.BUTTON_A.keycodes = new int[]{LWJGLGLFWKeycode.GLFW_KEY_SPACE};
        gameMap.BUTTON_B.keycodes = new int[]{LWJGLGLFWKeycode.GLFW_KEY_Q};
        gameMap.BUTTON_X.keycodes = new int[]{LWJGLGLFWKeycode.GLFW_KEY_E};
        gameMap.BUTTON_Y.keycodes = new int[]{LWJGLGLFWKeycode.GLFW_KEY_F};

        gameMap.DIRECTION_FORWARD = new int[]{LWJGLGLFWKeycode.GLFW_KEY_W};
        gameMap.DIRECTION_BACKWARD = new int[]{LWJGLGLFWKeycode.GLFW_KEY_S};
        gameMap.DIRECTION_RIGHT = new int[]{LWJGLGLFWKeycode.GLFW_KEY_D};
        gameMap.DIRECTION_LEFT = new int[]{LWJGLGLFWKeycode.GLFW_KEY_A};

        gameMap.DPAD_UP.keycodes = new int[]{LWJGLGLFWKeycode.GLFW_KEY_LEFT_SHIFT};
        gameMap.DPAD_DOWN.keycodes = new int[]{LWJGLGLFWKeycode.GLFW_KEY_O};    //For mods ?
        gameMap.DPAD_RIGHT.keycodes = new int[]{LWJGLGLFWKeycode.GLFW_KEY_K};   //For mods ?
        gameMap.DPAD_LEFT.keycodes = new int[]{LWJGLGLFWKeycode.GLFW_KEY_J};    //For mods ?

        gameMap.SHOULDER_LEFT.keycodes = new int[]{GamepadMap.MOUSE_SCROLL_UP};
        gameMap.SHOULDER_RIGHT.keycodes = new int[]{GamepadMap.MOUSE_SCROLL_DOWN};

        gameMap.TRIGGER_LEFT.keycodes = new int[]{LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_RIGHT};
        gameMap.TRIGGER_RIGHT.keycodes = new int[]{LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_LEFT};

        gameMap.THUMBSTICK_LEFT.keycodes = new int[]{LWJGLGLFWKeycode.GLFW_KEY_LEFT_CONTROL};
        gameMap.THUMBSTICK_RIGHT.keycodes = new int[]{LWJGLGLFWKeycode.GLFW_KEY_LEFT_SHIFT};
        gameMap.THUMBSTICK_RIGHT.isToggleable = true;

        gameMap.BUTTON_START.keycodes = new int[]{LWJGLGLFWKeycode.GLFW_KEY_ESCAPE};
        gameMap.BUTTON_SELECT.keycodes = new int[]{LWJGLGLFWKeycode.GLFW_KEY_TAB};

        return gameMap;
    }

    /*
     * Returns a pre-done mapping used when the mouse is NOT grabbed by the game.
     */
    public static GamepadMap getDefaultMenuMap(){
        GamepadMap menuMap = new GamepadMap();

        menuMap.BUTTON_A.keycodes = new int[]{LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_LEFT};
        menuMap.BUTTON_B.keycodes = new int[]{LWJGLGLFWKeycode.GLFW_KEY_ESCAPE};
        menuMap.BUTTON_X.keycodes = new int[]{LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_RIGHT};
        menuMap.BUTTON_Y.keycodes = new int[]{LWJGLGLFWKeycode.GLFW_KEY_LEFT_SHIFT, LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_RIGHT}; //Oops, doesn't work since left shift isn't properly applied.

        menuMap.DIRECTION_FORWARD = new int[]{GamepadMap.MOUSE_SCROLL_UP, GamepadMap.MOUSE_SCROLL_UP, GamepadMap.MOUSE_SCROLL_UP, GamepadMap.MOUSE_SCROLL_UP, GamepadMap.MOUSE_SCROLL_UP};
        menuMap.DIRECTION_BACKWARD = new int[]{GamepadMap.MOUSE_SCROLL_DOWN, GamepadMap.MOUSE_SCROLL_DOWN, GamepadMap.MOUSE_SCROLL_DOWN, GamepadMap.MOUSE_SCROLL_DOWN, GamepadMap.MOUSE_SCROLL_DOWN};
        menuMap.DIRECTION_RIGHT = new int[]{};
        menuMap.DIRECTION_LEFT = new int[]{};

        menuMap.DPAD_UP.keycodes = new int[]{};
        menuMap.DPAD_DOWN.keycodes = new int[]{LWJGLGLFWKeycode.GLFW_KEY_O};    //For mods ?
        menuMap.DPAD_RIGHT.keycodes = new int[]{LWJGLGLFWKeycode.GLFW_KEY_K};   //For mods ?
        menuMap.DPAD_LEFT.keycodes = new int[]{LWJGLGLFWKeycode.GLFW_KEY_J};    //For mods ?

        menuMap.SHOULDER_LEFT.keycodes = new int[]{GamepadMap.MOUSE_SCROLL_UP};
        menuMap.SHOULDER_RIGHT.keycodes = new int[]{GamepadMap.MOUSE_SCROLL_DOWN};

        menuMap.TRIGGER_LEFT.keycodes = new int[]{};
        menuMap.TRIGGER_RIGHT.keycodes = new int[]{};

        menuMap.THUMBSTICK_LEFT.keycodes = new int[]{};
        menuMap.THUMBSTICK_RIGHT.keycodes = new int[]{};

        menuMap.BUTTON_START.keycodes = new int[]{LWJGLGLFWKeycode.GLFW_KEY_ESCAPE};
        menuMap.BUTTON_SELECT.keycodes = new int[]{};

        return menuMap;
    }

    /*
     * Returns all GamepadButtons, does not include directional keys
     */
    public GamepadButton[] getButtons(){
        return new GamepadButton[]{ BUTTON_A, BUTTON_B, BUTTON_X, BUTTON_Y,
                                    BUTTON_SELECT, BUTTON_START,
                                    TRIGGER_LEFT, TRIGGER_RIGHT,
                                    SHOULDER_LEFT, SHOULDER_RIGHT,
                                    THUMBSTICK_LEFT, THUMBSTICK_RIGHT,
                                    DPAD_UP, DPAD_RIGHT, DPAD_DOWN, DPAD_LEFT};
    }

    /*
     * Returns an pre-initialized GamepadMap with only empty keycodes
     */
    public static GamepadMap getEmptyMap(){
        GamepadMap emptyMap = new GamepadMap();
        for(GamepadButton button : emptyMap.getButtons())
            button.keycodes = new int[]{};

        emptyMap.DIRECTION_LEFT = new int[]{};
        emptyMap.DIRECTION_FORWARD = new int[]{};
        emptyMap.DIRECTION_RIGHT = new int[]{};
        emptyMap.DIRECTION_BACKWARD = new int[]{};

        return emptyMap;
    }

}
