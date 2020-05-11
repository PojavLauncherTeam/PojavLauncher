package net.kdt.pojavlaunch.prefs;

import android.os.*;
import net.kdt.pojavlaunch.*;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.SeekBarPreference;
import android.support.v7.preference.CheckBoxPreference;

public class LauncherPreferenceFragment extends PreferenceFragmentCompat
{
	@Override
	public void onCreatePreferences(Bundle b, String str) {
		addPreferencesFromResource(R.xml.pref_main);
		
		// Disable freeform mode in Android 6.0 or below.
		findPreference("freeform").setEnabled(Build.VERSION.SDK_INT >= 24);
		
		// Is Release always to be checked?
		((CheckBoxPreference) findPreference("vertype_release")).setChecked(true);
		
		SeekBarPreference seek2 = (SeekBarPreference) findPreference("timeLongPressTrigger");
		seek2.setMin(100);
		seek2.setMax(1000);
		seek2.setValue(500);
	}
}
