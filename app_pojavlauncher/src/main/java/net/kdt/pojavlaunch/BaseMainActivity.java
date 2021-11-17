package net.kdt.pojavlaunch;

import static net.kdt.pojavlaunch.Architecture.ARCH_X86;

import static org.lwjgl.glfw.CallbackBridge.windowHeight;
import static org.lwjgl.glfw.CallbackBridge.windowWidth;

import android.app.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.graphics.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;

import androidx.drawerlayout.widget.*;
import com.google.android.material.navigation.*;
import java.io.*;
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
    public static TouchCharInput touchCharInput;

    volatile public static boolean isInputStackCall;

    private Gamepad gamepad;

    private DisplayMetrics displayMetrics;
    public float scaleFactor = 1;
    public double sensitivityFactor;

    private boolean mIsResuming = false;



    private MinecraftGLView minecraftGLView;
    private int guiScale;

    
    private static boolean isVirtualMouseEnabled;
    private static Touchpad touchpad;
    private ImageView mousePointer;
    private MinecraftAccount mProfile;
    
    private DrawerLayout drawerLayout;
    private NavigationView navDrawer;

    private LinearLayout contentLog;
    private TextView textLog;
    private ScrollView contentScroll;
    private ToggleButton toggleLog;

    private TextView debugText;
    private NavigationView.OnNavigationItemSelectedListener gameActionListener;
    public NavigationView.OnNavigationItemSelectedListener ingameControlsEditorListener;

    protected volatile JMinecraftVersionList.Version mVersionInfo;

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
            touchCharInput = findViewById(R.id.editTextTextPersonName2);

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
            windowWidth = Tools.getDisplayFriendlyRes(displayMetrics.widthPixels, scaleFactor);
            windowHeight = Tools.getDisplayFriendlyRes(displayMetrics.heightPixels, scaleFactor);
            System.out.println("WidthHeight: " + windowWidth + ":" + windowHeight);



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

            touchpad = findViewById(R.id.main_touchpad);

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
                        mouse_x += (e.getX()*scaleFactor);
                        mouse_y += (e.getY()*scaleFactor);
                        CallbackBridge.mouseX = (int) mouse_x;
                        CallbackBridge.mouseY = (int) mouse_y;
                        if(!CallbackBridge.isGrabbing()){
                            view.releasePointerCapture();
                            view.clearFocus();
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

            minecraftGLView.setSurfaceReadyListener(() -> {
                try {
                    runCraft();
                }catch (Throwable e){
                    Tools.showError(getApplicationContext(), e, true);
                }
            });

            minecraftGLView.init();

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
            if(BaseMainActivity.isAndroid8OrHigher()){
                minecraftGLView.requestFocus();
                minecraftGLView.requestPointerCapture();
            }
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

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
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

        int index = EfficientAndroidLWJGLKeycode.getIndexByKey(eventKeycode);
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
        if (Tools.DEVICE_ARCHITECTURE == ARCH_X86
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
                MainActivity.mControlLayout.hideAllHandleViews();
                MainActivity.mControlLayout.loadLayout((CustomControls)null);
                MainActivity.mControlLayout.setModifiable(false);
                System.gc();
                MainActivity.mControlLayout.loadLayout(LauncherPreferences.DEFAULT_PREF.getString("defaultCtrl",Tools.CTRLDEF_FILE));
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


    public void toggleMenu(View v) {
        drawerLayout.openDrawer(Gravity.RIGHT);
    }

    public void placeMouseAdd(float x, float y) {
        this.mousePointer.setX(mousePointer.getX() + x);
        this.mousePointer.setY(mousePointer.getY() + y);
    }



    public static void toggleMouse(Context ctx) {
        if (CallbackBridge.isGrabbing()) return;

        Toast.makeText(ctx, touchpad.switchState()
                 ? R.string.control_mouseon : R.string.control_mouseoff,
                Toast.LENGTH_SHORT).show();
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

    public static void switchKeyboardState() {
        if(touchCharInput != null) touchCharInput.switchKeyboardState();
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
