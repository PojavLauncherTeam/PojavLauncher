package net.kdt.pojavlaunch.customcontrols.mouse;

import static net.kdt.pojavlaunch.utils.MCOptionUtils.getMcScale;

import static org.lwjgl.glfw.CallbackBridge.sendKeyPress;
import static org.lwjgl.glfw.CallbackBridge.sendMouseButton;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.MotionEvent;

import androidx.annotation.NonNull;

import net.kdt.pojavlaunch.LwjglGlfwKeycode;
import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.prefs.LauncherPreferences;
import net.kdt.pojavlaunch.utils.MCOptionUtils;
import net.kdt.pojavlaunch.utils.MathUtils;

import org.lwjgl.glfw.CallbackBridge;

public class IngameEventProcessor implements TouchEventProcessor {

    private int mGuiScale;
    @SuppressWarnings("FieldCanBeLocal") // it can't, otherwise the weak reference will disappear
    private final MCOptionUtils.MCOptionListener mGuiScaleListener = () -> mGuiScale = getMcScale();
    public static final int FINGER_STILL_THRESHOLD = (int) Tools.dpToPx(9);
    public static final int MSG_LEFT_MOUSE_BUTTON_CHECK = 1028;
    public static final int MSG_DROP_ITEM_BUTTON_CHECK = 1029;
    private final Handler mGestureHandler =  new Handler(Looper.getMainLooper()) {
        public void handleMessage(@NonNull Message msg) {IngameEventProcessor.this.handleMessage(msg);}
    };

    private static final int[] HOTBAR_KEYS = {
            LwjglGlfwKeycode.GLFW_KEY_1, LwjglGlfwKeycode.GLFW_KEY_2,   LwjglGlfwKeycode.GLFW_KEY_3,
            LwjglGlfwKeycode.GLFW_KEY_4, LwjglGlfwKeycode.GLFW_KEY_5,   LwjglGlfwKeycode.GLFW_KEY_6,
            LwjglGlfwKeycode.GLFW_KEY_7, LwjglGlfwKeycode.GLFW_KEY_8, LwjglGlfwKeycode.GLFW_KEY_9};
    private float mGestureStartX, mGestureStartY;
    private boolean mHasPendingLongpressGesture;
    private boolean mHasPendingDropGesture;
    private boolean mGestureTriggered;
    private int mLastHudKey;
    private final float mScaleFactor;
    private final double mSensitivity;
    private final PointerTracker mTracker = new PointerTracker();

    public IngameEventProcessor(float scaleFactor, double sensitivity) {
        MCOptionUtils.addMCOptionListener(mGuiScaleListener);
        mScaleFactor = scaleFactor;
        mSensitivity = sensitivity;
    }

    @Override
    public boolean processTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mTracker.startTracking(motionEvent);
                break;
            case MotionEvent.ACTION_MOVE:
                mTracker.trackEvent(motionEvent);
                float[] motionVector = mTracker.getMotionVector();
                CallbackBridge.mouseX += motionVector[0] * mSensitivity;
                CallbackBridge.mouseY += motionVector[1] * mSensitivity;
                CallbackBridge.sendCursorPos(CallbackBridge.mouseX, CallbackBridge.mouseY);
                boolean hasGuiBarHit = handleGuiBar(motionEvent);
                if(LauncherPreferences.PREF_DISABLE_GESTURES) break;
                checkLongpressGesture();
                checkGuiBarGesture(hasGuiBarHit);
                break;
            case MotionEvent.ACTION_UP:
                cancelGestures();
        }
        return true;
    }

    @Override
    public void cancelPendingActions() {
        cancelGestures();
    }

    private void cancelGestures() {
        cancelLongpressGesture();
        cancelDropGesture();
        if(mGestureTriggered) sendMouseButton(LwjglGlfwKeycode.GLFW_MOUSE_BUTTON_LEFT, false);
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

    private void checkGuiBarGesture(boolean hasGuiBarHit) {
        if(hasGuiBarHit && !mHasPendingDropGesture) submitDropGesture();
        if(!hasGuiBarHit) cancelDropGesture();
    }

    private void checkLongpressGesture() {
        if(mHasPendingLongpressGesture &&
                MathUtils.dist(CallbackBridge.mouseX, CallbackBridge.mouseY, mGestureStartX, mGestureStartY)
                        >= FINGER_STILL_THRESHOLD) {
            cancelLongpressGesture();
        }
        if(!mHasPendingLongpressGesture) submitLongpressGesture();
    }

    private void cancelLongpressGesture() {
        mGestureHandler.removeMessages(MSG_LEFT_MOUSE_BUTTON_CHECK);
        mHasPendingLongpressGesture = false;
    }

    private void submitLongpressGesture() {
        mGestureStartX = CallbackBridge.mouseX;
        mGestureStartY = CallbackBridge.mouseY;
        mGestureHandler.sendEmptyMessageDelayed(MSG_LEFT_MOUSE_BUTTON_CHECK, LauncherPreferences.PREF_LONGPRESS_TRIGGER);
        mHasPendingLongpressGesture = true;
    }

    private void cancelDropGesture() {
        mGestureHandler.removeMessages(MSG_DROP_ITEM_BUTTON_CHECK);
        mHasPendingDropGesture = false;
    }

    private void submitDropGesture() {
        mGestureHandler.sendEmptyMessageDelayed(MSG_DROP_ITEM_BUTTON_CHECK, 350);
        mHasPendingDropGesture = true;
    }

    /** @return the hotbar key, given the position. -1 if no key are pressed */
    public int handleGuiBar(int x, int y) {
        if (!CallbackBridge.isGrabbing()) return -1;

        int barHeight = mcscale(20);
        int barY = CallbackBridge.physicalHeight - barHeight;
        if(y < barY) return -1;

        int barWidth = mcscale(180);
        int barX = (CallbackBridge.physicalWidth / 2) - (barWidth / 2);
        if(x < barX || x >= barX + barWidth) return -1;

        return HOTBAR_KEYS[(int) net.kdt.pojavlaunch.utils.MathUtils.map(x, barX, barX + barWidth, 0, 9)];
    }

    /** Return the size, given the UI scale size */
    private int mcscale(int input) {
        return (int)((mGuiScale * input)/ mScaleFactor);
    }

    private void handleMessage(Message message) {
        switch (message.what) {
            case MSG_LEFT_MOUSE_BUTTON_CHECK:
                float x = CallbackBridge.mouseX;
                float y = CallbackBridge.mouseY;
                if (MathUtils.dist(x, y, mGestureStartX, mGestureStartY) < FINGER_STILL_THRESHOLD) {
                    sendMouseButton(LwjglGlfwKeycode.GLFW_MOUSE_BUTTON_LEFT, true);
                    mGestureTriggered = true;
                }
                return;
            case MSG_DROP_ITEM_BUTTON_CHECK:
                sendKeyPress(LwjglGlfwKeycode.GLFW_KEY_Q);
                mGestureHandler.sendEmptyMessageDelayed(MSG_DROP_ITEM_BUTTON_CHECK, 600);
        }
    }
}
