package net.kdt.pojavlaunch;

import android.app.*;
import android.content.pm.*;
import android.util.*;
import net.kdt.pojavlaunch.prefs.*;
import net.kdt.pojavlaunch.value.customcontrols.*;
import org.lwjgl.opengl.*;
import java.util.*;
import android.content.res.*;

public class PojavApplication extends Application
{
	@Override
	public void onCreate()
	{
		super.onCreate();
		try {
			Tools.APP_NAME = getResources().getString(R.string.app_short_name);
			
			PackageInfo thisApp = getPackageManager().getPackageInfo(getPackageName(), 0);
			
			Tools.usingVerName = thisApp.versionName;
			Tools.usingVerCode = thisApp.versionCode;
			String fileDataPath = getDir("files", MODE_PRIVATE).getParent();
			/*
			 * "/s" meaning:
			 * - "/storage"
			 * - "/sdcard"
			 *
			 */
			if (fileDataPath.startsWith("/data/data") || !fileDataPath.startsWith("/s") && !fileDataPath.contains("storage") && !fileDataPath.contains("sdcard")) {
				Tools.datapath = fileDataPath;
			}
		
			LauncherPreferences.loadPreferences(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
