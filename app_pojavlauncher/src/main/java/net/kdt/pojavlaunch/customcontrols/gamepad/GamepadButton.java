package net.kdt.pojavlaunch.customcontrols.gamepad;

import android.view.KeyEvent;

public class GamepadButton {

    /*
    Just a simple button, that auto deal with the great habit from android to just SPAAAMS input events
     */
    public int[] keycodes;
    public boolean isToggleable = false;
    private boolean isDown = false;
    private boolean isToggled = false;

    public void update(KeyEvent event){
        boolean isKeyDown = (event.getAction() == KeyEvent.ACTION_DOWN);
        update(isKeyDown);
    }

    public void update(boolean isKeyDown){
        if(isKeyDown != isDown){
            isDown = isKeyDown;
            if(isToggleable){
                if(isKeyDown){
                    isToggled = !isToggled;
                    Gamepad.sendInput(keycodes, isToggled);
                }
                return;
            }
            Gamepad.sendInput(keycodes, isDown);
        }
    }

    public void resetButtonState(){
        if(isDown || isToggled){
            Gamepad.sendInput(keycodes, false);
        }
        isDown = false;
        isToggled = false;
    }

    public boolean isDown(){
        return isToggleable ? isToggled : isDown;
    }

}
