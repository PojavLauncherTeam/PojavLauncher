package net.kdt.pojavlaunch;

import static net.kdt.pojavlaunch.BaseMainActivity.touchCharInput;
import static net.kdt.pojavlaunch.utils.MCOptionUtils.getMcScale;

import static org.lwjgl.glfw.CallbackBridge.sendKeyPress;
import static org.lwjgl.glfw.CallbackBridge.sendMouseButton;
import static org.lwjgl.glfw.CallbackBridge.windowHeight;
import static org.lwjgl.glfw.CallbackBridge.windowWidth;

import android.app.Activity;
import android.content.*;
import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.*;
import android.view.*;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.google.android.material.math.MathUtils;

import net.kdt.pojavlaunch.customcontrols.gamepad.Gamepad;
import net.kdt.pojavlaunch.prefs.LauncherPreferences;
import net.kdt.pojavlaunch.utils.JREUtils;
import net.kdt.pojavlaunch.utils.MCOptionUtils;

import org.lwjgl.glfw.CallbackBridge;

/**
 * Class dealing with showing minecraft surface and taking inputs to dispatch them to minecraft
 */
public class MinecraftGLView extends TextureView {
    /* How much distance a finger has to go for touch sloppiness to be disabled */
    public static final int FINGER_STILL_THRESHOLD = (int) Tools.dpToPx(9);
    /* How much distance a finger has to go to scroll */
    public static final int FINGER_SCROLL_THRESHOLD = (int) Tools.dpToPx(6);
    /* Handle hotbar throw button and mouse mining button */
    public static final int MSG_LEFT_MOUSE_BUTTON_CHECK = 1028;
    public static final int MSG_DROP_ITEM_BUTTON_CHECK = 1029;
    
    /* Gamepad object for gamepad inputs, instantiated on need */
    private Gamepad mGamepad = null;
    /* Pointer Debug textview, used to show info about the pointer state */
    private TextView mPointerDebugTextView;
    /* Resolution scaler option, allow downsizing a window */
    private final float mScaleFactor = LauncherPreferences.DEFAULT_PREF.getInt("resolutionRatio",100)/100f;
    /* Display properties, such as resolution and DPI */
    private final DisplayMetrics mDisplayMetrics = Tools.getDisplayMetrics((Activity) getContext());
    /* Sensitivity, adjusted according to screen size */
    private final double mSensitivityFactor = (1.4 * (1080f/ mDisplayMetrics.heightPixels));
    /* Use to detect simple and double taps */
    private final TapDetector mSingleTapDetector = new TapDetector(1, TapDetector.DETECTION_METHOD_BOTH);
    private final TapDetector mDoubleTapDetector = new TapDetector(2, TapDetector.DETECTION_METHOD_DOWN);
    /* MC GUI scale, listened by MCOptionUtils */
    private int mGuiScale = getMcScale();
    private MCOptionUtils.MCOptionListener mGuiScaleListener = () -> mGuiScale = getMcScale();
    /* Surface ready listener, used by the activity to launch minecraft */
    SurfaceReadyListener mSurfaceReadyListener = null;

    /* List of hotbarKeys, used when clicking on the hotbar */
    private static final int[] sHotbarKeys = {
            LwjglGlfwKeycode.GLFW_KEY_1, LwjglGlfwKeycode.GLFW_KEY_2,   LwjglGlfwKeycode.GLFW_KEY_3,
            LwjglGlfwKeycode.GLFW_KEY_4, LwjglGlfwKeycode.GLFW_KEY_5,   LwjglGlfwKeycode.GLFW_KEY_6,
            LwjglGlfwKeycode.GLFW_KEY_7, LwjglGlfwKeycode.GLFW_KEY_8, LwjglGlfwKeycode.GLFW_KEY_9};
    /* Last hotbar button (0-9) registered */
    private int mLastHotbarKey = -1;
    /* Events can start with only a move instead of an pointerDown due to mouse passthrough */
    private boolean mShouldBeDown = false;
    /* When fingers are really near to each other, it tends to either swap or remove a pointer ! */
    private int mLastPointerCount = 0;
    /* Previous MotionEvent position, not scale */
    private float mPrevX, mPrevY;
    /* PointerID used for the moving camera */
    private int mCurrentPointerID = -1000;
    /* Initial first pointer positions non-scaled, used to test touch sloppiness */
    private float mInitialX, mInitialY;
    /* Last first pointer positions non-scaled, used to scroll distance */
    private float mScrollLastInitialX, mScrollLastInitialY;
    /* Handle hotbar throw button and mouse mining button */
    private final Handler theHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            if(msg.what == MSG_LEFT_MOUSE_BUTTON_CHECK) {
                if (LauncherPreferences.PREF_DISABLE_GESTURES) return;
                float x = CallbackBridge.mouseX;
                float y = CallbackBridge.mouseY;
                if (CallbackBridge.isGrabbing() &&
                        MathUtils.dist(x, y, mInitialX, mInitialY) < FINGER_STILL_THRESHOLD) {
                    mTriggeredLeftMouseButton = true;
                    sendMouseButton(LwjglGlfwKeycode.GLFW_MOUSE_BUTTON_LEFT, true);
                }
                return;
            }
            if(msg.what == MSG_DROP_ITEM_BUTTON_CHECK) {
                if(CallbackBridge.isGrabbing()){
                    sendKeyPress(LwjglGlfwKeycode.GLFW_KEY_Q);
                    theHandler.sendEmptyMessageDelayed(MSG_DROP_ITEM_BUTTON_CHECK, 600);
                }
                return;
            }

        }
    };
    /* Whether the button was triggered, used by the handler */
    private boolean mTriggeredLeftMouseButton = false;
    /* Whether the pointer debug has failed at some point */
    private boolean debugErrored = false;


    public MinecraftGLView(Context context) {
        this(context, null);
    }

    public MinecraftGLView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        //Fixes freeform and dex mode having transparent glass,
        //since it forces android to used the background color of the view/layout behind it.
        setOpaque(false);
        setFocusable(true);

        MCOptionUtils.addMCOptionListener(mGuiScaleListener);
    }


    /**
     * The touch event for both grabbed an non-grabbed mouse state on the touch screen
     * Does not cover the virtual mouse touchpad
     */
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // Looking for a mouse to handle, won't have an effect if no mouse exists.
        for (int i = 0; i < e.getPointerCount(); i++) {
            if (e.getToolType(i) != MotionEvent.TOOL_TYPE_MOUSE) continue;

            // Mouse found
            if(CallbackBridge.isGrabbing()) return false;
            CallbackBridge.sendCursorPos(   e.getX(i) * mScaleFactor, e.getY(i) * mScaleFactor);
            return true; //mouse event handled successfully
        }

        // System.out.println("Pre touch, isTouchInHotbar=" + Boolean.toString(isTouchInHotbar) + ", action=" + MotionEvent.actionToString(e.getActionMasked()));

        //Getting scaled position from the event
        /* Tells if a double tap happened [MOUSE GRAB ONLY]. Doesn't tell where though. */
        if(!CallbackBridge.isGrabbing()) {
            CallbackBridge.mouseX =  (e.getX() * mScaleFactor);
            CallbackBridge.mouseY =  (e.getY() * mScaleFactor);
            //One android click = one MC click
            if(mSingleTapDetector.onTouchEvent(e)){
                CallbackBridge.putMouseEventWithCoords(LwjglGlfwKeycode.GLFW_MOUSE_BUTTON_LEFT, CallbackBridge.mouseX, CallbackBridge.mouseY);
                return true;
            }
        }

        // Check double tap state, used for the hotbar
        boolean hasDoubleTapped = mDoubleTapDetector.onTouchEvent(e);

        switch (e.getActionMasked()) {
            case MotionEvent.ACTION_MOVE:
                int pointerCount = e.getPointerCount();

                // In-menu interactions
                if(!CallbackBridge.isGrabbing()){

                    // Touch hover
                    if(pointerCount == 1){
                        CallbackBridge.sendCursorPos(CallbackBridge.mouseX, CallbackBridge.mouseY);
                        mPrevX =  e.getX();
                        mPrevY =  e.getY();
                        break;
                    }

                    // Scrolling feature
                    if(LauncherPreferences.PREF_DISABLE_GESTURES) break;
                    // The pointer count can never be 0, and it is not 1, therefore it is >= 2
                    int hScroll =  ((int) (e.getX() - mScrollLastInitialX)) / FINGER_SCROLL_THRESHOLD;
                    int vScroll = ((int) (e.getY() - mScrollLastInitialY)) / FINGER_SCROLL_THRESHOLD;

                    if(vScroll != 0 || hScroll != 0){
                        CallbackBridge.sendScroll(hScroll, vScroll);
                        mScrollLastInitialX = e.getX();
                        mScrollLastInitialY = e.getY();
                    }
                    break;
                }

                // Camera movement
                int pointerIndex = e.findPointerIndex(mCurrentPointerID);
                int hudKeyHandled = handleGuiBar((int)e.getX(), (int) e.getY());
                // Start movement, due to new pointer or loss of pointer
                if (pointerIndex == -1 || mLastPointerCount != pointerCount || !mShouldBeDown) {
                    if(hudKeyHandled != -1) break; //No pointer attribution on hotbar

                    mShouldBeDown = true;
                    mCurrentPointerID = e.getPointerId(0);
                    mPrevX = e.getX();
                    mPrevY = e.getY();
                    break;
                }
                // Continue movement as usual
                if(hudKeyHandled == -1){ //No camera on hotbar
                    CallbackBridge.mouseX += (e.getX(pointerIndex) - mPrevX) * mSensitivityFactor;
                    CallbackBridge.mouseY += (e.getY(pointerIndex) - mPrevY) * mSensitivityFactor;
                }

                mPrevX = e.getX(pointerIndex);
                mPrevY = e.getY(pointerIndex);

                CallbackBridge.sendCursorPos(CallbackBridge.mouseX, CallbackBridge.mouseY);
                break;

            case MotionEvent.ACTION_DOWN: // 0
                CallbackBridge.sendPrepareGrabInitialPos();

                hudKeyHandled = handleGuiBar((int)e.getX(), (int) e.getY());
                boolean isTouchInHotbar = hudKeyHandled != -1;
                if (isTouchInHotbar) {
                    sendKeyPress(hudKeyHandled);
                    if(hasDoubleTapped && hudKeyHandled == mLastHotbarKey){
                        //Prevent double tapping Event on two different slots
                        sendKeyPress(LwjglGlfwKeycode.GLFW_KEY_F);
                    }

                    theHandler.sendEmptyMessageDelayed(MSG_DROP_ITEM_BUTTON_CHECK, 350);
                    CallbackBridge.sendCursorPos(CallbackBridge.mouseX, CallbackBridge.mouseY);
                    mLastHotbarKey = hudKeyHandled;
                    break;
                }

                CallbackBridge.sendCursorPos(CallbackBridge.mouseX, CallbackBridge.mouseY);
                mPrevX =  e.getX();
                mPrevY =  e.getY();

                if (CallbackBridge.isGrabbing()) {
                    mCurrentPointerID = e.getPointerId(0);
                    // It cause hold left mouse while moving camera
                    mInitialX = CallbackBridge.mouseX;
                    mInitialY = CallbackBridge.mouseY;
                    theHandler.sendEmptyMessageDelayed(MSG_LEFT_MOUSE_BUTTON_CHECK, LauncherPreferences.PREF_LONGPRESS_TRIGGER);
                }
                mLastHotbarKey = hudKeyHandled;
                break;

            case MotionEvent.ACTION_UP: // 1
            case MotionEvent.ACTION_CANCEL: // 3
                mShouldBeDown = false;
                mCurrentPointerID = -1;

                hudKeyHandled = handleGuiBar((int)e.getX(), (int) e.getY());
                isTouchInHotbar = hudKeyHandled != -1;
                // We only treat in world events
                if (!CallbackBridge.isGrabbing()) break;

                // Stop the dropping of items
                if (isTouchInHotbar) {
                    sendKeyPress(LwjglGlfwKeycode.GLFW_KEY_Q, 0, false);
                    theHandler.removeMessages(MSG_DROP_ITEM_BUTTON_CHECK);
                    break;
                }

                // Remove the mouse left button
                if(mTriggeredLeftMouseButton){
                    sendMouseButton(LwjglGlfwKeycode.GLFW_MOUSE_BUTTON_LEFT, false);
                    mTriggeredLeftMouseButton = false;
                    break;
                }
                theHandler.removeMessages(MSG_LEFT_MOUSE_BUTTON_CHECK);

                // In case of a short click, just send a quick right click
                if(!LauncherPreferences.PREF_DISABLE_GESTURES &&
                        MathUtils.dist(mInitialX, mInitialY, CallbackBridge.mouseX, CallbackBridge.mouseY) < FINGER_STILL_THRESHOLD){
                    sendMouseButton(LwjglGlfwKeycode.GLFW_MOUSE_BUTTON_RIGHT, true);
                    sendMouseButton(LwjglGlfwKeycode.GLFW_MOUSE_BUTTON_RIGHT, false);
                }
                break;

            case MotionEvent.ACTION_POINTER_DOWN: // 5
                //TODO Hey we could have some sort of middle click detection ?
                
                mScrollLastInitialX = e.getX();
                mScrollLastInitialY = e.getY();
                //Checking if we are pressing the hotbar to select the item
                hudKeyHandled = handleGuiBar((int)e.getX(e.getPointerCount()-1), (int) e.getY(e.getPointerCount()-1));
                if(hudKeyHandled != -1){
                    sendKeyPress(hudKeyHandled);
                    if(hasDoubleTapped && hudKeyHandled == mLastHotbarKey){
                        //Prevent double tapping Event on two different slots
                        sendKeyPress(LwjglGlfwKeycode.GLFW_KEY_F);
                    }
                }

                mLastHotbarKey = hudKeyHandled;
                break;

        }

        // Actualise the pointer count
        mLastPointerCount = e.getPointerCount();

        //debugText.setText(CallbackBridge.DEBUG_STRING.toString());
        CallbackBridge.DEBUG_STRING.setLength(0);

        return true;
    }

    /**
     * The event for mouse/joystick movements
     * We don't do the gamepad right now.
     */
    @Override
    public boolean dispatchGenericMotionEvent(MotionEvent event) {
        int mouseCursorIndex = -1;

        if(Gamepad.isGamepadEvent(event)){
            if(mGamepad == null){
                mGamepad = new Gamepad(this, event.getDevice());
            }

            mGamepad.update(event);
            return true;
        }

        for(int i = 0; i < event.getPointerCount(); i++) {
            if(event.getToolType(i) != MotionEvent.TOOL_TYPE_MOUSE) continue;
            // Mouse found
            mouseCursorIndex = i;
            break;
        }
        if(mouseCursorIndex == -1) return false; // we cant consoom that, theres no mice!
        if(CallbackBridge.isGrabbing()) {
            if(BaseMainActivity.isAndroid8OrHigher()){
                requestFocus();
                requestPointerCapture();
            }
        }
        switch(event.getActionMasked()) {
            case MotionEvent.ACTION_HOVER_MOVE:
                CallbackBridge.mouseX = (event.getX(mouseCursorIndex) * mScaleFactor);
                CallbackBridge.mouseY = (event.getY(mouseCursorIndex) * mScaleFactor);
                CallbackBridge.sendCursorPos(CallbackBridge.mouseX, CallbackBridge.mouseY);
                //debugText.setText(CallbackBridge.DEBUG_STRING.toString());
                CallbackBridge.DEBUG_STRING.setLength(0);
                return true;
            case MotionEvent.ACTION_SCROLL:
                CallbackBridge.sendScroll((double) event.getAxisValue(MotionEvent.AXIS_VSCROLL), (double) event.getAxisValue(MotionEvent.AXIS_HSCROLL));
                return true;
            case MotionEvent.ACTION_BUTTON_PRESS:
                return sendMouseButtonUnconverted(event.getActionButton(),true);
            case MotionEvent.ACTION_BUTTON_RELEASE:
                return sendMouseButtonUnconverted(event.getActionButton(),false);
            default:
                return false;
        }
    }



    /** The input event for mouse with a captured pointer */
    @RequiresApi(26)
    @Override
    public boolean dispatchCapturedPointerEvent(MotionEvent e) {
        CallbackBridge.mouseX += (e.getX()* mScaleFactor);
        CallbackBridge.mouseY += (e.getY()* mScaleFactor);
        if(!CallbackBridge.isGrabbing()){
            releasePointerCapture();
            clearFocus();
        }

        if (mPointerDebugTextView.getVisibility() == View.VISIBLE && !debugErrored) {
            StringBuilder builder = new StringBuilder();
            try {
                builder.append("PointerCapture debug\n");
                builder.append("MotionEvent=").append(e.getActionMasked()).append("\n");
                builder.append("PressingBtn=").append(MotionEvent.class.getDeclaredMethod("buttonStateToString").invoke(null, e.getButtonState())).append("\n\n");

                builder.append("PointerX=").append(e.getX()).append("\n");
                builder.append("PointerY=").append(e.getY()).append("\n");
                builder.append("RawX=").append(e.getRawX()).append("\n");
                builder.append("RawY=").append(e.getRawY()).append("\n\n");

                builder.append("XPos=").append(CallbackBridge.mouseX).append("\n");
                builder.append("YPos=").append(CallbackBridge.mouseY).append("\n\n");
                builder.append("MovingX=").append(getMoving(e.getX(), true)).append("\n");
                builder.append("MovingY=").append(getMoving(e.getY(), false)).append("\n");
            } catch (Throwable th) {
                debugErrored = true;
                builder.append("Error getting debug. The debug will be stopped!\n").append(Log.getStackTraceString(th));
            } finally {
                mPointerDebugTextView.setText(builder.toString());
                builder.setLength(0);
            }
        }

        mPointerDebugTextView.setText(CallbackBridge.DEBUG_STRING.toString());
        CallbackBridge.DEBUG_STRING.setLength(0);
        switch (e.getActionMasked()) {
            case MotionEvent.ACTION_MOVE:
                CallbackBridge.sendCursorPos(CallbackBridge.mouseX, CallbackBridge.mouseY);
                return true;
            case MotionEvent.ACTION_BUTTON_PRESS:
                return sendMouseButtonUnconverted(e.getActionButton(), true);
            case MotionEvent.ACTION_BUTTON_RELEASE:
                return sendMouseButtonUnconverted(e.getActionButton(), false);
            case MotionEvent.ACTION_SCROLL:
                CallbackBridge.sendScroll(e.getAxisValue(MotionEvent.AXIS_HSCROLL), e.getAxisValue(MotionEvent.AXIS_VSCROLL));
                return true;
            default:
                return false;
        }
    }

    /** The event for keyboard/ gamepad button inputs */
    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        //Toast.makeText(this, event.toString(),Toast.LENGTH_SHORT).show();
        //Toast.makeText(this, event.getDevice().toString(), Toast.LENGTH_SHORT).show();

        //Filtering useless events by order of probability
        if((event.getFlags() & KeyEvent.FLAG_FALLBACK) == KeyEvent.FLAG_FALLBACK) return true;
        int eventKeycode = event.getKeyCode();
        if(eventKeycode == KeyEvent.KEYCODE_UNKNOWN) return true;
        if(eventKeycode == KeyEvent.KEYCODE_VOLUME_DOWN) return false;
        if(eventKeycode == KeyEvent.KEYCODE_VOLUME_UP) return false;
        if(event.getRepeatCount() != 0) return true;
        if(event.getAction() == KeyEvent.ACTION_MULTIPLE) return true;

        //Toast.makeText(this, "FIRST VERIF PASSED", Toast.LENGTH_SHORT).show();

        //Sometimes, key events comes from SOME keys of the software keyboard
        //Even weirder, is is unknown why a key or another is selected to trigger a keyEvent
        if((event.getFlags() & KeyEvent.FLAG_SOFT_KEYBOARD) == KeyEvent.FLAG_SOFT_KEYBOARD){
            if(eventKeycode == KeyEvent.KEYCODE_ENTER) return true; //We already listen to it.
            touchCharInput.dispatchKeyEvent(event);
            return true;
        }
        //Toast.makeText(this, "SECOND VERIF PASSED", Toast.LENGTH_SHORT).show();


        //Sometimes, key events may come from the mouse
        if(event.getDevice() != null
                && ( (event.getSource() & InputDevice.SOURCE_MOUSE_RELATIVE) == InputDevice.SOURCE_MOUSE_RELATIVE
                ||   (event.getSource() & InputDevice.SOURCE_MOUSE) == InputDevice.SOURCE_MOUSE)  ){
            //Toast.makeText(this, "THE EVENT COMES FROM A MOUSE", Toast.LENGTH_SHORT).show();


            if(eventKeycode == KeyEvent.KEYCODE_BACK){
                sendMouseButton(LwjglGlfwKeycode.GLFW_MOUSE_BUTTON_RIGHT, event.getAction() == KeyEvent.ACTION_DOWN);
                return true;
            }
        }
        System.out.println(event);

        if(Gamepad.isGamepadEvent(event)){

            if(mGamepad == null){
                mGamepad = new Gamepad(this, event.getDevice());
            }

            mGamepad.update(event);
            return true;
        }

        int index = EfficientAndroidLWJGLKeycode.getIndexByKey(eventKeycode);
        if(index >= 0) {
            //Toast.makeText(this,"THIS IS A KEYBOARD EVENT !", Toast.LENGTH_SHORT).show();
            EfficientAndroidLWJGLKeycode.execKey(event, index);
            return true;
        }

        return false;
    }


    /** Initialize the view and all its settings */
    public void start(){
        // Add the pointer debug textview
        mPointerDebugTextView = new TextView(getContext());
        mPointerDebugTextView.setX(0);
        mPointerDebugTextView.setY(0);
        mPointerDebugTextView.setVisibility(GONE);
        ((ViewGroup)getParent()).addView(mPointerDebugTextView);

        setSurfaceTextureListener(new SurfaceTextureListener() {
            private boolean isCalled = false;
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height) {
                windowWidth = Tools.getDisplayFriendlyRes(width, mScaleFactor);
                windowHeight = Tools.getDisplayFriendlyRes(height, mScaleFactor);
                texture.setDefaultBufferSize(windowWidth, windowHeight);

                //Load Minecraft options:
                MCOptionUtils.load();
                MCOptionUtils.set("overrideWidth", String.valueOf(windowWidth));
                MCOptionUtils.set("overrideHeight", String.valueOf(windowHeight));
                MCOptionUtils.save();
                getMcScale();
                // Should we do that?
                if(isCalled) return;
                isCalled = true;

                JREUtils.setupBridgeWindow(new Surface(texture));

                new Thread(() -> {
                    try {
                        Thread.sleep(200);
                        if(mSurfaceReadyListener != null){
                            mSurfaceReadyListener.isReady();
                        }
                    } catch (Throwable e) {
                        Tools.showError(getContext(), e, true);
                    }
                }, "JVM Main thread").start();
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {
                return true;
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int width, int height) {
                windowWidth = Tools.getDisplayFriendlyRes(width, mScaleFactor);
                windowHeight = Tools.getDisplayFriendlyRes(height, mScaleFactor);
                CallbackBridge.sendUpdateWindowSize(windowWidth, windowHeight);
                getMcScale();
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture texture) {
                texture.setDefaultBufferSize(windowWidth, windowHeight);
            }
        });
    }

    /** Convert the mouse button, then send it
     * @return Whether the event was processed
     */
    public static boolean sendMouseButtonUnconverted(int button, boolean status) {
        int glfwButton = -256;
        switch (button) {
            case MotionEvent.BUTTON_PRIMARY:
                glfwButton = LwjglGlfwKeycode.GLFW_MOUSE_BUTTON_LEFT;
                break;
            case MotionEvent.BUTTON_TERTIARY:
                glfwButton = LwjglGlfwKeycode.GLFW_MOUSE_BUTTON_MIDDLE;
                break;
            case MotionEvent.BUTTON_SECONDARY:
                glfwButton = LwjglGlfwKeycode.GLFW_MOUSE_BUTTON_RIGHT;
                break;
        }
        if(glfwButton == -256) return false;
        sendMouseButton(glfwButton, status);
        return true;
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

        return sHotbarKeys[(int) net.kdt.pojavlaunch.utils.MathUtils.map(x, barX, barX + barWidth, 0, 9)];
    }

    /** Toggle the pointerDebugText visibility state */
    public void togglepointerDebugging() {
        mPointerDebugTextView.setVisibility(mPointerDebugTextView.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
    }

    /** A small interface called when the listener is ready for the first time */
    public interface SurfaceReadyListener {
        void isReady();
    }

    public void setSurfaceReadyListener(SurfaceReadyListener listener){
        mSurfaceReadyListener = listener;
    }


    /** Return the size, given the UI scale size */
    private int mcscale(int input) {
        return (int)((mGuiScale * input)/ mScaleFactor);
    }

    /** Get the mouse direction as a string */
    private String getMoving(float pos, boolean xOrY) {
        if (pos == 0) return "STOPPED";
        if (pos > 0) return xOrY ? "RIGHT" : "DOWN";
        return xOrY ? "LEFT" : "UP";
    }
}
