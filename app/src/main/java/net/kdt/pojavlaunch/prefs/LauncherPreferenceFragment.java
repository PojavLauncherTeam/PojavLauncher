package net.kdt.pojavlaunch.prefs;

import android.os.*;
import android.support.v4.app.*;
import android.support.v7.preference.*;
import net.kdt.pojavlaunch.*;

import net.kdt.pojavlaunch.R;

public class LauncherPreferenceFragment extends PreferenceFragmentCompat
{
	@Override
	public void onCreatePreferences(Bundle b, String str) {
		addPreferencesFromResource(R.xml.pref_main);
		
		// Disable freeform mode in Android 6.0 and below.
		findPreference("freeform").setEnabled(Build.VERSION.SDK_INT >= 24);
		
		SeekBarPreference seek2 = (SeekBarPreference) findPreference("timeLongPressTrigger");
		seek2.setMin(100);
		seek2.setMax(1000);
		seek2.setValue(500);
        
        SeekBarPreference seek3 = (SeekBarPreference) findPreference("buttonscale");
        seek3.setMin(20);
        seek3.setMax(500);
		seek3.setValue(100);
	}

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) LauncherPreferences.loadPreferences();
    }
}
