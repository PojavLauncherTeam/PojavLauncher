package net.kdt.pojavlaunch;

import android.app.*;
import android.graphics.*;
import android.os.*;
import android.widget.*;
import dalvik.system.*;
import java.io.*;
import java.lang.reflect.*;
import net.kdt.pojavlaunch.*;
import android.util.*;

public class UpdateAppActivity extends Activity
{
	private ProgressBar progress;
	private TextView logView;
	private boolean cancelable = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.launcher_update);
		
		progress = (ProgressBar) findViewById(R.id.launcherupdateProgressBar);
		logView = (TextView) findViewById(R.id.launcherupdateLogView);
		logView.setTypeface(Typeface.MONOSPACE);
		
		File inst = new File(Tools.worksDir + "/installer.jar");
		
		try {
			File optDir = getDir("dalvik-cache", 0);
			optDir.mkdirs();
			
			DexClassLoader mainLoader = new DexClassLoader(inst.getAbsolutePath(), optDir.getAbsolutePath(), getPackageManager().getPackageInfo(getPackageName(), 0).applicationInfo.nativeLibraryDir, MainActivity.class.getClassLoader());
			Class mClass = mainLoader.loadClass("Main");
			Method method = mClass.getMethod("main", UpdateAppActivity.class);
			method.invoke(null, this);
			
		} catch (Throwable th) {
			putLog("----- AN ERROR OCCURRED -----");
			putLog(Log.getStackTraceString(th));
			putLog("----- FAILED TO UPDATE! -----");
		} finally {
			cancelable = true;
		}
	}
	public void putLog(String message)
	{
		logView.append(message + "\n");
	}
	public ProgressBar getProgressBar()
	{
		return progress;
	}
	@Override
	public void onBackPressed()
	{
		if (cancelable) super.onBackPressed();
	}
}
