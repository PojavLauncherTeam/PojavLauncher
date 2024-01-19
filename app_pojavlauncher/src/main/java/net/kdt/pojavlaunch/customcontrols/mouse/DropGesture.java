package net.kdt.pojavlaunch.customcontrols.mouse;

import static org.lwjgl.glfw.CallbackBridge.sendKeyPress;

import android.os.Handler;

import net.kdt.pojavlaunch.LwjglGlfwKeycode;

public class DropGesture extends ValidatorGesture {
    public DropGesture(Handler mHandler) {
        super(mHandler, 250);
    }
    boolean mGuiBarHit;

    public void submit(boolean hasGuiBarHit) {
        submit();
        mGuiBarHit = hasGuiBarHit;
    }

    @Override
    public boolean checkAndTrigger() {
        if(mGuiBarHit) sendKeyPress(LwjglGlfwKeycode.GLFW_KEY_Q);
        return true;
    }

    @Override
    public void onGestureCancelled(boolean isSwitching) {}
}
