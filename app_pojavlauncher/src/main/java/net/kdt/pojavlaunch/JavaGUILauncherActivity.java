package net.kdt.pojavlaunch;

import android.graphics.*;
import android.os.*;
import androidx.appcompat.app.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.util.*;
import net.kdt.pojavlaunch.installers.*;
import net.kdt.pojavlaunch.utils.*;
import org.lwjgl.glfw.*;
import android.content.*;

public class JavaGUILauncherActivity extends LoggableActivity {
    private AWTCanvasView mTextureView;
    private LinearLayout contentLog;
    private TextView textLog;
    private ScrollView contentScroll;
    private ToggleButton toggleLog; 

    private File logFile;
    private PrintStream logStream;
    
    private final Object mDialogLock = new Object();

    private boolean isLogAllow, mSkipDetectMod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.install_mod);

        try {
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
