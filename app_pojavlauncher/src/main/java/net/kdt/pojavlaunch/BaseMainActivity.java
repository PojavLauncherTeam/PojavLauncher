package net.kdt.pojavlaunch;

import static net.kdt.pojavlaunch.Architecture.ARCH_X86;
import static net.kdt.pojavlaunch.Tools.currentDisplayMetrics;
import static net.kdt.pojavlaunch.prefs.LauncherPreferences.PREF_VIRTUAL_MOUSE_START;

import static org.lwjgl.glfw.CallbackBridge.sendKeyPress;
import static org.lwjgl.glfw.CallbackBridge.windowHeight;
import static org.lwjgl.glfw.CallbackBridge.windowWidth;

import android.app.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.*;

import com.kdt.LoggerView;

import java.io.*;
import java.util.*;
import net.kdt.pojavlaunch.customcontrols.*;

import net.kdt.pojavlaunch.customcontrols.keyboard.LwjglCharSender;
import net.kdt.pojavlaunch.customcontrols.keyboard.TouchCharInput;
import net.kdt.pojavlaunch.multirt.MultiRTUtils;

import net.kdt.pojavlaunch.prefs.*;
import net.kdt.pojavlaunch.utils.*;
import net.kdt.pojavlaunch.value.*;
import net.kdt.pojavlaunch.value.launcherprofiles.LauncherProfiles;
import net.kdt.pojavlaunch.value.launcherprofiles.MinecraftProfile;

import org.lwjgl.glfw.*;
import android.net.*;

public class BaseMainActivity extends BaseActivity {
    public static volatile ClipboardManager GLOBAL_CLIPBOARD;
    public static TouchCharInput touchCharInput;

    volatile public static boolean isInputStackCall;

    public float scaleFactor = 1;

    private boolean mIsResuming = false;

    ControlLayout mControlLayout;
    private MinecraftGLSurface minecraftGLView;
    private static Touchpad touchpad;
    private LoggerView loggerView;

    MinecraftAccount mProfile;
    MinecraftProfile minecraftProfile;
    
    private DrawerLayout drawerLayout;
    private ListView navDrawer;

    private ArrayAdapter<String> gameActionArrayAdapter;
    private AdapterView.OnItemClickListener gameActionClickListener;
    public ArrayAdapter<String> ingameControlsEditorArrayAdapter;
    public AdapterView.OnItemClickListener ingameControlsEditorListener;
    //public NavigationView.OnNavigationItemSelectedListener ingameControlsEditorListener;

    protected volatile JMinecraftVersionList.Version mVersionInfo;

    //private PerVersionConfig.VersionConfig config;

    protected void initLayout(int resId) {
        setContentView(resId);
        try {
            Logger.getInstance().reset();
            // FIXME: is it safe fot multi thread?
            GLOBAL_CLIPBOARD = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            touchCharInput = findViewById(R.id.mainTouchCharInput);
            touchCharInput.setCharacterSender(new LwjglCharSender());
            loggerView = findViewById(R.id.mainLoggerView);
            mControlLayout = findViewById(R.id.main_control_layout);
            
            mProfile = PojavProfile.getCurrentProfileContent(this);
            LauncherProfiles.update();
            minecraftProfile = LauncherProfiles.mainProfileJson.profiles.get(LauncherPreferences.DEFAULT_PREF.getString(LauncherPreferences.PREF_KEY_CURRENT_PROFILE,""));
            if(minecraftProfile == null) {
                Toast.makeText(this,"Attempted to launch nonexistent profile",Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            String runtime = LauncherPreferences.PREF_DEFAULT_RUNTIME;

                mVersionInfo = Tools.getVersionInfo(null, BaseLauncherActivity.getVersionId(
                        minecraftProfile.lastVersionId));
                if(minecraftProfile.javaDir != null && minecraftProfile.javaDir.startsWith(Tools.LAUNCHERPROFILES_RTPREFIX)) {
                    String runtimeName = minecraftProfile.javaDir.substring(Tools.LAUNCHERPROFILES_RTPREFIX.length());
                    if(MultiRTUtils.forceReread(runtimeName).versionString != null) {
                        runtime = runtimeName;
                    }
                }
                if(minecraftProfile.pojavRendererName != null) {
                    Log.i("RdrDebug","__P_renderer="+minecraftProfile.pojavRendererName);
                    Tools.LOCAL_RENDERER = minecraftProfile.pojavRendererName;
                }
            
            setTitle("Minecraft " + minecraftProfile.lastVersionId);

            MultiRTUtils.setRuntimeNamed(this,runtime);
            // Minecraft 1.13+
            isInputStackCall = mVersionInfo.arguments != null;
            
            Tools.getDisplayMetrics(this);
            windowWidth = Tools.getDisplayFriendlyRes(currentDisplayMetrics.widthPixels, scaleFactor);
            windowHeight = Tools.getDisplayFriendlyRes(currentDisplayMetrics.heightPixels, scaleFactor);
            System.out.println("WidthHeight: " + windowWidth + ":" + windowHeight);


            // Menu
            drawerLayout = findViewById(R.id.main_drawer_options);

            navDrawer = findViewById(R.id.main_navigation_view);

            gameActionArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.menu_ingame));
            gameActionClickListener = (parent, view, position, id) -> {
                switch(position) {
                    case 0:
                        dialogForceClose(BaseMainActivity.this);
                        break;
                    case 1:
                        openLogOutput();
                        break;
                    case 2:
                        minecraftGLView.togglepointerDebugging();
                        break;
                    case 3:
                        dialogSendCustomKey();
                        break;
                    case 4:
                        adjustMouseSpeedLive();
                        break;
                    case 5:
                        openCustomControls();
                }
                drawerLayout.closeDrawers();
            };
            navDrawer.setAdapter(gameActionArrayAdapter);
            navDrawer.setOnItemClickListener(gameActionClickListener);

            touchpad = findViewById(R.id.main_touchpad);


            this.minecraftGLView = findViewById(R.id.main_game_render_view);
            this.drawerLayout.closeDrawers();

            minecraftGLView.setSurfaceReadyListener(() -> {
                try {
                    // Setup virtual mouse right before launching
                    if (PREF_VIRTUAL_MOUSE_START)
                        touchpad.switchState();

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
        CallbackBridge.nativeSetWindowAttrib(LwjglGlfwKeycode.GLFW_HOVERED, 1);
    }

    @Override
    protected void onPause() {
        if (CallbackBridge.isGrabbing()){
            sendKeyPress(LwjglGlfwKeycode.GLFW_KEY_ESCAPE);
        }
        CallbackBridge.nativeSetWindowAttrib(LwjglGlfwKeycode.GLFW_HOVERED, 0);
        mIsResuming = false;
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        CallbackBridge.nativeSetWindowAttrib(LwjglGlfwKeycode.GLFW_VISIBLE, 1);
    }

    @Override
    protected void onStop() {
        CallbackBridge.nativeSetWindowAttrib(LwjglGlfwKeycode.GLFW_VISIBLE, 0);
        super.onStop();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        Tools.updateWindowSize(this);
        minecraftGLView.refreshSize();
        runOnUiThread(() -> mControlLayout.refreshControlButtonPositions());
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(minecraftGLView != null)  // Useful when backing out of the app
            new Handler(Looper.getMainLooper()).postDelayed(() -> minecraftGLView.refreshSize(), 500);
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
        Logger.getInstance().appendToLog("--------- beginning with launcher debug");
        Logger.getInstance().appendToLog("Info: Launcher version: " + BuildConfig.VERSION_NAME);
        if (Tools.LOCAL_RENDERER.equals("vulkan_zink")) {
            checkVulkanZinkIsSupported();
        }
        checkLWJGL3Installed();
        
        JREUtils.jreReleaseList = JREUtils.readJREReleaseProperties();
        Logger.getInstance().appendToLog("Architecture: " + Architecture.archAsString(Tools.DEVICE_ARCHITECTURE));
        checkJavaArgsIsLaunchable(JREUtils.jreReleaseList.get("JAVA_VERSION"));
        // appendlnToLog("Info: Custom Java arguments: \"" + LauncherPreferences.PREF_CUSTOM_JAVA_ARGS + "\"");

        Logger.getInstance().appendToLog("Info: Selected Minecraft version: " + mVersionInfo.id +
            ((mVersionInfo.inheritsFrom == null || mVersionInfo.inheritsFrom.equals(mVersionInfo.id)) ?
            "" : " (" + mVersionInfo.inheritsFrom + ")"));


        JREUtils.redirectAndPrintJRELog();
            LauncherProfiles.update();
            Tools.launchMinecraft(this, mProfile, BaseLauncherActivity.getVersionId(
                    minecraftProfile.lastVersionId));
    }
    
    private void checkJavaArgsIsLaunchable(String jreVersion) throws Throwable {
        Logger.getInstance().appendToLog("Info: Custom Java arguments: \"" + LauncherPreferences.PREF_CUSTOM_JAVA_ARGS + "\"");
    }

    private void checkLWJGL3Installed() {
        File lwjgl3dir = new File(Tools.DIR_GAME_HOME, "lwjgl3");
        if (!lwjgl3dir.exists() || lwjgl3dir.isFile() || lwjgl3dir.list().length == 0) {
            Logger.getInstance().appendToLog("Error: LWJGL3 was not installed!");
            throw new RuntimeException(getString(R.string.mcn_check_fail_lwjgl));
        } else {
            Logger.getInstance().appendToLog("Info: LWJGL3 directory: " + Arrays.toString(lwjgl3dir.list()));
        }
    }

    private void checkVulkanZinkIsSupported() {
        if (Tools.DEVICE_ARCHITECTURE == ARCH_X86
         || Build.VERSION.SDK_INT < 25
         || !getPackageManager().hasSystemFeature(PackageManager.FEATURE_VULKAN_HARDWARE_LEVEL)
         || !getPackageManager().hasSystemFeature(PackageManager.FEATURE_VULKAN_HARDWARE_VERSION)) {
            Logger.getInstance().appendToLog("Error: Vulkan Zink renderer is not supported!");
            throw new RuntimeException(getString(R.string. mcn_check_fail_vulkan_support));
        }
    }
    
    public void printStream(InputStream stream) {
        try {
            BufferedReader buffStream = new BufferedReader(new InputStreamReader(stream));
            String line = null;
            while ((line = buffStream.readLine()) != null) {
                Logger.getInstance().appendToLog(line);
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
        if(ingameControlsEditorListener == null || ingameControlsEditorArrayAdapter == null) return;

        ((MainActivity)this).mControlLayout.setModifiable(true);
        navDrawer.setAdapter(ingameControlsEditorArrayAdapter);
        navDrawer.setOnItemClickListener(ingameControlsEditorListener);
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
        navDrawer.setAdapter(gameActionArrayAdapter);
        navDrawer.setOnItemClickListener(gameActionClickListener);
        isInEditor = false;
    }
    private void openLogOutput() {
        loggerView.setVisibility(View.VISIBLE);
        mIsResuming = false;
    }

    public void closeLogOutput(View view) {
        loggerView.setVisibility(View.GONE);
        mIsResuming = true;
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
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && !touchCharInput.isEnabled()) {
            if(event.getAction() != KeyEvent.ACTION_UP) return true; // We eat it anyway
            sendKeyPress(LwjglGlfwKeycode.GLFW_KEY_ESCAPE);
            return true;
        }
        return minecraftGLView.processKeyEvent(event);
    }

    public static void switchKeyboardState() {
        if(touchCharInput != null) touchCharInput.switchKeyboardState();
    }


    int tmpMouseSpeed;
    public void adjustMouseSpeedLive() {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle(R.string.mcl_setting_title_mousespeed);
        View v = LayoutInflater.from(this).inflate(R.layout.dialog_live_mouse_speed_editor,null);
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

    public static void openLink(String link) {
        Context ctx = touchpad.getContext(); // no more better way to obtain a context statically
        ((Activity)ctx).runOnUiThread(() -> {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(link.replace("file://", "content://")), "*/*");
                ctx.startActivity(intent);
            } catch (Throwable th) {
                Tools.showError(ctx, th);
            }
        });
    }
}
