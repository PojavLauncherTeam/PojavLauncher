package net.kdt.pojavlaunch;

import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import androidx.appcompat.app.AlertDialog;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.kdt.pojavlaunch.installers.BaseInstaller;
import net.kdt.pojavlaunch.installers.FabricInstaller;
import net.kdt.pojavlaunch.installers.InstallerDetector;
import net.kdt.pojavlaunch.installers.Legacy1p12p2ForgeInstaller;
import net.kdt.pojavlaunch.installers.LegacyForgeInstaller;
import net.kdt.pojavlaunch.installers.LegacyOptifineInstaller;
import net.kdt.pojavlaunch.installers.NewForgeInstaller;
import net.kdt.pojavlaunch.prefs.LauncherPreferences;
import net.kdt.pojavlaunch.utils.JREUtils;
import org.lwjgl.glfw.CallbackBridge;
import android.os.Handler;

public class JavaGUILauncherActivity extends LoggableActivity {
    private AWTCanvasView mTextureView;
    private LinearLayout contentLog;
    private TextView textLog;
    private ScrollView contentScroll;
    private ToggleButton toggleLog; 

    private LinearLayout touchPad;
    private ImageView mousePointer;
    
    private File logFile;
    private PrintStream logStream;
    
    private final Object mDialogLock = new Object();

    private boolean isLogAllow, mSkipDetectMod;

    private boolean rightOverride = false;
    private float scaleFactor = 1;
    private int fingerStillThreshold = 8;
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
                            AWTInputBridge.sendMousePress(0, true);
                            sendMouseButton(LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_LEFT, true);
                        }
                    } break;
                case MSG_DROP_ITEM_BUTTON_CHECK: {
                        sendKeyPress(LWJGLGLFWKeycode.GLFW_KEY_Q, 0, true);
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
                        float mouseX = mousePointer.getTranslationX();
                        float mouseY = mousePointer.getTranslationY();

                        if (gestureDetector.onTouchEvent(event)) {

                            CallbackBridge.sendCursorPos((int) (mouseX * scaleFactor), (int) (mouseY *scaleFactor));
                            CallbackBridge.sendMouseKeycode(rightOverride ? LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_RIGHT : LWJGLGLFWKeycode.GLFW_MOUSE_BUTTON_LEFT);
                            if (!rightOverride) {
                                CallbackBridge.mouseLeft = true;
                            }

                        } else {
                            switch (action) {
                                case MotionEvent.ACTION_UP: // 1
                                case MotionEvent.ACTION_CANCEL: // 3
                                case MotionEvent.ACTION_POINTER_UP: // 6
                                    if (!rightOverride) {
                                        CallbackBridge.mouseLeft = false;
                                    }
                                    break;
                                case MotionEvent.ACTION_MOVE: // 2
                                    mouseX = Math.max(0, Math.min(displayMetrics.widthPixels, mouseX + x - prevX));
                                    mouseY = Math.max(0, Math.min(displayMetrics.heightPixels, mouseY + y - prevY));
                                    placeMouseAt(mouseX, mouseY);

                                    CallbackBridge.sendCursorPos((int) (mouseX * scaleFactor),  (int) (mouseY *scaleFactor));
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
            logFile = new File(Tools.DIR_GAME_NEW, "latestlog.txt");
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
    
    public String dialogInput(final String title, final int message) {
        final StringBuilder str = new StringBuilder();
        
        runOnUiThread(new Runnable(){
                @Override
                public void run() {
                    final EditText editText = new EditText(JavaGUILauncherActivity.this);
                    editText.setHint(message);
                    editText.setSingleLine();
                    
                    AlertDialog.Builder d = new AlertDialog.Builder(JavaGUILauncherActivity.this);
                    d.setCancelable(false);
                    d.setTitle(title);
                    d.setView(editText);
                    d.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){

                            @Override
                            public void onClick(DialogInterface i, int id) {
                                str.append(editText.getText().toString());
                                synchronized (mDialogLock) {
                                    mDialogLock.notifyAll();
                                }
                            }
                        });
                    d.show();
                }
            });

        try {
            synchronized (mDialogLock) {
                mDialogLock.wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        return str.toString();
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
        
        // Attempt to detects some mod installers 
        BaseInstaller installer = new BaseInstaller();
        installer.setInput(modFile);
        
        if (InstallerDetector.isForgeLegacy(installer)) {
            appendlnToLog("Detected Forge Installer 1.12.1 or below!");
            return new LegacyForgeInstaller(installer).install(this);
        } else if (InstallerDetector.isForge1p12p2(installer)) {
            appendlnToLog("Detected Forge Installer 1.12.2!");
            return new Legacy1p12p2ForgeInstaller(installer).install(this);
        } else if (InstallerDetector.isForgeNew(installer)) {
            appendlnToLog("Detected Forge Installer 1.13 or above!");
            return new NewForgeInstaller(installer).install(this);
        } else if (InstallerDetector.isFabric(installer)) {
            appendlnToLog("Detected Fabric Installer!");
            return new FabricInstaller(installer).install(this);
        }else if (InstallerDetector.isOptiFine(installer)) {
            appendlnToLog("Detected OptiFine Installer!");
            return new LegacyOptifineInstaller(installer).install(this);
        } else {
            appendlnToLog("No mod detected. Starting JVM");
            isLogAllow = false;
            mSkipDetectMod = true;
            return launchJavaRuntime(modFile, javaArgs);
        }
    }

    public int launchJavaRuntime(File modFile, String javaArgs) {
        JREUtils.redirectAndPrintJRELog(this, null);
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
            
            // System.out.println(Arrays.toString(javaArgList.toArray(new String[0])));

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
        textLog.post(new Runnable(){
                @Override
                public void run() {
                    textLog.append(text);
                    contentScroll.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });
    }
}
