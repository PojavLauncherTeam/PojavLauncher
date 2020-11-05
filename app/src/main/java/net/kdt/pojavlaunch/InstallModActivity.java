package net.kdt.pojavlaunch;

import android.graphics.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import com.oracle.dalvik.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import org.lwjgl.glfw.*;
import android.support.v7.app.*;
import android.content.*;

public class InstallModActivity extends LoggableActivity {
    public static volatile boolean IS_JRE_RUNNING;
    
	private TextureView mTextureView;
    private LinearLayout contentLog;
    private TextView textLog;
    private ScrollView contentScroll;
    private ToggleButton toggleLog; 
    
    private File logFile;
    private PrintStream logStream;
    
    private boolean isLogAllow;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.install_mod);
        
        try {
		    Tools.setFullscreen(this);
          
            logFile = new File(Tools.MAIN_PATH, "latestlog.txt");
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
            JREUtils.redirectAndPrintJRELog(this, null);
                
            final File modFile = (File) getIntent().getExtras().getSerializable("modFile");
            final String javaArgs = getIntent().getExtras().getString("javaArgs");
            
            mTextureView = findViewById(R.id.installmod_surfaceview);
            mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener(){

                    private boolean isAvailableCalled = false;
                    @Override
                    public void onSurfaceTextureAvailable(SurfaceTexture tex, final int w, final int h) {
                        if (!isAvailableCalled) {
                            isAvailableCalled = true;
                        } else return;
                        
                        // final Surface surface = new Surface(tex);
                        new Thread(new Runnable(){
                            @Override
                            public void run() {
                                while (IS_JRE_RUNNING) {
                                    Canvas canvas = mTextureView.lockCanvas();
                                    JREUtils.renderAWTScreenFrame(canvas, w, h);
                                    mTextureView.unlockCanvasAndPost(canvas);
                                }
                            }
                        }, "AWTSurfaceUpdater").start();

                        new Thread(new Runnable(){
                            @Override
                            public void run() {
                                final int exitCode = launchJavaRuntime(modFile, javaArgs);
                                IS_JRE_RUNNING = false;

                                appendlnToLog("Java Exit code: " + exitCode);

                                runOnUiThread(new Runnable(){
                                        @Override
                                        public void run() {
                                            AlertDialog.Builder dialog = new AlertDialog.Builder(InstallModActivity.this);
                                            dialog.setMessage(getString(R.string.mcn_exit_title, exitCode));
                                            dialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){

                                                    @Override
                                                    public void onClick(DialogInterface p1, int p2){
                                                        MainActivity.fullyExit();
                                                    }
                                                });
                                            dialog.show();
                                        }
                                    });
                            }
                        }, "JREMainThread").start();
                    }

                    @Override
                    public boolean onSurfaceTextureDestroyed(SurfaceTexture tex) {
                        return false;
                    }

                    @Override
                    public void onSurfaceTextureSizeChanged(SurfaceTexture tex, int w, int h) {

                    }

                    @Override
                    public void onSurfaceTextureUpdated(SurfaceTexture tex) {

                    }
                });
        } catch (Throwable th) {
            Tools.showError(this, th, true);
        }
	}
	
	public void forceClose(View v) {
		MainActivity.dialogForceClose(this);
	}
    
    public void openLogOutput(View v) {
        contentLog.setVisibility(View.VISIBLE);
    }
    
    public void closeLogOutput(View view) {
        contentLog.setVisibility(View.GONE);
        // mIsResuming = true;
	}
    
	private int launchJavaRuntime(File modFile, String javaArgs) {
		try {
            JREUtils.relocateLibPath();
            
			List<String> javaArgList = new ArrayList<String>();
			javaArgList.add(Tools.homeJreDir + "/bin/java");

			Tools.getJavaArgs(this, javaArgList);
			
			File cacioAwtLibPath = new File(Tools.MAIN_PATH, "cacioawtlib");
			if (cacioAwtLibPath.exists()) {
				StringBuilder libStr = new StringBuilder();
				for (File file: cacioAwtLibPath.listFiles()) {
					if (file.getName().endsWith(".jar")) {
						libStr.append(":" + file.getAbsolutePath());
					}
				}
				javaArgList.add("-Xbootclasspath/a" + libStr.toString());
			}
			
            javaArgList.add("-Dcacio.managed.screensize=" + CallbackBridge.windowWidth + "x" + CallbackBridge.windowHeight);
            
			File cacioArgOverrideFile = new File(cacioAwtLibPath, "overrideargs.txt");
			if (cacioArgOverrideFile.exists()) {
				javaArgList.addAll(Arrays.asList(Tools.read(cacioArgOverrideFile.getAbsolutePath()).split(" ")));
			}
			
			if (javaArgs != null) {
                javaArgList.addAll(Arrays.asList(javaArgs.split(" ")));
            } else {
                javaArgList.add("-jar");
                javaArgList.add(modFile.getAbsolutePath());
            }

			System.out.println(Arrays.toString(javaArgList.toArray(new String[0])));
			
			//JREUtils.redirectStdio(false);
			JREUtils.setJavaEnvironment(this, Tools.LAUNCH_TYPE);
			JREUtils.initJavaRuntime();
			JREUtils.chdir(Tools.MAIN_PATH);

			return VMLauncher.launchJVM(javaArgList.toArray(new String[0]));
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
