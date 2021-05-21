package net.kdt.pojavlaunch.customcontrols.gamepad;

import android.view.KeyEvent;

public class GamepadButton {

    /*
    Just a simple button, that auto deal with the great habit from android to just SPAAAM input events
     */
    public int[] keycodes;
    public boolean isToggleable = false;
    private boolean isDown = false;
    private boolean toggled = false;

    public void update(KeyEvent event){
        boolean isKeyDown = (event.getAction() == KeyEvent.ACTION_DOWN);
        update(isKeyDown);
    }

    public void update(boolean isKeyDown){
        if(isKeyDown != isDown){
            isDown = isKeyDown;
            if(isToggleable){
                if(isKeyDown){
                    toggled = !toggled;
                    Gamepad.sendInput(keycodes, toggled);
                }
                return;
            }
            
            Gamepad.sendInput(keycodes, isDown);
        }
    }

    public void resetButtonState(){
        isDown = false;
    }

}
