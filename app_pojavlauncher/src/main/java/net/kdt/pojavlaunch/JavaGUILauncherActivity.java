package net.kdt.pojavlaunch;

import static net.kdt.pojavlaunch.MainActivity.fullyExit;

import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
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
import net.kdt.pojavlaunch.utils.KeyEncoder;
import net.kdt.pojavlaunch.utils.MathUtils;

import org.lwjgl.glfw.CallbackBridge;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JavaGUILauncherActivity extends BaseActivity implements View.OnTouchListener {

    private AWTCanvasView mTextureView;
    private LoggerView mLoggerView;
    private TouchCharInput mTouchCharInput;

    private LinearLayout mTouchPad;
    private ImageView mMousePointerImageView;
    private GestureDetector mGestureDetector;
    private boolean cameraMode = false;
    private long lastPress = 0;
    private ScaleGestureDetector scaleGestureDetector;
    private boolean rcState = false;
    private boolean mSkipDetectMod;
    private static boolean mIsVirtualMouseEnabled;

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

        findViewById(R.id.mouseMode).setOnTouchListener(this);
        findViewById(R.id.keyboard).setOnTouchListener(this);
        findViewById(R.id.camera).setOnTouchListener(this);
        findViewById(R.id.mb2).setOnTouchListener(this);

        mTouchPad = findViewById(R.id.main_touchpad);
        mLoggerView = findViewById(R.id.launcherLoggerView);
        mMousePointerImageView = findViewById(R.id.main_mouse_pointer);
        mTextureView = findViewById(R.id.installmod_surfaceview);
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
        mGestureDetector = new GestureDetector(this, new SingleTapConfirm());
        mTouchPad.setFocusable(false);
        mTouchPad.setVisibility(View.GONE);

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
                    if(rcState) {
                        clearRC();
                    }
                } else {
                    if (action == MotionEvent.ACTION_MOVE) { // 2
                        mouseX = Math.max(0, Math.min(CallbackBridge.physicalWidth, mouseX + x - prevX));
                        mouseY = Math.max(0, Math.min(CallbackBridge.physicalHeight, mouseY + y - prevY));
                        placeMouseAt(mouseX, mouseY);
                        sendScaledMousePosition(mouseX, mouseY);
                    }
                    // Check if there are two fingers on the screen
                    if (action == MotionEvent.ACTION_POINTER_DOWN) {
                        if (event.getPointerCount() == 2) {
                            // Right-click event when a second finger touches the screen
                            // Simulating right-click by sending GLFW_MOUSE_BUTTON_RIGHT event
                            Log.i("downthecrop","Hi from a rightclick event!");
                            //activateRC();
                            AWTInputBridge.sendKey((char)AWTInputEvent.VK_F11,AWTInputEvent.VK_F11);
                            AWTInputBridge.sendMousePress(AWTInputEvent.BUTTON1_DOWN_MASK);
                        }
                    }
                }

                prevY = y;
                prevX = x;
                return true;
            }
        });

        mTextureView.setOnTouchListener((v, event) -> {
            scaleGestureDetector.onTouchEvent(event);
            float x = event.getX();
            float y = event.getY();
            if (mGestureDetector.onTouchEvent(event)) {
                sendScaledMousePosition(x + mTextureView.getX(), y);
                AWTInputBridge.sendMousePress(AWTInputEvent.BUTTON1_DOWN_MASK);
                if(rcState) {
                    clearRC();
                }
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

            String jreName = LauncherPreferences.PREF_DEFAULT_RUNTIME;
            final Runtime runtime = MultiRTUtils.forceReread(jreName);

            mSkipDetectMod = false;
            if (mSkipDetectMod) {
                new Thread(() -> launchJavaRuntime(runtime, ""), "JREMainThread").start();
                return;
            }

            // No skip detection
            openLogOutput(null);
            new Thread(() -> {
                try {
                    final int exit = launchJavaRuntime(runtime, "");
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

    public static class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = detector.getScaleFactor();
            if (scaleFactor > 1) { //Send F4 To Zoom Out
                AWTInputBridge.sendKey((char)AWTInputEvent.VK_F3, AWTInputEvent.VK_F3);
            } else { //116 F3 To Zoom In
                AWTInputBridge.sendKey((char)AWTInputEvent.VK_F4,AWTInputEvent.VK_F4);
            }
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {

        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent e) { // these AWTInputEvent doesn't work for some reason
        long time = System.currentTimeMillis();
        if (time > lastPress + 500) {
            switch (v.getId()) {
                case R.id.keyboard:
                    toggleKeyboard(this.getCurrentFocus());
                    break;
                case R.id.mb2:
                    if (!rcState) {
                        activateRC(); // Send F11 to activate RightClick
                    } else {
                        clearRC(); // Send F10 to clear RightClick
                    }
                    break;
                case R.id.camera:
                    if (!cameraMode) { // Camera Mode On
                        Log.i("downthecrop", "Hello from the camrea Button");
                        AWTInputBridge.sendKey((char) AWTInputEvent.VK_F9, AWTInputEvent.VK_F9); // Send F9
                        cameraMode = true;
                        findViewById(R.id.camera).setBackground(getResources().getDrawable( R.drawable.control_button_pressed ));
                    } else { // Camera Mode off
                        AWTInputBridge.sendKey((char) AWTInputEvent.VK_F8, AWTInputEvent.VK_F8);
                        cameraMode = false;
                        findViewById(R.id.camera).setBackground(getResources().getDrawable( R.drawable.control_button_normal ));
                    }
                    break;
                case R.id.mouseMode:
                    toggleVirtualMouse(this.getCurrentFocus());
            }
            lastPress = time;
        }
        return true;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_DOWN){
            KeyEncoder.sendEncodedChar(event.getKeyCode(),(char)event.getUnicodeChar());
        }
        return true;
    }

    public void placeMouseAt(float x, float y) {
        mMousePointerImageView.setX(x);
        mMousePointerImageView.setY(y);
    }

    private void clearRC(){
        rcState = false;
        findViewById(R.id.mb2).setBackground(getResources().getDrawable( R.drawable.control_button_normal ));
        AWTInputBridge.sendKey((char)AWTInputEvent.VK_F10,AWTInputEvent.VK_F10);
    }

    private void activateRC(){
        rcState = true;
        findViewById(R.id.mb2).setBackground(getResources().getDrawable( R.drawable.control_button_pressed ));
        AWTInputBridge.sendKey((char)AWTInputEvent.VK_F11,AWTInputEvent.VK_F11);
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

    public void openLogOutput(View v) {
        mLoggerView.setVisibility(View.VISIBLE);
    }

    public void toggleVirtualMouse(View v) {
        mIsVirtualMouseEnabled = !mIsVirtualMouseEnabled;
        ImageView view = findViewById(R.id.mouseModeIco);
        if(!mIsVirtualMouseEnabled){
            view.setImageResource(R.drawable.touch);
        } else{
            view.setImageResource(R.drawable.ic_mouse3);
        }
        mTouchPad.setVisibility(mIsVirtualMouseEnabled ? View.VISIBLE : View.GONE);
        Toast.makeText(this,
                mIsVirtualMouseEnabled ? R.string.control_mouseon : R.string.control_mouseoff,
                Toast.LENGTH_SHORT).show();
    }

    public int launchJavaRuntime(Runtime runtime, String javaArgs) {
        runtime = MultiRTUtils.forceReread("Internal");
        JREUtils.redirectAndPrintJRELog();
        try {
            List<String> javaArgList = new ArrayList<>();
            File gamedir = new File(Tools.DIR_DATA);

            // Enable Caciocavallo
            Tools.getCacioJavaArgs(javaArgList,runtime.javaVersion == 8);
            javaArgList.add("-DconfigFile="+Tools.DIR_DATA + "/config.json");
            javaArgList.add("-DpluginDir="+ Tools.DIR_DATA + "/plugins/");
            javaArgList.add("-DclientHomeOverride="+gamedir);
            javaArgList.add("-jar");
            javaArgList.add(Tools.DIR_DATA+"/rt4.jar");

            Logger.appendToLog("Info: Java arguments: " + Arrays.toString(javaArgList.toArray(new String[0])));

            return JREUtils.launchJavaVM(this, runtime,gamedir,javaArgList, LauncherPreferences.PREF_CUSTOM_JAVA_ARGS);
        } catch (Throwable th) {
            Tools.showError(this, th, true);
            return -1;
        }
    }
    public void toggleKeyboard(View view) {
        mTouchCharInput.switchKeyboardState();
    }
}
