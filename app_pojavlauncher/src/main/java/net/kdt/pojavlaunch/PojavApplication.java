package net.kdt.pojavlaunch;

import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.content.res.*;
import android.os.*;
import androidx.core.app.*;
import androidx.preference.*;
import android.util.*;
import java.io.*;
import java.text.*;
import java.util.*;

import net.kdt.pojavlaunch.utils.*;

public class PojavApplication extends Application
{
    public static final String CRASH_REPORT_TAG = "PojavCrashReport";
    @Override
    public void onCreate() {
         super.onCreate();

         if (Build.VERSION.SDK_INT < 30) {
             Tools.setHomePath(Environment.getExternalStorageDirectory().getAbsolutePath() + "/games/PojavLauncher");
         } else {
             Tools.setHomePath(getExternalFilesDir(null).getAbsolutePath());
         }

         Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler(){
             @Override
             public void uncaughtException(Thread thread, Throwable th) {
                 boolean storagePermAllowed = Build.VERSION.SDK_INT < 23 || Build.VERSION.SDK_INT >= 30
                   || ActivityCompat.checkSelfPermission(PojavApplication.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
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
            Tools.APP_NAME = getResources().getString(R.string.app_short_name);
			
            Tools.DIR_DATA = getDir("files", MODE_PRIVATE).getParent();
            Tools.DIR_HOME_JRE = Tools.DIR_DATA + "/jre_runtime";
            Tools.DIR_ACCOUNT_OLD = Tools.DIR_DATA + "/Users";
            Tools.DIR_ACCOUNT_NEW = Tools.DIR_DATA + "/accounts";
            // Tools.FILE_ACCOUNT_JSON = getFilesDir().getAbsolutePath() + "/account_profiles.json";
            
            File nativeLibDir = new File(getApplicationInfo().nativeLibraryDir);
            
            Tools.CURRENT_ARCHITECTURE = nativeLibDir.getName();
            switch (Tools.CURRENT_ARCHITECTURE) {
                case "arm": Tools.CURRENT_ARCHITECTURE = "arm/aarch32"; break;
                case "arm64": Tools.CURRENT_ARCHITECTURE = "arm64/aarch64"; break;
                case "x86": Tools.CURRENT_ARCHITECTURE = "x86/i*86"; break;
                case "x86_64": Tools.CURRENT_ARCHITECTURE = "x86_64/amd64"; break;
            }
            
            // Special case for Asus x86 devixes
            if (Build.SUPPORTED_ABIS[0].equals("x86")) {
                getApplicationInfo().nativeLibraryDir = nativeLibDir.getParent() + "/x86";
                Tools.CURRENT_ARCHITECTURE = "x86/i*86";
            }

            FontChanger.initFonts(this);
        } catch (Throwable th) {
            Intent ferrorIntent = new Intent(this, FatalErrorActivity.class);
            ferrorIntent.putExtra("throwable", th);
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
