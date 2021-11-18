package net.kdt.pojavlaunch;

import static net.kdt.pojavlaunch.BaseMainActivity.sendKeyPress;
import static net.kdt.pojavlaunch.BaseMainActivity.sendMouseButton;
import static net.kdt.pojavlaunch.utils.MCOptionUtils.getMcScale;

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
import android.widget.Toast;

import com.google.android.material.math.MathUtils;

import net.kdt.pojavlaunch.prefs.LauncherPreferences;
import net.kdt.pojavlaunch.utils.JREUtils;
import net.kdt.pojavlaunch.utils.MCOptionUtils;

import org.lwjgl.glfw.CallbackBridge;

/**
 * Class dealing with showing minecraft surface and taking inputs and dispatching them to minecraft
 */
public class MinecraftGLView extends TextureView {

    /* Resolution scaler option, allow downsizing a window */
    private final float scaleFactor = LauncherPreferences.DEFAULT_PREF.getInt("resolutionRatio",100)/100f;
    /* Display properties, such as resolution and DPI */
    private final DisplayMetrics displayMetrics = Tools.getDisplayMetrics((Activity) getContext());
    /* Sensitivity, adjusted according to screen size */
    private final double sensitivityFactor = (1.4 * (1080f/ displayMetrics.heightPixels));
    /* Use to detect simple and double taps */
    private final TapDetector singleTapDetector = new TapDetector(1, TapDetector.DETECTION_METHOD_BOTH);
    private final TapDetector doubleTapDetector  = new TapDetector(2, TapDetector.DETECTION_METHOD_DOWN);
    /* MC GUI scale, listened by MCOptionUtils */
    private int GUIScale = getMcScale();
    private MCOptionUtils.MCOptionListener GUIScaleListener = () -> GUIScale = getMcScale();
    /* Surface ready listener, used by the activity to launch minecraft */
    SurfaceReadyListener surfaceReadyListener = null;

    /* List of hotbarKeys, used when clicking on the hotbar */
    private static final int[] hotbarKeys = {
            LWJGLGLFWKeycode.GLFW_KEY_1, LWJGLGLFWKeycode.GLFW_KEY_2,   LWJGLGLFWKeycode.GLFW_KEY_3,
            LWJGLGLFWKeycode.GLFW_KEY_4, LWJGLGLFWKeycode.GLFW_KEY_5,   LWJGLGLFWKeycode.GLFW_KEY_6,
            LWJGLGLFWKeycode.GLFW_KEY_7, LWJGLGLFWKeycode.GLFW_KEY_8, LWJGLGLFWKeycode.GLFW_KEY_9};
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
    public static final int FINGER_STILL_THRESHOLD = (int) Tools.dpToPx(9);
    /* How much distance a finger has to go to scroll */
    public static final int FINGER_SCROLL_THRESHOLD = (int) Tools.dpToPx(6);
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
                        MathUtils.dist(x, y, mouse_x, mouse_y) < FINGER_STILL_THRESHOLD) {
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

        MCOptionUtils.addMCOptionListener(GUIScaleListener);
    }


    public void init(){
        setSurfaceTextureListener(new SurfaceTextureListener() {
            private boolean isCalled = false;
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height) {
                windowWidth = Tools.getDisplayFriendlyRes(width, scaleFactor);
                windowHeight = Tools.getDisplayFriendlyRes(height, scaleFactor);
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
                        if(surfaceReadyListener != null){
                            surfaceReadyListener.isReady();
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
                windowWidth = Tools.getDisplayFriendlyRes(width, scaleFactor);
                windowHeight = Tools.getDisplayFriendlyRes(height, scaleFactor);
                CallbackBridge.sendUpdateWindowSize(windowWidth, windowHeight);
                getMcScale();
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture texture) {
                texture.setDefaultBufferSize(windowWidth, windowHeight);
            }
        });
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
        if(!CallbackBridge.isGrabbing()) {
            mouse_x =  (e.getX() * scaleFactor);
            mouse_y =  (e.getY() * scaleFactor);
            //One android click = one MC click
            if(singleTapDetector.onTouchEvent(e)){
                CallbackBridge.putMouseEventWithCoords(LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_LEFT, mouse_x, mouse_y);
                return true;
            }
        }

        // Check double tap state, used for the hotbar
        boolean hasDoubleTapped = doubleTapDetector.onTouchEvent(e);

        switch (e.getActionMasked()) {
            case MotionEvent.ACTION_MOVE:
                int pointerCount = e.getPointerCount();

                // In-menu interactions
                if(!CallbackBridge.isGrabbing()){

                    // Touch hover
                    if(pointerCount == 1){
                        CallbackBridge.sendCursorPos(mouse_x, mouse_y);
                        prevX =  e.getX();
                        prevY =  e.getY();
                        break;
                    }

                    // Scrolling feature
                    if(LauncherPreferences.PREF_DISABLE_GESTURES) break;
                    // The pointer count can never be 0, and it is not 1, therefore it is >= 2
                    int hScroll =  ((int) (e.getX() - scrollLastInitialX)) / FINGER_SCROLL_THRESHOLD;
                    int vScroll = ((int) (e.getY() - scrollLastInitialY)) / FINGER_SCROLL_THRESHOLD;

                    if(vScroll != 0 || hScroll != 0){
                        CallbackBridge.sendScroll(hScroll, vScroll);
                        scrollLastInitialX = e.getX();
                        scrollLastInitialY = e.getY();
                    }
                    break;
                }

                // Camera movement
                int pointerIndex = e.findPointerIndex(currentPointerID);
                int hudKeyHandled = handleGuiBar((int)e.getX(), (int) e.getY());
                // Start movement, due to new pointer or loss of pointer
                if (pointerIndex == -1 || lastPointerCount != pointerCount || !shouldBeDown) {
                    if(hudKeyHandled != -1) break; //No pointer attribution on hotbar

                    shouldBeDown = true;
                    currentPointerID = e.getPointerId(0);
                    prevX = e.getX();
                    prevY = e.getY();
                    break;
                }
                // Continue movement as usual
                if(hudKeyHandled == -1){ //No camera on hotbar
                    mouse_x += (e.getX(pointerIndex) - prevX) * sensitivityFactor;
                    mouse_y += (e.getY(pointerIndex) - prevY) * sensitivityFactor;
                }

                prevX = e.getX(pointerIndex);
                prevY = e.getY(pointerIndex);

                CallbackBridge.sendCursorPos(mouse_x, mouse_y);
                break;

            case MotionEvent.ACTION_DOWN: // 0
                CallbackBridge.sendPrepareGrabInitialPos();

                hudKeyHandled = handleGuiBar((int)e.getX(), (int) e.getY());
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
                // We only treat in world events
                if (!CallbackBridge.isGrabbing()) break;

                // Stop the dropping of items
                if (isTouchInHotbar) {
                    sendKeyPress(LWJGLGLFWKeycode.GLFW_KEY_Q, 0, false);
                    theHandler.removeMessages(MSG_DROP_ITEM_BUTTON_CHECK);
                    break;
                }

                // Remove the mouse left button
                if(triggeredLeftMouseButton){
                    sendMouseButton(LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_LEFT, false);
                    triggeredLeftMouseButton = false;
                    theHandler.removeMessages(MSG_LEFT_MOUSE_BUTTON_CHECK);
                    break;
                }

                // In case of a short click, just send a quick right click
                if(!LauncherPreferences.PREF_DISABLE_GESTURES &&
                        MathUtils.dist(initialX, initialY, mouse_x, mouse_y) < FINGER_STILL_THRESHOLD){
                    sendMouseButton(LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_RIGHT, true);
                    sendMouseButton(LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_RIGHT, false);
                }
                break;

            case MotionEvent.ACTION_POINTER_DOWN: // 5
                scrollLastInitialX = e.getX();
                scrollLastInitialY = e.getY();
                //Checking if we are pressing the hotbar to select the item
                hudKeyHandled = handleGuiBar((int)e.getX(e.getPointerCount()-1), (int) e.getY(e.getPointerCount()-1));
                if(hudKeyHandled == -1){
                    sendKeyPress(hudKeyHandled);
                    if(hasDoubleTapped && hudKeyHandled == lastHotbarKey){
                        //Prevent double tapping Event on two different slots
                        sendKeyPress(LWJGLGLFWKeycode.GLFW_KEY_F);
                    }
                }

                lastHotbarKey = hudKeyHandled;
                break;

        }

        // Actualise the pointer count
        lastPointerCount = e.getPointerCount();

        //debugText.setText(CallbackBridge.DEBUG_STRING.toString());
        CallbackBridge.DEBUG_STRING.setLength(0);

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

        return hotbarKeys[((x - barX) / barWidth / 9) % 9];
    }

    /** Return the size, given the UI scale size */
    private int mcscale(int input) {
        return (int)((GUIScale * input)/scaleFactor);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return super.dispatchKeyEvent(event);
    }


    /** A small interface called when the listener is ready for the first time */
    public interface SurfaceReadyListener {
        void isReady();
    }

    public void setSurfaceReadyListener(SurfaceReadyListener listener){
        surfaceReadyListener = listener;
    }
}
