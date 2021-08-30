package net.kdt.pojavlaunch;

import android.graphics.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;

import java.io.*;
import java.util.*;

import net.kdt.pojavlaunch.multirt.MultiRTUtils;
import net.kdt.pojavlaunch.prefs.*;
import net.kdt.pojavlaunch.utils.*;
import org.lwjgl.glfw.*;

import static net.kdt.pojavlaunch.utils.MathUtils.map;

public class JavaGUILauncherActivity extends LoggableActivity implements View.OnTouchListener {
    private static final int MSG_LEFT_MOUSE_BUTTON_CHECK = 1028;
    
    private AWTCanvasView mTextureView;
    private LinearLayout contentLog;
    private TextView textLog;
    private ScrollView contentScroll;
    private ToggleButton toggleLog; 

    private LinearLayout touchPad;
    private ImageView mousePointer;
    private GestureDetector gestureDetector;
    
    private File logFile;
    private PrintStream logStream;
    
    private final Object mDialogLock = new Object();

    private boolean isLogAllow, mSkipDetectMod;

    private boolean rightOverride = false;
    private int scaleFactor;
    private int[] scaleFactors = initScaleFactors();

    private final int fingerStillThreshold = 8;
    private int initialX;
    private int initialY;
    private static boolean triggeredLeftMouseButton = false;
    private Handler theHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_LEFT_MOUSE_BUTTON_CHECK: {
                        int x = CallbackBridge.mouseX;
                        int y = CallbackBridge.mouseY;
                        if (CallbackBridge.isGrabbing() &&
                            Math.abs(initialX - x) < fingerStillThreshold &&
                            Math.abs(initialY - y) < fingerStillThreshold) {
                            triggeredLeftMouseButton = true;
                            AWTInputBridge.sendMousePress(AWTInputEvent.BUTTON1_DOWN_MASK, true);
                        }
                    } break;
            }
        }
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.install_mod);

        Tools.updateWindowSize(this);
        
        try {
            MultiRTUtils.setRuntimeNamed(this,LauncherPreferences.PREF_DEFAULT_RUNTIME);
            gestureDetector = new GestureDetector(this, new SingleTapConfirm());

            findViewById(R.id.installmod_mouse_pri).setOnTouchListener(this);
            findViewById(R.id.installmod_mouse_sec).setOnTouchListener(this);
            
            this.touchPad = findViewById(R.id.main_touchpad);
            touchPad.setFocusable(false);

            this.mousePointer = findViewById(R.id.main_mouse_pointer);
            this.mousePointer.post(() -> {
                ViewGroup.LayoutParams params = mousePointer.getLayoutParams();
                params.width = (int) (36 / 100f * LauncherPreferences.PREF_MOUSESCALE);
                params.height = (int) (54 / 100f * LauncherPreferences.PREF_MOUSESCALE);
            });


            touchPad.setOnTouchListener(new OnTouchListener(){
                    private float prevX, prevY;
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        // MotionEvent reports input details from the touch screen
                        // and other input controls. In this case, you are only
                        // interested in events where the touch position changed.
                        // int index = event.getActionIndex();

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
                        float mouseX = mousePointer.getX();
                        float mouseY = mousePointer.getY();

                        if (gestureDetector.onTouchEvent(event)) {

                            sendScaledMousePosition(mouseX,mouseY);

                            AWTInputBridge.sendMousePress(rightOverride ? AWTInputEvent.BUTTON3_DOWN_MASK : AWTInputEvent.BUTTON1_DOWN_MASK);


                        } else {
                            switch (action) {
                                case MotionEvent.ACTION_UP: // 1
                                case MotionEvent.ACTION_CANCEL: // 3
                                case MotionEvent.ACTION_POINTER_UP: // 6
                                    break;
                                case MotionEvent.ACTION_MOVE: // 2
                                    mouseX = Math.max(0, Math.min(CallbackBridge.physicalWidth, mouseX + x - prevX));
                                    mouseY = Math.max(0, Math.min(CallbackBridge.physicalHeight, mouseY + y - prevY));
                                    placeMouseAt(mouseX, mouseY);

                                    sendScaledMousePosition(mouseX,mouseY);
                                    /*
                                     if (!CallbackBridge.isGrabbing()) {
                                     CallbackBridge.sendMouseKeycode(LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_LEFT, 0, isLeftMouseDown);
                                     CallbackBridge.sendMouseKeycode(LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_RIGHT, 0, isRightMouseDown);
                                     }
                                     */
                                    break;
                            }
                        }

                        // debugText.setText(CallbackBridge.DEBUG_STRING.toString());
                        CallbackBridge.DEBUG_STRING.setLength(0);

                        return true;
                    }
                });
                
            placeMouseAt(CallbackBridge.physicalWidth / 2, CallbackBridge.physicalHeight / 2);
                
            logFile = new File(Tools.DIR_GAME_HOME, "latestlog.txt");
            logFile.delete();
            logFile.createNewFile();
            logStream = new PrintStream(logFile.getAbsolutePath());
            
            this.contentLog = findViewById(R.id.content_log_layout);
            this.contentScroll = (ScrollView) findViewById(R.id.content_log_scroll);
            this.textLog = (TextView) contentScroll.getChildAt(0);
            this.toggleLog = (ToggleButton) findViewById(R.id.content_log_toggle_log);
            this.toggleLog.setChecked(false);
            // this.textLogBehindGL = (TextView) findViewById(R.id.main_log_behind_GL);
            // this.textLogBehindGL.setTypeface(Typeface.MONOSPACE);
            this.textLog.setTypeface(Typeface.MONOSPACE);
            this.toggleLog.setOnCheckedChangeListener(new ToggleButton.OnCheckedChangeListener(){
                    @Override
                    public void onCheckedChanged(CompoundButton button, boolean isChecked) {
                        isLogAllow = isChecked;
                        appendToLog("");
                    }
                });
            
            final File modFile = (File) getIntent().getExtras().getSerializable("modFile");
            final String javaArgs = getIntent().getExtras().getString("javaArgs");

            mTextureView = findViewById(R.id.installmod_surfaceview);
           
            mSkipDetectMod = getIntent().getExtras().getBoolean("skipDetectMod", false);
            if (mSkipDetectMod) {
                new Thread(new Runnable(){
                        @Override
                        public void run() {
                            launchJavaRuntime(modFile, javaArgs);
                        }
                    }, "JREMainThread").start();
            } else {
                openLogOutput(null);
                new Thread(new Runnable(){
                        @Override
                        public void run() {
                            try {
                                final int exit = doCustomInstall(modFile, javaArgs);
                                appendlnToLog(getString(R.string.toast_optifine_success));
                                if (exit == 0) {
                                    runOnUiThread(new Runnable(){
                                            @Override
                                            public void run() {
                                                Toast.makeText(JavaGUILauncherActivity.this, R.string.toast_optifine_success, Toast.LENGTH_SHORT).show();
                                                MainActivity.fullyExit();
                                            }
                                        });
                                } /* else {
                                    throw new ErrnoException(getString(R.string.glo, exit);
                                } */
                            } catch (Throwable e) {
                                appendlnToLog("Install failed:");
                                appendlnToLog(Log.getStackTraceString(e));
                                Tools.showError(JavaGUILauncherActivity.this, e);
                            }
                        }
                    }, "Installer").start();
            }
        } catch (Throwable th) {
            Tools.showError(this, th, true);
        }
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
        this.mousePointer.setX(mousePointer.getX() + x);
        this.mousePointer.setY(mousePointer.getY() + y);
    }

    public void placeMouseAt(float x, float y) {
        this.mousePointer.setX(x);
        this.mousePointer.setY(y);
    }

    void sendScaledMousePosition(float x, float y){
        AWTInputBridge.sendMousePos((int) map(x,0,CallbackBridge.physicalWidth, scaleFactors[0], scaleFactors[2]),
                (int) map(y,0,CallbackBridge.physicalHeight, scaleFactors[1], scaleFactors[3]));
    }

    public void forceClose(View v) {
        BaseMainActivity.dialogForceClose(this);
    }

    public void openLogOutput(View v) {
        contentLog.setVisibility(View.VISIBLE);
    }

    public void closeLogOutput(View view) {
        if (mSkipDetectMod) {
            contentLog.setVisibility(View.GONE);
        } else {
            forceClose(null);
        }
    }
    
    private int doCustomInstall(File modFile, String javaArgs) throws IOException {
        isLogAllow = true;
        mSkipDetectMod = true;
        return launchJavaRuntime(modFile, javaArgs);
    }

    public int launchJavaRuntime(File modFile, String javaArgs) {
        JREUtils.redirectAndPrintJRELog(this);
        try {
            jreReleaseList = JREUtils.readJREReleaseProperties();
            List<String> javaArgList = new ArrayList<String>();

            // Enable Caciocavallo
            Tools.getCacioJavaArgs(javaArgList,false);
            
            if (javaArgs != null) {
                javaArgList.addAll(Arrays.asList(javaArgs.split(" ")));
            } else {
                javaArgList.add("-jar");
                javaArgList.add(modFile.getAbsolutePath());
            }

            appendlnToLog("Info: Java arguments: " + Arrays.toString(javaArgList.toArray(new String[0])));
            
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

    @Override
    public void onResume() {
        super.onResume();
        final int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        final View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(uiOptions);
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

    int[] initScaleFactors(){
        return initScaleFactors(true);
    }

    int[] initScaleFactors(boolean autoScale){
        //Could be optimized

        if(autoScale) { //Auto scale
            int minDimension = Math.min(CallbackBridge.physicalHeight, CallbackBridge.physicalWidth);
            scaleFactor = Math.max(((3 * minDimension) / 1080) - 1, 1);
        }

        int[] scales = new int[4]; //Left, Top, Right, Bottom

        scales[0] = (CallbackBridge.physicalWidth/2);
        scales[0] -= scales[0]/scaleFactor;

        scales[1] = (CallbackBridge.physicalHeight/2);
        scales[1] -= scales[1]/scaleFactor;

        scales[2] = (CallbackBridge.physicalWidth/2);
        scales[2] += scales[2]/scaleFactor;

        scales[3] = (CallbackBridge.physicalHeight/2);
        scales[3] += scales[3]/scaleFactor;

        return scales;
    }

    public void scaleDown(View view) {
        scaleFactor = Math.max(scaleFactor - 1, 1);
        scaleFactors = initScaleFactors(false);
        mTextureView.initScaleFactors(scaleFactor);
        sendScaledMousePosition(mousePointer.getX(),mousePointer.getY());
    }

    public void scaleUp(View view) {
        scaleFactor = Math.min(scaleFactor + 1, 6);
        scaleFactors = initScaleFactors(false);
        mTextureView.initScaleFactors(scaleFactor);
        sendScaledMousePosition(mousePointer.getX(),mousePointer.getY());
    }
}
