package net.kdt.pojavlaunch.customcontrols.gamepad;

import android.view.KeyEvent;

/**
 * Simple button able to store its state and some properties
 */
public class GamepadButton {

    public int[] keycodes;
    public boolean isToggleable = false;
    private boolean mIsDown = false;
    private boolean mIsToggled = false;

    public void update(KeyEvent event){
        boolean isKeyDown = (event.getAction() == KeyEvent.ACTION_DOWN);
        update(isKeyDown);
    }

    public void update(boolean isKeyDown){
        if(isKeyDown != mIsDown){
            mIsDown = isKeyDown;
            if(isToggleable){
                if(isKeyDown){
                    mIsToggled = !mIsToggled;
                    Gamepad.sendInput(keycodes, mIsToggled);
                }
                return;
            }
            Gamepad.sendInput(keycodes, mIsDown);
        }
    }

    public void resetButtonState(){
        if(mIsDown || mIsToggled){
            Gamepad.sendInput(keycodes, false);
        }
        mIsDown = false;
        mIsToggled = false;
    }

    public boolean isDown(){
        return isToggleable ? mIsToggled : mIsDown;
    }

}
