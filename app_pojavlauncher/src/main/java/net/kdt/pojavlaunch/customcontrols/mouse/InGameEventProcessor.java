package net.kdt.pojavlaunch.customcontrols.mouse;

import static net.kdt.pojavlaunch.utils.MCOptionUtils.getMcScale;

import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;

import net.kdt.pojavlaunch.LwjglGlfwKeycode;
import net.kdt.pojavlaunch.TapDetector;
import net.kdt.pojavlaunch.prefs.LauncherPreferences;
import net.kdt.pojavlaunch.utils.MCOptionUtils;

import org.lwjgl.glfw.CallbackBridge;

public class InGameEventProcessor implements TouchEventProcessor {

    private int mGuiScale;
    @SuppressWarnings("FieldCanBeLocal") // it can't, otherwise the weak reference will disappear
    private final MCOptionUtils.MCOptionListener mGuiScaleListener = () -> mGuiScale = getMcScale();
    private final Handler mGestureHandler = new Handler(Looper.getMainLooper());
    private static final int[] HOTBAR_KEYS = {
            LwjglGlfwKeycode.GLFW_KEY_1, LwjglGlfwKeycode.GLFW_KEY_2,   LwjglGlfwKeycode.GLFW_KEY_3,
            LwjglGlfwKeycode.GLFW_KEY_4, LwjglGlfwKeycode.GLFW_KEY_5,   LwjglGlfwKeycode.GLFW_KEY_6,
            LwjglGlfwKeycode.GLFW_KEY_7, LwjglGlfwKeycode.GLFW_KEY_8, LwjglGlfwKeycode.GLFW_KEY_9};
    private int mLastHudKey;
    private final float mScaleFactor;
    private final double mSensitivity;
    private final PointerTracker mTracker = new PointerTracker();
    private final LeftClickGesture mLeftClickGesture = new LeftClickGesture(mGestureHandler);
    private final RightClickGesture mRightClickGesture = new RightClickGesture(mGestureHandler);
    private final DropGesture mDropGesture = new DropGesture(mGestureHandler);
    private final TapDetector mDoubleTapDetector = new TapDetector(2, TapDetector.DETECTION_METHOD_DOWN);

    public InGameEventProcessor(float scaleFactor, double sensitivity) {
        MCOptionUtils.addMCOptionListener(mGuiScaleListener);
        mScaleFactor = scaleFactor;
        mSensitivity = sensitivity;
    }

    @Override
    public boolean processTouchEvent(MotionEvent motionEvent) {
        boolean hasDoubleTapped = mDoubleTapDetector.onTouchEvent(motionEvent);
        switch (motionEvent.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mTracker.startTracking(motionEvent);
                if(LauncherPreferences.PREF_DISABLE_GESTURES) break;
                checkGestures(handleGuiBar(motionEvent));
                break;
            case MotionEvent.ACTION_MOVE:
                mTracker.trackEvent(motionEvent);
                float[] motionVector = mTracker.getMotionVector();
                CallbackBridge.mouseX += motionVector[0] * mSensitivity;
                CallbackBridge.mouseY += motionVector[1] * mSensitivity;
                CallbackBridge.sendCursorPos(CallbackBridge.mouseX, CallbackBridge.mouseY);
                boolean hasGuiBarHit = handleGuiBar(motionEvent);
                // Handle this gesture separately as it's a separate toggle in settings
                if(hasGuiBarHit && hasDoubleTapped && !LauncherPreferences.PREF_DISABLE_SWAP_HAND) {
                    CallbackBridge.sendKeyPress(LwjglGlfwKeycode.GLFW_KEY_F);
                }
                if(LauncherPreferences.PREF_DISABLE_GESTURES) break;
                checkGestures(hasGuiBarHit);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                cancelGestures(false);
        }
        return true;
    }

    @Override
    public void cancelPendingActions() {
        cancelGestures(true);
    }

    private void checkGestures(boolean hasGuiBarHit) {
        if(!hasGuiBarHit) {
            mLeftClickGesture.inputEvent();
            mRightClickGesture.inputEvent();
        }
        mDropGesture.submit(hasGuiBarHit);
    }

    private void cancelGestures(boolean isSwitching) {
        mLeftClickGesture.cancel(isSwitching);
        mRightClickGesture.cancel(isSwitching);
        mDropGesture.cancel(isSwitching);
    }

    private boolean handleGuiBar(MotionEvent motionEvent) {
        int hudKeyHandled = -1;
        for(int i = 0; i < motionEvent.getPointerCount(); i++) {
            hudKeyHandled = handleGuiBar(
                    (int)motionEvent.getX(i), (int)motionEvent.getY(i)
            );
            if(hudKeyHandled != -1) break;
        }
        boolean hasGuiBarHit = hudKeyHandled != -1;
        if(hasGuiBarHit && hudKeyHandled != mLastHudKey) {
            CallbackBridge.sendKeyPress(hudKeyHandled);
            mLastHudKey = hudKeyHandled;
        }
        return hasGuiBarHit;
    }

    /** @return the hotbar key, given the position. -1 if no key are pressed */
    public int handleGuiBar(int x, int y) {
        if (!CallbackBridge.isGrabbing()) return -1;

        int barHeight = mcScale(20);
        int barY = CallbackBridge.physicalHeight - barHeight;
        if(y < barY) return -1;

        int barWidth = mcScale(180);
        int barX = (CallbackBridge.physicalWidth / 2) - (barWidth / 2);
        if(x < barX || x >= barX + barWidth) return -1;

        return HOTBAR_KEYS[(int) net.kdt.pojavlaunch.utils.MathUtils.map(x, barX, barX + barWidth, 0, 9)];
    }

    /** Return the size, given the UI scale size */
    private int mcScale(int input) {
        return (int)((mGuiScale * input)/ mScaleFactor);
    }
}
