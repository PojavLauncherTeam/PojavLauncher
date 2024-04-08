package net.kdt.pojavlaunch;

import static net.kdt.pojavlaunch.Tools.currentDisplayMetrics;
import static net.kdt.pojavlaunch.customcontrols.mouse.InGUIEventProcessor.FINGER_SCROLL_THRESHOLD;
import static net.kdt.pojavlaunch.prefs.LauncherPreferences.DEFAULT_PREF;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import net.kdt.pojavlaunch.prefs.LauncherPreferences;

import org.lwjgl.glfw.CallbackBridge;

/**
 * Class dealing with the virtual mouse
 */
public class Touchpad extends View implements GrabListener {
    /* Whether the Touchpad should be displayed */
    private boolean mDisplayState;
    /* Mouse pointer icon used by the touchpad */
    private Drawable mMousePointerDrawable;
    private float mMouseX, mMouseY;
    /* Detect a classic android Tap */
    private final GestureDetector mSingleTapDetector = new GestureDetector(getContext(), new SingleTapConfirm());
    /* Resolution scaler option, allow downsizing a window */
    private final float mScaleFactor = DEFAULT_PREF.getInt("resolutionRatio",100)/100f;
    /* Current pointer ID to move the mouse */
    private int mCurrentPointerID = -1000;
    /* Previous MotionEvent position, not scale */
    private float mPrevX, mPrevY;
    /* Last first pointer positions non-scaled, used to scroll distance */
    private float mScrollLastInitialX, mScrollLastInitialY;
    /* Handler and message to check if we are grabbing */

    public Touchpad(@NonNull Context context) {
        this(context, null);
    }

    public Touchpad(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @SuppressWarnings("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.
        // int index = event.getActionIndex();
        if(CallbackBridge.isGrabbing()) {
            disable();
            return false;
        }
        int action = event.getActionMasked();

        float x = event.getX();
        float y = event.getY();

        if (mSingleTapDetector.onTouchEvent(event)) {
            sendMousePosition();
            CallbackBridge.sendMouseKeycode(LwjglGlfwKeycode.GLFW_MOUSE_BUTTON_LEFT);
            return true;
        }

        switch (action) {
            case MotionEvent.ACTION_POINTER_DOWN: // 5
                mScrollLastInitialX = event.getX();
                mScrollLastInitialY = event.getY();
                break;

            case MotionEvent.ACTION_DOWN:
                mPrevX = x;
                mPrevY = y;
                mCurrentPointerID = event.getPointerId(0);
                break;

            case MotionEvent.ACTION_MOVE: // 2
                //Scrolling feature
                if (!LauncherPreferences.PREF_DISABLE_GESTURES && !CallbackBridge.isGrabbing() && event.getPointerCount() >= 2) {
                    int hScroll =  (int) ((x - mScrollLastInitialX) / FINGER_SCROLL_THRESHOLD);
                    int vScroll = (int) ((y - mScrollLastInitialY) / FINGER_SCROLL_THRESHOLD);

                    if(vScroll != 0 || hScroll != 0){
                        CallbackBridge.sendScroll(hScroll, vScroll);
                        mScrollLastInitialX = x;
                        mScrollLastInitialY = y;
                    }
                    break;
                }

                // Mouse movement
                if(mCurrentPointerID == event.getPointerId(0)) {
                    mMouseX = Math.max(0, Math.min(currentDisplayMetrics.widthPixels, mMouseX + (x - mPrevX) * LauncherPreferences.PREF_MOUSESPEED));
                    mMouseY = Math.max(0, Math.min(currentDisplayMetrics.heightPixels, mMouseY + (y - mPrevY) * LauncherPreferences.PREF_MOUSESPEED));
                    updateMousePosition();
                }else mCurrentPointerID = event.getPointerId(0);

                mPrevX = x;
                mPrevY = y;
                break;

            case MotionEvent.ACTION_UP:
                mPrevX = x;
                mPrevY = y;
                mCurrentPointerID = -1000;
                break;
        }

        return true;
    }

    /** Enable the touchpad */
    public void enable(){
        setVisibility(VISIBLE);
        placeMouseAt(currentDisplayMetrics.widthPixels / 2f, currentDisplayMetrics.heightPixels / 2f);
    }

    /** Disable the touchpad and hides the mouse */
    public void disable(){
        setVisibility(GONE);
    }

    /** @return The new state, enabled or disabled */
    public boolean switchState(){
        mDisplayState = !mDisplayState;
        if(!CallbackBridge.isGrabbing()) {
            if(mDisplayState) enable();
            else disable();
        }
        return mDisplayState;
    }

    public void placeMouseAt(float x, float y) {
        mMouseX = x;
        mMouseY = y;
        updateMousePosition();
    }

    private void sendMousePosition() {
        CallbackBridge.mouseX = (mMouseX * mScaleFactor);
        CallbackBridge.mouseY = (mMouseY * mScaleFactor);
        CallbackBridge.sendCursorPos(CallbackBridge.mouseX, CallbackBridge.mouseY);
    }

    private void updateMousePosition() {
        sendMousePosition();
        // I wanted to implement a dirty rect for this, but it is ignored since API level 21
        // (which is our min API)
        // Let's hope the "internally calculated area" is good enough.
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.translate(mMouseX, mMouseY);
        mMousePointerDrawable.draw(canvas);
    }

    private void init(){
        // Setup mouse pointer
        mMousePointerDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_mouse_pointer, getContext().getTheme());
        // For some reason it's annotated as Nullable even though it doesn't seem to actually
        // ever return null
        assert mMousePointerDrawable != null;
        mMousePointerDrawable.setBounds(
                0, 0,
                (int) (36 / 100f * LauncherPreferences.PREF_MOUSESCALE),
                (int) (54 / 100f * LauncherPreferences.PREF_MOUSESCALE)
        );
        setFocusable(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setDefaultFocusHighlightEnabled(false);
        }

        // When the game is grabbing, we should not display the mouse
        disable();
        mDisplayState = false;
    }

    @Override
    public void onGrabState(boolean isGrabbing) {
        post(()->updateGrabState(isGrabbing));
    }
    private void updateGrabState(boolean isGrabbing) {
        if(!isGrabbing) {
            if(mDisplayState && getVisibility() != VISIBLE) enable();
            if(!mDisplayState && getVisibility() == VISIBLE) disable();
        }else{
            if(getVisibility() != View.GONE) disable();
        }
    }
}
