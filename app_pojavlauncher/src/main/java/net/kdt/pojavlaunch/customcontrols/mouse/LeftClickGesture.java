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
    private float mGestureStartX, mGestureStartY, mGestureEndX, mGestureEndY;
    private boolean mMouseActivated;

    public LeftClickGesture(Handler handler) {
        super(handler);
    }

    public final void inputEvent() {
        if(submit()) {
            mGestureStartX = mGestureEndX = CallbackBridge.mouseX;
            mGestureStartY = mGestureEndY = CallbackBridge.mouseY;
        }
    }

    @Override
    protected int getGestureDelay() {
        return LauncherPreferences.PREF_LONGPRESS_TRIGGER;
    }

    @Override
    public boolean checkAndTrigger() {
        boolean fingerStill = LeftClickGesture.isFingerStill(mGestureStartX, mGestureStartY, mGestureEndX, mGestureEndY, FINGER_STILL_THRESHOLD);
        // If the finger is still, fire the gesture.
        if(fingerStill) {
            sendMouseButton(LwjglGlfwKeycode.GLFW_MOUSE_BUTTON_LEFT, true);
            mMouseActivated = true;
        }
        // Otherwise, don't click but still keep it active
        return true;
    }

    @Override
    public void onGestureCancelled(boolean isSwitching) {
        if(mMouseActivated) {
            sendMouseButton(LwjglGlfwKeycode.GLFW_MOUSE_BUTTON_LEFT, false);
            mMouseActivated = false;
        }
    }

    public void setMotion(float deltaX, float deltaY) {
        mGestureEndX += deltaX;
        mGestureEndY += deltaY;
    }

    /**
     * Check if the finger is still when compared to mouseX/mouseY in CallbackBridge.
     * @param startX the starting X of the gesture
     * @param startY the starting Y of the gesture
     * @return whether the finger's position counts as "still" or not
     */
    public static boolean isFingerStill(float startX, float startY, float threshold) {
        return MathUtils.dist(
                CallbackBridge.mouseX,
                CallbackBridge.mouseY,
                startX,
                startY
        ) <= threshold;
    }

    public static boolean isFingerStill(float startX, float startY, float endX, float endY, float threshold) {
        return MathUtils.dist(
                endX,
                endY,
                startX,
                startY
        ) <= threshold;
    }
}
