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
    public static final ArrayMap<Integer, Integer> androidToLwjglMap;
    public static String[] androidKeyNameArray;

    static {
        // Mapping Android Keycodes to LWJGL Keycodes
        androidToLwjglMap = new ArrayMap<Integer, Integer>();
        
        // 0-9 keys
        androidToLwjglMap.put(KeyEvent.KEYCODE_0, LWJGLGLFWKeycode.GLFW_KEY_0);
        androidToLwjglMap.put(KeyEvent.KEYCODE_1, LWJGLGLFWKeycode.GLFW_KEY_1);
        androidToLwjglMap.put(KeyEvent.KEYCODE_2, LWJGLGLFWKeycode.GLFW_KEY_2);
        androidToLwjglMap.put(KeyEvent.KEYCODE_3, LWJGLGLFWKeycode.GLFW_KEY_3);
        androidToLwjglMap.put(KeyEvent.KEYCODE_4, LWJGLGLFWKeycode.GLFW_KEY_4);
        androidToLwjglMap.put(KeyEvent.KEYCODE_5, LWJGLGLFWKeycode.GLFW_KEY_5);
        androidToLwjglMap.put(KeyEvent.KEYCODE_6, LWJGLGLFWKeycode.GLFW_KEY_6);
        androidToLwjglMap.put(KeyEvent.KEYCODE_7, LWJGLGLFWKeycode.GLFW_KEY_7);
        androidToLwjglMap.put(KeyEvent.KEYCODE_8, LWJGLGLFWKeycode.GLFW_KEY_8);
        androidToLwjglMap.put(KeyEvent.KEYCODE_9, LWJGLGLFWKeycode.GLFW_KEY_9);
        
        // A-Z keys
        androidToLwjglMap.put(KeyEvent.KEYCODE_A, LWJGLGLFWKeycode.GLFW_KEY_A);
        androidToLwjglMap.put(KeyEvent.KEYCODE_B, LWJGLGLFWKeycode.GLFW_KEY_B);
        androidToLwjglMap.put(KeyEvent.KEYCODE_C, LWJGLGLFWKeycode.GLFW_KEY_C);
        androidToLwjglMap.put(KeyEvent.KEYCODE_D, LWJGLGLFWKeycode.GLFW_KEY_D);
        androidToLwjglMap.put(KeyEvent.KEYCODE_E, LWJGLGLFWKeycode.GLFW_KEY_E);
        androidToLwjglMap.put(KeyEvent.KEYCODE_F, LWJGLGLFWKeycode.GLFW_KEY_F);
        androidToLwjglMap.put(KeyEvent.KEYCODE_G, LWJGLGLFWKeycode.GLFW_KEY_G);
        androidToLwjglMap.put(KeyEvent.KEYCODE_H, LWJGLGLFWKeycode.GLFW_KEY_H);
        androidToLwjglMap.put(KeyEvent.KEYCODE_I, LWJGLGLFWKeycode.GLFW_KEY_I);
        androidToLwjglMap.put(KeyEvent.KEYCODE_J, LWJGLGLFWKeycode.GLFW_KEY_J);
        androidToLwjglMap.put(KeyEvent.KEYCODE_K, LWJGLGLFWKeycode.GLFW_KEY_K);
        androidToLwjglMap.put(KeyEvent.KEYCODE_L, LWJGLGLFWKeycode.GLFW_KEY_L);
        androidToLwjglMap.put(KeyEvent.KEYCODE_M, LWJGLGLFWKeycode.GLFW_KEY_M);
        androidToLwjglMap.put(KeyEvent.KEYCODE_N, LWJGLGLFWKeycode.GLFW_KEY_N);
        androidToLwjglMap.put(KeyEvent.KEYCODE_O, LWJGLGLFWKeycode.GLFW_KEY_O);
        androidToLwjglMap.put(KeyEvent.KEYCODE_P, LWJGLGLFWKeycode.GLFW_KEY_P);
        androidToLwjglMap.put(KeyEvent.KEYCODE_Q, LWJGLGLFWKeycode.GLFW_KEY_Q);
        androidToLwjglMap.put(KeyEvent.KEYCODE_R, LWJGLGLFWKeycode.GLFW_KEY_R);
        androidToLwjglMap.put(KeyEvent.KEYCODE_S, LWJGLGLFWKeycode.GLFW_KEY_S);
        androidToLwjglMap.put(KeyEvent.KEYCODE_T, LWJGLGLFWKeycode.GLFW_KEY_T);
        androidToLwjglMap.put(KeyEvent.KEYCODE_U, LWJGLGLFWKeycode.GLFW_KEY_U);
        androidToLwjglMap.put(KeyEvent.KEYCODE_V, LWJGLGLFWKeycode.GLFW_KEY_V);
        androidToLwjglMap.put(KeyEvent.KEYCODE_W, LWJGLGLFWKeycode.GLFW_KEY_W);
        androidToLwjglMap.put(KeyEvent.KEYCODE_X, LWJGLGLFWKeycode.GLFW_KEY_X);
        androidToLwjglMap.put(KeyEvent.KEYCODE_Y, LWJGLGLFWKeycode.GLFW_KEY_Y);
        androidToLwjglMap.put(KeyEvent.KEYCODE_Z, LWJGLGLFWKeycode.GLFW_KEY_Z);
        
        // Alt keys
        androidToLwjglMap.put(KeyEvent.KEYCODE_ALT_LEFT, LWJGLGLFWKeycode.GLFW_KEY_LEFT_ALT);
        androidToLwjglMap.put(KeyEvent.KEYCODE_ALT_RIGHT, LWJGLGLFWKeycode.GLFW_KEY_RIGHT_ALT);
        
        // Escape key
        androidToLwjglMap.put(KeyEvent.KEYCODE_BACK, LWJGLGLFWKeycode.GLFW_KEY_ESCAPE);
        
        androidToLwjglMap.put(KeyEvent.KEYCODE_BACKSLASH, LWJGLGLFWKeycode.GLFW_KEY_BACKSLASH);
        androidToLwjglMap.put(KeyEvent.KEYCODE_BREAK, LWJGLGLFWKeycode.GLFW_KEY_PAUSE);
        androidToLwjglMap.put(KeyEvent.KEYCODE_CAPS_LOCK, LWJGLGLFWKeycode.GLFW_KEY_CAPS_LOCK);
        androidToLwjglMap.put(KeyEvent.KEYCODE_COMMA, LWJGLGLFWKeycode.GLFW_KEY_COMMA);
        
        // Control keys
        androidToLwjglMap.put(KeyEvent.KEYCODE_CTRL_LEFT, LWJGLGLFWKeycode.GLFW_KEY_LEFT_CONTROL);
        androidToLwjglMap.put(KeyEvent.KEYCODE_CTRL_RIGHT, LWJGLGLFWKeycode.GLFW_KEY_RIGHT_CONTROL);
        
        androidToLwjglMap.put(KeyEvent.KEYCODE_DEL, LWJGLGLFWKeycode.GLFW_KEY_BACKSPACE); // Backspace
        
        // Arrow keys
        androidToLwjglMap.put(KeyEvent.KEYCODE_DPAD_DOWN, LWJGLGLFWKeycode.GLFW_KEY_DOWN);
        androidToLwjglMap.put(KeyEvent.KEYCODE_DPAD_LEFT, LWJGLGLFWKeycode.GLFW_KEY_LEFT);
        androidToLwjglMap.put(KeyEvent.KEYCODE_DPAD_RIGHT, LWJGLGLFWKeycode.GLFW_KEY_RIGHT);
        androidToLwjglMap.put(KeyEvent.KEYCODE_DPAD_UP, LWJGLGLFWKeycode.GLFW_KEY_UP);
        
        androidToLwjglMap.put(KeyEvent.KEYCODE_ENTER, LWJGLGLFWKeycode.GLFW_KEY_ENTER);
        androidToLwjglMap.put(KeyEvent.KEYCODE_EQUALS, LWJGLGLFWKeycode.GLFW_KEY_EQUAL);
        androidToLwjglMap.put(KeyEvent.KEYCODE_ESCAPE, LWJGLGLFWKeycode.GLFW_KEY_ESCAPE);
        
        // Fn keys
        androidToLwjglMap.put(KeyEvent.KEYCODE_F1, LWJGLGLFWKeycode.GLFW_KEY_F1);
        androidToLwjglMap.put(KeyEvent.KEYCODE_F2, LWJGLGLFWKeycode.GLFW_KEY_F2);
        androidToLwjglMap.put(KeyEvent.KEYCODE_F3, LWJGLGLFWKeycode.GLFW_KEY_F3);
        androidToLwjglMap.put(KeyEvent.KEYCODE_F4, LWJGLGLFWKeycode.GLFW_KEY_F4);
        androidToLwjglMap.put(KeyEvent.KEYCODE_F5, LWJGLGLFWKeycode.GLFW_KEY_F5);
        androidToLwjglMap.put(KeyEvent.KEYCODE_F6, LWJGLGLFWKeycode.GLFW_KEY_F6);
        androidToLwjglMap.put(KeyEvent.KEYCODE_F7, LWJGLGLFWKeycode.GLFW_KEY_F7);
        androidToLwjglMap.put(KeyEvent.KEYCODE_F8, LWJGLGLFWKeycode.GLFW_KEY_F8);
        androidToLwjglMap.put(KeyEvent.KEYCODE_F9, LWJGLGLFWKeycode.GLFW_KEY_F9);
        androidToLwjglMap.put(KeyEvent.KEYCODE_F10, LWJGLGLFWKeycode.GLFW_KEY_F10);
        androidToLwjglMap.put(KeyEvent.KEYCODE_F11, LWJGLGLFWKeycode.GLFW_KEY_F11);
        androidToLwjglMap.put(KeyEvent.KEYCODE_F12, LWJGLGLFWKeycode.GLFW_KEY_F12);
        // FIXME GLFW Function key
        // androidToLwjglMap.put(KeyEvent.KEYCODE_FUNCTION, LWJGLGLFWKeycode.GLFW_KEY_FUNCTION);
        
        androidToLwjglMap.put(KeyEvent.KEYCODE_GRAVE, LWJGLGLFWKeycode.GLFW_KEY_GRAVE_ACCENT);
        androidToLwjglMap.put(KeyEvent.KEYCODE_HOME, LWJGLGLFWKeycode.GLFW_KEY_HOME);
        androidToLwjglMap.put(KeyEvent.KEYCODE_INSERT, LWJGLGLFWKeycode.GLFW_KEY_INSERT);
        androidToLwjglMap.put(KeyEvent.KEYCODE_KANA, LWJGLGLFWKeycode.GLFW_KEY_K);
        androidToLwjglMap.put(KeyEvent.KEYCODE_LEFT_BRACKET, LWJGLGLFWKeycode.GLFW_KEY_LEFT_BRACKET);
        androidToLwjglMap.put(KeyEvent.KEYCODE_MINUS, LWJGLGLFWKeycode.GLFW_KEY_MINUS);
        
        // Num keys
        androidToLwjglMap.put(KeyEvent.KEYCODE_NUM_LOCK, LWJGLGLFWKeycode.GLFW_KEY_NUM_LOCK);
        androidToLwjglMap.put(KeyEvent.KEYCODE_NUMPAD_0, LWJGLGLFWKeycode.GLFW_KEY_0);
        androidToLwjglMap.put(KeyEvent.KEYCODE_NUMPAD_1, LWJGLGLFWKeycode.GLFW_KEY_1);
        androidToLwjglMap.put(KeyEvent.KEYCODE_NUMPAD_2, LWJGLGLFWKeycode.GLFW_KEY_2);
        androidToLwjglMap.put(KeyEvent.KEYCODE_NUMPAD_3, LWJGLGLFWKeycode.GLFW_KEY_3);
        androidToLwjglMap.put(KeyEvent.KEYCODE_NUMPAD_4, LWJGLGLFWKeycode.GLFW_KEY_4);
        androidToLwjglMap.put(KeyEvent.KEYCODE_NUMPAD_5, LWJGLGLFWKeycode.GLFW_KEY_5);
        androidToLwjglMap.put(KeyEvent.KEYCODE_NUMPAD_6, LWJGLGLFWKeycode.GLFW_KEY_6);
        androidToLwjglMap.put(KeyEvent.KEYCODE_NUMPAD_7, LWJGLGLFWKeycode.GLFW_KEY_7);
        androidToLwjglMap.put(KeyEvent.KEYCODE_NUMPAD_8, LWJGLGLFWKeycode.GLFW_KEY_8);
        androidToLwjglMap.put(KeyEvent.KEYCODE_NUMPAD_9, LWJGLGLFWKeycode.GLFW_KEY_9);
        androidToLwjglMap.put(KeyEvent.KEYCODE_NUMPAD_ADD, LWJGLGLFWKeycode.GLFW_KEY_KP_ADD);
        androidToLwjglMap.put(KeyEvent.KEYCODE_NUMPAD_COMMA, LWJGLGLFWKeycode.GLFW_KEY_COMMA);
        androidToLwjglMap.put(KeyEvent.KEYCODE_NUMPAD_DIVIDE, LWJGLGLFWKeycode.GLFW_KEY_KP_DIVIDE);
        androidToLwjglMap.put(KeyEvent.KEYCODE_NUMPAD_DOT, LWJGLGLFWKeycode.GLFW_KEY_PERIOD);
        androidToLwjglMap.put(KeyEvent.KEYCODE_NUMPAD_ENTER, LWJGLGLFWKeycode.GLFW_KEY_ENTER);
        androidToLwjglMap.put(KeyEvent.KEYCODE_NUMPAD_EQUALS, LWJGLGLFWKeycode.GLFW_KEY_EQUAL);
        androidToLwjglMap.put(KeyEvent.KEYCODE_NUMPAD_MULTIPLY, LWJGLGLFWKeycode.GLFW_KEY_KP_MULTIPLY);
        androidToLwjglMap.put(KeyEvent.KEYCODE_NUMPAD_SUBTRACT, LWJGLGLFWKeycode.GLFW_KEY_KP_SUBTRACT);
        
        // Page keys
        androidToLwjglMap.put(KeyEvent.KEYCODE_PAGE_DOWN, LWJGLGLFWKeycode.GLFW_KEY_PAGE_DOWN);
        androidToLwjglMap.put(KeyEvent.KEYCODE_PAGE_UP, LWJGLGLFWKeycode.GLFW_KEY_PAGE_UP);
        
        androidToLwjglMap.put(KeyEvent.KEYCODE_PERIOD, LWJGLGLFWKeycode.GLFW_KEY_PERIOD);
        androidToLwjglMap.put(KeyEvent.KEYCODE_PLUS, LWJGLGLFWKeycode.GLFW_KEY_KP_ADD);
        // androidToLwjglMap.put(KeyEvent.KEYCODE_POWER, LWJGLGLFWKeycode.GLFW_KEY_POWER);
        androidToLwjglMap.put(KeyEvent.KEYCODE_RIGHT_BRACKET, LWJGLGLFWKeycode.GLFW_KEY_RIGHT_BRACKET);
        androidToLwjglMap.put(KeyEvent.KEYCODE_SEMICOLON, LWJGLGLFWKeycode.GLFW_KEY_SEMICOLON);
        
        // Shift keys
        androidToLwjglMap.put(KeyEvent.KEYCODE_SHIFT_LEFT, LWJGLGLFWKeycode.GLFW_KEY_LEFT_SHIFT);
        androidToLwjglMap.put(KeyEvent.KEYCODE_SHIFT_RIGHT, LWJGLGLFWKeycode.GLFW_KEY_RIGHT_SHIFT);
        
        androidToLwjglMap.put(KeyEvent.KEYCODE_SLASH, LWJGLGLFWKeycode.GLFW_KEY_SLASH);
        // androidToLwjglMap.put(KeyEvent.KEYCODE_SLEEP, LWJGLGLFWKeycode.GLFW_KEY_SLEEP);
        androidToLwjglMap.put(KeyEvent.KEYCODE_SPACE, LWJGLGLFWKeycode.GLFW_KEY_SPACE);
        // androidToLwjglMap.put(KeyEvent.KEYCODE_SYSRQ, LWJGLGLFWKeycode.GLFW_KEY_SYSRQ);
        androidToLwjglMap.put(KeyEvent.KEYCODE_TAB, LWJGLGLFWKeycode.GLFW_KEY_TAB);
        // androidToLwjglMap.put(KeyEvent.KEYCODE_YEN, LWJGLGLFWKeycode.GLFW_KEY_YEN);
        
        // androidToLwjglMap.put(KeyEvent.KEYCODE_BUTTON_1, LWJGLGLFWKeycode.G
        androidToLwjglMap.put(KeyEvent.KEYCODE_AT,LWJGLGLFWKeycode.GLFW_KEY_2);
        androidToLwjglMap.put(KeyEvent.KEYCODE_POUND,LWJGLGLFWKeycode.GLFW_KEY_3);
    }
    
    public static String[] generateKeyName() {
        if (androidKeyNameArray == null) {
            List<String> keyName = new ArrayList<String>();
            for (Integer perKey : androidToLwjglMap.keySet()) {
                keyName.add(KeyEvent.keyCodeToString(perKey.intValue()).replace("KEYCODE_", ""));
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
