package net.kdt.pojavlaunch;

import android.app.Application;
import android.os.*;
import android.content.pm.PackageManager.*;
import android.content.pm.*;
import net.kdt.pojavlaunch.prefs.*;
import net.kdt.pojavlaunch.value.customcontrols.*;

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

			ControlButton.pixelOf2dp = (int) Tools.dpToPx(this, 2);
			ControlButton.pixelOf30dp = (int) Tools.dpToPx(this, 30);
			ControlButton.pixelOf50dp = (int) Tools.dpToPx(this, 50);
			ControlButton.pixelOf80dp = (int) Tools.dpToPx(this, 80);
			ControlButton[] specialButtons = ControlButton.getSpecialButtons();
			specialButtons[0].name = getString(R.string.control_keyboard);
			specialButtons[1].name = getString(R.string.control_toggle);
			specialButtons[2].name = getString(R.string.control_primary);
			specialButtons[3].name = getString(R.string.control_secondary);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
