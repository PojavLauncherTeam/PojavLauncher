package net.kdt.pojavlaunch;

import android.view.KeyEvent;

import net.kdt.pojavlaunch.prefs.LauncherPreferences;

import org.lwjgl.glfw.CallbackBridge;

import java.util.Arrays;

public class EfficientAndroidLWJGLKeycode {

    //This old version of this class was using an ArrayMap, a generic Key -> Value data structure.
    //The key being the android keycode from a KeyEvent
    //The value its LWJGL equivalent.
    private static final int KEYCODE_COUNT = 103;
    private static final int[] androidKeycodes = new int[KEYCODE_COUNT];
    private static final short[] LWJGLKeycodes = new short[KEYCODE_COUNT];
    private static String[] androidKeyNameArray; /* = new String[androidKeycodes.length]; */

    static {

        /*  BINARY SEARCH IS PERFORMED ON THE androidKeycodes ARRAY !
            WHEN ADDING A MAPPING, ADD IT SO THE androidKeycodes ARRAY STAYS SORTED ! */
        // Mapping Android Keycodes to LWJGL Keycodes
        add(KeyEvent.KEYCODE_UNKNOWN,LWJGLGLFWKeycode.GLFW_KEY_UNKNOWN);
        add(KeyEvent.KEYCODE_HOME, LWJGLGLFWKeycode.GLFW_KEY_HOME);
        // Escape key
        add(KeyEvent.KEYCODE_BACK, LWJGLGLFWKeycode.GLFW_KEY_ESCAPE);

        // 0-9 keys
        add(KeyEvent.KEYCODE_0, LWJGLGLFWKeycode.GLFW_KEY_0); //7
        add(KeyEvent.KEYCODE_1, LWJGLGLFWKeycode.GLFW_KEY_1);
        add(KeyEvent.KEYCODE_2, LWJGLGLFWKeycode.GLFW_KEY_2);
        add(KeyEvent.KEYCODE_3, LWJGLGLFWKeycode.GLFW_KEY_3);
        add(KeyEvent.KEYCODE_4, LWJGLGLFWKeycode.GLFW_KEY_4);
        add(KeyEvent.KEYCODE_5, LWJGLGLFWKeycode.GLFW_KEY_5);
        add(KeyEvent.KEYCODE_6, LWJGLGLFWKeycode.GLFW_KEY_6);
        add(KeyEvent.KEYCODE_7, LWJGLGLFWKeycode.GLFW_KEY_7);
        add(KeyEvent.KEYCODE_8, LWJGLGLFWKeycode.GLFW_KEY_8);
        add(KeyEvent.KEYCODE_9, LWJGLGLFWKeycode.GLFW_KEY_9); //16

        add(KeyEvent.KEYCODE_POUND,LWJGLGLFWKeycode.GLFW_KEY_3);

        // Arrow keys
        add(KeyEvent.KEYCODE_DPAD_UP, LWJGLGLFWKeycode.GLFW_KEY_UP); //19
        add(KeyEvent.KEYCODE_DPAD_DOWN, LWJGLGLFWKeycode.GLFW_KEY_DOWN);
        add(KeyEvent.KEYCODE_DPAD_LEFT, LWJGLGLFWKeycode.GLFW_KEY_LEFT);
        add(KeyEvent.KEYCODE_DPAD_RIGHT, LWJGLGLFWKeycode.GLFW_KEY_RIGHT); //22

        // A-Z keys
        add(KeyEvent.KEYCODE_A, LWJGLGLFWKeycode.GLFW_KEY_A); //29
        add(KeyEvent.KEYCODE_B, LWJGLGLFWKeycode.GLFW_KEY_B);
        add(KeyEvent.KEYCODE_C, LWJGLGLFWKeycode.GLFW_KEY_C);
        add(KeyEvent.KEYCODE_D, LWJGLGLFWKeycode.GLFW_KEY_D);
        add(KeyEvent.KEYCODE_E, LWJGLGLFWKeycode.GLFW_KEY_E);
        add(KeyEvent.KEYCODE_F, LWJGLGLFWKeycode.GLFW_KEY_F);
        add(KeyEvent.KEYCODE_G, LWJGLGLFWKeycode.GLFW_KEY_G);
        add(KeyEvent.KEYCODE_H, LWJGLGLFWKeycode.GLFW_KEY_H);
        add(KeyEvent.KEYCODE_I, LWJGLGLFWKeycode.GLFW_KEY_I);
        add(KeyEvent.KEYCODE_J, LWJGLGLFWKeycode.GLFW_KEY_J);
        add(KeyEvent.KEYCODE_K, LWJGLGLFWKeycode.GLFW_KEY_K);
        add(KeyEvent.KEYCODE_L, LWJGLGLFWKeycode.GLFW_KEY_L);
        add(KeyEvent.KEYCODE_M, LWJGLGLFWKeycode.GLFW_KEY_M);
        add(KeyEvent.KEYCODE_N, LWJGLGLFWKeycode.GLFW_KEY_N);
        add(KeyEvent.KEYCODE_O, LWJGLGLFWKeycode.GLFW_KEY_O);
        add(KeyEvent.KEYCODE_P, LWJGLGLFWKeycode.GLFW_KEY_P);
        add(KeyEvent.KEYCODE_Q, LWJGLGLFWKeycode.GLFW_KEY_Q);
        add(KeyEvent.KEYCODE_R, LWJGLGLFWKeycode.GLFW_KEY_R);
        add(KeyEvent.KEYCODE_S, LWJGLGLFWKeycode.GLFW_KEY_S);
        add(KeyEvent.KEYCODE_T, LWJGLGLFWKeycode.GLFW_KEY_T);
        add(KeyEvent.KEYCODE_U, LWJGLGLFWKeycode.GLFW_KEY_U);
        add(KeyEvent.KEYCODE_V, LWJGLGLFWKeycode.GLFW_KEY_V);
        add(KeyEvent.KEYCODE_W, LWJGLGLFWKeycode.GLFW_KEY_W);
        add(KeyEvent.KEYCODE_X, LWJGLGLFWKeycode.GLFW_KEY_X);
        add(KeyEvent.KEYCODE_Y, LWJGLGLFWKeycode.GLFW_KEY_Y);
        add(KeyEvent.KEYCODE_Z, LWJGLGLFWKeycode.GLFW_KEY_Z); //54


        add(KeyEvent.KEYCODE_COMMA, LWJGLGLFWKeycode.GLFW_KEY_COMMA);
        add(KeyEvent.KEYCODE_PERIOD, LWJGLGLFWKeycode.GLFW_KEY_PERIOD);

        // Alt keys
        add(KeyEvent.KEYCODE_ALT_LEFT, LWJGLGLFWKeycode.GLFW_KEY_LEFT_ALT);
        add(KeyEvent.KEYCODE_ALT_RIGHT, LWJGLGLFWKeycode.GLFW_KEY_RIGHT_ALT);

        // Shift keys
        add(KeyEvent.KEYCODE_SHIFT_LEFT, LWJGLGLFWKeycode.GLFW_KEY_LEFT_SHIFT);
        add(KeyEvent.KEYCODE_SHIFT_RIGHT, LWJGLGLFWKeycode.GLFW_KEY_RIGHT_SHIFT);

        add(KeyEvent.KEYCODE_TAB, LWJGLGLFWKeycode.GLFW_KEY_TAB);
        add(KeyEvent.KEYCODE_SPACE, LWJGLGLFWKeycode.GLFW_KEY_SPACE);
        add(KeyEvent.KEYCODE_ENTER, LWJGLGLFWKeycode.GLFW_KEY_ENTER); //66
        add(KeyEvent.KEYCODE_DEL, LWJGLGLFWKeycode.GLFW_KEY_BACKSPACE); // Backspace
        add(KeyEvent.KEYCODE_GRAVE, LWJGLGLFWKeycode.GLFW_KEY_GRAVE_ACCENT);
        add(KeyEvent.KEYCODE_MINUS, LWJGLGLFWKeycode.GLFW_KEY_MINUS);
        add(KeyEvent.KEYCODE_EQUALS, LWJGLGLFWKeycode.GLFW_KEY_EQUAL);
        add(KeyEvent.KEYCODE_LEFT_BRACKET, LWJGLGLFWKeycode.GLFW_KEY_LEFT_BRACKET);
        add(KeyEvent.KEYCODE_RIGHT_BRACKET, LWJGLGLFWKeycode.GLFW_KEY_RIGHT_BRACKET);
        add(KeyEvent.KEYCODE_BACKSLASH, LWJGLGLFWKeycode.GLFW_KEY_BACKSLASH);
        add(KeyEvent.KEYCODE_SEMICOLON, LWJGLGLFWKeycode.GLFW_KEY_SEMICOLON); //74

        add(KeyEvent.KEYCODE_SLASH, LWJGLGLFWKeycode.GLFW_KEY_SLASH); //76
        add(KeyEvent.KEYCODE_AT,LWJGLGLFWKeycode.GLFW_KEY_2);

        add(KeyEvent.KEYCODE_PLUS, LWJGLGLFWKeycode.GLFW_KEY_KP_ADD);

        // Page keys
        add(KeyEvent.KEYCODE_PAGE_UP, LWJGLGLFWKeycode.GLFW_KEY_PAGE_UP); //92
        add(KeyEvent.KEYCODE_PAGE_DOWN, LWJGLGLFWKeycode.GLFW_KEY_PAGE_DOWN);

        add(KeyEvent.KEYCODE_ESCAPE, LWJGLGLFWKeycode.GLFW_KEY_ESCAPE);

        // Control keys
        add(KeyEvent.KEYCODE_CTRL_LEFT, LWJGLGLFWKeycode.GLFW_KEY_LEFT_CONTROL);
        add(KeyEvent.KEYCODE_CTRL_RIGHT, LWJGLGLFWKeycode.GLFW_KEY_RIGHT_CONTROL);

        add(KeyEvent.KEYCODE_CAPS_LOCK, LWJGLGLFWKeycode.GLFW_KEY_CAPS_LOCK);
        add(KeyEvent.KEYCODE_BREAK, LWJGLGLFWKeycode.GLFW_KEY_PAUSE);
        add(KeyEvent.KEYCODE_INSERT, LWJGLGLFWKeycode.GLFW_KEY_INSERT);

        // Fn keys
        add(KeyEvent.KEYCODE_F1, LWJGLGLFWKeycode.GLFW_KEY_F1); //131
        add(KeyEvent.KEYCODE_F2, LWJGLGLFWKeycode.GLFW_KEY_F2);
        add(KeyEvent.KEYCODE_F3, LWJGLGLFWKeycode.GLFW_KEY_F3);
        add(KeyEvent.KEYCODE_F4, LWJGLGLFWKeycode.GLFW_KEY_F4);
        add(KeyEvent.KEYCODE_F5, LWJGLGLFWKeycode.GLFW_KEY_F5);
        add(KeyEvent.KEYCODE_F6, LWJGLGLFWKeycode.GLFW_KEY_F6);
        add(KeyEvent.KEYCODE_F7, LWJGLGLFWKeycode.GLFW_KEY_F7);
        add(KeyEvent.KEYCODE_F8, LWJGLGLFWKeycode.GLFW_KEY_F8);
        add(KeyEvent.KEYCODE_F9, LWJGLGLFWKeycode.GLFW_KEY_F9);
        add(KeyEvent.KEYCODE_F10, LWJGLGLFWKeycode.GLFW_KEY_F10);
        add(KeyEvent.KEYCODE_F11, LWJGLGLFWKeycode.GLFW_KEY_F11);
        add(KeyEvent.KEYCODE_F12, LWJGLGLFWKeycode.GLFW_KEY_F12); //142

        // Num keys
        add(KeyEvent.KEYCODE_NUM_LOCK, LWJGLGLFWKeycode.GLFW_KEY_NUM_LOCK); //143
        add(KeyEvent.KEYCODE_NUMPAD_0, LWJGLGLFWKeycode.GLFW_KEY_0);
        add(KeyEvent.KEYCODE_NUMPAD_1, LWJGLGLFWKeycode.GLFW_KEY_1);
        add(KeyEvent.KEYCODE_NUMPAD_2, LWJGLGLFWKeycode.GLFW_KEY_2);
        add(KeyEvent.KEYCODE_NUMPAD_3, LWJGLGLFWKeycode.GLFW_KEY_3);
        add(KeyEvent.KEYCODE_NUMPAD_4, LWJGLGLFWKeycode.GLFW_KEY_4);
        add(KeyEvent.KEYCODE_NUMPAD_5, LWJGLGLFWKeycode.GLFW_KEY_5);
        add(KeyEvent.KEYCODE_NUMPAD_6, LWJGLGLFWKeycode.GLFW_KEY_6);
        add(KeyEvent.KEYCODE_NUMPAD_7, LWJGLGLFWKeycode.GLFW_KEY_7);
        add(KeyEvent.KEYCODE_NUMPAD_8, LWJGLGLFWKeycode.GLFW_KEY_8);
        add(KeyEvent.KEYCODE_NUMPAD_9, LWJGLGLFWKeycode.GLFW_KEY_9);
        add(KeyEvent.KEYCODE_NUMPAD_DIVIDE, LWJGLGLFWKeycode.GLFW_KEY_KP_DIVIDE);
        add(KeyEvent.KEYCODE_NUMPAD_MULTIPLY, LWJGLGLFWKeycode.GLFW_KEY_KP_MULTIPLY);
        add(KeyEvent.KEYCODE_NUMPAD_SUBTRACT, LWJGLGLFWKeycode.GLFW_KEY_KP_SUBTRACT);
        add(KeyEvent.KEYCODE_NUMPAD_ADD, LWJGLGLFWKeycode.GLFW_KEY_KP_ADD);
        add(KeyEvent.KEYCODE_NUMPAD_DOT, LWJGLGLFWKeycode.GLFW_KEY_PERIOD);
        add(KeyEvent.KEYCODE_NUMPAD_COMMA, LWJGLGLFWKeycode.GLFW_KEY_COMMA);
        add(KeyEvent.KEYCODE_NUMPAD_ENTER, LWJGLGLFWKeycode.GLFW_KEY_ENTER);
        add(KeyEvent.KEYCODE_NUMPAD_EQUALS, LWJGLGLFWKeycode.GLFW_KEY_EQUAL); //161

    }

    private static short index = 0;

    private static void add(int androidKeycode, short LWJGLKeycode){
        androidKeycodes[index] = androidKeycode;
        LWJGLKeycodes[index] = LWJGLKeycode;
        ++index;
    }


    public static boolean containsKey(int keycode){
        return getIndexByKey(keycode) >= 0;
    }



    public static String[] generateKeyName() {
        if (androidKeyNameArray == null) {
            androidKeyNameArray = new String[androidKeycodes.length];
            for(int i=0; i < androidKeyNameArray.length; ++i){
                androidKeyNameArray[i] = KeyEvent.keyCodeToString(androidKeycodes[i]).replace("KEYCODE_", "");
            }
        }
        return androidKeyNameArray;
    }
    
    public static void execKey(KeyEvent keyEvent) {
        execKey(keyEvent, getIndexByKey(keyEvent.getKeyCode()));
    }


    public static void execKey(KeyEvent keyEvent, int valueIndex) {
        //valueIndex points to where the value is stored in the array.
        CallbackBridge.holdingAlt = keyEvent.isAltPressed();
        CallbackBridge.holdingCapslock = keyEvent.isCapsLockOn();
        CallbackBridge.holdingCtrl = keyEvent.isCtrlPressed();
        CallbackBridge.holdingNumlock = keyEvent.isNumLockOn();
        CallbackBridge.holdingShift = keyEvent.isShiftPressed();

        try {
            System.out.println(keyEvent.getKeyCode() + " " +keyEvent.getDisplayLabel());
            char key = (char)(keyEvent.getUnicodeChar() != 0 ? keyEvent.getUnicodeChar() : '\u0000');
            BaseMainActivity.sendKeyPress(
                    getValueByIndex(valueIndex),
                    key,
                    0,
                    CallbackBridge.getCurrentMods(),
                    keyEvent.getAction() == KeyEvent.ACTION_DOWN);

        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    public static void execKeyIndex(int index){
        //Send a quick key press.
        BaseMainActivity.sendKeyPress(getValueByIndex(index));
    }
    
    public static int getValueByIndex(int index) {
        return LWJGLKeycodes[index];
    }

    public static int getIndexByKey(int key){
        return Arrays.binarySearch(androidKeycodes, key);
    }

    public static short getValue(int key){
        return LWJGLKeycodes[Arrays.binarySearch(androidKeycodes, key)];
    }

    public static int getIndexByValue(int lwjglKey) {
        //Since the LWJGL keycodes aren't sorted, linear search is used.
        //You should avoid using this function on performance critical areas
        for (int i = 0; i < LWJGLKeycodes.length; i++) {
            if(LWJGLKeycodes[i] == lwjglKey) return i;
        }
        
        return 0;
    }
}
