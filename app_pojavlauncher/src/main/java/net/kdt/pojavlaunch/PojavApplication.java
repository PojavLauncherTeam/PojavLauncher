package net.kdt.pojavlaunch;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.multidex.MultiDexApplication;

import net.kdt.pojavlaunch.utils.LocaleUtils;

import java.io.File;
import java.io.PrintStream;
import java.text.DateFormat;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class PojavApplication extends MultiDexApplication {
	public static String CRASH_REPORT_TAG = "PojavCrashReport";
	
	@Override
	public void onCreate() {
		if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
			try {
				SSLContext ctx = SSLContext.getInstance("TLSv1");
				ctx.init(null,new TrustManager[] { new X509TrustManager() {
					public java.security.cert.X509Certificate[] getAcceptedIssuers() {
						return new java.security.cert.X509Certificate[]{};
					}

					public void checkClientTrusted(X509Certificate[] chain,
												   String authType) throws CertificateException {
					}

					public void checkServerTrusted(X509Certificate[] chain,
												   String authType) throws CertificateException {
					}
				} },null);
				HttpsURLConnection.setDefaultSSLSocketFactory(ctx.getSocketFactory());
			}catch (Exception e) {
				Log.i("Unsecure","Can't ignore SSL",e);
			}
		}
		Thread.setDefaultUncaughtExceptionHandler((thread, th) -> {
			th.printStackTrace();
			boolean storagePermAllowed = Build.VERSION.SDK_INT < 23 ||
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
			throwable.printStackTrace();
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
