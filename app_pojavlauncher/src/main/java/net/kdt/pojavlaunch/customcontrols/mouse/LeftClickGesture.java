package net.kdt.pojavlaunch.customcontrols.mouse;

import static org.lwjgl.glfw.CallbackBridge.sendMouseButton;

import android.os.Handler;

import net.kdt.pojavlaunch.LwjglGlfwKeycode;
import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.prefs.LauncherPreferences;
import net.kdt.pojavlaunch.utils.MathUtils;

import org.lwjgl.glfw.CallbackBridge;

public class LeftClickGesture extends ValidatorGesture {
    public static final int FINGER_STILL_THRESHOLD = (int) Tools.dpToPx(9);
    private float mGestureStartX, mGestureStartY;

    public LeftClickGesture(Handler handler) {
        super(handler, LauncherPreferences.PREF_LONGPRESS_TRIGGER);
    }

    public final void inputEvent() {
        if(submit()) {
            mGestureStartX = CallbackBridge.mouseX;
            mGestureStartY = CallbackBridge.mouseY;
        }
    }

    @Override
    public boolean checkAndTrigger() {
        boolean fingerStill = LeftClickGesture.isFingerStill(mGestureStartX, mGestureStartY);
        if(fingerStill) sendMouseButton(LwjglGlfwKeycode.GLFW_MOUSE_BUTTON_LEFT, true);
        return fingerStill;
    }

    @Override
    public void onGestureCancelled(boolean isSwitching) {
        sendMouseButton(LwjglGlfwKeycode.GLFW_MOUSE_BUTTON_LEFT, false);
    }

    /**
     * Check if the finger is still when compared to mouseX/mouseY in CallbackBridge.
     * @param startX the starting X of the gesture
     * @param startY the starting Y of the gesture
     * @return whether the finger's position counts as "still" or not
     */
    public static boolean isFingerStill(float startX, float startY) {
        return MathUtils.dist(
                CallbackBridge.mouseX,
                CallbackBridge.mouseY,
                startX,
                startY
        ) <= FINGER_STILL_THRESHOLD;
    }
}
