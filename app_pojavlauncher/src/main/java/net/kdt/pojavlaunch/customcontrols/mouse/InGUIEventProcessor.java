package net.kdt.pojavlaunch.customcontrols.mouse;

import android.view.GestureDetector;
import android.view.MotionEvent;

import net.kdt.pojavlaunch.LwjglGlfwKeycode;
import net.kdt.pojavlaunch.SingleTapConfirm;
import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.prefs.LauncherPreferences;

import org.lwjgl.glfw.CallbackBridge;

public class InGUIEventProcessor implements TouchEventProcessor {
    public static final float FINGER_SCROLL_THRESHOLD = Tools.dpToPx(6);
    public static final float FINGER_STILL_THRESHOLD = Tools.dpToPx(5);

    private final PointerTracker mTracker = new PointerTracker();
    private final TapDetector mSingleTapDetector;
    private AbstractTouchpad mTouchpad;
    private boolean mIsMouseDown = false;
    private float mStartX, mStartY;
    private final float mScaleFactor;
    private final Scroller mScroller = new Scroller(FINGER_SCROLL_THRESHOLD);

    public InGUIEventProcessor(float scaleFactor) {
        mSingleTapDetector = new TapDetector(1, TapDetector.DETECTION_METHOD_BOTH);
        mScaleFactor = scaleFactor;
    }

    @Override
    public boolean processTouchEvent(MotionEvent motionEvent) {
        boolean singleTap = mSingleTapDetector.onTouchEvent(motionEvent);

        switch (motionEvent.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mTracker.startTracking(motionEvent);
                if(!touchpadDisplayed()) {
                    sendTouchCoordinates(motionEvent.getX(), motionEvent.getY());

                    // disabled gestures means no scrolling possible, send gesture early
                    if (LauncherPreferences.PREF_DISABLE_GESTURES) enableMouse();
                    else setGestureStart(motionEvent);
                }
                break;

            case MotionEvent.ACTION_MOVE:
                int pointerCount = motionEvent.getPointerCount();
                int pointerIndex = mTracker.trackEvent(motionEvent);
                if(pointerCount == 1 || LauncherPreferences.PREF_DISABLE_GESTURES) {
                    if(touchpadDisplayed()) {
                        mTouchpad.applyMotionVector(mTracker.getMotionVector());
                    } else {
                        float mainPointerX = motionEvent.getX(pointerIndex);
                        float mainPointerY = motionEvent.getY(pointerIndex);
                        sendTouchCoordinates(mainPointerX, mainPointerY);

                        if(!mIsMouseDown) {
                            if(!hasGestureStarted()) setGestureStart(motionEvent);
                            if(!LeftClickGesture.isFingerStill(mStartX, mStartY, FINGER_STILL_THRESHOLD))
                                enableMouse();
                        }

                    }
                } else mScroller.performScroll(mTracker.getMotionVector());
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mScroller.resetScrollOvershoot();
                mTracker.cancelTracking();

                // Handle single tap on gestures
                if((!LauncherPreferences.PREF_DISABLE_GESTURES || touchpadDisplayed()) && !mIsMouseDown && singleTap) {
                    CallbackBridge.putMouseEventWithCoords(LwjglGlfwKeycode.GLFW_MOUSE_BUTTON_LEFT, CallbackBridge.mouseX, CallbackBridge.mouseY);
                }

                if(mIsMouseDown) disableMouse();
                resetGesture();
        }


        return true;
    }

    private boolean touchpadDisplayed() {
        return mTouchpad != null && mTouchpad.getDisplayState();
    }

    public void setAbstractTouchpad(AbstractTouchpad touchpad) {
        mTouchpad = touchpad;
    }

    private void sendTouchCoordinates(float x, float y) {
        CallbackBridge.sendCursorPos( x * mScaleFactor, y * mScaleFactor);
    }

    private void enableMouse() {
        CallbackBridge.sendMouseButton(LwjglGlfwKeycode.GLFW_MOUSE_BUTTON_LEFT, true);
        mIsMouseDown = true;
    }

    private void disableMouse() {
        CallbackBridge.sendMouseButton(LwjglGlfwKeycode.GLFW_MOUSE_BUTTON_LEFT, false);
        mIsMouseDown = false;
    }

    private void setGestureStart(MotionEvent event) {
        mStartX = event.getX() * mScaleFactor;
        mStartY = event.getY() * mScaleFactor;
    }

    private void resetGesture() {
        mStartX = mStartY = -1;
    }

    private boolean hasGestureStarted() {
        return mStartX != -1 || mStartY != -1;
    }

    @Override
    public void cancelPendingActions() {
        mScroller.resetScrollOvershoot();
        disableMouse();
    }
}
