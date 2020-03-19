package net.kdt.pojavlaunch;

import android.util.*;
import android.view.*;
import java.util.*;
import org.lwjgl.input.*;
import org.lwjgl.opengl.*;

public class AndroidLWJGLKeycode {
	private static final Map<Integer, Integer> androidToLwjglMap;
	static {
		androidToLwjglMap = new ArrayMap<Integer, Integer>();
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
		androidToLwjglMap.put(KeyEvent.KEYCODE_ALT_LEFT, Keyboard.KEY_LMENU);
		androidToLwjglMap.put(KeyEvent.KEYCODE_ALT_RIGHT, Keyboard.KEY_RMENU);
		androidToLwjglMap.put(KeyEvent.KEYCODE_BACK, Keyboard.KEY_ESCAPE); // Might not be correctly!
		androidToLwjglMap.put(KeyEvent.KEYCODE_BACKSLASH, Keyboard.KEY_BACKSLASH);
		// androidToLwjglMap.put(KeyEvent.keyCode_del, Keyboard.KEY_DELETE);
		androidToLwjglMap.put(KeyEvent.KEYCODE_BREAK, Keyboard.KEY_PAUSE);
		androidToLwjglMap.put(KeyEvent.KEYCODE_CAPS_LOCK, Keyboard.KEY_CAPITAL);
		androidToLwjglMap.put(KeyEvent.KEYCODE_COMMA, Keyboard.KEY_COMMA);
		androidToLwjglMap.put(KeyEvent.KEYCODE_CTRL_LEFT, Keyboard.KEY_LCONTROL);
		androidToLwjglMap.put(KeyEvent.KEYCODE_CTRL_RIGHT, Keyboard.KEY_RCONTROL);
		androidToLwjglMap.put(KeyEvent.KEYCODE_DEL, Keyboard.KEY_BACK); // Backspace
		androidToLwjglMap.put(KeyEvent.KEYCODE_ENTER, Keyboard.KEY_RETURN);
		androidToLwjglMap.put(KeyEvent.KEYCODE_EQUALS, Keyboard.KEY_EQUALS);
		androidToLwjglMap.put(KeyEvent.KEYCODE_ESCAPE, Keyboard.KEY_ESCAPE);
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
		androidToLwjglMap.put(KeyEvent.KEYCODE_FORWARD, Keyboard.KEY_UP); // Might not be correctly;
		androidToLwjglMap.put(KeyEvent.KEYCODE_GRAVE, Keyboard.KEY_GRAVE);
		androidToLwjglMap.put(KeyEvent.KEYCODE_HOME, Keyboard.KEY_HOME);
		androidToLwjglMap.put(KeyEvent.KEYCODE_INSERT, Keyboard.KEY_INSERT);
		androidToLwjglMap.put(KeyEvent.KEYCODE_KANA, Keyboard.KEY_KANA);
		androidToLwjglMap.put(KeyEvent.KEYCODE_LEFT_BRACKET, Keyboard.KEY_LBRACKET);
		androidToLwjglMap.put(KeyEvent.KEYCODE_MINUS, Keyboard.KEY_MINUS);
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
		androidToLwjglMap.put(KeyEvent.KEYCODE_NUMPAD_ENTER, Keyboard.KEY_NUMPADENTER);
		androidToLwjglMap.put(KeyEvent.KEYCODE_NUMPAD_EQUALS, Keyboard.KEY_NUMPADEQUALS);
		androidToLwjglMap.put(KeyEvent.KEYCODE_NUMPAD_COMMA, Keyboard.KEY_NUMPADCOMMA);
		androidToLwjglMap.put(KeyEvent.KEYCODE_NUMPAD_ADD, Keyboard.KEY_ADD);
		androidToLwjglMap.put(KeyEvent.KEYCODE_NUMPAD_DIVIDE, Keyboard.KEY_DIVIDE);
		androidToLwjglMap.put(KeyEvent.KEYCODE_NUMPAD_DOT, Keyboard.KEY_PERIOD);
		androidToLwjglMap.put(KeyEvent.KEYCODE_NUMPAD_MULTIPLY, Keyboard.KEY_MULTIPLY);
		androidToLwjglMap.put(KeyEvent.KEYCODE_NUMPAD_SUBTRACT, Keyboard.KEY_SUBTRACT);
		androidToLwjglMap.put(KeyEvent.KEYCODE_PAGE_DOWN, Keyboard.KEY_NEXT);
		androidToLwjglMap.put(KeyEvent.KEYCODE_PAGE_UP, Keyboard.KEY_PRIOR);
		androidToLwjglMap.put(KeyEvent.KEYCODE_PERIOD, Keyboard.KEY_PERIOD);
		androidToLwjglMap.put(KeyEvent.KEYCODE_PLUS, Keyboard.KEY_ADD);
		androidToLwjglMap.put(KeyEvent.KEYCODE_POWER, Keyboard.KEY_POWER);
		androidToLwjglMap.put(KeyEvent.KEYCODE_RIGHT_BRACKET, Keyboard.KEY_RBRACKET);
		androidToLwjglMap.put(KeyEvent.KEYCODE_SEMICOLON, Keyboard.KEY_SEMICOLON);
		androidToLwjglMap.put(KeyEvent.KEYCODE_SHIFT_LEFT, Keyboard.KEY_LSHIFT);
		androidToLwjglMap.put(KeyEvent.KEYCODE_SHIFT_RIGHT, Keyboard.KEY_RSHIFT);
		androidToLwjglMap.put(KeyEvent.KEYCODE_SLASH, Keyboard.KEY_SLASH);
		androidToLwjglMap.put(KeyEvent.KEYCODE_SLEEP, Keyboard.KEY_SLEEP);
		androidToLwjglMap.put(KeyEvent.KEYCODE_SPACE, Keyboard.KEY_SPACE);
		androidToLwjglMap.put(KeyEvent.KEYCODE_SYSRQ, Keyboard.KEY_SYSRQ);
		androidToLwjglMap.put(KeyEvent.KEYCODE_TAB, Keyboard.KEY_TAB);
		androidToLwjglMap.put(KeyEvent.KEYCODE_YEN, Keyboard.KEY_YEN);
	}
	
    public static void execKey(MainActivity mainActivity, KeyEvent keyEvent, int i, boolean isDown) {
        for (Map.Entry<Integer, Integer> perKey : androidToLwjglMap.entrySet()) {
			if (perKey.getKey() == i) {
				mainActivity.sendKeyPress(perKey.getValue(), isDown);
			}
		}
		
		if (!AndroidDisplay.grab) {
			try {
				// Old method works without dead chars:
				if (isDown) {
					mainActivity.sendKeyPress((char) keyEvent.getUnicodeChar());
				}
			} catch (Throwable th) {
				th.printStackTrace();
			}
        }
    }
}

