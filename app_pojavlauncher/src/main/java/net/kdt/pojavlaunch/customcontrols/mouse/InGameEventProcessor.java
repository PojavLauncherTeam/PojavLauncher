package net.kdt.pojavlaunch.customcontrols.mouse;

import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;

import net.kdt.pojavlaunch.TapDetector;
import net.kdt.pojavlaunch.prefs.LauncherPreferences;

import org.lwjgl.glfw.CallbackBridge;

public class InGameEventProcessor implements TouchEventProcessor {
    private final Handler mGestureHandler = new Handler(Looper.getMainLooper());
    private final double mSensitivity;
    private final PointerTracker mTracker = new PointerTracker();
    private final HotbarTracker mGuiBarTracker;
    private final LeftClickGesture mLeftClickGesture = new LeftClickGesture(mGestureHandler);
    private final RightClickGesture mRightClickGesture = new RightClickGesture(mGestureHandler);
    private final TapDetector mDoubleTapDetector = new TapDetector(2, TapDetector.DETECTION_METHOD_DOWN);

    public InGameEventProcessor(float scaleFactor, double sensitivity) {
        mGuiBarTracker = new HotbarTracker(mGestureHandler, scaleFactor);
        mSensitivity = sensitivity;
    }

    @Override
    public boolean processTouchEvent(MotionEvent motionEvent) {
        boolean hasDoubleTapped = mDoubleTapDetector.onTouchEvent(motionEvent);
        switch (motionEvent.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if(mGuiBarTracker.begin(motionEvent, hasDoubleTapped)) break;
                mTracker.startTracking(motionEvent);
                if(LauncherPreferences.PREF_DISABLE_GESTURES) break;
                checkGestures();
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mGuiBarTracker.begin(motionEvent, hasDoubleTapped);
                break;
            case MotionEvent.ACTION_MOVE:
                int trackedIndex = mTracker.trackEvent(motionEvent);
                // Don't send mouse positions if there's a finger in the gui bar *and* the camera tracker
                // tracks the same finger as the gui bar.
                if(mGuiBarTracker.track(motionEvent, trackedIndex, hasDoubleTapped)) break;
                checkGestures();
                float[] motionVector = mTracker.getMotionVector();
                CallbackBridge.mouseX += motionVector[0] * mSensitivity;
                CallbackBridge.mouseY += motionVector[1] * mSensitivity;
                CallbackBridge.sendCursorPos(CallbackBridge.mouseX, CallbackBridge.mouseY);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mGuiBarTracker.cancel();
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
