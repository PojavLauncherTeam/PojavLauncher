package net.kdt.pojavlaunch;

import android.annotation.SuppressLint;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;

import java.io.*;
import java.util.*;

import net.kdt.pojavlaunch.multirt.MultiRTUtils;
import net.kdt.pojavlaunch.prefs.*;
import net.kdt.pojavlaunch.utils.*;
import org.lwjgl.glfw.*;

import static net.kdt.pojavlaunch.utils.MathUtils.map;

import com.kdt.LoggerView;

public class JavaGUILauncherActivity extends BaseActivity implements View.OnTouchListener {
    private static final int MSG_LEFT_MOUSE_BUTTON_CHECK = 1028;
    
    private AWTCanvasView mTextureView;
    private LoggerView mLoggerView;

    private LinearLayout mTouchPad;
    private ImageView mMousePointerImageView;
    private GestureDetector gestureDetector;

    private boolean mSkipDetectMod, mIsVirtualMouseEnabled;

    private int mScaleFactor;
    private int[] mScaleFactors = initScaleFactors();
    
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_java_gui_launcher);

        Logger.getInstance().reset();
        findViewById(R.id.installmod_btn2).setOnClickListener(this::forceClose);
        findViewById(R.id.installmod_btn3).setOnClickListener(this::openLogOutput);
        findViewById(R.id.installmod_btn4).setOnClickListener(this::toggleVirtualMouse);
        findViewById(R.id.installmod_scale_down).setOnClickListener(this::scaleDown);
        findViewById(R.id.installmod_scale_up).setOnClickListener(this::scaleUp);
        mTouchPad = findViewById(R.id.main_touchpad);
        mLoggerView = findViewById(R.id.launcherLoggerView);
        mMousePointerImageView = findViewById(R.id.main_mouse_pointer);
        mTextureView = findViewById(R.id.installmod_surfaceview);
        gestureDetector = new GestureDetector(this, new SingleTapConfirm());
        mTouchPad.setFocusable(false);
        mTouchPad.setVisibility(View.GONE);

        findViewById(R.id.installmod_mouse_pri).setOnTouchListener(this);
        findViewById(R.id.installmod_mouse_sec).setOnTouchListener(this);

        mMousePointerImageView.post(() -> {
            ViewGroup.LayoutParams params = mMousePointerImageView.getLayoutParams();
            params.width = (int) (36 / 100f * LauncherPreferences.PREF_MOUSESCALE);
            params.height = (int) (54 / 100f * LauncherPreferences.PREF_MOUSESCALE);
        });

        mTouchPad.setOnTouchListener((v, event) -> {
            // MotionEvent reports input details from the touch screen
            // and other input controls. In this case, you are only
            // interested in events where the touch position changed.
            // int index = event.getActionIndex();
            int action = event.getActionMasked();

            float x = event.getX();
            float y = event.getY();
            float prevX, prevY, mouseX, mouseY;
            if(event.getHistorySize() > 0) {
                prevX = event.getHistoricalX(0);
                prevY = event.getHistoricalY(0);
            }else{
                prevX = x;
                prevY = y;
            }

            mouseX = mMousePointerImageView.getX();
            mouseY = mMousePointerImageView.getY();

            if (gestureDetector.onTouchEvent(event)) {
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

            // debugText.setText(CallbackBridge.DEBUG_STRING.toString());
            CallbackBridge.DEBUG_STRING.setLength(0);
            return true;
        });

        mTextureView.setOnTouchListener((v, event) -> {
            float x = event.getX();
            float y = event.getY();
            if (gestureDetector.onTouchEvent(event)) {
                sendScaledMousePosition(x, y);
                AWTInputBridge.sendMousePress(AWTInputEvent.BUTTON1_DOWN_MASK);
                return true;
            }

            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_UP: // 1
                case MotionEvent.ACTION_CANCEL: // 3
                case MotionEvent.ACTION_POINTER_UP: // 6
                    break;
                case MotionEvent.ACTION_MOVE: // 2
                    sendScaledMousePosition(x, y);
                    break;
            }
            return true;
        });

        try {
            JREUtils.jreReleaseList = JREUtils.readJREReleaseProperties(LauncherPreferences.PREF_DEFAULT_RUNTIME);
            if (JREUtils.jreReleaseList.get("JAVA_VERSION").equals("1.8.0")) {
                MultiRTUtils.setRuntimeNamed(this,LauncherPreferences.PREF_DEFAULT_RUNTIME);
            } else {
                MultiRTUtils.setRuntimeNamed(this,MultiRTUtils.getExactJreName(8));
                JREUtils.jreReleaseList = JREUtils.readJREReleaseProperties();
            }

            placeMouseAt(CallbackBridge.physicalWidth / 2, CallbackBridge.physicalHeight / 2);
            
            final File modFile = (File) getIntent().getExtras().getSerializable("modFile");
            final String javaArgs = getIntent().getExtras().getString("javaArgs");

            mSkipDetectMod = getIntent().getExtras().getBoolean("skipDetectMod", false);
            if (mSkipDetectMod) {
                new Thread(() -> launchJavaRuntime(modFile, javaArgs), "JREMainThread").start();
                return;
            }

            // No skip detection
            openLogOutput(null);
            new Thread(() -> {
                try {
                    final int exit = doCustomInstall(modFile, javaArgs);
                    Logger.getInstance().appendToLog(getString(R.string.toast_optifine_success));
                    if (exit != 0) return;
                    runOnUiThread(() -> {
                        Toast.makeText(JavaGUILauncherActivity.this, R.string.toast_optifine_success, Toast.LENGTH_SHORT).show();
                        MainActivity.fullyExit();
                    });

                } catch (Throwable e) {
                    Logger.getInstance().appendToLog("Install failed:");
                    Logger.getInstance().appendToLog(Log.getStackTraceString(e));
                    Tools.showError(JavaGUILauncherActivity.this, e);
                }
            }, "Installer").start();
        } catch (Throwable th) {
            Tools.showError(this, th, true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        final int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        final View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(uiOptions);
    }

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
        return true;
    }

    public void placeMouseAdd(float x, float y) {
        mMousePointerImageView.setX(mMousePointerImageView.getX() + x);
        mMousePointerImageView.setY(mMousePointerImageView.getY() + y);
    }

    public void placeMouseAt(float x, float y) {
        mMousePointerImageView.setX(x);
        mMousePointerImageView.setY(y);
    }

    void sendScaledMousePosition(float x, float y){
        AWTInputBridge.sendMousePos((int) map(x,0,CallbackBridge.physicalWidth, mScaleFactors[0], mScaleFactors[2]),
                (int) map(y,0,CallbackBridge.physicalHeight, mScaleFactors[1], mScaleFactors[3]));
    }

    public void forceClose(View v) {
        BaseMainActivity.dialogForceClose(this);
    }

    public void openLogOutput(View v) {
        mLoggerView.setVisibility(View.VISIBLE);
    }

    public void closeLogOutput(View view) {
        if (mSkipDetectMod) {
            mLoggerView.setVisibility(View.GONE);
        } else {
            forceClose(null);
        }
    }

    public void toggleVirtualMouse(View v) {
        mIsVirtualMouseEnabled = !mIsVirtualMouseEnabled;
        mTouchPad.setVisibility(mIsVirtualMouseEnabled ? View.VISIBLE : View.GONE);
        Toast.makeText(this,
                mIsVirtualMouseEnabled ? R.string.control_mouseon : R.string.control_mouseoff,
                Toast.LENGTH_SHORT).show();
    }

    public int launchJavaRuntime(File modFile, String javaArgs) {
        JREUtils.redirectAndPrintJRELog();
        try {
            List<String> javaArgList = new ArrayList<String>();

            // Enable Caciocavallo
            Tools.getCacioJavaArgs(javaArgList,false);
            
            if (javaArgs != null) {
                javaArgList.addAll(Arrays.asList(javaArgs.split(" ")));
            } else {
                javaArgList.add("-jar");
                javaArgList.add(modFile.getAbsolutePath());
            }

            Logger.getInstance().appendToLog("Info: Java arguments: " + Arrays.toString(javaArgList.toArray(new String[0])));
            
            // Run java on sandbox, non-overrideable.
            Collections.reverse(javaArgList);
            javaArgList.add("-Xbootclasspath/a:" + Tools.DIR_DATA + "/pro-grade.jar");
            javaArgList.add("-Djava.security.manager=net.sourceforge.prograde.sm.ProGradeJSM");
            javaArgList.add("-Djava.security.policy=" + Tools.DIR_DATA + "/java_sandbox.policy");
            Collections.reverse(javaArgList);

            return JREUtils.launchJavaVM(this, javaArgList);
        } catch (Throwable th) {
            Tools.showError(this, th, true);
            return -1;
        }
    }



    int[] initScaleFactors(){
        return initScaleFactors(true);
    }

    int[] initScaleFactors(boolean autoScale){
        //Could be optimized

        if(autoScale) { //Auto scale
            int minDimension = Math.min(CallbackBridge.physicalHeight, CallbackBridge.physicalWidth);
            mScaleFactor = Math.max(((3 * minDimension) / 1080) - 1, 1);
        }

        int[] scales = new int[4]; //Left, Top, Right, Bottom

        scales[0] = (CallbackBridge.physicalWidth/2);
        scales[0] -= scales[0]/ mScaleFactor;

        scales[1] = (CallbackBridge.physicalHeight/2);
        scales[1] -= scales[1]/ mScaleFactor;

        scales[2] = (CallbackBridge.physicalWidth/2);
        scales[2] += scales[2]/ mScaleFactor;

        scales[3] = (CallbackBridge.physicalHeight/2);
        scales[3] += scales[3]/ mScaleFactor;

        return scales;
    }

    public void scaleDown(View view) {
        mScaleFactor = Math.max(mScaleFactor - 1, 1);
        mScaleFactors = initScaleFactors(false);
        mTextureView.initScaleFactors(mScaleFactor);
        sendScaledMousePosition(mMousePointerImageView.getX(), mMousePointerImageView.getY());
    }

    public void scaleUp(View view) {
        mScaleFactor = Math.min(mScaleFactor + 1, 6);
        mScaleFactors = initScaleFactors(false);
        mTextureView.initScaleFactors(mScaleFactor);
        sendScaledMousePosition(mMousePointerImageView.getX(), mMousePointerImageView.getY());
    }

    private int doCustomInstall(File modFile, String javaArgs) throws IOException {
        mSkipDetectMod = true;
        return launchJavaRuntime(modFile, javaArgs);
    }
}
