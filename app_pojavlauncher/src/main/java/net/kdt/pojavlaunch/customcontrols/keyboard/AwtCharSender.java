package net.kdt.pojavlaunch.customcontrols.keyboard;

import net.kdt.pojavlaunch.AWTInputBridge;
import net.kdt.pojavlaunch.AWTInputEvent;

/** Send chars via the AWT Bridgee */
public class AwtCharSender implements CharacterSenderStrategy {
    @Override
    public void sendBackspace() {
        AWTInputBridge.sendKey(' ', AWTInputEvent.VK_BACK_SPACE);
    }

    @Override
    public void sendEnter() {
        AWTInputBridge.sendKey(' ', AWTInputEvent.VK_ENTER);
    }

    @Override
    public void sendChar(char character) {
        AWTInputBridge.sendChar(character);
    }

}
