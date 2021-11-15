package net.kdt.pojavlaunch;

import static net.kdt.pojavlaunch.BaseMainActivity.sendKeyPress;
import static net.kdt.pojavlaunch.BaseMainActivity.sendMouseButton;

import android.app.Activity;
import android.content.*;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.*;
import android.view.*;

import net.kdt.pojavlaunch.prefs.LauncherPreferences;

import org.lwjgl.glfw.CallbackBridge;

public class MinecraftGLView extends TextureView {

    /* Resolution scaler option, allow downsizing a window */
    private float scaleFactor = 1f;
    /* Display properties, such as resolution and DPI */
    private DisplayMetrics displayMetrics = Tools.getDisplayMetrics((Activity) getContext());
    /* Sensitivity, adjusted according to screen size */
    private double sensitivityFactor = (1.4 * (1080f/ displayMetrics.heightPixels));
    /* Use to detect simple and double taps */
    private final TapDetector singleTapDetector;
    private final TapDetector doubleTapDetector;


    /* Last hotbar button (0-9) registered */
    private int lastHotbarKey = -1;
    /* Events can start with only a move instead of an pointerDown due to mouse passthrough */
    private boolean shouldBeDown = false;
    /* When fingers are really near to each other, it tends to either swap or remove a pointer ! */
    private int lastPointerCount = 0;
    /* Mouse positions, scaled by the scaleFactor */
    private float mouse_x, mouse_y;
    /* Previous MotionEvent position, not scale */
    private float prevX, prevY;
    /* PointerID used for the moving camera */
    private int currentPointerID = -1000;
    /* Initial first pointer positions non-scaled, used to test touch sloppiness */
    private float initialX, initialY;
    /* Last first pointer positions non-scaled, used to scroll distance */
    private float scrollLastInitialX, scrollLastInitialY;
    /* How much distance a finger has to go for touch sloppiness to be disabled */
    private final int fingerStillThreshold = (int) Tools.dpToPx(9);
    /* How much distance a finger has to go to scroll */
    private final int fingerScrollThreshold = (int) Tools.dpToPx(6);
    /* Handle hotbar throw button and mouse mining button */
    public static final int MSG_LEFT_MOUSE_BUTTON_CHECK = 1028;
    public static final int MSG_DROP_ITEM_BUTTON_CHECK = 1029;
    private final Handler theHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            if(msg.what == MSG_LEFT_MOUSE_BUTTON_CHECK) {
                if (LauncherPreferences.PREF_DISABLE_GESTURES) return;
                float x = CallbackBridge.mouseX;
                float y = CallbackBridge.mouseY;
                if (CallbackBridge.isGrabbing() &&
                        Math.abs(initialX - x) < fingerStillThreshold &&
                        Math.abs(initialY - y) < fingerStillThreshold) {
                    triggeredLeftMouseButton = true;
                    sendMouseButton(LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_LEFT, true);
                }
                return;
            }
            if(msg.what == MSG_DROP_ITEM_BUTTON_CHECK) {
                sendKeyPress(LWJGLGLFWKeycode.GLFW_KEY_Q);
                theHandler.sendEmptyMessageDelayed(MSG_DROP_ITEM_BUTTON_CHECK, 600);
                return;
            }

        }
    };
    /* Whether the button was triggered, used by the handler */
    private static boolean triggeredLeftMouseButton = false;


    public MinecraftGLView(Context context) {
        this(context, null);
    }

    public MinecraftGLView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        //Fixes freeform and dex mode having transparent glass,
        //since it forces android to used the background color of the view/layout behind it.
        setOpaque(false);
        setFocusable(true);

        singleTapDetector = new TapDetector(1, TapDetector.DETECTION_METHOD_BOTH);
        doubleTapDetector = new TapDetector(2, TapDetector.DETECTION_METHOD_DOWN);
    }



    /**
     * The touch event for both grabbed an non-grabbed mouse state
     * Does not cover the virtual mouse touchpad
     */
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        //Looking for a mouse to handle, won't have an effect if no mouse exists.
        for (int i = 0; i < e.getPointerCount(); i++) {
            if (e.getToolType(i) == MotionEvent.TOOL_TYPE_MOUSE) {

                if(CallbackBridge.isGrabbing()) return false;
                CallbackBridge.sendCursorPos(   e.getX(i) * scaleFactor, e.getY(i) * scaleFactor);
                return true; //mouse event handled successfully
            }
        }

        // System.out.println("Pre touch, isTouchInHotbar=" + Boolean.toString(isTouchInHotbar) + ", action=" + MotionEvent.actionToString(e.getActionMasked()));

        //Getting scaled position from the event
        /* Tells if a double tap happened [MOUSE GRAB ONLY]. Doesn't tell where though. */
        boolean hasDoubleTapped = false;
        if(!CallbackBridge.isGrabbing()) {
            mouse_x =  (e.getX() * scaleFactor);
            mouse_y =  (e.getY() * scaleFactor);
            //One android click = one MC click
            if(singleTapDetector.onTouchEvent(e)){
                CallbackBridge.putMouseEventWithCoords(rightOverride ? (byte) 1 : (byte) 0, (int)mouse_x, (int)mouse_y);
                return true;
            }
        }else{
            hasDoubleTapped = doubleTapDetector.onTouchEvent(e);
        }

        switch (e.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: // 0
                //shouldBeDown = true;
                CallbackBridge.sendPrepareGrabInitialPos();


                int hudKeyHandled = handleGuiBar((int)e.getX(), (int) e.getY());
                boolean isTouchInHotbar = hudKeyHandled != -1;
                if (isTouchInHotbar) {
                    sendKeyPress(hudKeyHandled);
                    if(hasDoubleTapped && hudKeyHandled == lastHotbarKey){
                        //Prevent double tapping Event on two different slots
                        sendKeyPress(LWJGLGLFWKeycode.GLFW_KEY_F);
                    }

                    theHandler.sendEmptyMessageDelayed(MSG_DROP_ITEM_BUTTON_CHECK, 350);
                    CallbackBridge.sendCursorPos(mouse_x, mouse_y);
                    lastHotbarKey = hudKeyHandled;
                    break;
                }

                CallbackBridge.sendCursorPos(mouse_x, mouse_y);
                prevX =  e.getX();
                prevY =  e.getY();

                if (CallbackBridge.isGrabbing()) {
                    currentPointerID = e.getPointerId(0);
                    // It cause hold left mouse while moving camera
                    initialX = mouse_x;
                    initialY = mouse_y;
                    theHandler.sendEmptyMessageDelayed(MSG_LEFT_MOUSE_BUTTON_CHECK, LauncherPreferences.PREF_LONGPRESS_TRIGGER);
                }
                lastHotbarKey = hudKeyHandled;
                break;

            case MotionEvent.ACTION_UP: // 1
            case MotionEvent.ACTION_CANCEL: // 3
                shouldBeDown = false;
                currentPointerID = -1;

                hudKeyHandled = handleGuiBar((int)e.getX(), (int) e.getY());
                isTouchInHotbar = hudKeyHandled != -1;

                if (CallbackBridge.isGrabbing()) {
                    if (!isTouchInHotbar && !triggeredLeftMouseButton && Math.abs(initialX - mouse_x) < fingerStillThreshold && Math.abs(initialY - mouse_y) < fingerStillThreshold) {
                        if (!LauncherPreferences.PREF_DISABLE_GESTURES) {
                            sendMouseButton(LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_RIGHT, true);
                            sendMouseButton(LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_RIGHT, false);
                        }
                    }
                    if (!isTouchInHotbar) {
                        if (triggeredLeftMouseButton) sendMouseButton(LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_LEFT, false);

                        triggeredLeftMouseButton = false;
                        theHandler.removeMessages(MSG_LEFT_MOUSE_BUTTON_CHECK);
                    } else {
                        sendKeyPress(LWJGLGLFWKeycode.GLFW_KEY_Q, 0, false);
                        theHandler.removeMessages(MSG_DROP_ITEM_BUTTON_CHECK);
                    }
                }

                break;

            case MotionEvent.ACTION_POINTER_DOWN: // 5
                scrollLastInitialX = e.getX();
                scrollLastInitialY = e.getY();
                //Checking if we are pressing the hotbar to select the item
                hudKeyHandled = handleGuiBar((int)e.getX(e.getPointerCount()-1), (int) e.getY(e.getPointerCount()-1));
                if(hudKeyHandled != -1){
                    sendKeyPress(hudKeyHandled);
                    if(hasDoubleTapped && hudKeyHandled == lastHotbarKey){
                        //Prevent double tapping Event on two different slots
                        sendKeyPress(LWJGLGLFWKeycode.GLFW_KEY_F);
                    }
                }

                lastHotbarKey = hudKeyHandled;
                break;

            case MotionEvent.ACTION_MOVE:
                if (!CallbackBridge.isGrabbing() && e.getPointerCount() >= 2 && !LauncherPreferences.PREF_DISABLE_GESTURES) { //Scrolling feature
                    int hScroll =  ((int) (e.getX() - scrollLastInitialX)) / fingerScrollThreshold;
                    int vScroll = ((int) (e.getY() - scrollLastInitialY)) / fingerScrollThreshold;

                    if(vScroll != 0 || hScroll != 0){
                        CallbackBridge.sendScroll(hScroll, vScroll);
                        scrollLastInitialX = e.getX();
                        scrollLastInitialY = e.getY();
                    }


                } else if (!CallbackBridge.isGrabbing() && e.getPointerCount() == 1) { //Touch hover
                    CallbackBridge.sendCursorPos(mouse_x, mouse_y);
                    prevX =  e.getX();
                    prevY =  e.getY();
                } else {
                    //Camera movement
                    if (CallbackBridge.isGrabbing()) {
                        int pointerIndex = e.findPointerIndex(currentPointerID);
                        if (pointerIndex == -1 || lastPointerCount != e.getPointerCount() || !shouldBeDown) {
                            shouldBeDown = true;

                            hudKeyHandled = handleGuiBar((int)e.getX(), (int) e.getY());
                            if(hudKeyHandled != -1) break; //No camera movement on hotbar

                            currentPointerID = e.getPointerId(0);
                            prevX = e.getX();
                            prevY = e.getY();
                        } else {
                            hudKeyHandled = handleGuiBar((int)e.getX(), (int) e.getY());
                            if(hudKeyHandled == -1){ //No camera on hotbar
                                mouse_x += (e.getX(pointerIndex) - prevX) * sensitivityFactor;
                                mouse_y += (e.getY(pointerIndex) - prevY) * sensitivityFactor;
                            }

                            prevX = e.getX(pointerIndex);
                            prevY = e.getY(pointerIndex);

                            CallbackBridge.sendCursorPos(mouse_x, mouse_y);
                        }

                    }
                }



                lastPointerCount = e.getPointerCount();
                break;
        }

        debugText.setText(CallbackBridge.DEBUG_STRING.toString());
        CallbackBridge.DEBUG_STRING.setLength(0);

        return true;
    }
}
