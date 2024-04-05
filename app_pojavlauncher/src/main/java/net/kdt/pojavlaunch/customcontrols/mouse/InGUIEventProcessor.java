package net.kdt.pojavlaunch.customcontrols.mouse;

import android.view.MotionEvent;

import net.kdt.pojavlaunch.LwjglGlfwKeycode;
import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.prefs.LauncherPreferences;

import org.lwjgl.glfw.CallbackBridge;

public class InGUIEventProcessor implements TouchEventProcessor {
    private final PointerTracker mTracker = new PointerTracker();
    private boolean mIsMouseDown = false;
    private final float mScaleFactor;
    public static final int FINGER_SCROLL_THRESHOLD = (int) Tools.dpToPx(6);
    public InGUIEventProcessor(float scaleFactor) {
        mScaleFactor = scaleFactor;
    }
    @Override
    public boolean processTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mTracker.startTracking(motionEvent);
                sendTouchCoordinates(motionEvent.getX(), motionEvent.getY());
                enableMouse();
                break;
            case MotionEvent.ACTION_MOVE:
                int pointerCount = motionEvent.getPointerCount();
                int pointerIndex = mTracker.trackEvent(motionEvent);
                float mainPointerX = motionEvent.getX(pointerIndex);
                float mainPointerY = motionEvent.getY(pointerIndex);
                if(pointerCount == 1 || LauncherPreferences.PREF_DISABLE_GESTURES) {
                    sendTouchCoordinates(mainPointerX, mainPointerY);
                    if(!mIsMouseDown) enableMouse();
                } else {
                    float[] motionVector = mTracker.getMotionVector();
                    int hScroll =  ((int) motionVector[0]) / FINGER_SCROLL_THRESHOLD;
                    int vScroll = ((int) motionVector[1]) / FINGER_SCROLL_THRESHOLD;
                    if(hScroll != 0 | vScroll != 0) CallbackBridge.sendScroll(hScroll, vScroll);
                }
                break;
            case MotionEvent.ACTION_UP:
                disableMouse();
        }
        return true;
    }

    private void sendTouchCoordinates(float x, float y) {
        CallbackBridge.mouseX = x * mScaleFactor;
        CallbackBridge.mouseY = y * mScaleFactor;
        CallbackBridge.sendCursorPos(CallbackBridge.mouseX, CallbackBridge.mouseY);
    }

    private void enableMouse() {
        CallbackBridge.sendMouseButton(LwjglGlfwKeycode.GLFW_MOUSE_BUTTON_LEFT, true);
        mIsMouseDown = true;
    }

    private void disableMouse() {
        CallbackBridge.sendMouseButton(LwjglGlfwKeycode.GLFW_MOUSE_BUTTON_LEFT, false);
        mIsMouseDown = false;
    }

    @Override
    public void cancelPendingActions() {
        disableMouse();
    }
}
