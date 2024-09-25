package net.kdt.pojavlaunch.customcontrols.gamepad;

import net.kdt.pojavlaunch.GrabListener;

public interface GamepadDataProvider {
    GamepadMap getMenuMap();
    GamepadMap getGameMap();
    boolean isGrabbing();
    void attachGrabListener(GrabListener grabListener);
}
