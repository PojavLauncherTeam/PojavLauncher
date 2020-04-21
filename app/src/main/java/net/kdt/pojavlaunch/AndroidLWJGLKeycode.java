package net.kdt.pojavlaunch;

import android.util.*;
import android.view.*;
import java.util.*;
import org.lwjgl.input.*;
import org.lwjgl.opengl.*;

public class AndroidLWJGLKeycode {
	// Fix double letters on MC 1.9 and above
	public static boolean isBackspaceAfterChar;
	private static final ArrayMap<Integer, Integer> androidToLwjglMap;
	private static String[] androidKeyNameArray;
	static {
		/*
		 * There are already have mapped in
		 * org.lwjgl.opengl.AndroidKeyCodes
		 * but this key map is more than.
		 */
		
		
		// Mapping Android Keycodes to LWJGL Keycodes
		androidToLwjglMap = new ArrayMap<Integer, Integer>();
		
		// 0-9 keys
		androidToLwjglMap.put(KeyEvent.KEYCODE_0, Keyboard.KEY_0);
		androidToLwjglMap.put(KeyEvent.KEYCODE_1, Keyboard.KEY_1);
		androidToLwjglMap.put(KeyEvent.KEYCODE_2, Keyboard.KEY_2);
		androidToLwjglMap.put(KeyEvent.KEYCODE_3, Keyboard.KEY_3);
		androidToLwjglMap.put(KeyEvent.KEYCODE_4, Keyboard.KEY_4);
		androidToLwjglMap.put(KeyEvent.KEYCODE_5, Keyboard.KEY_5);
		androidToLwjglMap.put(KeyEvent.KEYCODE_6, Keyboard.KEY_6);
		androidToLwjglMap.put(KeyEvent.KEYCODE_7, Keyboard.KEY_7);
		androidToLwjglMap.put(KeyEvent.KEYCODE_8, Keyboard.KEY_8);
		androidToLwjglMap.put(KeyEvent.KEYCODE_9, Keyboard.KEY_9);
		
		// A-Z keys
		androidToLwjglMap.put(KeyEvent.KEYCODE_A, Keyboard.KEY_A);
		androidToLwjglMap.put(KeyEvent.KEYCODE_B, Keyboard.KEY_B);
		androidToLwjglMap.put(KeyEvent.KEYCODE_C, Keyboard.KEY_C);
		androidToLwjglMap.put(KeyEvent.KEYCODE_D, Keyboard.KEY_D);
		androidToLwjglMap.put(KeyEvent.KEYCODE_E, Keyboard.KEY_E);
		androidToLwjglMap.put(KeyEvent.KEYCODE_F, Keyboard.KEY_F);
		androidToLwjglMap.put(KeyEvent.KEYCODE_G, Keyboard.KEY_G);
		androidToLwjglMap.put(KeyEvent.KEYCODE_H, Keyboard.KEY_H);
		androidToLwjglMap.put(KeyEvent.KEYCODE_I, Keyboard.KEY_I);
		androidToLwjglMap.put(KeyEvent.KEYCODE_J, Keyboard.KEY_J);
		androidToLwjglMap.put(KeyEvent.KEYCODE_K, Keyboard.KEY_K);
		androidToLwjglMap.put(KeyEvent.KEYCODE_L, Keyboard.KEY_L);
		androidToLwjglMap.put(KeyEvent.KEYCODE_M, Keyboard.KEY_M);
		androidToLwjglMap.put(KeyEvent.KEYCODE_N, Keyboard.KEY_M);
		androidToLwjglMap.put(KeyEvent.KEYCODE_O, Keyboard.KEY_O);
		androidToLwjglMap.put(KeyEvent.KEYCODE_P, Keyboard.KEY_P);
		androidToLwjglMap.put(KeyEvent.KEYCODE_Q, Keyboard.KEY_Q);
		androidToLwjglMap.put(KeyEvent.KEYCODE_R, Keyboard.KEY_R);
		androidToLwjglMap.put(KeyEvent.KEYCODE_S, Keyboard.KEY_S);
		androidToLwjglMap.put(KeyEvent.KEYCODE_T, Keyboard.KEY_T);
		androidToLwjglMap.put(KeyEvent.KEYCODE_U, Keyboard.KEY_U);
		androidToLwjglMap.put(KeyEvent.KEYCODE_V, Keyboard.KEY_V);
		androidToLwjglMap.put(KeyEvent.KEYCODE_W, Keyboard.KEY_W);
		androidToLwjglMap.put(KeyEvent.KEYCODE_X, Keyboard.KEY_X);
		androidToLwjglMap.put(KeyEvent.KEYCODE_Y, Keyboard.KEY_Y);
		androidToLwjglMap.put(KeyEvent.KEYCODE_Z, Keyboard.KEY_Z);
		
		// Alt keys
		androidToLwjglMap.put(KeyEvent.KEYCODE_ALT_LEFT, Keyboard.KEY_LMENU);
		androidToLwjglMap.put(KeyEvent.KEYCODE_ALT_RIGHT, Keyboard.KEY_RMENU);
		
		// Escape key
		androidToLwjglMap.put(KeyEvent.KEYCODE_BACK, Keyboard.KEY_ESCAPE);
		
		androidToLwjglMap.put(KeyEvent.KEYCODE_BACKSLASH, Keyboard.KEY_BACKSLASH);
		androidToLwjglMap.put(KeyEvent.KEYCODE_BREAK, Keyboard.KEY_PAUSE);
		androidToLwjglMap.put(KeyEvent.KEYCODE_CAPS_LOCK, Keyboard.KEY_CAPITAL);
		androidToLwjglMap.put(KeyEvent.KEYCODE_COMMA, Keyboard.KEY_COMMA);
		
		// Control keys
		androidToLwjglMap.put(KeyEvent.KEYCODE_CTRL_LEFT, Keyboard.KEY_LCONTROL);
		androidToLwjglMap.put(KeyEvent.KEYCODE_CTRL_RIGHT, Keyboard.KEY_RCONTROL);
		
		androidToLwjglMap.put(KeyEvent.KEYCODE_DEL, Keyboard.KEY_BACK); // Backspace
		
		// Arrow keys
		androidToLwjglMap.put(KeyEvent.KEYCODE_DPAD_DOWN, Keyboard.KEY_DOWN);
		androidToLwjglMap.put(KeyEvent.KEYCODE_DPAD_LEFT, Keyboard.KEY_LEFT);
		androidToLwjglMap.put(KeyEvent.KEYCODE_DPAD_RIGHT, Keyboard.KEY_RIGHT);
		androidToLwjglMap.put(KeyEvent.KEYCODE_DPAD_UP, Keyboard.KEY_UP);
		
		androidToLwjglMap.put(KeyEvent.KEYCODE_ENTER, Keyboard.KEY_RETURN);
		androidToLwjglMap.put(KeyEvent.KEYCODE_EQUALS, Keyboard.KEY_EQUALS);
		androidToLwjglMap.put(KeyEvent.KEYCODE_ESCAPE, Keyboard.KEY_ESCAPE);
		
		// Fn keys
		androidToLwjglMap.put(KeyEvent.KEYCODE_F1, Keyboard.KEY_F1);
		androidToLwjglMap.put(KeyEvent.KEYCODE_F2, Keyboard.KEY_F2);
		androidToLwjglMap.put(KeyEvent.KEYCODE_F3, Keyboard.KEY_F3);
		androidToLwjglMap.put(KeyEvent.KEYCODE_F4, Keyboard.KEY_F4);
		androidToLwjglMap.put(KeyEvent.KEYCODE_F5, Keyboard.KEY_F5);
		androidToLwjglMap.put(KeyEvent.KEYCODE_F6, Keyboard.KEY_F6);
		androidToLwjglMap.put(KeyEvent.KEYCODE_F7, Keyboard.KEY_F7);
		androidToLwjglMap.put(KeyEvent.KEYCODE_F8, Keyboard.KEY_F8);
		androidToLwjglMap.put(KeyEvent.KEYCODE_F9, Keyboard.KEY_F9);
		androidToLwjglMap.put(KeyEvent.KEYCODE_F10, Keyboard.KEY_F10);
		androidToLwjglMap.put(KeyEvent.KEYCODE_F11, Keyboard.KEY_F11);
		androidToLwjglMap.put(KeyEvent.KEYCODE_F12, Keyboard.KEY_F12);
		androidToLwjglMap.put(KeyEvent.KEYCODE_FUNCTION, Keyboard.KEY_FUNCTION);
		
		androidToLwjglMap.put(KeyEvent.KEYCODE_GRAVE, Keyboard.KEY_GRAVE);
		androidToLwjglMap.put(KeyEvent.KEYCODE_HOME, Keyboard.KEY_HOME);
		androidToLwjglMap.put(KeyEvent.KEYCODE_INSERT, Keyboard.KEY_INSERT);
		androidToLwjglMap.put(KeyEvent.KEYCODE_KANA, Keyboard.KEY_KANA);
		androidToLwjglMap.put(KeyEvent.KEYCODE_LEFT_BRACKET, Keyboard.KEY_LBRACKET);
		androidToLwjglMap.put(KeyEvent.KEYCODE_MINUS, Keyboard.KEY_MINUS);
		
		// Num keys
		androidToLwjglMap.put(KeyEvent.KEYCODE_NUM_LOCK, Keyboard.KEY_NUMLOCK);
		androidToLwjglMap.put(KeyEvent.KEYCODE_NUMPAD_0, Keyboard.KEY_NUMPAD0);
		androidToLwjglMap.put(KeyEvent.KEYCODE_NUMPAD_1, Keyboard.KEY_NUMPAD1);
		androidToLwjglMap.put(KeyEvent.KEYCODE_NUMPAD_2, Keyboard.KEY_NUMPAD2);
		androidToLwjglMap.put(KeyEvent.KEYCODE_NUMPAD_3, Keyboard.KEY_NUMPAD3);
		androidToLwjglMap.put(KeyEvent.KEYCODE_NUMPAD_4, Keyboard.KEY_NUMPAD4);
		androidToLwjglMap.put(KeyEvent.KEYCODE_NUMPAD_5, Keyboard.KEY_NUMPAD5);
		androidToLwjglMap.put(KeyEvent.KEYCODE_NUMPAD_6, Keyboard.KEY_NUMPAD6);
		androidToLwjglMap.put(KeyEvent.KEYCODE_NUMPAD_7, Keyboard.KEY_NUMPAD7);
		androidToLwjglMap.put(KeyEvent.KEYCODE_NUMPAD_8, Keyboard.KEY_NUMPAD8);
		androidToLwjglMap.put(KeyEvent.KEYCODE_NUMPAD_9, Keyboard.KEY_NUMPAD9);
		androidToLwjglMap.put(KeyEvent.KEYCODE_NUMPAD_ADD, Keyboard.KEY_ADD);
		androidToLwjglMap.put(KeyEvent.KEYCODE_NUMPAD_COMMA, Keyboard.KEY_NUMPADCOMMA);
		androidToLwjglMap.put(KeyEvent.KEYCODE_NUMPAD_DIVIDE, Keyboard.KEY_DIVIDE);
		androidToLwjglMap.put(KeyEvent.KEYCODE_NUMPAD_DOT, Keyboard.KEY_PERIOD);
		androidToLwjglMap.put(KeyEvent.KEYCODE_NUMPAD_ENTER, Keyboard.KEY_NUMPADENTER);
		androidToLwjglMap.put(KeyEvent.KEYCODE_NUMPAD_EQUALS, Keyboard.KEY_NUMPADEQUALS);
		androidToLwjglMap.put(KeyEvent.KEYCODE_NUMPAD_MULTIPLY, Keyboard.KEY_MULTIPLY);
		androidToLwjglMap.put(KeyEvent.KEYCODE_NUMPAD_SUBTRACT, Keyboard.KEY_SUBTRACT);
		
		// Page keys
		androidToLwjglMap.put(KeyEvent.KEYCODE_PAGE_DOWN, Keyboard.KEY_NEXT);
		androidToLwjglMap.put(KeyEvent.KEYCODE_PAGE_UP, Keyboard.KEY_PRIOR);
		
		androidToLwjglMap.put(KeyEvent.KEYCODE_PERIOD, Keyboard.KEY_PERIOD);
		androidToLwjglMap.put(KeyEvent.KEYCODE_PLUS, Keyboard.KEY_ADD);
		androidToLwjglMap.put(KeyEvent.KEYCODE_POWER, Keyboard.KEY_POWER);
		androidToLwjglMap.put(KeyEvent.KEYCODE_RIGHT_BRACKET, Keyboard.KEY_RBRACKET);
		androidToLwjglMap.put(KeyEvent.KEYCODE_SEMICOLON, Keyboard.KEY_SEMICOLON);
		
		// Shift keys
		androidToLwjglMap.put(KeyEvent.KEYCODE_SHIFT_LEFT, Keyboard.KEY_LSHIFT);
		androidToLwjglMap.put(KeyEvent.KEYCODE_SHIFT_RIGHT, Keyboard.KEY_RSHIFT);
		
		androidToLwjglMap.put(KeyEvent.KEYCODE_SLASH, Keyboard.KEY_SLASH);
		androidToLwjglMap.put(KeyEvent.KEYCODE_SLEEP, Keyboard.KEY_SLEEP);
		androidToLwjglMap.put(KeyEvent.KEYCODE_SPACE, Keyboard.KEY_SPACE);
		androidToLwjglMap.put(KeyEvent.KEYCODE_SYSRQ, Keyboard.KEY_SYSRQ);
		androidToLwjglMap.put(KeyEvent.KEYCODE_TAB, Keyboard.KEY_TAB);
		androidToLwjglMap.put(KeyEvent.KEYCODE_YEN, Keyboard.KEY_YEN);
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
	
    public static void execKey(MainActivity mainActivity, KeyEvent keyEvent, int i, boolean isDown) {
		for (Map.Entry<Integer, Integer> perKey : androidToLwjglMap.entrySet()) {
			if (perKey.getKey() == i) {
				if (i == KeyEvent.KEYCODE_BACK && (keyEvent.getSource() == InputDevice.SOURCE_MOUSE)) {
					// Right mouse detection
					mainActivity.sendMouseButton(1, true);
					mainActivity.sendMouseButton(1, false);
				} else {
					mainActivity.sendKeyPress(perKey.getValue(), isDown);
				}
			}
		}
		
		if (keyEvent.isAltPressed()) {
			mainActivity.sendKeyPress(Keyboard.KEY_LMENU, isDown);
		} if (keyEvent.isCtrlPressed()) {
			mainActivity.sendKeyPress(Keyboard.KEY_LCONTROL, isDown);
		} if (keyEvent.isFunctionPressed()) {
			mainActivity.sendKeyPress(Keyboard.KEY_FUNCTION, isDown);
		} if (keyEvent.isShiftPressed()) {
			mainActivity.sendKeyPress(Keyboard.KEY_LSHIFT, isDown);
		}
		
		try {
			if (/* (int) keyEvent.getDisplayLabel() != KeyEvent.KEYCODE_UNKNOWN && */ !AndroidDisplay.grab) {
				mainActivity.sendKeyPress(0, (char) keyEvent.getUnicodeChar(), isDown);
			}
		} catch (Throwable th) {
			th.printStackTrace();
		}
		
		if (isBackspaceAfterChar && !AndroidDisplay.grab && i != KeyEvent.KEYCODE_DEL) {
			mainActivity.sendKeyPress(Keyboard.KEY_BACK, isDown);
		}
    }

	public static void execKeyIndex(MainActivity mainActivity, int index) {
		mainActivity.sendKeyPress(getKeyIndex(index));
	}
	
	public static int getKeyIndex(int index) {
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

