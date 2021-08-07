package net.kdt.pojavlaunch;

import android.app.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.graphics.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.view.inputmethod.*;
import android.widget.*;

import androidx.drawerlayout.widget.*;
import com.google.android.material.navigation.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import net.kdt.pojavlaunch.customcontrols.*;

import net.kdt.pojavlaunch.multirt.MultiRTUtils;

import net.kdt.pojavlaunch.customcontrols.gamepad.Gamepad;

import net.kdt.pojavlaunch.prefs.*;
import net.kdt.pojavlaunch.utils.*;
import net.kdt.pojavlaunch.value.*;
import org.lwjgl.glfw.*;

public class BaseMainActivity extends LoggableActivity {
    public static volatile ClipboardManager GLOBAL_CLIPBOARD;

    volatile public static boolean isInputStackCall;

    private static final int[] hotbarKeys = {
        LWJGLGLFWKeycode.GLFW_KEY_1, LWJGLGLFWKeycode.GLFW_KEY_2,   LWJGLGLFWKeycode.GLFW_KEY_3,
        LWJGLGLFWKeycode.GLFW_KEY_4, LWJGLGLFWKeycode.GLFW_KEY_5,   LWJGLGLFWKeycode.GLFW_KEY_6,
        LWJGLGLFWKeycode.GLFW_KEY_7, LWJGLGLFWKeycode.GLFW_KEY_8, LWJGLGLFWKeycode.GLFW_KEY_9};

    private Gamepad gamepad;

    private boolean rightOverride = false;
    private DisplayMetrics displayMetrics;
    public float scaleFactor = 1;
    public double sensitivityFactor;
    private final int fingerStillThreshold = 8;
    private float initialX, initialY;
    private int scrollInitialX, scrollInitialY;
    private float prevX, prevY;
    private int currentPointerID;

    private boolean mIsResuming = false;
    private static final int MSG_LEFT_MOUSE_BUTTON_CHECK = 1028;
    private static final int MSG_DROP_ITEM_BUTTON_CHECK = 1029;
    private static boolean triggeredLeftMouseButton = false;
    private final Handler theHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (!LauncherPreferences.PREF_DISABLE_GESTURES) {
                switch (msg.what) {
                    case MSG_LEFT_MOUSE_BUTTON_CHECK: {
                        int x = CallbackBridge.mouseX;
                        int y = CallbackBridge.mouseY;
                        if (CallbackBridge.isGrabbing() &&
                                Math.abs(initialX - x) < fingerStillThreshold &&
                                Math.abs(initialY - y) < fingerStillThreshold) {
                            triggeredLeftMouseButton = true;
                            sendMouseButton(LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_LEFT, true);
                        }
                    } break;
                    case MSG_DROP_ITEM_BUTTON_CHECK: {
                        sendKeyPress(LWJGLGLFWKeycode.GLFW_KEY_Q, 0, true);
                    } break;
                }
            }
        }
    };

    private MinecraftGLView minecraftGLView;
    private int guiScale;

    public boolean hiddenTextIgnoreUpdate = true;
    
    private boolean isVirtualMouseEnabled;
    private LinearLayout touchPad;
    private ImageView mousePointer;
    private MinecraftAccount mProfile;
    
    private DrawerLayout drawerLayout;
    private NavigationView navDrawer;

    private LinearLayout contentLog;
    private TextView textLog;
    private ScrollView contentScroll;
    private ToggleButton toggleLog;
    private GestureDetector gestureDetector;

    private TextView debugText;
    private NavigationView.OnNavigationItemSelectedListener gameActionListener;
    public NavigationView.OnNavigationItemSelectedListener ingameControlsEditorListener;

    protected volatile JMinecraftVersionList.Version mVersionInfo;

    private View.OnTouchListener glTouchListener;

    private File logFile;
    private PrintStream logStream;
    private PerVersionConfig.VersionConfig config;
    private final boolean lastEnabled = false;
    private boolean lastGrab = false;
    private final boolean isExited = false;
    private boolean isLogAllow = false;

    public volatile float mouse_x, mouse_y;

    // @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void initLayout(int resId) {
        setContentView(resId);

        try {
            // FIXME: is it safe fot multi thread?
            GLOBAL_CLIPBOARD = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

            logFile = new File(Tools.DIR_GAME_HOME, "latestlog.txt");
            logFile.delete();
            logFile.createNewFile();
            logStream = new PrintStream(logFile.getAbsolutePath());
            
            mProfile = PojavProfile.getCurrentProfileContent(this);
            mVersionInfo = Tools.getVersionInfo(null,mProfile.selectedVersion);
            
            setTitle("Minecraft " + mProfile.selectedVersion);
            PerVersionConfig.update();
            config = PerVersionConfig.configMap.get(mProfile.selectedVersion);
            String runtime = LauncherPreferences.PREF_DEFAULT_RUNTIME;
            if(config != null) {
                if(config.selectedRuntime != null) {
                    if(MultiRTUtils.forceReread(config.selectedRuntime).versionString != null) {
                        runtime = config.selectedRuntime;
                    }
                }
                if(config.renderer != null) {
                    Tools.LOCAL_RENDERER = config.renderer;
                }
            }
            MultiRTUtils.setRuntimeNamed(this,runtime);
            // Minecraft 1.13+
            isInputStackCall = mVersionInfo.arguments != null;
            
            displayMetrics = Tools.getDisplayMetrics(this);
            sensitivityFactor = 1.4 * (1080f/ displayMetrics.heightPixels);
            CallbackBridge.windowWidth = (int) ((float)displayMetrics.widthPixels * scaleFactor);
            CallbackBridge.windowHeight = (int) ((float)displayMetrics.heightPixels * scaleFactor);
            System.out.println("WidthHeight: " + CallbackBridge.windowWidth + ":" + CallbackBridge.windowHeight);

            
            gestureDetector = new GestureDetector(this, new SingleTapConfirm());

            // Menu
            drawerLayout = findViewById(R.id.main_drawer_options);

            navDrawer = findViewById(R.id.main_navigation_view);
            gameActionListener = menuItem -> {
                switch (menuItem.getItemId()) {
                    case R.id.nav_forceclose: dialogForceClose(BaseMainActivity.this);
                        break;
                    case R.id.nav_viewlog: openLogOutput();
                        break;
                    case R.id.nav_debug: toggleDebug();
                        break;
                    case R.id.nav_customkey: dialogSendCustomKey();
                        break;
                    case R.id.nav_mousespd: adjustMouseSpeedLive();
                        break;
                    case R.id.nav_customctrl: openCustomControls();
                        break;
                }

                drawerLayout.closeDrawers();
                return true;
            };
            navDrawer.setNavigationItemSelectedListener(gameActionListener);

            this.touchPad = findViewById(R.id.main_touchpad);
            touchPad.setFocusable(false);
            
            this.mousePointer = findViewById(R.id.main_mouse_pointer);
            this.mousePointer.post(() -> {
                ViewGroup.LayoutParams params = mousePointer.getLayoutParams();
                params.width = (int) (36 / 100f * LauncherPreferences.PREF_MOUSESCALE);
                params.height = (int) (54 / 100f * LauncherPreferences.PREF_MOUSESCALE);
            });

            this.contentLog = findViewById(R.id.content_log_layout);
            this.contentScroll = findViewById(R.id.content_log_scroll);
            this.textLog = (TextView) contentScroll.getChildAt(0);
            this.toggleLog = findViewById(R.id.content_log_toggle_log);
            this.toggleLog.setChecked(false);

            this.textLog.setTypeface(Typeface.MONOSPACE);
            this.toggleLog.setOnCheckedChangeListener((button, isChecked) -> {
                isLogAllow = isChecked;
                appendToLog("");
            });

            this.debugText = findViewById(R.id.content_text_debug);

            this.minecraftGLView = findViewById(R.id.main_game_render_view);
            this.drawerLayout.closeDrawers();

            placeMouseAt(CallbackBridge.physicalWidth / 2, CallbackBridge.physicalHeight / 2);
            Thread virtualMouseGrabThread = new Thread(() -> {
                while (!isExited) {
                    if (lastGrab != CallbackBridge.isGrabbing())
                    mousePointer.post(() -> {
                        if (!CallbackBridge.isGrabbing() && isVirtualMouseEnabled) {
                            touchPad.setVisibility(View.VISIBLE);
                            placeMouseAt(displayMetrics.widthPixels / 2, displayMetrics.heightPixels / 2);
                        }

                        if (CallbackBridge.isGrabbing() && touchPad.getVisibility() != View.GONE) {
                            touchPad.setVisibility(View.GONE);
                        }

                        lastGrab = CallbackBridge.isGrabbing();
                    });

                }
            }, "VirtualMouseGrabThread");
            virtualMouseGrabThread.setPriority(Thread.MIN_PRIORITY);
            virtualMouseGrabThread.start();

            if (isAndroid8OrHigher()) {
                touchPad.setDefaultFocusHighlightEnabled(false);
            }
            touchPad.setOnTouchListener((v, event) -> {
                // MotionEvent reports input details from the touch screen
                // and other input controls. In this case, you are only
                // interested in events where the touch position changed.
                // int index = event.getActionIndex();
                if(CallbackBridge.isGrabbing()) {
                    minecraftGLView.dispatchTouchEvent(MotionEvent.obtain(event));
                    System.out.println("Transitioned event" + event.hashCode() + " to MinecraftGLView");
                    return false;
                }
                int action = event.getActionMasked();

                float x = event.getX();
                float y = event.getY();
                float mouseX = mousePointer.getX();
                float mouseY = mousePointer.getY();

                if (gestureDetector.onTouchEvent(event)) {
                    mouse_x = (mouseX * scaleFactor);
                    mouse_y = (mouseY * scaleFactor);
                    CallbackBridge.sendCursorPos(mouse_x, mouse_y);
                    CallbackBridge.sendMouseKeycode(rightOverride ? LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_RIGHT : LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_LEFT);
                    if (!rightOverride) CallbackBridge.mouseLeft = true;


                } else {
                    switch (action) {
                        case MotionEvent.ACTION_UP: // 1
                        case MotionEvent.ACTION_CANCEL: // 3
                            if (!rightOverride) CallbackBridge.mouseLeft = false;
                            break;

                        case MotionEvent.ACTION_POINTER_DOWN: // 5
                            scrollInitialX = CallbackBridge.mouseX;
                            scrollInitialY = CallbackBridge.mouseY;
                            break;

                        case MotionEvent.ACTION_DOWN:
                            prevX = x;
                            prevY = y;
                            currentPointerID = event.getPointerId(0);
                            break;

                        case MotionEvent.ACTION_MOVE: // 2

                            if (!CallbackBridge.isGrabbing() && event.getPointerCount() == 2 && !LauncherPreferences.PREF_DISABLE_GESTURES) { //Scrolling feature
                                CallbackBridge.sendScroll( Tools.pxToDp(CallbackBridge.mouseX - scrollInitialX)/30, Tools.pxToDp(CallbackBridge.mouseY - scrollInitialY)/30);
                                scrollInitialX = CallbackBridge.mouseX;
                                scrollInitialY = CallbackBridge.mouseY;
                            } else {
                                if(currentPointerID == event.getPointerId(0)) {
                                    mouseX = Math.max(0, Math.min(displayMetrics.widthPixels, mouseX + (x - prevX) * LauncherPreferences.PREF_MOUSESPEED));
                                    mouseY = Math.max(0, Math.min(displayMetrics.heightPixels, mouseY + (y - prevY) * LauncherPreferences.PREF_MOUSESPEED));
                                    mouse_x = (mouseX * scaleFactor);
                                    mouse_y = (mouseY * scaleFactor);
                                    placeMouseAt(mouseX, mouseY);
                                    CallbackBridge.sendCursorPos(mouse_x, mouse_y);
                                }else currentPointerID = event.getPointerId(0);

                                prevX = x;
                                prevY = y;
                            }
                            break;
                    }
                }

                debugText.setText(CallbackBridge.DEBUG_STRING.toString());
                CallbackBridge.DEBUG_STRING.setLength(0);

                return true;
            });


            minecraftGLView.setFocusable(true);
            glTouchListener = new OnTouchListener(){
                private boolean isTouchInHotbar = false;
                /*
                 * Events can start with only a move instead of an pointerDown
                 * It is due to the mouse passthrough option bundled with the control button.
                 */
                private boolean shouldBeDown = false;
                @Override
                public boolean onTouch(View p1, MotionEvent e) {

                    //Looking for a mouse to handle, won't have an effect if no mouse exists.
                    for (int i = 0; i < e.getPointerCount(); i++) {
                        if (e.getToolType(i) == MotionEvent.TOOL_TYPE_MOUSE) {

                            if(CallbackBridge.isGrabbing()) return false;
                            CallbackBridge.sendCursorPos(   e.getX(i) * scaleFactor,
                                                            e.getY(i) * scaleFactor);
                            return true; //mouse event handled successfully
                        }
                    }

                    // System.out.println("Pre touch, isTouchInHotbar=" + Boolean.toString(isTouchInHotbar) + ", action=" + MotionEvent.actionToString(e.getActionMasked()));

                    //Getting scaled position from the event
                    if(!CallbackBridge.isGrabbing()) {
                        mouse_x =  (e.getX() * scaleFactor);
                        mouse_y =  (e.getY() * scaleFactor);
                    }

                    int hudKeyHandled;
                    if (!CallbackBridge.isGrabbing() && gestureDetector.onTouchEvent(e)){
                        CallbackBridge.putMouseEventWithCoords(rightOverride ? (byte) 1 : (byte) 0, (int)mouse_x, (int)mouse_y);
                        if (!rightOverride) CallbackBridge.mouseLeft = true;
                        return true;
                    }

                    switch (e.getActionMasked()) {
                        case MotionEvent.ACTION_DOWN: // 0
                            //shouldBeDown = true;
                            CallbackBridge.sendPrepareGrabInitialPos();

                            currentPointerID = e.getPointerId(0);
                            CallbackBridge.sendCursorPos(mouse_x, mouse_y);
                            prevX =  e.getX();
                            prevY =  e.getY();


                            hudKeyHandled = handleGuiBar((int)e.getX(), (int) e.getY());
                            isTouchInHotbar = hudKeyHandled != -1;
                            if (isTouchInHotbar) {
                                sendKeyPress(hudKeyHandled);

                                theHandler.sendEmptyMessageDelayed(BaseMainActivity.MSG_DROP_ITEM_BUTTON_CHECK, LauncherPreferences.PREF_LONGPRESS_TRIGGER);
                                CallbackBridge.sendCursorPos(mouse_x, mouse_y);
                                break;
                            }

                            if (!rightOverride) CallbackBridge.mouseLeft = true;

                            if (CallbackBridge.isGrabbing()) {
                                // It cause hold left mouse while moving camera
                                initialX = mouse_x;
                                initialY = mouse_y;
                                if(!isTouchInHotbar) theHandler.sendEmptyMessageDelayed(BaseMainActivity.MSG_LEFT_MOUSE_BUTTON_CHECK, LauncherPreferences.PREF_LONGPRESS_TRIGGER);
                            }
                            break;

                        case MotionEvent.ACTION_UP: // 1
                        case MotionEvent.ACTION_CANCEL: // 3
                            shouldBeDown = false;
                            if (!isTouchInHotbar) {
                                // -TODO uncomment after fix wrong trigger
                                if (!rightOverride) CallbackBridge.mouseLeft = false;
                            }

                            if (CallbackBridge.isGrabbing()) {
                                if (!triggeredLeftMouseButton && Math.abs(initialX - mouse_x) < fingerStillThreshold && Math.abs(initialY - mouse_y) < fingerStillThreshold) {
                                    if (!LauncherPreferences.PREF_DISABLE_GESTURES) {
                                        sendMouseButton(LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_RIGHT, true);
                                        sendMouseButton(LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_RIGHT, false);
                                    }
                                }
                                if (!isTouchInHotbar) {
                                    if (triggeredLeftMouseButton) sendMouseButton(LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_LEFT, false);

                                    triggeredLeftMouseButton = false;
                                    theHandler.removeMessages(BaseMainActivity.MSG_LEFT_MOUSE_BUTTON_CHECK);
                                } else {
                                    sendKeyPress(LWJGLGLFWKeycode.GLFW_KEY_Q, 0, false);
                                    theHandler.removeMessages(MSG_DROP_ITEM_BUTTON_CHECK);
                                }
                            }

                            break;

                        case MotionEvent.ACTION_POINTER_DOWN: // 5
                            scrollInitialX = CallbackBridge.mouseX;
                            scrollInitialY = CallbackBridge.mouseY;
                            //Checking if we are pressing the hotbar to select the item
                            hudKeyHandled = handleGuiBar((int)e.getX(e.getPointerCount()-1), (int) e.getY(e.getPointerCount()-1));
                            if(hudKeyHandled != -1) sendKeyPress(hudKeyHandled);
                            break;

                        case MotionEvent.ACTION_MOVE:
                            if (!CallbackBridge.isGrabbing() && e.getPointerCount() == 2 && !LauncherPreferences.PREF_DISABLE_GESTURES) { //Scrolling feature
                                CallbackBridge.sendScroll(Tools.pxToDp(mouse_x - scrollInitialX)/30 , Tools.pxToDp(mouse_y - scrollInitialY)/30);
                                scrollInitialX = (int)mouse_x;
                                scrollInitialY = (int)mouse_y;
                            } else if (!isTouchInHotbar) {
                                //Camera movement
                                if(CallbackBridge.isGrabbing()){
                                    int pointerIndex = e.findPointerIndex(currentPointerID);
                                    if(pointerIndex == -1 || !shouldBeDown){
                                        shouldBeDown = true;
                                        currentPointerID = e.getPointerId(0);
                                    }else{
                                        mouse_x += (e.getX(pointerIndex) - prevX) * sensitivityFactor;
                                        mouse_y += (e.getY(pointerIndex) - prevY) * sensitivityFactor;
                                    }
                                    prevX = e.getX();
                                    prevY = e.getY();
                                }

                                CallbackBridge.sendCursorPos(mouse_x, mouse_y);
                            }
                            break;
                    }

                    debugText.setText(CallbackBridge.DEBUG_STRING.toString());
                    CallbackBridge.DEBUG_STRING.setLength(0);

                    return true;
                }
            };
            
            if (isAndroid8OrHigher()) {
                minecraftGLView.setDefaultFocusHighlightEnabled(false);
                minecraftGLView.setOnCapturedPointerListener(new View.OnCapturedPointerListener() {
                    //private int x, y;
                    private boolean debugErrored = false;

                    private String getMoving(float pos, boolean xOrY) {
                        if (pos == 0) return "STOPPED";
                        if (pos > 0) return xOrY ? "RIGHT" : "DOWN";
                        return xOrY ? "LEFT" : "UP";
                    }

                    @Override
                    public boolean onCapturedPointer (View view, MotionEvent e) {
                            if(e.getHistorySize() > 0) {
                                mouse_x += (e.getX()*scaleFactor);
                                mouse_y += (e.getY()*scaleFactor);
                            }
                            CallbackBridge.mouseX = (int) mouse_x;
                            CallbackBridge.mouseY = (int) mouse_y;
                            if(!CallbackBridge.isGrabbing()){
                                view.releasePointerCapture();
                            }

                            if (debugText.getVisibility() == View.VISIBLE && !debugErrored) {
                                StringBuilder builder = new StringBuilder();
                                try {
                                    builder.append("PointerCapture debug\n");
                                    builder.append("MotionEvent=").append(e.getActionMasked()).append("\n");
                                    builder.append("PressingBtn=").append(MotionEvent.class.getDeclaredMethod("buttonStateToString").invoke(null, e.getButtonState())).append("\n\n");

                                    builder.append("PointerX=").append(e.getX()).append("\n");
                                    builder.append("PointerY=").append(e.getY()).append("\n");
                                    builder.append("RawX=").append(e.getRawX()).append("\n");
                                    builder.append("RawY=").append(e.getRawY()).append("\n\n");

                                    builder.append("XPos=").append(mouse_x).append("\n");
                                    builder.append("YPos=").append(mouse_y).append("\n\n");
                                    builder.append("MovingX=").append(getMoving(e.getX(), true)).append("\n");
                                    builder.append("MovingY=").append(getMoving(e.getY(), false)).append("\n");
                                } catch (Throwable th) {
                                    debugErrored = true;
                                    builder.append("Error getting debug. The debug will be stopped!\n").append(Log.getStackTraceString(th));
                                } finally {
                                    debugText.setText(builder.toString());
                                    builder.setLength(0);
                                }
                            }
                            debugText.setText(CallbackBridge.DEBUG_STRING.toString());
                            CallbackBridge.DEBUG_STRING.setLength(0);
                            switch (e.getActionMasked()) {
                                case MotionEvent.ACTION_MOVE:
                                    CallbackBridge.sendCursorPos(mouse_x, mouse_y);
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
                    });
            }
            minecraftGLView.setOnTouchListener(glTouchListener);
            minecraftGLView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener(){
                
                    private boolean isCalled = false;
                    @Override
                    public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height) {
                        scaleFactor = (LauncherPreferences.DEFAULT_PREF.getInt("resolutionRatio",100)/100f);
                        texture.setDefaultBufferSize((int)(width*scaleFactor),(int)(height*scaleFactor));
                        CallbackBridge.windowWidth = (int)(width*scaleFactor);
                        CallbackBridge.windowHeight = (int)(height*scaleFactor);

                        //Load Minecraft options:
                        MCOptionUtils.load();
                        MCOptionUtils.set("overrideWidth", String.valueOf(CallbackBridge.windowWidth));
                        MCOptionUtils.set("overrideHeight", String.valueOf(CallbackBridge.windowHeight));
                        MCOptionUtils.save();
                        getMcScale();
                        // Should we do that?
                        if (!isCalled) {
                            isCalled = true;
                            
                            JREUtils.setupBridgeWindow(new Surface(texture));
                            
                            new Thread(() -> {
                                try {
                                    Thread.sleep(200);
                                    runCraft();
                                } catch (Throwable e) {
                                    Tools.showError(BaseMainActivity.this, e, true);
                                }
                            }, "JVM Main thread").start();
                        }
                    }

                    @Override
                    public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {
                        return true;
                    }

                    @Override
                    public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int width, int height) {
                        CallbackBridge.windowWidth = (int)(width*scaleFactor);
                        CallbackBridge.windowHeight = (int)(height*scaleFactor);
                        CallbackBridge.sendUpdateWindowSize((int)(width*scaleFactor),(int)(height*scaleFactor));
                        getMcScale();
                    }

                    @Override
                    public void onSurfaceTextureUpdated(SurfaceTexture texture) {
                        texture.setDefaultBufferSize(CallbackBridge.windowWidth,CallbackBridge.windowHeight);
                    }
                });
        } catch (Throwable e) {
            Tools.showError(this, e, true);
        }
    }


    @Override
    public boolean dispatchGenericMotionEvent(MotionEvent ev) {
        int mouseCursorIndex = -1;

        if(Gamepad.isGamepadEvent(ev)){
            if(gamepad == null){
                gamepad = new Gamepad(this, ev.getDevice());
            }

            gamepad.update(ev);
            return true;
        }

        for(int i = 0; i < ev.getPointerCount(); i++) {
            if(ev.getToolType(i) == MotionEvent.TOOL_TYPE_MOUSE) {
                mouseCursorIndex = i;
                break;
            }
        }
        if(mouseCursorIndex == -1) return false; // we cant consoom that, theres no mice!
        if(CallbackBridge.isGrabbing()) {
            if(BaseMainActivity.isAndroid8OrHigher()) minecraftGLView.requestPointerCapture();
        }
        switch(ev.getActionMasked()) {
            case MotionEvent.ACTION_HOVER_MOVE:
                mouse_x = (ev.getX(mouseCursorIndex) * scaleFactor);
                mouse_y = (ev.getY(mouseCursorIndex) * scaleFactor);
                CallbackBridge.sendCursorPos(mouse_x, mouse_y);
                debugText.setText(CallbackBridge.DEBUG_STRING.toString());
                CallbackBridge.DEBUG_STRING.setLength(0);
                return true;
            case MotionEvent.ACTION_SCROLL:
                CallbackBridge.sendScroll((double) ev.getAxisValue(MotionEvent.AXIS_VSCROLL), (double) ev.getAxisValue(MotionEvent.AXIS_HSCROLL));
                return true;
            case MotionEvent.ACTION_BUTTON_PRESS:
                 return sendMouseButtonUnconverted(ev.getActionButton(),true);
            case MotionEvent.ACTION_BUTTON_RELEASE:
                return sendMouseButtonUnconverted(ev.getActionButton(),false);
            default:
                return false;
        }


    }

    boolean isKeyboard(KeyEvent evt) {
        System.out.println("Event:" +evt);
        return EfficientAndroidLWJGLKeycode.containsKey(evt.getKeyCode());
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        //Filtering useless events
        if(event.getRepeatCount() != 0 || event.getAction() == KeyEvent.ACTION_MULTIPLE || event.getKeyCode() == KeyEvent.KEYCODE_UNKNOWN || (event.getFlags() & KeyEvent.FLAG_FALLBACK) == KeyEvent.FLAG_FALLBACK) return true;

        //Sometimes, key events may come from the mouse
        if((event.getDevice().getSources() & InputDevice.SOURCE_MOUSE_RELATIVE) == InputDevice.SOURCE_MOUSE_RELATIVE){
            if(event.getKeyCode() == KeyEvent.KEYCODE_BACK){
                sendMouseButton(LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_RIGHT, event.getAction() == KeyEvent.ACTION_DOWN);
                return true;
            }
        }
        System.out.println(event);

        if(Gamepad.isGamepadEvent(event)){
            if(gamepad == null){
                gamepad = new Gamepad(this, event.getDevice());
            }

            gamepad.update(event);
            return true;
        }

        int index = EfficientAndroidLWJGLKeycode.getIndexByKey(event.getKeyCode());
        if(index >= 0) {
            //Toast.makeText(this,"THIS IS A KEYBOARD EVENT !", Toast.LENGTH_SHORT).show();
            EfficientAndroidLWJGLKeycode.execKey(event, index);
            return true;
        }

        return false;
    }


    @Override
    public void onResume() {
        super.onResume();
        mIsResuming = true;
        final int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        final View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(uiOptions);
    }

    @Override
    protected void onPause() {
        if (CallbackBridge.isGrabbing()){
            sendKeyPress(LWJGLGLFWKeycode.GLFW_KEY_ESCAPE);
        }
        mIsResuming = false;
        super.onPause();
    }

    public static void fullyExit() {
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public static boolean isAndroid8OrHigher() {
        return Build.VERSION.SDK_INT >= 26; 
    }

    private void runCraft() throws Throwable {
        if(Tools.LOCAL_RENDERER == null) {
            Tools.LOCAL_RENDERER = LauncherPreferences.PREF_RENDERER;
        }
        appendlnToLog("--------- beggining with launcher debug");
        appendlnToLog("Info: Launcher version: " + BuildConfig.VERSION_NAME);
        if (Tools.LOCAL_RENDERER.equals("vulkan_zink")) {
            checkVulkanZinkIsSupported();
        }
        checkLWJGL3Installed();
        
        jreReleaseList = JREUtils.readJREReleaseProperties();
        JREUtils.checkJavaArchitecture(this, jreReleaseList.get("OS_ARCH"));
        checkJavaArgsIsLaunchable(jreReleaseList.get("JAVA_VERSION"));
        // appendlnToLog("Info: Custom Java arguments: \"" + LauncherPreferences.PREF_CUSTOM_JAVA_ARGS + "\"");

        appendlnToLog("Info: Selected Minecraft version: " + mVersionInfo.id +
            ((mVersionInfo.inheritsFrom == null || mVersionInfo.inheritsFrom.equals(mVersionInfo.id)) ?
            "" : " (" + mVersionInfo.inheritsFrom + ")"));

        JREUtils.redirectAndPrintJRELog(this);
        Tools.launchMinecraft(this, mProfile, mProfile.selectedVersion);
    }
    
    private void checkJavaArgsIsLaunchable(String jreVersion) throws Throwable {
        appendlnToLog("Info: Custom Java arguments: \"" + LauncherPreferences.PREF_CUSTOM_JAVA_ARGS + "\"");
    }

    private void checkLWJGL3Installed() {
        File lwjgl3dir = new File(Tools.DIR_GAME_HOME, "lwjgl3");
        if (!lwjgl3dir.exists() || lwjgl3dir.isFile() || lwjgl3dir.list().length == 0) {
            appendlnToLog("Error: LWJGL3 was not installed!");
            throw new RuntimeException(getString(R.string.mcn_check_fail_lwjgl));
        } else {
            appendlnToLog("Info: LWJGL3 directory: " + Arrays.toString(lwjgl3dir.list()));
        }
    }

    private void checkVulkanZinkIsSupported() {
        if (Tools.CURRENT_ARCHITECTURE.equals("x86")
         || Build.VERSION.SDK_INT < 25
         || !getPackageManager().hasSystemFeature(PackageManager.FEATURE_VULKAN_HARDWARE_LEVEL)
         || !getPackageManager().hasSystemFeature(PackageManager.FEATURE_VULKAN_HARDWARE_VERSION)) {
            appendlnToLog("Error: Vulkan Zink renderer is not supported!");
            throw new RuntimeException(getString(R.string. mcn_check_fail_vulkan_support));
        }
    }
    
    public void printStream(InputStream stream) {
        try {
            BufferedReader buffStream = new BufferedReader(new InputStreamReader(stream));
            String line = null;
            while ((line = buffStream.readLine()) != null) {
                appendlnToLog(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String fromArray(List<String> arr) {
        StringBuilder s = new StringBuilder();
        for (String exec : arr) {
            s.append(" ").append(exec);
        }
        return s.toString();
    }

    private void toggleDebug() {
        debugText.setVisibility(debugText.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
    }

    private void dialogSendCustomKey() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.control_customkey);
        dialog.setItems(EfficientAndroidLWJGLKeycode.generateKeyName(), (dInterface, position) -> EfficientAndroidLWJGLKeycode.execKeyIndex(position));
        dialog.show();
    }

    boolean isInEditor;
    private void openCustomControls() {
        if(ingameControlsEditorListener != null) {
            ((MainActivity)this).mControlLayout.setModifiable(true);
            navDrawer.getMenu().clear();
            navDrawer.inflateMenu(R.menu.menu_customctrl);
            navDrawer.setNavigationItemSelectedListener(ingameControlsEditorListener);
            isInEditor = true;
        }
    }

    public void leaveCustomControls() {
        if(this instanceof MainActivity) {
            try {
                ((MainActivity) this).mControlLayout.hideAllHandleViews();
                ((MainActivity) this).mControlLayout.loadLayout((CustomControls)null);
                ((MainActivity) this).mControlLayout.setModifiable(false);
                System.gc();
                ((MainActivity) this).mControlLayout.loadLayout(LauncherPreferences.DEFAULT_PREF.getString("defaultCtrl",Tools.CTRLDEF_FILE));
            } catch (IOException e) {
                Tools.showError(this,e);
            }
            //((MainActivity) this).mControlLayout.loadLayout((CustomControls)null);
        }
        navDrawer.getMenu().clear();
        navDrawer.inflateMenu(R.menu.menu_runopt);
        navDrawer.setNavigationItemSelectedListener(gameActionListener);
        isInEditor = false;
    }
    private void openLogOutput() {
        contentLog.setVisibility(View.VISIBLE);
        mIsResuming = false;
    }

    public void closeLogOutput(View view) {
        contentLog.setVisibility(View.GONE);
        mIsResuming = true;
    }
     
    @Override
    public void appendToLog(final String text, boolean checkAllow) {
        logStream.print(text);
        if (checkAllow && !isLogAllow) return;
        textLog.post(() -> {
            textLog.append(text);
            contentScroll.fullScroll(ScrollView.FOCUS_DOWN);
        });
    }

    public int mcscale(int input) {
        return (int)((this.guiScale * input)/scaleFactor);
    }


    public void toggleMenu(View v) {
        drawerLayout.openDrawer(Gravity.RIGHT);
    }

    public void placeMouseAdd(float x, float y) {
        this.mousePointer.setX(mousePointer.getX() + x);
        this.mousePointer.setY(mousePointer.getY() + y);
    }

    public void placeMouseAt(float x, float y) {
        this.mousePointer.setX(x);
        this.mousePointer.setY(y);
    }

    public void toggleMouse(View view) {
        if (CallbackBridge.isGrabbing()) return;

        isVirtualMouseEnabled = !isVirtualMouseEnabled;
        touchPad.setVisibility(isVirtualMouseEnabled ? View.VISIBLE : View.GONE);
        ((Button) view).setText(isVirtualMouseEnabled ? R.string.control_mouseon: R.string.control_mouseoff);
    }

    public static void dialogForceClose(Context ctx) {
        new AlertDialog.Builder(ctx)
            .setMessage(R.string.mcn_exit_confirm)
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(android.R.string.ok, (p1, p2) -> {
                try {
                    fullyExit();
                } catch (Throwable th) {
                    Log.w(Tools.APP_NAME, "Could not enable System.exit() method!", th);
                }
            }).show();
    }

    @Override
    public void onBackPressed() {
        // Prevent back
        // Catch back as Esc keycode at another place
        sendKeyPress(LWJGLGLFWKeycode.GLFW_KEY_ESCAPE);
    }
    
    public void hideKeyboard() {
        try {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
                ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow((this).getCurrentFocus().getWindowToken(), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showKeyboard() {
        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        minecraftGLView.requestFocusFromTouch();
        minecraftGLView.requestFocus();
    }

    protected void setRightOverride(boolean val) {
        this.rightOverride = val;
    }

    public static void sendKeyPress(int keyCode, int modifiers, boolean status) {
        sendKeyPress(keyCode, 0, modifiers, status);
    }

    public static void sendKeyPress(int keyCode, int scancode, int modifiers, boolean status) {
        sendKeyPress(keyCode, '\u0000', scancode, modifiers, status);
    }

    public static void sendKeyPress(int keyCode, char keyChar, int scancode, int modifiers, boolean status) {
        CallbackBridge.sendKeycode(keyCode, keyChar, scancode, modifiers, status);
    }
    public static boolean doesObjectContainField(Class objectClass, String fieldName) {
        for (Field field : objectClass.getFields()) {
            if (field.getName().equals(fieldName)) {
                return true;
            }
        }
        return false;
    }
    public void sendKeyPress(char keyChar) {
        if(doesObjectContainField(KeyEvent.class,"KEYCODE_" + Character.toUpperCase(keyChar))) {
            try {
                int keyCode = KeyEvent.class.getField("KEYCODE_" + Character.toUpperCase(keyChar)).getInt(null);
                sendKeyPress(EfficientAndroidLWJGLKeycode.getValue(keyCode), keyChar, 0, CallbackBridge.getCurrentMods(), true);
                sendKeyPress(EfficientAndroidLWJGLKeycode.getValue(keyCode), keyChar, 0, CallbackBridge.getCurrentMods(), false);
            } catch (IllegalAccessException | NoSuchFieldException e) {

            }
            return;
        }

        sendKeyPress(0, keyChar, 0, CallbackBridge.getCurrentMods(), true);
        sendKeyPress(0, keyChar, 0, CallbackBridge.getCurrentMods(), false);
    }

    public static void sendKeyPress(int keyCode) {
        sendKeyPress(keyCode, CallbackBridge.getCurrentMods(), true);
        sendKeyPress(keyCode, CallbackBridge.getCurrentMods(), false);
    }
    
    public static void sendMouseButton(int button, boolean status) {
        CallbackBridge.sendMouseKeycode(button, CallbackBridge.getCurrentMods(), status);
    }
    
    public static boolean sendMouseButtonUnconverted(int button, boolean status) {
        int glfwButton = -256;
        switch (button) {
            case MotionEvent.BUTTON_PRIMARY:
                glfwButton = LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_LEFT;
                break;
            case MotionEvent.BUTTON_TERTIARY:
                glfwButton = LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_MIDDLE;
                break;
            case MotionEvent.BUTTON_SECONDARY:
                glfwButton = LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_RIGHT;
                break;
        }
        if(glfwButton == -256) return false;
        sendMouseButton(glfwButton, status);
        return true;
    }

    public int getMcScale() {
        //Get the scale stored in game files, used auto scale if found or if the stored scaled is bigger than the authorized size.
        MCOptionUtils.load();
        String str = MCOptionUtils.get("guiScale");
        this.guiScale = (str == null ? 0 :Integer.parseInt(str));
        

        int scale = Math.max(Math.min(CallbackBridge.windowWidth / 320, CallbackBridge.windowHeight / 240), 1);
        if(scale < this.guiScale || guiScale == 0){
            this.guiScale = scale;
        }

        if(gamepad != null) gamepad.notifyGUISizeChange(this.guiScale);
        return this.guiScale;
    }

    public int handleGuiBar(int x, int y) {
        if (!CallbackBridge.isGrabbing()) return -1;

        int barHeight = mcscale(20);
        int barWidth = mcscale(180);
        int barX = (CallbackBridge.physicalWidth / 2) - (barWidth / 2);
        int barY = CallbackBridge.physicalHeight - barHeight;
        if (x < barX || x >= barX + barWidth || y < barY || y >= barY + barHeight) {
            return -1;
        }
        return hotbarKeys[((x - barX) / mcscale(180 / 9)) % 9];
    }
    int tmpMouseSpeed;

    public void adjustMouseSpeedLive() {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle(R.string.mcl_setting_title_mousespeed);
        View v = LayoutInflater.from(this).inflate(R.layout.live_mouse_speed_editor,null);
        final SeekBar sb = v.findViewById(R.id.mouseSpeed);
        final TextView tv = v.findViewById(R.id.mouseSpeedTV);
        sb.setMax(275);
        tmpMouseSpeed = (int) ((LauncherPreferences.PREF_MOUSESPEED*100));
        sb.setProgress(tmpMouseSpeed-25);
        tv.setText(tmpMouseSpeed +" %");
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tmpMouseSpeed = i+25;
                tv.setText(tmpMouseSpeed +" %");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        b.setView(v);
        b.setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
            LauncherPreferences.PREF_MOUSESPEED = ((float)tmpMouseSpeed)/100f;
            LauncherPreferences.DEFAULT_PREF.edit().putInt("mousespeed",tmpMouseSpeed).commit();
            dialogInterface.dismiss();
            System.gc();
        });
        b.setNegativeButton(android.R.string.cancel, (dialogInterface, i) -> {
            dialogInterface.dismiss();
            System.gc();
        });
        b.show();
    }
}
