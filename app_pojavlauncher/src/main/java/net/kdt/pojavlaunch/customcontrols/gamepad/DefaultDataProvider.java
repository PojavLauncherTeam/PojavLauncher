package net.kdt.pojavlaunch.customcontrols.gamepad;

import net.kdt.pojavlaunch.GrabListener;

import org.lwjgl.glfw.CallbackBridge;

public class DefaultDataProvider implements GamepadDataProvider {
    public static final DefaultDataProvider INSTANCE = new DefaultDataProvider();

    // Cannot instantiate this class publicly
    private DefaultDataProvider() {}

    @Override
    public GamepadMap getGameMap() {
        return GamepadMapStore.getGameMap();
    }


    @Override
    public GamepadMap getMenuMap() {
        return GamepadMapStore.getMenuMap();
    }

    @Override
    public boolean isGrabbing() {
        return CallbackBridge.isGrabbing();
    }

    @Override
    public void attachGrabListener(GrabListener grabListener) {
        CallbackBridge.addGrabListener(grabListener);
    }
}
