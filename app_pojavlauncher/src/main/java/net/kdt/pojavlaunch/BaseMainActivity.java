package net.kdt.pojavlaunch;

import static net.kdt.pojavlaunch.Architecture.ARCH_X86;
import static net.kdt.pojavlaunch.Tools.currentDisplayMetrics;

import static org.lwjgl.glfw.CallbackBridge.sendKeyPress;
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

    public float scaleFactor = 1;
    public double sensitivityFactor;

    private boolean mIsResuming = false;

    private MinecraftGLView minecraftGLView;

    private static Touchpad touchpad;
    private MinecraftAccount mProfile;
    
    private DrawerLayout drawerLayout;
    private NavigationView navDrawer;

    private LinearLayout contentLog;
    private TextView textLog;
    private ScrollView contentScroll;
    private ToggleButton toggleLog;


    private NavigationView.OnNavigationItemSelectedListener gameActionListener;
    public NavigationView.OnNavigationItemSelectedListener ingameControlsEditorListener;

    protected volatile JMinecraftVersionList.Version mVersionInfo;

    private File logFile;
    private PrintStream logStream;
    private PerVersionConfig.VersionConfig config;
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
            
            Tools.getDisplayMetrics(this);
            sensitivityFactor = 1.4 * (1080f/ currentDisplayMetrics.heightPixels);
            windowWidth = Tools.getDisplayFriendlyRes(currentDisplayMetrics.widthPixels, scaleFactor);
            windowHeight = Tools.getDisplayFriendlyRes(currentDisplayMetrics.heightPixels, scaleFactor);
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
                    case R.id.nav_debug: minecraftGLView.togglepointerDebugging();
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


            this.minecraftGLView = findViewById(R.id.main_game_render_view);
            this.drawerLayout.closeDrawers();

            minecraftGLView.setSurfaceReadyListener(() -> {
                try {
                    runCraft();
                }catch (Throwable e){
                    Tools.showError(getApplicationContext(), e, true);
                }
            });

            minecraftGLView.start();

        } catch (Throwable e) {
            Tools.showError(this, e, true);
        }
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

    private void dialogSendCustomKey() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.control_customkey);
        dialog.setItems(EfficientAndroidLWJGLKeycode.generateKeyName(), (dInterface, position) -> EfficientAndroidLWJGLKeycode.execKeyIndex(position));
        dialog.show();
    }

    boolean isInEditor;
    private void openCustomControls() {
        if(ingameControlsEditorListener == null) return;

        ((MainActivity)this).mControlLayout.setModifiable(true);
        navDrawer.getMenu().clear();
        navDrawer.inflateMenu(R.menu.menu_customctrl);
        navDrawer.setNavigationItemSelectedListener(ingameControlsEditorListener);
        isInEditor = true;
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
