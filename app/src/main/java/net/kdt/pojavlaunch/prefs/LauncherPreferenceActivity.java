package net.kdt.pojavlaunch.prefs;

import android.os.*;
import net.kdt.pojavlaunch.*;
import com.kdt.mcgui.app.*;

public class LauncherPreferenceActivity extends MinePrefActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setFragment(new LauncherPreferenceFragment());
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		LauncherPreferences.loadPreferences(this);
	}
}

