package net.kdt.pojavlaunch;

import static net.kdt.pojavlaunch.MainActivity.fullyExit;

import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;

import com.kdt.LoggerView;

import net.kdt.pojavlaunch.customcontrols.keyboard.AwtCharSender;
import net.kdt.pojavlaunch.customcontrols.keyboard.TouchCharInput;
import net.kdt.pojavlaunch.multirt.MultiRTUtils;
import net.kdt.pojavlaunch.multirt.Runtime;
import net.kdt.pojavlaunch.prefs.LauncherPreferences;
import net.kdt.pojavlaunch.utils.JREUtils;
import net.kdt.pojavlaunch.utils.MathUtils;

import org.lwjgl.glfw.CallbackBridge;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class JavaGUILauncherActivity extends BaseActivity implements View.OnTouchListener {

    private AWTCanvasView mTextureView;
    private LoggerView mLoggerView;
    private TouchCharInput mTouchCharInput;

    private LinearLayout mTouchPad;
    private ImageView mMousePointerImageView;
    private GestureDetector mGestureDetector;

    private boolean mSkipDetectMod, mIsVirtualMouseEnabled;
    
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_java_gui_launcher);

        try {
            File latestLogFile = new File(Tools.DIR_GAME_HOME, "latestlog.txt");
            if (!latestLogFile.exists() && !latestLogFile.createNewFile())
                throw new IOException("Failed to create a new log file");
            Logger.begin(latestLogFile.getAbsolutePath());
        }catch (IOException e) {
            Tools.showError(this, e, true);
        }
        MainActivity.GLOBAL_CLIPBOARD = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        mTouchCharInput = findViewById(R.id.awt_touch_char);
        mTouchCharInput.setCharacterSender(new AwtCharSender());

        mTouchPad = findViewById(R.id.main_touchpad);
        mLoggerView = findViewById(R.id.launcherLoggerView);
        mMousePointerImageView = findViewById(R.id.main_mouse_pointer);
        mTextureView = findViewById(R.id.installmod_surfaceview);
        mGestureDetector = new GestureDetector(this, new SingleTapConfirm());
        mTouchPad.setFocusable(false);
        mTouchPad.setVisibility(View.GONE);

        findViewById(R.id.installmod_mouse_pri).setOnTouchListener(this);
        findViewById(R.id.installmod_mouse_sec).setOnTouchListener(this);
        findViewById(R.id.installmod_window_moveup).setOnTouchListener(this);
        findViewById(R.id.installmod_window_movedown).setOnTouchListener(this);
        findViewById(R.id.installmod_window_moveleft).setOnTouchListener(this);
        findViewById(R.id.installmod_window_moveright).setOnTouchListener(this);

        mMousePointerImageView.post(() -> {
            ViewGroup.LayoutParams params = mMousePointerImageView.getLayoutParams();
            params.width = (int) (36 / 100f * LauncherPreferences.PREF_MOUSESCALE);
            params.height = (int) (54 / 100f * LauncherPreferences.PREF_MOUSESCALE);
        });

        mTouchPad.setOnTouchListener(new View.OnTouchListener() {
            float prevX = 0, prevY = 0;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // MotionEvent reports input details from the touch screen
                // and other input controls. In this case, you are only
                // interested in events where the touch position changed.
                // int index = event.getActionIndex();
                int action = event.getActionMasked();

                float x = event.getX();
                float y = event.getY();
                float mouseX, mouseY;

                mouseX = mMousePointerImageView.getX();
                mouseY = mMousePointerImageView.getY();

                if (mGestureDetector.onTouchEvent(event)) {
                    sendScaledMousePosition(mouseX,mouseY);
                    AWTInputBridge.sendMousePress(AWTInputEvent.BUTTON1_DOWN_MASK);
                } else {
                    if (action == MotionEvent.ACTION_MOVE) { // 2
                        mouseX = Math.max(0, Math.min(CallbackBridge.physicalWidth, mouseX + x - prevX));
                        mouseY = Math.max(0, Math.min(CallbackBridge.physicalHeight, mouseY + y - prevY));
                        placeMouseAt(mouseX, mouseY);
                        sendScaledMousePosition(mouseX, mouseY);
                    }
                }

                prevY = y;
                prevX = x;
                return true;
            }
        });

        mTextureView.setOnTouchListener((v, event) -> {
            float x = event.getX();
            float y = event.getY();
            if (mGestureDetector.onTouchEvent(event)) {
                sendScaledMousePosition(x + mTextureView.getX(), y);
                AWTInputBridge.sendMousePress(AWTInputEvent.BUTTON1_DOWN_MASK);
                return true;
            }

            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_UP: // 1
                case MotionEvent.ACTION_CANCEL: // 3
                case MotionEvent.ACTION_POINTER_UP: // 6
                    break;
                case MotionEvent.ACTION_MOVE: // 2
                    sendScaledMousePosition(x + mTextureView.getX(), y);
                    break;
            }
            return true;
        });

        try {

            placeMouseAt(CallbackBridge.physicalWidth / 2f, CallbackBridge.physicalHeight / 2f);
            
            final File modFile = (File) getIntent().getExtras().getSerializable("modFile");
            final String javaArgs = getIntent().getExtras().getString("javaArgs");
            String jreName = LauncherPreferences.PREF_DEFAULT_RUNTIME;
            if(modFile != null) {
                int javaVersion = getJavaVersion(modFile);
                if(javaVersion != -1) {
                    String autoselectRuntime = MultiRTUtils.getNearestJreName(javaVersion);
                    if (autoselectRuntime != null) jreName = autoselectRuntime;
                }
            }
            final Runtime runtime = MultiRTUtils.forceReread(jreName);

            mSkipDetectMod = getIntent().getExtras().getBoolean("skipDetectMod", false);
            if(getIntent().getExtras().getBoolean("openLogOutput", false)) openLogOutput(null);
            if (mSkipDetectMod) {
                new Thread(() -> launchJavaRuntime(runtime, modFile, javaArgs), "JREMainThread").start();
                return;
            }

            // No skip detection
            openLogOutput(null);
            new Thread(() -> {
                try {
                    final int exit = doCustomInstall(runtime, modFile, javaArgs);
                    Logger.appendToLog(getString(R.string.toast_optifine_success));
                    if (exit != 0) return;
                    runOnUiThread(() -> {
                        Toast.makeText(JavaGUILauncherActivity.this, R.string.toast_optifine_success, Toast.LENGTH_SHORT).show();
                        fullyExit();
                    });

                } catch (Throwable e) {
                    Logger.appendToLog("Install failed:");
                    Logger.appendToLog(Log.getStackTraceString(e));
                    Tools.showError(JavaGUILauncherActivity.this, e);
                }
            }, "Installer").start();
        } catch (Throwable th) {
            Tools.showError(this, th, true);
        }


        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                MainActivity.dialogForceClose(JavaGUILauncherActivity.this);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        final int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        final View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(uiOptions);
    }



    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent e) {
        boolean isDown;
        switch (e.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: // 0
            case MotionEvent.ACTION_POINTER_DOWN: // 5
                isDown = true;
                break;
            case MotionEvent.ACTION_UP: // 1
            case MotionEvent.ACTION_CANCEL: // 3
            case MotionEvent.ACTION_POINTER_UP: // 6
                isDown = false;
                break;
            default:
                return false;
        }
        
        switch (v.getId()) {
            case R.id.installmod_mouse_pri:
                AWTInputBridge.sendMousePress(AWTInputEvent.BUTTON1_DOWN_MASK, isDown);
                break;
                
            case R.id.installmod_mouse_sec:
                AWTInputBridge.sendMousePress(AWTInputEvent.BUTTON3_DOWN_MASK, isDown);
                break;
        }
        if(isDown) switch(v.getId()) {
            case R.id.installmod_window_moveup:
                AWTInputBridge.nativeMoveWindow(0, -10);
                break;
            case R.id.installmod_window_movedown:
                AWTInputBridge.nativeMoveWindow(0, 10);
                break;
            case R.id.installmod_window_moveleft:
                AWTInputBridge.nativeMoveWindow(-10, 0);
                break;
            case R.id.installmod_window_moveright:
                AWTInputBridge.nativeMoveWindow(10, 0);
                break;
        }
        return true;
    }

    public void placeMouseAt(float x, float y) {
        mMousePointerImageView.setX(x);
        mMousePointerImageView.setY(y);
    }

    @SuppressWarnings("SuspiciousNameCombination")
    void sendScaledMousePosition(float x, float y){
        // Clamp positions to the borders of the usable view, then scale them
        x = androidx.core.math.MathUtils.clamp(x, mTextureView.getX(), mTextureView.getX() + mTextureView.getWidth());
        y = androidx.core.math.MathUtils.clamp(y, mTextureView.getY(), mTextureView.getY() + mTextureView.getHeight());

        AWTInputBridge.sendMousePos(
                (int) MathUtils.map(x, mTextureView.getX(), mTextureView.getX() + mTextureView.getWidth(), 0, AWTCanvasView.AWT_CANVAS_WIDTH),
                (int) MathUtils.map(y, mTextureView.getY(), mTextureView.getY() + mTextureView.getHeight(), 0, AWTCanvasView.AWT_CANVAS_HEIGHT)
                );
    }

    public void forceClose(View v) {
        MainActivity.dialogForceClose(this);
    }

    public void openLogOutput(View v) {
        mLoggerView.setVisibility(View.VISIBLE);
    }

    public void toggleVirtualMouse(View v) {
        mIsVirtualMouseEnabled = !mIsVirtualMouseEnabled;
        mTouchPad.setVisibility(mIsVirtualMouseEnabled ? View.VISIBLE : View.GONE);
        Toast.makeText(this,
                mIsVirtualMouseEnabled ? R.string.control_mouseon : R.string.control_mouseoff,
                Toast.LENGTH_SHORT).show();
    }

    public int launchJavaRuntime(Runtime runtime, File modFile, String javaArgs) {
        JREUtils.redirectAndPrintJRELog();
        try {
            List<String> javaArgList = new ArrayList<>();

            // Enable Caciocavallo
            Tools.getCacioJavaArgs(javaArgList,runtime.javaVersion == 8);
            
            if (javaArgs != null) {
                javaArgList.addAll(Arrays.asList(javaArgs.split(" ")));
            } else {
                javaArgList.add("-jar");
                javaArgList.add(modFile.getAbsolutePath());
            }

            
            if (LauncherPreferences.PREF_JAVA_SANDBOX) {
                Collections.reverse(javaArgList);
                javaArgList.add("-Xbootclasspath/a:" + Tools.DIR_DATA + "/security/pro-grade.jar");
                javaArgList.add("-Djava.security.manager=net.sourceforge.prograde.sm.ProGradeJSM");
                javaArgList.add("-Djava.security.policy=" + Tools.DIR_DATA + "/security/java_sandbox.policy");
                Collections.reverse(javaArgList);
            }

            Logger.appendToLog("Info: Java arguments: " + Arrays.toString(javaArgList.toArray(new String[0])));

            return JREUtils.launchJavaVM(this, runtime,null,javaArgList, LauncherPreferences.PREF_CUSTOM_JAVA_ARGS);
        } catch (Throwable th) {
            Tools.showError(this, th, true);
            return -1;
        }
    }



    private int doCustomInstall(Runtime runtime, File modFile, String javaArgs) {
        mSkipDetectMod = true;
        return launchJavaRuntime(runtime, modFile, javaArgs);
    }

    public void toggleKeyboard(View view) {
        mTouchCharInput.switchKeyboardState();
    }
    public void performCopy(View view) {
        AWTInputBridge.sendKey(' ', AWTInputEvent.VK_CONTROL, 1);
        AWTInputBridge.sendKey(' ', AWTInputEvent.VK_C);
        AWTInputBridge.sendKey(' ', AWTInputEvent.VK_CONTROL, 0);
    }

    public void performPaste(View view) {
        AWTInputBridge.sendKey(' ', AWTInputEvent.VK_CONTROL, 1);
        AWTInputBridge.sendKey(' ', AWTInputEvent.VK_V);
        AWTInputBridge.sendKey(' ', AWTInputEvent.VK_CONTROL, 0);
    }

    public int getJavaVersion(File modFile) {
        try (ZipFile zipFile = new ZipFile(modFile)){
            ZipEntry manifest = zipFile.getEntry("META-INF/MANIFEST.MF");
            if(manifest == null) return -1;

            String manifestString = Tools.read(zipFile.getInputStream(manifest));
            String mainClass = Tools.extractUntilCharacter(manifestString, "Main-Class:", '\n');
            if(mainClass == null) return -1;

            mainClass = mainClass.trim().replace('.', '/') + ".class";
            ZipEntry mainClassFile = zipFile.getEntry(mainClass);
            if(mainClassFile == null) return -1;

            InputStream classStream = zipFile.getInputStream(mainClassFile);
            byte[] bytesWeNeed = new byte[8];
            int readCount = classStream.read(bytesWeNeed);
            classStream.close();
            if(readCount < 8) return -1;

            ByteBuffer byteBuffer = ByteBuffer.wrap(bytesWeNeed);
            if(byteBuffer.getInt() != 0xCAFEBABE) return -1;
            short minorVersion = byteBuffer.getShort();
            short majorVersion = byteBuffer.getShort();
            Log.i("JavaGUILauncher", majorVersion+","+minorVersion);
            return classVersionToJavaVersion(majorVersion);
        }catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
    public static int classVersionToJavaVersion(int majorVersion) {
        if(majorVersion < 46) return 2; // there isn't even an arm64 port of jre 1.1 (or anything before 1.8 in fact)
        return majorVersion - 44;
    }
}
