package net.kdt.pojavlaunch.customcontrols.gamepad;

import android.view.KeyEvent;

/**
 * This class corresponds to a button that does not physically exist on the gamepad, but is
 * emulated from other inputs on it (like WASD directional keys)
 */
public class GamepadEmulatedButton {
    public short[] keycodes;
    protected boolean mIsDown = false;

    public void update(KeyEvent event) {
        boolean isKeyDown = (event.getAction() == KeyEvent.ACTION_DOWN);
        update(isKeyDown);
    }

    public void update(boolean isKeyDown){
        if(isKeyDown != mIsDown){
            mIsDown = isKeyDown;
            onDownStateChanged(mIsDown);
        }
    }

    public void resetButtonState() {
        if(mIsDown) Gamepad.sendInput(keycodes, false);
        mIsDown = false;
    }

    protected void onDownStateChanged(boolean isDown) {
        Gamepad.sendInput(keycodes, mIsDown);
    }
}
