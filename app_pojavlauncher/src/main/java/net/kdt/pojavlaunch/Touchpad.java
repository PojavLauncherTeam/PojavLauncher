package net.kdt.pojavlaunch;

import static net.kdt.pojavlaunch.MinecraftGLSurface.FINGER_SCROLL_THRESHOLD;
import static net.kdt.pojavlaunch.Tools.currentDisplayMetrics;
import static net.kdt.pojavlaunch.prefs.LauncherPreferences.DEFAULT_PREF;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import net.kdt.pojavlaunch.prefs.LauncherPreferences;

import org.lwjgl.glfw.CallbackBridge;

/**
 * Class dealing with the virtual mouse
 */
public class Touchpad extends FrameLayout {
    /* Whether the Touchpad should be displayed */
    private boolean mDisplayState;
    /* Mouse pointer icon used by the touchpad */
    private final ImageView mMousePointerImageView = new ImageView(getContext());
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
    private final Runnable mGrabRunnable = new Runnable() {
        @Override
        public void run() {
            if (!CallbackBridge.isGrabbing() && mDisplayState && getVisibility() != VISIBLE) {
                enable();
            }else{
                if ((CallbackBridge.isGrabbing() && getVisibility() != View.GONE) || !mDisplayState && getVisibility() == VISIBLE) {
                    disable();
                }
            }
            postDelayed(this, 250);
        }
    };

    public Touchpad(@NonNull Context context) {
        this(context, null);
    }

    public Touchpad(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

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
        float mouseX = mMousePointerImageView.getX();
        float mouseY = mMousePointerImageView.getY();

        if (mSingleTapDetector.onTouchEvent(event)) {
            CallbackBridge.mouseX = (mouseX * mScaleFactor);
            CallbackBridge.mouseY = (mouseY * mScaleFactor);
            CallbackBridge.sendCursorPos(CallbackBridge.mouseX, CallbackBridge.mouseY);
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
                    int hScroll =  ((int) (event.getX() - mScrollLastInitialX)) / FINGER_SCROLL_THRESHOLD;
                    int vScroll = ((int) (event.getY() - mScrollLastInitialY)) / FINGER_SCROLL_THRESHOLD;

                    if(vScroll != 0 || hScroll != 0){
                        CallbackBridge.sendScroll(hScroll, vScroll);
                        mScrollLastInitialX = event.getX();
                        mScrollLastInitialY = event.getY();
                    }
                    break;
                }

                // Mouse movement
                if(mCurrentPointerID == event.getPointerId(0)) {
                    mouseX = Math.max(0, Math.min(currentDisplayMetrics.widthPixels, mouseX + (x - mPrevX) * LauncherPreferences.PREF_MOUSESPEED));
                    mouseY = Math.max(0, Math.min(currentDisplayMetrics.heightPixels, mouseY + (y - mPrevY) * LauncherPreferences.PREF_MOUSESPEED));

                    placeMouseAt(mouseX, mouseY);
                    CallbackBridge.sendCursorPos(CallbackBridge.mouseX, CallbackBridge.mouseY);
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

        //debugText.setText(CallbackBridge.DEBUG_STRING.toString());
        CallbackBridge.DEBUG_STRING.setLength(0);

        return true;
    }

    /** Enable the touchpad */
    public void enable(){
        setVisibility(VISIBLE);
        placeMouseAt(currentDisplayMetrics.widthPixels / 2, currentDisplayMetrics.heightPixels / 2);
    }

    /** Disable the touchpad and hides the mouse */
    public void disable(){
        setVisibility(GONE);
    }

    /** @return The new state, enabled or disabled */
    public boolean switchState(){
        mDisplayState = !mDisplayState;
        return mDisplayState;
    }

    public void placeMouseAt(float x, float y) {
        mMousePointerImageView.setX(x);
        mMousePointerImageView.setY(y);
        CallbackBridge.mouseX = (x * mScaleFactor);
        CallbackBridge.mouseY = (y * mScaleFactor);
        CallbackBridge.sendCursorPos(CallbackBridge.mouseX, CallbackBridge.mouseY);
    }

    private void init(){
        // Setup mouse pointer
        mMousePointerImageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_mouse_pointer, getContext().getTheme()));
        mMousePointerImageView.post(() -> {
            ViewGroup.LayoutParams params = mMousePointerImageView.getLayoutParams();
            params.width = (int) (36 / 100f * LauncherPreferences.PREF_MOUSESCALE);
            params.height = (int) (54 / 100f * LauncherPreferences.PREF_MOUSESCALE);
        });
        addView(mMousePointerImageView);
        setFocusable(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setDefaultFocusHighlightEnabled(false);
        }

        // When the game is grabbing, we should not display the mouse
        disable();
        mDisplayState = false;
        postDelayed(mGrabRunnable, 250);
    }

}
