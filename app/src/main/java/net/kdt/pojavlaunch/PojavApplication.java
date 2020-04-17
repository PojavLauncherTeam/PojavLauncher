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
			Tools.datapath = getDir("files", MODE_PRIVATE).getParent();
			
			LauncherPreferences.loadPreferences(this);
			
			ControlButton.pixelOf50dp = (int) Tools.dpToPx(this, 50);
			ControlButton[] specialButtons = ControlButton.getSpecialButtons();
			specialButtons[0].name = getString(R.string.control_keyboard);
			specialButtons[1].name = getString(R.string.control_toggle);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
