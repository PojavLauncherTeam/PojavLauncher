package net.kdt.pojavlaunch;

import android.util.*;
import android.view.*;

import java.net.CookieHandler;
import java.util.*;

import net.kdt.pojavlaunch.prefs.LauncherPreferences;
import org.lwjgl.glfw.*;

public class AndroidLWJGLKeycode {
    // Fix double letters on MC 1.9 and above
    public static boolean isBackspaceAfterChar = true;
    public static final ArrayMap<Short, Short> androidToLwjglMap;
    public static String[] androidKeyNameArray;

    static {
        // Mapping Android Keycodes to LWJGL Keycodes
        androidToLwjglMap = new ArrayMap<>();

        // 0-9 keys
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_0, LWJGLGLFWKeycode.GLFW_KEY_0);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_1, LWJGLGLFWKeycode.GLFW_KEY_1);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_2, LWJGLGLFWKeycode.GLFW_KEY_2);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_3, LWJGLGLFWKeycode.GLFW_KEY_3);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_4, LWJGLGLFWKeycode.GLFW_KEY_4);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_5, LWJGLGLFWKeycode.GLFW_KEY_5);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_6, LWJGLGLFWKeycode.GLFW_KEY_6);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_7, LWJGLGLFWKeycode.GLFW_KEY_7);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_8, LWJGLGLFWKeycode.GLFW_KEY_8);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_9, LWJGLGLFWKeycode.GLFW_KEY_9);
        
        // A-Z keys
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_A, LWJGLGLFWKeycode.GLFW_KEY_A);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_B, LWJGLGLFWKeycode.GLFW_KEY_B);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_C, LWJGLGLFWKeycode.GLFW_KEY_C);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_D, LWJGLGLFWKeycode.GLFW_KEY_D);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_E, LWJGLGLFWKeycode.GLFW_KEY_E);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_F, LWJGLGLFWKeycode.GLFW_KEY_F);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_G, LWJGLGLFWKeycode.GLFW_KEY_G);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_H, LWJGLGLFWKeycode.GLFW_KEY_H);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_I, LWJGLGLFWKeycode.GLFW_KEY_I);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_J, LWJGLGLFWKeycode.GLFW_KEY_J);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_K, LWJGLGLFWKeycode.GLFW_KEY_K);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_L, LWJGLGLFWKeycode.GLFW_KEY_L);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_M, LWJGLGLFWKeycode.GLFW_KEY_M);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_N, LWJGLGLFWKeycode.GLFW_KEY_N);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_O, LWJGLGLFWKeycode.GLFW_KEY_O);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_P, LWJGLGLFWKeycode.GLFW_KEY_P);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_Q, LWJGLGLFWKeycode.GLFW_KEY_Q);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_R, LWJGLGLFWKeycode.GLFW_KEY_R);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_S, LWJGLGLFWKeycode.GLFW_KEY_S);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_T, LWJGLGLFWKeycode.GLFW_KEY_T);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_U, LWJGLGLFWKeycode.GLFW_KEY_U);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_V, LWJGLGLFWKeycode.GLFW_KEY_V);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_W, LWJGLGLFWKeycode.GLFW_KEY_W);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_X, LWJGLGLFWKeycode.GLFW_KEY_X);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_Y, LWJGLGLFWKeycode.GLFW_KEY_Y);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_Z, LWJGLGLFWKeycode.GLFW_KEY_Z);
        
        // Alt keys
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_ALT_LEFT, LWJGLGLFWKeycode.GLFW_KEY_LEFT_ALT);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_ALT_RIGHT, LWJGLGLFWKeycode.GLFW_KEY_RIGHT_ALT);
        
        // Escape key
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_BACK, LWJGLGLFWKeycode.GLFW_KEY_ESCAPE);
        
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_BACKSLASH, LWJGLGLFWKeycode.GLFW_KEY_BACKSLASH);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_BREAK, LWJGLGLFWKeycode.GLFW_KEY_PAUSE);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_CAPS_LOCK, LWJGLGLFWKeycode.GLFW_KEY_CAPS_LOCK);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_COMMA, LWJGLGLFWKeycode.GLFW_KEY_COMMA);
        
        // Control keys
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_CTRL_LEFT, LWJGLGLFWKeycode.GLFW_KEY_LEFT_CONTROL);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_CTRL_RIGHT, LWJGLGLFWKeycode.GLFW_KEY_RIGHT_CONTROL);
        
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_DEL, LWJGLGLFWKeycode.GLFW_KEY_BACKSPACE); // Backspace
        
        // Arrow keys
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_DPAD_DOWN, LWJGLGLFWKeycode.GLFW_KEY_DOWN);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_DPAD_LEFT, LWJGLGLFWKeycode.GLFW_KEY_LEFT);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_DPAD_RIGHT, LWJGLGLFWKeycode.GLFW_KEY_RIGHT);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_DPAD_UP, LWJGLGLFWKeycode.GLFW_KEY_UP);
        
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_ENTER, LWJGLGLFWKeycode.GLFW_KEY_ENTER);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_EQUALS, LWJGLGLFWKeycode.GLFW_KEY_EQUAL);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_ESCAPE, LWJGLGLFWKeycode.GLFW_KEY_ESCAPE);
        
        // Fn keys
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_F1, LWJGLGLFWKeycode.GLFW_KEY_F1);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_F2, LWJGLGLFWKeycode.GLFW_KEY_F2);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_F3, LWJGLGLFWKeycode.GLFW_KEY_F3);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_F4, LWJGLGLFWKeycode.GLFW_KEY_F4);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_F5, LWJGLGLFWKeycode.GLFW_KEY_F5);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_F6, LWJGLGLFWKeycode.GLFW_KEY_F6);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_F7, LWJGLGLFWKeycode.GLFW_KEY_F7);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_F8, LWJGLGLFWKeycode.GLFW_KEY_F8);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_F9, LWJGLGLFWKeycode.GLFW_KEY_F9);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_F10, LWJGLGLFWKeycode.GLFW_KEY_F10);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_F11, LWJGLGLFWKeycode.GLFW_KEY_F11);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_F12, LWJGLGLFWKeycode.GLFW_KEY_F12);
        // FIXME GLFW Function key
        // androidToLwjglMap.put((short)KeyEvent.KEYCODE_FUNCTION, LWJGLGLFWKeycode.GLFW_KEY_FUNCTION);
        
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_GRAVE, LWJGLGLFWKeycode.GLFW_KEY_GRAVE_ACCENT);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_HOME, LWJGLGLFWKeycode.GLFW_KEY_HOME);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_INSERT, LWJGLGLFWKeycode.GLFW_KEY_INSERT);
        //androidToLwjglMap.put((short)KeyEvent.KEYCODE_KANA, LWJGLGLFWKeycode.GLFW_KEY_K);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_LEFT_BRACKET, LWJGLGLFWKeycode.GLFW_KEY_LEFT_BRACKET);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_MINUS, LWJGLGLFWKeycode.GLFW_KEY_MINUS);
        
        // Num keys
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_NUM_LOCK, LWJGLGLFWKeycode.GLFW_KEY_NUM_LOCK);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_NUMPAD_0, LWJGLGLFWKeycode.GLFW_KEY_0);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_NUMPAD_1, LWJGLGLFWKeycode.GLFW_KEY_1);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_NUMPAD_2, LWJGLGLFWKeycode.GLFW_KEY_2);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_NUMPAD_3, LWJGLGLFWKeycode.GLFW_KEY_3);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_NUMPAD_4, LWJGLGLFWKeycode.GLFW_KEY_4);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_NUMPAD_5, LWJGLGLFWKeycode.GLFW_KEY_5);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_NUMPAD_6, LWJGLGLFWKeycode.GLFW_KEY_6);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_NUMPAD_7, LWJGLGLFWKeycode.GLFW_KEY_7);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_NUMPAD_8, LWJGLGLFWKeycode.GLFW_KEY_8);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_NUMPAD_9, LWJGLGLFWKeycode.GLFW_KEY_9);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_NUMPAD_ADD, LWJGLGLFWKeycode.GLFW_KEY_KP_ADD);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_NUMPAD_COMMA, LWJGLGLFWKeycode.GLFW_KEY_COMMA);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_NUMPAD_DIVIDE, LWJGLGLFWKeycode.GLFW_KEY_KP_DIVIDE);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_NUMPAD_DOT, LWJGLGLFWKeycode.GLFW_KEY_PERIOD);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_NUMPAD_ENTER, LWJGLGLFWKeycode.GLFW_KEY_ENTER);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_NUMPAD_EQUALS, LWJGLGLFWKeycode.GLFW_KEY_EQUAL);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_NUMPAD_MULTIPLY, LWJGLGLFWKeycode.GLFW_KEY_KP_MULTIPLY);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_NUMPAD_SUBTRACT, LWJGLGLFWKeycode.GLFW_KEY_KP_SUBTRACT);
        
        // Page keys
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_PAGE_DOWN, LWJGLGLFWKeycode.GLFW_KEY_PAGE_DOWN);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_PAGE_UP, LWJGLGLFWKeycode.GLFW_KEY_PAGE_UP);
        
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_PERIOD, LWJGLGLFWKeycode.GLFW_KEY_PERIOD);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_PLUS, LWJGLGLFWKeycode.GLFW_KEY_KP_ADD);
        // androidToLwjglMap.put((short)KeyEvent.KEYCODE_POWER, LWJGLGLFWKeycode.GLFW_KEY_POWER);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_RIGHT_BRACKET, LWJGLGLFWKeycode.GLFW_KEY_RIGHT_BRACKET);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_SEMICOLON, LWJGLGLFWKeycode.GLFW_KEY_SEMICOLON);
        
        // Shift keys
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_SHIFT_LEFT, LWJGLGLFWKeycode.GLFW_KEY_LEFT_SHIFT);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_SHIFT_RIGHT, LWJGLGLFWKeycode.GLFW_KEY_RIGHT_SHIFT);
        
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_SLASH, LWJGLGLFWKeycode.GLFW_KEY_SLASH);
        // androidToLwjglMap.put((short)KeyEvent.KEYCODE_SLEEP, LWJGLGLFWKeycode.GLFW_KEY_SLEEP);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_SPACE, LWJGLGLFWKeycode.GLFW_KEY_SPACE);
        // androidToLwjglMap.put((short)KeyEvent.KEYCODE_SYSRQ, LWJGLGLFWKeycode.GLFW_KEY_SYSRQ);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_TAB, LWJGLGLFWKeycode.GLFW_KEY_TAB);
        // androidToLwjglMap.put((short)KeyEvent.KEYCODE_YEN, LWJGLGLFWKeycode.GLFW_KEY_YEN);
        
        // androidToLwjglMap.put((short)KeyEvent.KEYCODE_BUTTON_1, LWJGLGLFWKeycode.G
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_AT,LWJGLGLFWKeycode.GLFW_KEY_2);
        androidToLwjglMap.put((short)KeyEvent.KEYCODE_POUND,LWJGLGLFWKeycode.GLFW_KEY_3);

        androidToLwjglMap.put((short)KeyEvent.KEYCODE_UNKNOWN,LWJGLGLFWKeycode.GLFW_KEY_UNKNOWN);

    }
    
    public static String[] generateKeyName() {
        if (androidKeyNameArray == null) {
            List<String> keyName = new ArrayList<String>();
            for (int perKey : androidToLwjglMap.keySet()) {
                keyName.add(KeyEvent.keyCodeToString(perKey).replace("KEYCODE_", ""));
            }
            androidKeyNameArray = keyName.toArray(new String[0]);
        }
        return androidKeyNameArray;
    }
    
    public static void execKey(KeyEvent keyEvent, int i, boolean isDown) {
        CallbackBridge.holdingAlt = keyEvent.isAltPressed();
        CallbackBridge.holdingCapslock = keyEvent.isCapsLockOn();
        CallbackBridge.holdingCtrl = keyEvent.isCtrlPressed();
        CallbackBridge.holdingNumlock = keyEvent.isNumLockOn();
        CallbackBridge.holdingShift = keyEvent.isShiftPressed();

        try {
                System.out.println(keyEvent.getKeyCode() + " " +keyEvent.getDisplayLabel());
            if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK && LauncherPreferences.PREF_BACK_TO_RIGHT_MOUSE) {
                BaseMainActivity.sendMouseButton(LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_RIGHT, keyEvent.getAction() == KeyEvent.ACTION_DOWN);
            } else {
                if(keyEvent.getUnicodeChar() != 0) {
                    char key = (char)keyEvent.getUnicodeChar();
                     BaseMainActivity.sendKeyPress(
                             androidToLwjglMap.get(keyEvent.getKeyCode()),
                             key,
                             0,
                             CallbackBridge.getCurrentMods(),
                             keyEvent.getAction() == KeyEvent.ACTION_DOWN);
                }else{
                     BaseMainActivity.sendKeyPress(
                             androidToLwjglMap.get(keyEvent.getKeyCode()),
                             CallbackBridge.getCurrentMods(),
                             keyEvent.getAction()==KeyEvent.ACTION_DOWN);
                }
            }
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    public static void execKeyIndex(BaseMainActivity mainActivity, int index) {
        mainActivity.sendKeyPress(getKeyByIndex(index));

    }
    
    public static int getKeyByIndex(int index) {
        return androidToLwjglMap.valueAt(index);
    }

    public static int getIndexByLWJGLKey(int lwjglKey) {
        for (int i = 0; i < androidToLwjglMap.size(); i++) {
            int currKey = androidToLwjglMap.valueAt(i);
            if (currKey == lwjglKey) {
                return i;
            }
        }
        
        return 0;
    }
}
