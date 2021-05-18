package net.kdt.pojavlaunch;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.view.inputmethod.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.*;
import com.google.android.material.navigation.*;
import java.io.*;
import java.lang.reflect.*;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.*;
import net.kdt.pojavlaunch.customcontrols.*;
import net.kdt.pojavlaunch.prefs.*;
import net.kdt.pojavlaunch.utils.*;
import net.kdt.pojavlaunch.value.*;
import net.kdt.pojavlaunch.value.launcherprofiles.LauncherProfiles;
import net.kdt.pojavlaunch.value.launcherprofiles.MinecraftProfile;

import org.lwjgl.glfw.*;

public class BaseMainActivity extends LoggableActivity {
    public static volatile ClipboardManager GLOBAL_CLIPBOARD;
    
    public static final String initText = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA  ";
    volatile public static boolean isInputStackCall;

    private static int[] hotbarKeys = {
        LWJGLGLFWKeycode.GLFW_KEY_1, LWJGLGLFWKeycode.GLFW_KEY_2,   LWJGLGLFWKeycode.GLFW_KEY_3,
        LWJGLGLFWKeycode.GLFW_KEY_4, LWJGLGLFWKeycode.GLFW_KEY_5,   LWJGLGLFWKeycode.GLFW_KEY_6,
        LWJGLGLFWKeycode.GLFW_KEY_7, LWJGLGLFWKeycode.GLFW_KEY_8, LWJGLGLFWKeycode.GLFW_KEY_9};

    private boolean rightOverride = false;
    private float scaleFactor = 1;
    private int fingerStillThreshold = 8;
    private int initialX, initialY;
    private int scrollInitialX, scrollInitialY;
    private boolean mIsResuming = false;
    private static final int MSG_LEFT_MOUSE_BUTTON_CHECK = 1028;
    private static final int MSG_DROP_ITEM_BUTTON_CHECK = 1029;
    private static boolean triggeredLeftMouseButton = false;
    private Handler theHandler = new Handler() {
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
    private DisplayMetrics displayMetrics;
    public boolean hiddenTextIgnoreUpdate = true;
    public String hiddenTextContents = initText;
    
    private boolean isVirtualMouseEnabled;
    private LinearLayout touchPad;
    private ImageView mousePointer;
    //private EditText hiddenEditor;
    // private ViewGroup overlayView;
    private MinecraftAccount mProfile;
    private MinecraftProfile mGameProfile;
    private DrawerLayout drawerLayout;
    private NavigationView navDrawer;
    
    // protected CapturedEditText mKeyHandlerView;

    private LinearLayout contentLog;
    private TextView textLog;
    private ScrollView contentScroll;
    private ToggleButton toggleLog;
    private GestureDetector gestureDetector;

    private TextView debugText;
    private NavigationView.OnNavigationItemSelectedListener gameActionListener;
    public NavigationView.OnNavigationItemSelectedListener ingameControlsEditorListener;
    // private String mQueueText = new String();

    protected JMinecraftVersionList.Version mVersionInfo;

    private View.OnTouchListener glTouchListener;

    private File logFile;
    private PrintStream logStream;
    
    /*
     private LinearLayout contentCanvas;
     private AWTSurfaceView contentCanvasView;
     */
    private boolean resuming;
    private boolean lastEnabled = false;
    private boolean lastGrab = false;
    private boolean isExited = false;
    private boolean isLogAllow = false;
    private volatile int mouse_x, mouse_y;
    // private int navBarHeight = 40;
    
    // private static Collection<? extends Provider.Service> rsaPkcs1List;

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
            mGameProfile = LauncherProfiles.mainProfileJson.profiles.get(mProfile.selectedProfile);
            mVersionInfo = Tools.getVersionInfo(null,mGameProfile.lastVersionId);
            
            setTitle("Minecraft " + mGameProfile.lastVersionId);
            
            // Minecraft 1.13+
            isInputStackCall = mVersionInfo.arguments != null;
            
            this.displayMetrics = Tools.getDisplayMetrics(this);
            CallbackBridge.windowWidth = (int) ((float)displayMetrics.widthPixels * scaleFactor);
            CallbackBridge.windowHeight = (int) ((float)displayMetrics.heightPixels * scaleFactor);
            System.out.println("WidthHeight: " + CallbackBridge.windowWidth + ":" + CallbackBridge.windowHeight);

            
            gestureDetector = new GestureDetector(this, new SingleTapConfirm());

            // Menu
            drawerLayout = findViewById(R.id.main_drawer_options);

            navDrawer = findViewById(R.id.main_navigation_view);
            gameActionListener = new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem menuItem) {
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
                    //Toast.makeText(MainActivity.this, menuItem.getTitle() + ":" + menuItem.getItemId(), Toast.LENGTH_SHORT).show();

                    drawerLayout.closeDrawers();
                    return true;
                }
            };
            navDrawer.setNavigationItemSelectedListener(
                gameActionListener);

            // this.overlayView = (ViewGroup) findViewById(R.id.main_control_overlay);

            //this.hiddenEditor = findViewById(R.id.hiddenTextbox);

            // Mouse pointer part
            //this.mouseToggleButton = findButton(R.id.control_togglemouse);
            this.touchPad = findViewById(R.id.main_touchpad);
            touchPad.setFocusable(false);
            
            this.mousePointer = findViewById(R.id.main_mouse_pointer);
            this.mousePointer.post(new Runnable(){

                @Override
                public void run() {
                    ViewGroup.LayoutParams params = mousePointer.getLayoutParams();
                    params.width = (int) (36 / 100f * LauncherPreferences.PREF_MOUSESCALE);
                    params.height = (int) (54 / 100f * LauncherPreferences.PREF_MOUSESCALE);
                }
            });

            this.contentLog = findViewById(R.id.content_log_layout);
            this.contentScroll = findViewById(R.id.content_log_scroll);
            this.textLog = (TextView) contentScroll.getChildAt(0);
            this.toggleLog = findViewById(R.id.content_log_toggle_log);
            this.toggleLog.setChecked(false);
            // this.textLogBehindGL = (TextView) findViewById(R.id.main_log_behind_GL);
            // this.textLogBehindGL.setTypeface(Typeface.MONOSPACE);

            this.textLog.setTypeface(Typeface.MONOSPACE);
            this.toggleLog.setOnCheckedChangeListener(new ToggleButton.OnCheckedChangeListener(){

                    @Override
                    public void onCheckedChanged(CompoundButton button, boolean isChecked)
                    {
                        isLogAllow = isChecked;
                        appendToLog("");
                    }
                });

            this.debugText = findViewById(R.id.content_text_debug);

            this.minecraftGLView = findViewById(R.id.main_game_render_view);
            // toggleGui(null);
            this.drawerLayout.closeDrawers();
/*
            mKeyHandlerView = findViewById(R.id.main_key_handler);
            mKeyHandlerView.setSingleLine(false);
            mKeyHandlerView.clearFocus();
            
            AndroidLWJGLKeycode.isBackspaceAfterChar = true; // mVersionInfo.minimumLauncherVersion >= 18;
*/
            placeMouseAt(CallbackBridge.physicalWidth / 2, CallbackBridge.physicalHeight / 2);
            new Thread(new Runnable(){

                    //private boolean isCapturing = false;
                    @Override
                    public void run()
                    {
                        while (!isExited) {
                            if (lastGrab != CallbackBridge.isGrabbing())
                            mousePointer.post(new Runnable(){

                                    @Override
                                    public void run()
                                    {
                                        if (!CallbackBridge.isGrabbing() && isVirtualMouseEnabled) {
                                            touchPad.setVisibility(View.VISIBLE);
                                            placeMouseAt(displayMetrics.widthPixels / 2, displayMetrics.heightPixels / 2);
                                        }

                                        if (CallbackBridge.isGrabbing() && touchPad.getVisibility() != View.GONE) {
                                            touchPad.setVisibility(View.GONE);
                                        }
                                        /*
                                        if (isAndroid8OrHigher()) {
                                            if (!CallbackBridge.isGrabbing() && isCapturing) {
                                                minecraftGLView.releasePointerCapture();
                                                minecraftGLView.clearFocus();
                                                isCapturing = false;
                                            } else if (CallbackBridge.isGrabbing() && !isCapturing) {
                                                minecraftGLView.requestFocus();
                                                minecraftGLView.requestPointerCapture();
                                                isCapturing = true;
                                            }
                                        }
                                        */
                                        lastGrab = CallbackBridge.isGrabbing();
                                    }
                                });

                            // try {
                            //     Thread.sleep(100);
                            // } catch (Throwable th) {}
                        }
                    }
                }, "VirtualMouseGrabThread").start();


            if (isAndroid8OrHigher()) {
                touchPad.setDefaultFocusHighlightEnabled(false);
            }
            touchPad.setOnTouchListener(new OnTouchListener(){
                    private float prevX, prevY;
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
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
                        if(event.getHistorySize() > 0) {
                            prevX = event.getHistoricalX(0);
                            prevY = event.getHistoricalY(0);
                        }else{
                            prevX = x;
                            prevY = y;
                        }
                        float mouseX = mousePointer.getTranslationX();
                        float mouseY = mousePointer.getTranslationY();

                        if (gestureDetector.onTouchEvent(event)) {
                            mouse_x = (int) (mouseX * scaleFactor);
                            mouse_y = (int) (mouseY * scaleFactor);
                            CallbackBridge.sendCursorPos((int) (mouseX * scaleFactor), (int) (mouseY *scaleFactor));
                            CallbackBridge.sendMouseKeycode(rightOverride ? LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_RIGHT : LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_LEFT);
                            if (!rightOverride) {
                                CallbackBridge.mouseLeft = true;
                            }

                        } else {
                            switch (action) {
                                case MotionEvent.ACTION_UP: // 1
                                case MotionEvent.ACTION_CANCEL: // 3
                                    if (!rightOverride) {
                                        CallbackBridge.mouseLeft = false;
                                    }
                                    break;

                                case MotionEvent.ACTION_POINTER_DOWN: // 5
                                    scrollInitialX = CallbackBridge.mouseX;
                                    scrollInitialY = CallbackBridge.mouseY;
                                    break;

                                case MotionEvent.ACTION_POINTER_UP: // 6
                                    break;
                                    
                                case MotionEvent.ACTION_MOVE: // 2
                                    if (!CallbackBridge.isGrabbing() && event.getPointerCount() == 2 && !LauncherPreferences.PREF_DISABLE_GESTURES) {
                                        CallbackBridge.sendScroll(CallbackBridge.mouseX - scrollInitialX, CallbackBridge.mouseY - scrollInitialY);
                                        scrollInitialX = CallbackBridge.mouseX;
                                        scrollInitialY = CallbackBridge.mouseY;
                                    } else {
                                        mouseX = Math.max(0, Math.min(displayMetrics.widthPixels, mouseX + (x - prevX)*LauncherPreferences.PREF_MOUSESPEED));
                                        mouseY = Math.max(0, Math.min(displayMetrics.heightPixels, mouseY + (y - prevY)*LauncherPreferences.PREF_MOUSESPEED));
                                        mouse_x = (int) (mouseX * scaleFactor);
                                        mouse_y = (int) (mouseY * scaleFactor);
                                        placeMouseAt(mouseX, mouseY);
                                        CallbackBridge.sendCursorPos((int) (mouseX * scaleFactor),  (int) (mouseY *scaleFactor));
                                        /*
                                        if (!CallbackBridge.isGrabbing()) {
                                            CallbackBridge.sendMouseKeycode(LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_LEFT, 0, isLeftMouseDown);
                                            CallbackBridge.sendMouseKeycode(LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_RIGHT, 0, isRightMouseDown);
                                        }
                                        */
                                    }
                                    break;
                            }
                        }
                        
                        debugText.setText(CallbackBridge.DEBUG_STRING.toString());
                        CallbackBridge.DEBUG_STRING.setLength(0);
                        
                        return true;
                    }
                });

            // System.loadLibrary("Regal");

            minecraftGLView.setFocusable(true);
            // minecraftGLView.setEGLContextClientVersion(2);
            glTouchListener = new OnTouchListener(){
                private boolean isTouchInHotbar = false;
                private int hotbarX, hotbarY;
                @Override
                public boolean onTouch(View p1, MotionEvent e)
                {

                    {
                        int mptrIndex = -1;
                        for (int i = 0; i < e.getPointerCount(); i++) {
                            if (e.getToolType(i) == MotionEvent.TOOL_TYPE_MOUSE) { //if there's at least one mouse...
                                mptrIndex = i; //index it
                            }
                        }
                        if (mptrIndex != -1) {
                            if(CallbackBridge.isGrabbing()) {
                                return false;
                            }
                            //handle mouse events by just sending the coords of the new point in touch event
                            int x = (int) (e.getX(mptrIndex) * scaleFactor);
                            int y = (int) (e.getY(mptrIndex) * scaleFactor);
                            CallbackBridge.mouseX = x;
                            CallbackBridge.mouseY = y;
                            CallbackBridge.sendCursorPos(x, y);
                            return true; // event handled sucessfully
                        }//if index IS -1, continue handling as an usual touch event
                    }

                    // System.out.println("Pre touch, isTouchInHotbar=" + Boolean.toString(isTouchInHotbar) + ", action=" + MotionEvent.actionToString(e.getActionMasked()));
                   /* int x = ((int) e.getX()) * scaleFactor;
                    int y = ((int) e.getY()) * scaleFactor;*/
                    if(e.getHistorySize() > 0 && CallbackBridge.isGrabbing()) {
                        mouse_x += (int)(e.getX() - e.getHistoricalX(0));
                        mouse_y += (int)(e.getY() - e.getHistoricalY(0));
                    }
                    if(!CallbackBridge.isGrabbing()) {
                        mouse_x = (int) (e.getX() * scaleFactor);
                        mouse_y = (int) (e.getY() * scaleFactor);
                    }

                    int hudKeyHandled = handleGuiBar((int)e.getX(), (int)e.getY());
                    if (!CallbackBridge.isGrabbing() && gestureDetector.onTouchEvent(e)) {
                        if (hudKeyHandled != -1) {
                            sendKeyPress(hudKeyHandled);
                        } else {
                            CallbackBridge.putMouseEventWithCoords(rightOverride ? (byte) 1 : (byte) 0, mouse_x, mouse_y);
                            if (!rightOverride) {
                                CallbackBridge.mouseLeft = true;
                            }
                        }
                    } else {
                        switch (e.getActionMasked()) {
                            case MotionEvent.ACTION_DOWN: // 0
                                CallbackBridge.sendPrepareGrabInitialPos();
                                
                                isTouchInHotbar = hudKeyHandled != -1;
                                if (isTouchInHotbar) {
                                    sendKeyPress(hudKeyHandled, 0, true);
                                    hotbarX = (int)e.getX();
                                    hotbarY = (int)e.getY();

                                    theHandler.sendEmptyMessageDelayed(BaseMainActivity.MSG_DROP_ITEM_BUTTON_CHECK, LauncherPreferences.PREF_LONGPRESS_TRIGGER);
                                } else {
                                    CallbackBridge.mouseX = mouse_x;
                                    CallbackBridge.mouseY = mouse_y;
                                    CallbackBridge.sendCursorPos(mouse_x, mouse_y);
                                    if (!rightOverride) {
                                        CallbackBridge.mouseLeft = true;
                                    }

                                    if (CallbackBridge.isGrabbing()) {
                                        // It cause hold left mouse while moving camera
                                        // CallbackBridge.putMouseEventWithCoords(rightOverride ? (byte) 1 : (byte) 0, (byte) 1, x, y);
                                        initialX = mouse_x;
                                        initialY = mouse_y;
                                        theHandler.sendEmptyMessageDelayed(BaseMainActivity.MSG_LEFT_MOUSE_BUTTON_CHECK, LauncherPreferences.PREF_LONGPRESS_TRIGGER);
                                    }
                                }
                                break;
                                
                            case MotionEvent.ACTION_UP: // 1
                            case MotionEvent.ACTION_CANCEL: // 3
                                if (!isTouchInHotbar) {
                                    CallbackBridge.mouseX = mouse_x;
                                    CallbackBridge.mouseY = mouse_y;
                                    
                                    // -TODO uncomment after fix wrong trigger
                                    // CallbackBridge.putMouseEventWithCoords(rightOverride ? (byte) 1 : (byte) 0, (byte) 0, x, y);
                                    CallbackBridge.sendCursorPos(mouse_x, mouse_y);
                                    if (!rightOverride) {
                                        CallbackBridge.mouseLeft = false;
                                    }
                                } 

                                if (CallbackBridge.isGrabbing()) {
                                    // System.out.println((String) ("[Math.abs(" + initialX + " - " + x + ") = " + Math.abs(initialX - x) + "] < " + fingerStillThreshold));
                                    // System.out.println((String) ("[Math.abs(" + initialY + " - " + y + ") = " + Math.abs(initialY - y) + "] < " + fingerStillThreshold));
                                    if (isTouchInHotbar && Math.abs(hotbarX - mouse_x) < fingerStillThreshold && Math.abs(hotbarY - mouse_y) < fingerStillThreshold) {
                                        sendKeyPress(hudKeyHandled, 0, false);
                                    } else if (!triggeredLeftMouseButton && Math.abs(initialX - mouse_x) < fingerStillThreshold && Math.abs(initialY - mouse_y) < fingerStillThreshold) {
                                        if (!LauncherPreferences.PREF_DISABLE_GESTURES) {
                                            sendMouseButton(LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_RIGHT, true);
                                            sendMouseButton(LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_RIGHT, false);
                                        }
                                    }
                                    if (!isTouchInHotbar) {
                                        if (triggeredLeftMouseButton) {
                                            sendMouseButton(LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_LEFT, false);
                                        }
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
                                break;
                                
                            case MotionEvent.ACTION_POINTER_UP: // 6
                                break;

                            case MotionEvent.ACTION_MOVE:
                                if (!CallbackBridge.isGrabbing() && e.getPointerCount() == 2 && !LauncherPreferences.PREF_DISABLE_GESTURES) {
                                    CallbackBridge.sendScroll(mouse_x - scrollInitialX, mouse_y - scrollInitialY);
                                    scrollInitialX = mouse_x;
                                    scrollInitialY = mouse_y;
                                } else if (!isTouchInHotbar) {
                                    CallbackBridge.mouseX = mouse_x;
                                    CallbackBridge.mouseY = mouse_y;

                                    CallbackBridge.sendCursorPos(mouse_x, mouse_y);
                                }
                                break;
                        }
                    }
                    
/*
                    int x = ((int) e.getX()) * scaleFactor;
                    int y = (minecraftGLView.getHeight() - ((int) e.getY())) * scaleFactor;
                    int hudKeyHandled = handleGuiBar(x, y, e);
                    if (!CallbackBridge.isGrabbing() && gestureDetector.onTouchEvent(e)) {
                        if (hudKeyHandled != -1) {
                            sendKeyPress(hudKeyHandled);
                        } else {
                            CallbackBridge.sendMouseEvent(
                                x, CallbackBridge.windowHeight - y,
                                rightOverride ? LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_RIGHT : LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_LEFT
                            );
                            if (!rightOverride) {
                                CallbackBridge.mouseLeft = true;
                            }
                        }
                    } else {
                        switch (e.getActionMasked()) {
                            case MotionEvent.ACTION_DOWN: // 0
                            case MotionEvent.ACTION_POINTER_DOWN: // 5
                                isTouchInHotbar = hudKeyHandled != -1;
                                if (isTouchInHotbar) {
                                    sendKeyPress(hudKeyHandled, 0, true);
                                    hotbarX = x;
                                    hotbarY = y;

                                    theHandler.sendEmptyMessageDelayed(MainActivity.MSG_DROP_ITEM_BUTTON_CHECK, LauncherPreferences.PREF_LONGPRESS_TRIGGER);
                                } else {
                                    CallbackBridge.sendCursorPos(x, CallbackBridge.windowHeight - y);
                                    
                                    // if (!rightOverride)
                                        // CallbackBridge.mouseLeft = true;
                                    
                                    

                                    if (CallbackBridge.isGrabbing()) {
                                        CallbackBridge.sendMouseKeycode(rightOverride ? LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_RIGHT : LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_LEFT, 0, true);
                                        initialX = x;
                                        initialY = y;
                                        theHandler.sendEmptyMessageDelayed(MainActivity.MSG_LEFT_MOUSE_BUTTON_CHECK, LauncherPreferences.PREF_LONGPRESS_TRIGGER);
                                    }
                                }
                                break;
                            case MotionEvent.ACTION_UP: // 1
                            case MotionEvent.ACTION_CANCEL: // 3
                            case MotionEvent.ACTION_POINTER_UP: // 6
                                if (!isTouchInHotbar) {
                                    CallbackBridge.sendCursorPos(x, CallbackBridge.windowHeight - y);

                                    // TODO uncomment after fix wrong trigger
                                    // CallbackBridge.putMouseEventWithCoords(rightOverride ? (byte) 1 : (byte) 0, (byte) 0, x, y);
                                    if (!rightOverride) {
                                        // CallbackBridge.mouseLeft = false;
                                    }
                                } 

                                if (CallbackBridge.isGrabbing()) {
                                    // System.out.println((String) ("[Math.abs(" + initialX + " - " + x + ") = " + Math.abs(initialX - x) + "] < " + fingerStillThreshold));
                                    // System.out.println((String) ("[Math.abs(" + initialY + " - " + y + ") = " + Math.abs(initialY - y) + "] < " + fingerStillThreshold));
                                    if (isTouchInHotbar && Math.abs(hotbarX - x) < fingerStillThreshold && Math.abs(hotbarY - y) < fingerStillThreshold) {
                                        sendKeyPress(hudKeyHandled, 0, false);
                                    } else if (!triggeredLeftMouseButton && Math.abs(initialX - x) < fingerStillThreshold && Math.abs(initialY - y) < fingerStillThreshold) {
                                        sendMouseButton(LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_RIGHT, true);
                                        sendMouseButton(LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_RIGHT, false);
                                    }
                                    if (!isTouchInHotbar) {
                                        if (triggeredLeftMouseButton) {
                                            sendMouseButton(LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_LEFT, false);
                                        }
                                        triggeredLeftMouseButton = false;
                                        theHandler.removeMessages(MainActivity.MSG_LEFT_MOUSE_BUTTON_CHECK);
                                    } else {
                                        sendKeyPress(LWJGLGLFWKeycode.GLFW_KEY_Q, 0, false);
                                        theHandler.removeMessages(MSG_DROP_ITEM_BUTTON_CHECK);
                                    }
                                }
                                break;

                            default:
                                if (!isTouchInHotbar) {
                                    CallbackBridge.sendCursorPos(x, CallbackBridge.windowHeight - y);
                                }
                                break;
                                
                        }
                    }
                    */
                    
                    debugText.setText(CallbackBridge.DEBUG_STRING.toString());
                    CallbackBridge.DEBUG_STRING.setLength(0);

                    return true;
                    // return !CallbackBridge.isGrabbing();
                }
            };
            
            if (isAndroid8OrHigher()) {
                minecraftGLView.setDefaultFocusHighlightEnabled(false);
                minecraftGLView.setOnCapturedPointerListener(new View.OnCapturedPointerListener() {
                    //private int x, y;
                    private boolean debugErrored = false;

                    private String getMoving(float pos, boolean xOrY) {
                        if (pos == 0) {
                            return "STOPPED";
                        } else if (pos > 0) {
                            return xOrY ? "RIGHT" : "DOWN";
                        } else { // if (pos3 < 0) {
                            return xOrY ? "LEFT" : "UP";
                        }
                    }

                    @Override
                    public boolean onCapturedPointer (View view, MotionEvent e) {
                            if(e.getHistorySize() > 0) {
                                mouse_x += (int)(e.getX()*scaleFactor);
                                mouse_y += (int)(e.getY()*scaleFactor);
                            }
                            CallbackBridge.mouseX = mouse_x;
                            CallbackBridge.mouseY = mouse_y;
                            if(!CallbackBridge.isGrabbing()){
                                view.releasePointerCapture();
                            }

                            if (debugText.getVisibility() == View.VISIBLE && !debugErrored) {
                                StringBuilder builder = new StringBuilder();
                                try {
                                    builder.append("PointerCapture debug\n");
                                    builder.append("MotionEvent=" + e.getActionMasked() + "\n");
                                    builder.append("PressingBtn=" + MotionEvent.class.getDeclaredMethod("buttonStateToString").invoke(null, e.getButtonState()) + "\n\n");

                                    builder.append("PointerX=" + e.getX() + "\n");
                                    builder.append("PointerY=" + e.getY() + "\n");
                                    builder.append("RawX=" + e.getRawX() + "\n");
                                    builder.append("RawY=" + e.getRawY() + "\n\n");

                                    builder.append("XPos=" + mouse_x + "\n");
                                    builder.append("YPos=" + mouse_y + "\n\n");
                                    builder.append("MovingX=" + getMoving(e.getX(), true) + "\n");
                                    builder.append("MovingY=" + getMoving(e.getY(), false) + "\n");
                                } catch (Throwable th) {
                                    debugErrored = true;
                                    builder.append("Error getting debug. The debug will be stopped!\n" + Log.getStackTraceString(th));
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
                        MCOptionUtils.set("overrideWidth", ""+CallbackBridge.windowWidth);
                        MCOptionUtils.set("overrideHeight", ""+CallbackBridge.windowHeight);
                        MCOptionUtils.save();
                        getMcScale();
                        if(mGameProfile.javaArgs != null && !mGameProfile.javaArgs.isEmpty()) LauncherPreferences.PREF_CUSTOM_JAVA_ARGS = mGameProfile.javaArgs;
                        // Should we do that?
                        if (!isCalled) {
                            isCalled = true;
                            
                            JREUtils.setupBridgeWindow(new Surface(texture));
                            
                            new Thread(new Runnable(){

                                    @Override
                                    public void run() {
                                        try {
                                            Thread.sleep(200);
                                            runCraft();
                                        } catch (Throwable e) {
                                            Tools.showError(BaseMainActivity.this, e, true);
                                        }
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

    /*
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getAction()) {
            case KeyEvent.ACTION_DOWN:
                AndroidLWJGLKeycode.execKey(event, event.getKeyCode(), true);
                break;
                
            case KeyEvent.ACTION_UP:
                AndroidLWJGLKeycode.execKey(event, event.getKeyCode(), false);
                break;
        }
        
        return super.dispatchKeyEvent(event);
    }
    */
    @Override
    public boolean dispatchGenericMotionEvent(MotionEvent ev) {
        int mouseCursorIndex = -1;
        if(ev.getSource() == InputDevice.SOURCE_CLASS_JOYSTICK) {
            CallbackBridge.nativePutControllerAxes((FloatBuffer)FloatBuffer.allocate(8)
                    .put(ev.getAxisValue(MotionEvent.AXIS_X))
                    .put(ev.getAxisValue(MotionEvent.AXIS_Y))
                    .put(ev.getAxisValue(MotionEvent.AXIS_Z))
                    .put(ev.getAxisValue(MotionEvent.AXIS_RX))
                    .put(ev.getAxisValue(MotionEvent.AXIS_RY))
                    .put(ev.getAxisValue(MotionEvent.AXIS_RZ))
                    .put(ev.getAxisValue(MotionEvent.AXIS_HAT_X))
                    .put(ev.getAxisValue(MotionEvent.AXIS_HAT_Y))
            .flip());
            return true;//consume the cum chalice
        }else {
            for(int i = 0; i < ev.getPointerCount(); i++) {
                if(ev.getToolType(i) == MotionEvent.TOOL_TYPE_MOUSE) {
                    mouseCursorIndex = i;
                }
            }
            if(mouseCursorIndex == -1) return false; // we cant consoom that, theres no mice!
            if(CallbackBridge.isGrabbing()) {
                if(BaseMainActivity.isAndroid8OrHigher()) minecraftGLView.requestPointerCapture();
            }
            switch(ev.getActionMasked()) {
                case MotionEvent.ACTION_HOVER_MOVE:
                    mouse_x = (int) (ev.getX(mouseCursorIndex) * scaleFactor);
                    mouse_y = (int) (ev.getY(mouseCursorIndex) * scaleFactor);
                    CallbackBridge.mouseX = mouse_x;
                    CallbackBridge.mouseY = mouse_y;
                    CallbackBridge.sendCursorPos(mouse_x,mouse_y);
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

    }
    boolean isKeyboard(KeyEvent evt) {
        System.out.println("Event:" +evt);
        //if((evt.getFlags() & KeyEvent.FLAG_SOFT_KEYBOARD) == KeyEvent.FLAG_SOFT_KEYBOARD) return true;
        //if(evt.getSource() == InputDevice.SOURCE_KEYBOARD) return true;
        //if(evt.getUnicodeChar() != 0) return true;
        if(AndroidLWJGLKeycode.androidToLwjglMap.containsKey(evt.getKeyCode())) return true;
        return false;
    }
    byte[] kevArray = new byte[8];
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        System.out.println(event.getSource() + " "+ event.getFlags());
        if(event.getSource() == InputDevice.SOURCE_CLASS_JOYSTICK) {
            switch(event.getKeyCode()) {
                case KeyEvent.KEYCODE_BUTTON_A:
                    kevArray[0]= (byte) ((event.getAction() == KeyEvent.ACTION_DOWN)?1:0);
                case KeyEvent.KEYCODE_BUTTON_B:
                    kevArray[1]=(byte) ((event.getAction() == KeyEvent.ACTION_DOWN)?1:0);
                case KeyEvent.KEYCODE_BUTTON_X:
                    kevArray[2]=(byte) ((event.getAction() == KeyEvent.ACTION_DOWN)?1:0);
                case KeyEvent.KEYCODE_BUTTON_Y:
                    kevArray[3]=(byte) ((event.getAction() == KeyEvent.ACTION_DOWN)?1:0);
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    kevArray[4]=(byte) ((event.getAction() == KeyEvent.ACTION_DOWN)?1:0);
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    kevArray[5]=(byte) ((event.getAction() == KeyEvent.ACTION_DOWN)?1:0);
                case KeyEvent.KEYCODE_DPAD_UP:
                    kevArray[6]=(byte) ((event.getAction() == KeyEvent.ACTION_DOWN)?1:0);
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    kevArray[7]=(byte) ((event.getAction() == KeyEvent.ACTION_DOWN)?1:0);
            }
            CallbackBridge.nativePutControllerButtons(ByteBuffer.wrap(kevArray));
            return true;
        }else if(isKeyboard(event)) {
             AndroidLWJGLKeycode.execKey(event,event.getKeyCode(),event.getAction() == KeyEvent.ACTION_DOWN);
            return true;
        }else return false;
    }

    //private Dialog menuDial;
    @Override
    public void onResume() {
        super.onResume();
        mIsResuming = true;
        // if (minecraftGLView != null) minecraftGLView.requestRender();
        final int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        final View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(uiOptions);
    }

    @Override
    protected void onPause()
    {
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

    // private FileObserver mLogObserver;
    private void runCraft() throws Throwable {
        System.gc();
        /* Old logger
        if (Tools.LAUNCH_TYPE != Tools.LTYPE_PROCESS) {
            currLogFile = JREUtils.redirectStdio(true);
            // DEPRECATED constructor (String) api 29
            mLogObserver = new FileObserver(currLogFile.getAbsolutePath(), FileObserver.MODIFY){
                @Override
                public void onEvent(int event, String file) {
                    try {
                        if (event == FileObserver.MODIFY && currLogFile.length() > 0l) {
                            System.out.println(Tools.read(currLogFile.getAbsolutePath()));
                            Tools.write(currLogFile.getAbsolutePath(), "");
                        }
                    } catch (Throwable th) {
                        Tools.showError(MainActivity.this, th);
                        mLogObserver.stopWatching();
                    }
                }
            };
            mLogObserver.startWatching();
        }
        */
        
        appendlnToLog("--------- beggining with launcher debug");
        checkLWJGL3Installed();
        
        Map<String, String> jreReleaseList = JREUtils.readJREReleaseProperties();
        JREUtils.checkJavaArchitecture(this, jreReleaseList.get("OS_ARCH"));
        checkJavaArgsIsLaunchable(jreReleaseList.get("JAVA_VERSION"));
        // appendlnToLog("Info: Custom Java arguments: \"" + LauncherPreferences.PREF_CUSTOM_JAVA_ARGS + "\"");
        
        JREUtils.redirectAndPrintJRELog(this, mProfile.accessToken);
        Tools.launchMinecraft(this, mProfile, mGameProfile);
    }
    
    private void checkJavaArgsIsLaunchable(String jreVersion) throws Throwable {
        appendlnToLog("Info: Custom Java arguments: \"" + LauncherPreferences.PREF_CUSTOM_JAVA_ARGS + "\"");
        
        if (jreVersion.equals("1.8.0")) return;
        
    /*
        // Test java
        ShellProcessOperation shell = new ShellProcessOperation(new ShellProcessOperation.OnPrintListener(){
            @Override
            public void onPrintLine(String text){
                appendlnToLog("[JRETest] " + text);
            }
        });
        JREUtils.setJavaEnvironment(this, shell);
        
        List<String> testArgs = new ArrayList<String>();
        testArgs.add(Tools.homeJreDir + "/bin/java");
        Tools.getJavaArgs(this, testArgs);
        testArgs.add("-version");
        
        new File(Tools.homeJreDir + "/bin/java").setExecutable(true);
        
        // shell.writeToProcess("chmod 777 " + Tools.homeJreDir + "/bin/java");
        shell.writeToProcess("set -e");
        shell.writeToProcess(testArgs.toArray(new String[0]));
        
        int exitCode = shell.waitFor();
        appendlnToLog("Info: java test command exited with " + exitCode);
        
        if (exitCode != 0) {
            appendlnToLog("Error: the test returned non-zero exit code.");
            // throw new RuntimeException(getString(R.string.mcn_check_fail_java));
        }
    */
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
        String s = "";
        for (String exec : arr) {
            s = s + " " + exec;
        }
        return s;
    }

    private void toggleDebug() {
        debugText.setVisibility(debugText.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
    }

    private void dialogSendCustomKey() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.control_customkey);
        dialog.setItems(AndroidLWJGLKeycode.generateKeyName(), new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface dInterface, int position) {
                    AndroidLWJGLKeycode.execKeyIndex(BaseMainActivity.this, position);
                }
            });
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
    /*
     private void openCanvasOutput() {
     WindowAnimation.fadeIn(contentCanvas, 500);
     }

     public void closeCanvasOutput(View view) {
     WindowAnimation.fadeOut(contentCanvas, 500);
     }
     */
     
    @Override
    public void appendToLog(final String text, boolean checkAllow) {
        logStream.print(text);
        if (checkAllow && !isLogAllow) return;
        textLog.post(new Runnable(){
                @Override
                public void run() {
                    textLog.append(text);
                    contentScroll.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });
    }
    public int mcscale(int input) {
        return (int)((this.guiScale * input)/scaleFactor);
    }

    /*
     public int randomInRange(int min, int max) {
     return min + (int)(Math.random() * (max - min + 1));
     }
     */

    public void toggleMenu(View v) {
        drawerLayout.openDrawer(Gravity.RIGHT);
    }

    public void placeMouseAdd(float x, float y) {
        this.mousePointer.setTranslationX(mousePointer.getTranslationX() + x);
        this.mousePointer.setTranslationY(mousePointer.getTranslationY() + y);
    }

    public void placeMouseAt(float x, float y) {
        this.mousePointer.setTranslationX(x);
        this.mousePointer.setTranslationY(y);
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
            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface p1, int p2)
                {
                    try {
                        fullyExit();
                    } catch (Throwable th) {
                        Log.w(Tools.APP_NAME, "Could not enable System.exit() method!", th);
                    }

                    // If we are unable to enable exit, use method: kill myself.
                    // android.os.Process.killProcess(android.os.Process.myPid());

                    // Toast.makeText(MainActivity.this, "Could not exit. Please force close this app.", Toast.LENGTH_LONG).show();
                }
            })
            .show();
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
        // this.secondaryButton.setBackgroundDrawable(this.rightOverride ? this.secondaryButtonColorBackground : this.secondaryButtonDefaultBackground);
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
                sendKeyPress(AndroidLWJGLKeycode.androidToLwjglMap.get(keyCode), keyChar, 0, CallbackBridge.getCurrentMods(), true);
                sendKeyPress(AndroidLWJGLKeycode.androidToLwjglMap.get(keyCode), keyChar, 0, CallbackBridge.getCurrentMods(), false);
            } catch (IllegalAccessException | NoSuchFieldException e) {

            }
        }else{
            sendKeyPress(0, keyChar, 0, CallbackBridge.getCurrentMods(), true);
            sendKeyPress(0, keyChar, 0, CallbackBridge.getCurrentMods(), false);
        }
    }

    public void sendKeyPress(int keyCode) {
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

    public void getMcScale() {
        //Get the scale stored in game files, used auto scale if found or if the stored scaled is bigger than the authorized size.
        String str = MCOptionUtils.get("guiScale");
        this.guiScale = (str == null ? 0 :Integer.parseInt(str));
        

        int scale = Math.max(Math.min(CallbackBridge.windowWidth / 320, CallbackBridge.windowHeight / 240), 1);
        if(scale < this.guiScale || guiScale == 0){
            this.guiScale = scale;
        }
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
        b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                LauncherPreferences.PREF_MOUSESPEED = ((float)tmpMouseSpeed)/100f;
                LauncherPreferences.DEFAULT_PREF.edit().putInt("mousespeed",tmpMouseSpeed).commit();
                dialogInterface.dismiss();
                System.gc();
            }
        });
        b.setNegativeButton(android.R.string.cancel,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                System.gc();
            }
        });
        b.show();
    }
}
