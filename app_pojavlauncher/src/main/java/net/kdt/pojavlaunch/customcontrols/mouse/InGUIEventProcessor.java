package net.kdt.pojavlaunch.customcontrols.mouse;

import android.view.GestureDetector;
import android.view.MotionEvent;

import net.kdt.pojavlaunch.LwjglGlfwKeycode;
import net.kdt.pojavlaunch.SingleTapConfirm;
import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.prefs.LauncherPreferences;

import org.lwjgl.glfw.CallbackBridge;

public class InGUIEventProcessor implements TouchEventProcessor {
    private final PointerTracker mTracker = new PointerTracker();
    private final GestureDetector mSingleTapDetector;
    private AbstractTouchpad mTouchpad;
    private boolean mIsMouseDown = false;
    private final float mScaleFactor;
    private float mScrollOvershootH, mScrollOvershootV;
    public static final float FINGER_SCROLL_THRESHOLD = Tools.dpToPx(6);
    public InGUIEventProcessor(float scaleFactor) {
        mSingleTapDetector = new GestureDetector(null, new SingleTapConfirm());
        mScaleFactor = scaleFactor;
    }
    @Override
    public boolean processTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mTracker.startTracking(motionEvent);
                if(!touchpadDisplayed()) {
                    sendTouchCoordinates(motionEvent.getX(), motionEvent.getY());
                    enableMouse();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                int pointerCount = motionEvent.getPointerCount();
                int pointerIndex = mTracker.trackEvent(motionEvent);
                if(pointerCount == 1 || LauncherPreferences.PREF_DISABLE_GESTURES) {
                    if(touchpadDisplayed()) {
                        mTouchpad.applyMotionVector(mTracker.getMotionVector());
                    }else {
                        float mainPointerX = motionEvent.getX(pointerIndex);
                        float mainPointerY = motionEvent.getY(pointerIndex);
                        sendTouchCoordinates(mainPointerX, mainPointerY);
                        if(!mIsMouseDown) enableMouse();
                    }
                } else performScroll();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                resetScrollOvershoot();
                mTracker.cancelTracking();
                if(mIsMouseDown) disableMouse();
        }
        if(touchpadDisplayed() && mSingleTapDetector.onTouchEvent(motionEvent)) clickMouse();
        return true;
    }

    private boolean touchpadDisplayed() {
        return mTouchpad != null && mTouchpad.getDisplayState();
    }

    public void setAbstractTouchpad(AbstractTouchpad touchpad) {
        mTouchpad = touchpad;
    }

    private void performScroll() {
        float[] motionVector = mTracker.getMotionVector();
        float hScroll = (motionVector[0] / FINGER_SCROLL_THRESHOLD) + mScrollOvershootH;
        float vScroll = (motionVector[1] / FINGER_SCROLL_THRESHOLD) + mScrollOvershootV;
        int hScrollRound = (int) hScroll, vScrollRound = (int) vScroll;
        if(hScrollRound != 0 || vScrollRound != 0) CallbackBridge.sendScroll(hScroll, vScroll);
        mScrollOvershootH = hScroll - hScrollRound;
        mScrollOvershootV = vScroll - vScrollRound;
    }

    private void resetScrollOvershoot() {
        mScrollOvershootH = mScrollOvershootV = 0f;
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

    private void clickMouse() {
        CallbackBridge.sendMouseButton(LwjglGlfwKeycode.GLFW_MOUSE_BUTTON_LEFT, true);
        CallbackBridge.sendMouseButton(LwjglGlfwKeycode.GLFW_MOUSE_BUTTON_LEFT, false);
    }

    @Override
    public void cancelPendingActions() {
        resetScrollOvershoot();
        disableMouse();
    }
}
