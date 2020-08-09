package net.kdt.pojavlaunch;

import android.app.Application;
import android.os.*;
import android.content.pm.PackageManager.*;
import android.content.pm.*;
import net.kdt.pojavlaunch.prefs.*;
import net.kdt.pojavlaunch.value.customcontrols.*;
import android.support.v7.preference.*;
import java.io.*;
import android.content.*;
import android.support.v4.app.*;
import android.util.*;
import net.kdt.pojavlaunch.exit.*;
import java.time.*;
import java.text.*;
import java.util.*;

public class PojavApplication extends Application
{
	public static String CRASH_REPORT_TAG = "PojavCrashReport";
	
	@Override
	public void onCreate() {
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler(){
			@Override
			public void uncaughtException(Thread thread, Throwable th) {
				boolean storagePermAllowed = Build.VERSION.SDK_INT < 23 || ActivityCompat.checkSelfPermission(PojavApplication.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
				File crashFile = new File(storagePermAllowed ? Tools.MAIN_PATH : Tools.datapath, "latestcrash.txt");
				try {
					// Write to file, since some devices may not able to show error
					crashFile.createNewFile();
					PrintStream crashStream = new PrintStream(crashFile);
					crashStream.append("PojavLauncher crash report\n");
					crashStream.append(" - Time: " + DateFormat.getDateTimeInstance().format(new Date()));
					crashStream.append(" - Device: " + Build.PRODUCT + " " + Build.MODEL);
					crashStream.append(" - Android version: " + Build.VERSION.RELEASE);
					crashStream.append(" - Crash stack trace:");
					crashStream.append(Log.getStackTraceString(th));
					crashStream.close();
				} catch (Throwable th2) {
					Log.e(CRASH_REPORT_TAG, " - Exception attempt saving crash stack trace:", th2);
					Log.e(CRASH_REPORT_TAG, " - The crash stack trace was:", th);
				}
				
				FatalErrorActivity.showError(PojavApplication.this, crashFile.getAbsolutePath(), storagePermAllowed, th);
				// android.os.Process.killProcess(android.os.Process.myPid());
				
				MainActivity.fullyExit();
			}
		});
		
		try {
			super.onCreate();
			Tools.APP_NAME = getResources().getString(R.string.app_short_name);
			
			PackageInfo thisApp = getPackageManager().getPackageInfo(getPackageName(), 0);
			
			Tools.usingVerName = thisApp.versionName;
			Tools.usingVerCode = thisApp.versionCode;
			Tools.datapath = getDir("files", MODE_PRIVATE).getParent();
			
			LauncherPreferences.DEFAULT_PREF = PreferenceManager.getDefaultSharedPreferences(this);
			LauncherPreferences.loadPreferences(this);

			ControlButton.pixelOf2dp = (int) Tools.dpToPx(this, 2);
			ControlButton.pixelOf30dp = (int) Tools.dpToPx(this, 30);
			ControlButton.pixelOf50dp = (int) Tools.dpToPx(this, 50);
			ControlButton.pixelOf80dp = (int) Tools.dpToPx(this, 80);
			ControlButton[] specialButtons = ControlButton.getSpecialButtons();
			specialButtons[0].name = getString(R.string.control_keyboard);
			specialButtons[1].name = getString(R.string.control_toggle);
			specialButtons[2].name = getString(R.string.control_primary);
			specialButtons[3].name = getString(R.string.control_secondary);
			specialButtons[4].name = getString(R.string.control_mouse);
			
			FontChanger.initFonts(this);
		} catch (Throwable th) {
			Intent ferrorIntent = new Intent(this, FatalErrorActivity.class);
			ferrorIntent.putExtra("throwable", th);
			startActivity(ferrorIntent);
		}
	}
}
