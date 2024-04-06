package net.kdt.pojavlaunch.customcontrols.mouse;

import android.view.MotionEvent;

import net.kdt.pojavlaunch.LwjglGlfwKeycode;
import net.kdt.pojavlaunch.prefs.LauncherPreferences;
import net.kdt.pojavlaunch.utils.MCOptionUtils;

import org.lwjgl.glfw.CallbackBridge;

import static net.kdt.pojavlaunch.utils.MCOptionUtils.getMcScale;

import android.os.Handler;

public class HotbarTracker implements MCOptionUtils.MCOptionListener {

    private static final int[] HOTBAR_KEYS = {
            LwjglGlfwKeycode.GLFW_KEY_1, LwjglGlfwKeycode.GLFW_KEY_2,   LwjglGlfwKeycode.GLFW_KEY_3,
            LwjglGlfwKeycode.GLFW_KEY_4, LwjglGlfwKeycode.GLFW_KEY_5,   LwjglGlfwKeycode.GLFW_KEY_6,
            LwjglGlfwKeycode.GLFW_KEY_7, LwjglGlfwKeycode.GLFW_KEY_8, LwjglGlfwKeycode.GLFW_KEY_9};
    private int mLastHudKey, mHudPointerId;
    private final DropGesture mDropGesture;
    private int mBarWidth, mBarHeight;
    private final float mScaleFactor;

    /**
     * @param mGestureHandler the gesture handler for the integrated drop gesture
     * @param mScaleFactor the screen scale factor
     */
    public HotbarTracker(Handler mGestureHandler, float mScaleFactor) {
        computeBarDimensions();
        MCOptionUtils.addMCOptionListener(this);
        this.mScaleFactor = mScaleFactor;
        mDropGesture = new DropGesture(mGestureHandler);
        mHudPointerId = -1;
    }

    public boolean begin(MotionEvent motionEvent) {
        if(mHudPointerId != -1) return false;
        int pointer = motionEvent.getActionIndex();
        if(motionEvent.getActionMasked() == MotionEvent.ACTION_DOWN) pointer = 0;
        int x = (int)motionEvent.getX(pointer);
        if(isWithinHotbar(x, (int)motionEvent.getY(pointer))) {
            mHudPointerId = motionEvent.getPointerId(pointer);
            hotbarClick(x);
            mDropGesture.submit(true);
            return true;
        }else {
            mHudPointerId = -1;
            return false;
        }
    }

    public boolean track(MotionEvent motionEvent, int trackedIndex, boolean hasDoubleTapped) {
        if(mHudPointerId == -1) return false;
        int index = motionEvent.findPointerIndex(mHudPointerId);
        if(index == -1) {
            cancel();
            return false;
        }
        int x = (int)motionEvent.getX(index);
        if(isWithinHotbar(x, (int)motionEvent.getY(index))) {
            hotbarClick(x);
            mDropGesture.submit(true);
            if(hasDoubleTapped && !LauncherPreferences.PREF_DISABLE_SWAP_HAND) {
                CallbackBridge.sendKeyPress(LwjglGlfwKeycode.GLFW_KEY_F);
            }
        }else {
            mDropGesture.submit(false);
        }
        return trackedIndex == index;
    }

    public void cancel() {
        mDropGesture.cancel();
        mHudPointerId = -1;
    }

    private boolean isWithinHotbar(int x, int y) {
        int barY = CallbackBridge.physicalHeight - mBarHeight;
        if(y < barY) return false;

        int barX = (CallbackBridge.physicalWidth / 2) - (mBarWidth / 2);
        return x >= barX && x < barX + mBarWidth;
    }

    private void hotbarClick(int x) {
        int barX = (CallbackBridge.physicalWidth / 2) - (mBarWidth / 2);
        if(x < barX || x >= barX + mBarWidth) return;

        int key = HOTBAR_KEYS[(int) net.kdt.pojavlaunch.utils.MathUtils.map(x, barX, barX + mBarWidth, 0, 9)];
        if(key != mLastHudKey) {
            CallbackBridge.sendKeyPress(key);
            // The GUI bar is handled before the gesture will be submitted, so this
            // will be resubmitted again soon (with the timer restarted)
            mDropGesture.cancel();
            mLastHudKey = key;
        }
    }

    private int mcScale(int input, int guiScale) {
        return (int)((guiScale * input)/ mScaleFactor);
    }

    private void computeBarDimensions() {
        int guiScale = getMcScale();
        mBarHeight = mcScale(20, guiScale);
        mBarWidth = mcScale(180, guiScale);
    }
    @Override
    public void onOptionChanged() {
        computeBarDimensions();
    }
}
