package net.kdt.pojavlaunch;

import android.app.Application;
import android.os.*;
import android.content.pm.PackageManager.*;
import android.content.pm.*;
import android.support.v7.preference.*;
import android.content.*;
import android.support.v4.app.*;
import android.util.*;

import java.io.*;
import java.time.*;
import java.text.*;
import java.util.*;

import net.kdt.pojavlaunch.prefs.*;
import net.kdt.pojavlaunch.customcontrols.*;

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
					crashStream.append(" - Time: " + DateFormat.getDateTimeInstance().format(new Date()) + "\n");
					crashStream.append(" - Device: " + Build.PRODUCT + " " + Build.MODEL + "\n");
					crashStream.append(" - Android version: " + Build.VERSION.RELEASE + "\n");
					crashStream.append(" - Crash stack trace:\n");
					crashStream.append(Log.getStackTraceString(th));
					crashStream.close();
				} catch (Throwable th2) {
					Log.e(CRASH_REPORT_TAG, " - Exception attempt saving crash stack trace:", th2);
					Log.e(CRASH_REPORT_TAG, " - The crash stack trace was:", th);
				}
				
				FatalErrorActivity.showError(PojavApplication.this, crashFile.getAbsolutePath(), storagePermAllowed, th);
				// android.os.Process.killProcess(android.os.Process.myPid());
				
				BaseMainActivity.fullyExit();
			}
		});
		
		try {
			super.onCreate();
			Tools.APP_NAME = getResources().getString(R.string.app_short_name);
			
			PackageInfo thisApp = getPackageManager().getPackageInfo(getPackageName(), 0);
			
			Tools.usingVerName = thisApp.versionName;
			Tools.usingVerCode = thisApp.versionCode;
			Tools.datapath = getDir("files", MODE_PRIVATE).getParent();
            Tools.currentArch = new File(getApplicationInfo().nativeLibraryDir).getName();
			switch (Tools.currentArch) {
                case "arm": Tools.currentArch = "arm/aarch32"; break;
                case "arm64": Tools.currentArch = "arm64/aarch64"; break;
                case "x86": Tools.currentArch = "x86/i*86"; break;
                case "x86_64": Tools.currentArch = "x86_64/amd64"; break;
            }
            
			LauncherPreferences.DEFAULT_PREF = PreferenceManager.getDefaultSharedPreferences(this);
			LauncherPreferences.loadPreferences();

			FontChanger.initFonts(this);
		} catch (Throwable th) {
			Intent ferrorIntent = new Intent(this, FatalErrorActivity.class);
			ferrorIntent.putExtra("throwable", th);
			startActivity(ferrorIntent);
		}
	}
}
