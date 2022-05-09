package net.kdt.pojavlaunch;

import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.content.res.*;
import android.os.*;
import androidx.core.app.*;

import android.util.*;
import java.io.*;
import java.text.*;
import java.util.*;

import net.kdt.pojavlaunch.utils.*;

public class PojavApplication extends Application {
	public static String CRASH_REPORT_TAG = "PojavCrashReport";
	
	@Override
	public void onCreate() {
		Thread.setDefaultUncaughtExceptionHandler((thread, th) -> {
			boolean storagePermAllowed = Build.VERSION.SDK_INT < 23 || Build.VERSION.SDK_INT >= 29 ||
					ActivityCompat.checkSelfPermission(PojavApplication.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
			File crashFile = new File(storagePermAllowed ? Tools.DIR_GAME_HOME : Tools.DIR_DATA, "latestcrash.txt");
			try {
				// Write to file, since some devices may not able to show error
				crashFile.getParentFile().mkdirs();
				crashFile.createNewFile();
				PrintStream crashStream = new PrintStream(crashFile);
				crashStream.append("PojavLauncher crash report\n");
				crashStream.append(" - Time: " + DateFormat.getDateTimeInstance().format(new Date()) + "\n");
				crashStream.append(" - Device: " + Build.PRODUCT + " " + Build.MODEL + "\n");
				crashStream.append(" - Android version: " + Build.VERSION.RELEASE + "\n");
				crashStream.append(" - Crash stack trace:\n");
				crashStream.append(" - Launcher version: " + BuildConfig.VERSION_NAME + "\n");
				crashStream.append(Log.getStackTraceString(th));
				crashStream.close();
			} catch (Throwable throwable) {
				Log.e(CRASH_REPORT_TAG, " - Exception attempt saving crash stack trace:", throwable);
				Log.e(CRASH_REPORT_TAG, " - The crash stack trace was:", th);
			}

			FatalErrorActivity.showError(PojavApplication.this, crashFile.getAbsolutePath(), storagePermAllowed, th);
			// android.os.Process.killProcess(android.os.Process.myPid());

			BaseMainActivity.fullyExit();
		});
		
		try {
			super.onCreate();
			Tools.APP_NAME = getResources().getString(R.string.app_short_name);
			
			Tools.DIR_DATA = getDir("files", MODE_PRIVATE).getParent();
            //Tools.DIR_HOME_JRE = Tools.DIR_DATA + "/jre_runtime".replace("/data/user/0", "/data/data");
            Tools.DIR_ACCOUNT_OLD = Tools.DIR_DATA + "/Users";
            Tools.DIR_ACCOUNT_NEW = Tools.DIR_DATA + "/accounts";
            // Tools.FILE_ACCOUNT_JSON = getFilesDir().getAbsolutePath() + "/account_profiles.json";


			Tools.DEVICE_ARCHITECTURE = Architecture.getDeviceArchitecture();
			//Force x86 lib directory for Asus x86 based zenfones
			if(Architecture.isx86Device() && Architecture.is32BitsDevice()){
				String originalJNIDirectory = getApplicationInfo().nativeLibraryDir;
				getApplicationInfo().nativeLibraryDir = originalJNIDirectory.substring(0,
												originalJNIDirectory.lastIndexOf("/"))
												.concat("/x86");
			}

		} catch (Throwable throwable) {
			Intent ferrorIntent = new Intent(this, FatalErrorActivity.class);
			ferrorIntent.putExtra("throwable", throwable);
			startActivity(ferrorIntent);
		}
	}
    
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleUtils.setLocale(base));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocaleUtils.setLocale(this);
    }
}
