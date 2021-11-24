package net.kdt.pojavlaunch;

import static net.kdt.pojavlaunch.MinecraftGLView.FINGER_SCROLL_THRESHOLD;
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
    private boolean displayState;

    /* Mouse pointer icon used by the touchpad */
    private final ImageView mousePointer = new ImageView(getContext());
    /* Detect a classic android Tap */
    private final GestureDetector singleTapDetector = new GestureDetector(getContext(), new SingleTapConfirm());
    /* Mc mouse position, scaled by the scaleFactor */
    float mouse_x, mouse_y;
    /* Resolution scaler option, allow downsizing a window */
    private final float scaleFactor = DEFAULT_PREF.getInt("resolutionRatio",100)/100f;
    /* Current pointer ID to move the mouse */
    private int currentPointerID = -1000;
    /* Previous MotionEvent position, not scale */
    private float prevX, prevY;
    /* Last first pointer positions non-scaled, used to scroll distance */
    private float scrollLastInitialX, scrollLastInitialY;

    public Touchpad(@NonNull Context context) {
        this(context, null);
    }

    public Touchpad(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    private void init(){
        // Setup mouse pointer
        mousePointer.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.mouse_pointer, getContext().getTheme()));
        mousePointer.post(() -> {
            ViewGroup.LayoutParams params = mousePointer.getLayoutParams();
            params.width = (int) (36 / 100f * LauncherPreferences.PREF_MOUSESCALE);
            params.height = (int) (54 / 100f * LauncherPreferences.PREF_MOUSESCALE);
        });
        addView(mousePointer);
        setFocusable(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setDefaultFocusHighlightEnabled(false);
        }

        // When the game is grabbing, we should not display the mouse
        disable();
        displayState = false;
        Thread virtualMouseGrabThread = new Thread(() -> {
            while (true) {
                if (!CallbackBridge.isGrabbing() && displayState && getVisibility() != VISIBLE) {
                    post(this::enable);
                }else{
                    if ((CallbackBridge.isGrabbing() && getVisibility() != View.GONE) || !displayState && getVisibility() == VISIBLE) {
                        post(this::disable);
                    }
                }

            }
        }, "VirtualMouseGrabThread");
        virtualMouseGrabThread.setPriority(Thread.MIN_PRIORITY);
        virtualMouseGrabThread.start();
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
        displayState = !displayState;
        return displayState;
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
        float mouseX = mousePointer.getX();
        float mouseY = mousePointer.getY();

        if (singleTapDetector.onTouchEvent(event)) {
            mouse_x = (mouseX * scaleFactor);
            mouse_y = (mouseY * scaleFactor);
            CallbackBridge.sendCursorPos(mouse_x, mouse_y);
            CallbackBridge.sendMouseKeycode(LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_LEFT);

            return true;
        }

        switch (action) {
            case MotionEvent.ACTION_POINTER_DOWN: // 5
                scrollLastInitialX = event.getX();
                scrollLastInitialY = event.getY();
                break;

            case MotionEvent.ACTION_DOWN:
                prevX = x;
                prevY = y;
                currentPointerID = event.getPointerId(0);
                break;

            case MotionEvent.ACTION_MOVE: // 2
                //Scrolling feature
                if (!LauncherPreferences.PREF_DISABLE_GESTURES && !CallbackBridge.isGrabbing() && event.getPointerCount() >= 2) {
                    int hScroll =  ((int) (event.getX() - scrollLastInitialX)) / FINGER_SCROLL_THRESHOLD;
                    int vScroll = ((int) (event.getY() - scrollLastInitialY)) / FINGER_SCROLL_THRESHOLD;

                    if(vScroll != 0 || hScroll != 0){
                        CallbackBridge.sendScroll(hScroll, vScroll);
                        scrollLastInitialX = event.getX();
                        scrollLastInitialY = event.getY();
                    }
                    break;
                }

                // Mouse movement
                if(currentPointerID == event.getPointerId(0)) {
                    mouseX = Math.max(0, Math.min(currentDisplayMetrics.widthPixels, mouseX + (x - prevX) * LauncherPreferences.PREF_MOUSESPEED));
                    mouseY = Math.max(0, Math.min(currentDisplayMetrics.heightPixels, mouseY + (y - prevY) * LauncherPreferences.PREF_MOUSESPEED));

                    placeMouseAt(mouseX, mouseY);
                    CallbackBridge.sendCursorPos(mouse_x, mouse_y);
                }else currentPointerID = event.getPointerId(0);

                prevX = x;
                prevY = y;
                break;

            case MotionEvent.ACTION_UP:
                prevX = x;
                prevY = y;
                currentPointerID = -1000;
                break;
        }


        //debugText.setText(CallbackBridge.DEBUG_STRING.toString());
        CallbackBridge.DEBUG_STRING.setLength(0);

        return true;
    }

    public void placeMouseAt(float x, float y) {
        mousePointer.setX(x);
        mousePointer.setY(y);
        mouse_x = (x * scaleFactor);
        mouse_y = (y * scaleFactor);
        CallbackBridge.sendCursorPos(mouse_x, mouse_y);
    }

}
