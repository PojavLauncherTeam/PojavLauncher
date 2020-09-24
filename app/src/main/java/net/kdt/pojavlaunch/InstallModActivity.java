package net.kdt.pojavlaunch;

import android.graphics.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import com.oracle.dalvik.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;

public class InstallModActivity extends LoggableActivity
{
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
            logFile = new File(Tools.MAIN_PATH, "latestlog.txt");
            logFile.delete();
            logFile.createNewFile();
            logStream = new PrintStream(logFile.getAbsolutePath());
            this.contentLog = (LinearLayout) findViewById(R.id.content_log_layout);
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
            JREUtils.redirectAndPrintJRELog(this);
                
            final File modFile = (File) getIntent().getExtras().getSerializable("modFile");

            mTextureView = findViewById(R.id.installmod_surfaceview);
            mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener(){

                    @Override
                    public void onSurfaceTextureAvailable(SurfaceTexture tex, int w, int h) {
                        try {
                            Surface surface = new Surface(tex);
                            Field field = surface.getClass().getDeclaredField("mNativeObject");
                            field.setAccessible(true);
                            JREUtils.setupBridgeSurfaceAWT((long) field.get(surface));
                        } catch (Throwable th) {
                            Tools.showError(InstallModActivity.this, th, true);
                        }

                        new Thread(new Runnable(){
                                @Override
                                public void run() {
                                    launchJavaRuntime(modFile);
                                    // finish();
                                }
                            }).start();
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
    
	private void launchJavaRuntime(File modFile) {
		try {
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
			
			File cacioArgOverrideFile = new File(cacioAwtLibPath, "overrideargs.txt");
			if (cacioArgOverrideFile.exists()) {
				javaArgList.addAll(Arrays.asList(Tools.read(cacioArgOverrideFile.getAbsolutePath()).split(" ")));
			}
			
			javaArgList.add("-jar");
			javaArgList.add(modFile.getAbsolutePath());

			System.out.println(Arrays.toString(javaArgList.toArray(new String[0])));
			
			//JREUtils.redirectStdio(false);
			JREUtils.setJavaEnvironment(this);
			JREUtils.initJavaRuntime();
			JREUtils.chdir(Tools.MAIN_PATH);

			VMLauncher.launchJVM(javaArgList.toArray(new String[0]));
		} catch (Throwable th) {
			Tools.showError(this, th, true);
		}
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
