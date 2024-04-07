package net.kdt.pojavlaunch.customcontrols.mouse;

import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;

import net.kdt.pojavlaunch.prefs.LauncherPreferences;

import org.lwjgl.glfw.CallbackBridge;

public class InGameEventProcessor implements TouchEventProcessor {
    private final Handler mGestureHandler = new Handler(Looper.getMainLooper());
    private final double mSensitivity;
    private final PointerTracker mTracker = new PointerTracker();
    private final LeftClickGesture mLeftClickGesture = new LeftClickGesture(mGestureHandler);
    private final RightClickGesture mRightClickGesture = new RightClickGesture(mGestureHandler);

    public InGameEventProcessor(double sensitivity) {
        mSensitivity = sensitivity;
    }

    @Override
    public boolean processTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mTracker.startTracking(motionEvent);
                if(LauncherPreferences.PREF_DISABLE_GESTURES) break;
                checkGestures();
                break;
            case MotionEvent.ACTION_MOVE:
                mTracker.trackEvent(motionEvent);
                float[] motionVector = mTracker.getMotionVector();
                CallbackBridge.mouseX += motionVector[0] * mSensitivity;
                CallbackBridge.mouseY += motionVector[1] * mSensitivity;
                CallbackBridge.sendCursorPos(CallbackBridge.mouseX, CallbackBridge.mouseY);
                if(LauncherPreferences.PREF_DISABLE_GESTURES) break;
                checkGestures();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mTracker.cancelTracking();
                cancelGestures(false);
        }
        return true;
    }

    @Override
    public void cancelPendingActions() {
        cancelGestures(true);
    }

    private void checkGestures() {
        mLeftClickGesture.inputEvent();
        mRightClickGesture.inputEvent();
    }

    private void cancelGestures(boolean isSwitching) {
        mLeftClickGesture.cancel(isSwitching);
        mRightClickGesture.cancel(isSwitching);
    }
}
