package net.kdt.pojavlaunch;

import android.text.method.*;
import android.view.*;
import android.view.inputmethod.*;
import org.lwjgl.opengl.*;
import org.lwjgl.input.*;

public class DroidToJavaKey {
    private static StringBuilder mComposing = new StringBuilder();
	private static long mMetaState;
	
    public static void execKey(MainActivity mainActivity, KeyEvent keyEvent, int i, boolean status) {
		/*
        if (i >= KeyEvent.KEYCODE_F1 && i <= KeyEvent.KEYCODE_F12) {
            mainActivity.sendKeyPress(i - 72, status);
        } else if (i == KeyEvent.KEYCODE_DEL) {
            mainActivity.sendKeyPress(Keyboard.KEY_DELETE, status);
        } else if (i == KeyEvent.KEYCODE_ENTER) {
            mainActivity.sendKeyPress(Keyboard.KEY_RETURN, status);
        } else if (i == KeyEvent.KEYCODE_SHIFT_LEFT) {
            mainActivity.sendKeyPress(Keyboard.KEY_LSHIFT, status);
        } else if (i == KeyEvent.KEYCODE_SHIFT_RIGHT) {
            mainActivity.sendKeyPress(Keyboard.KEY_RSHIFT, status);
        } else if (i == KeyEvent.KEYCODE_DPAD_LEFT) {
            mainActivity.sendKeyPress(Keyboard.KEY_LEFT, status);
        } else if (i == KeyEvent.KEYCODE_DPAD_UP) {
            mainActivity.sendKeyPress(Keyboard.KEY_UP, status);
        } else if (i == keyEvent.KEYCODE_DPAD_RIGHT) {
            mainActivity.sendKeyPress(Keyboard.KEY_RIGHT, status);
        } else if (i == KeyEvent.KEYCODE_DPAD_DOWN) {
            mainActivity.sendKeyPress(Keyboard.KEY_DOWN, status);
        } else if (i >= KeyEvent.KEYCODE_BUTTON_1 && i <= KeyEvent.KEYCODE_BUTTON_16) {
            mainActivity.sendKeyPress(i - 188, status);
        }
		
		if (!AndroidDisplay.grab) {
			try {
				// Old method works without dead chars:
				mainActivity.sendKeyPress(keyEvent.getDisplayLabel(), status);
			} catch (Throwable th) {
				th.printStackTrace();
			}
        }
		*/
		
		// Fix press 'e' key close inventory (while search item)
		// Should it be or other ways?
		/*
		if (!AndroidDisplay.grab && keyEvent.getDisplayLabel() != 'e') {
			mainActivity.sendKeyPress(keyEvent.getDisplayLabel(), status);
		} else {
			mainActivity.sendKeyPress(keyEvent.getDisplayLabel());
		}
		*/
		
		mainActivity.sendKeyPress(keyEvent.getDisplayLabel(), status);
    }
}

