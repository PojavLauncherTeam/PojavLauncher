package net.kdt.pojavlaunch.customcontrols.gamepad;

import android.view.KeyEvent;

public class GamepadButton {

    /*
    Just a simple button, that auto deal with the great habit from android to just SPAAAM input events
     */
    public int[] keycodes;
    private boolean isDown = false;

    public void update(KeyEvent event){
        boolean down = (event.getAction() == KeyEvent.ACTION_DOWN);
        if(down != isDown){
            isDown = down;
            Gamepad.sendInput(keycodes, isDown);
        }

    }

    public void resetButtonState(){
        isDown = false;
    }

}
