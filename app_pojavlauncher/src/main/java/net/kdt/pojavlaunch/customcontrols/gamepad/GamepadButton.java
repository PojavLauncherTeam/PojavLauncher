package net.kdt.pojavlaunch.customcontrols.gamepad;

/**
 *  This class corresponds to a button that does exist on the gamepad
 */
public class GamepadButton extends GamepadEmulatedButton {
    public boolean isToggleable = false;
    private boolean mIsToggled = false;


    @Override
    protected void onDownStateChanged(boolean isDown) {
        if(isToggleable) {
            if(!isDown) return;
            mIsToggled = !mIsToggled;
            Gamepad.sendInput(keycodes, mIsToggled);
            return;
        }
        super.onDownStateChanged(isDown);
    }

    @Override
    public void resetButtonState() {
        if(!mIsDown && mIsToggled) {
            Gamepad.sendInput(keycodes, false);
            mIsToggled = false;
        } else {
            super.resetButtonState();
        }
    }
}
