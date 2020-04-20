package net.kdt.pojavlaunch.prefs;

import android.os.*;
import net.kdt.pojavlaunch.*;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.SeekBarPreference;

public class LauncherPreferenceFragment extends PreferenceFragmentCompat
{
	@Override
	public void onCreatePreferences(Bundle b, String str) {
		addPreferencesFromResource(R.xml.pref_main);
		
		// Disable freeform mode in Android 6.0 or below.
		findPreference("freeform").setEnabled(Build.VERSION.SDK_INT >= 24);
		
		SeekBarPreference seek1 = (SeekBarPreference) findPreference("maxDxRefs");
		seek1.setMin(0xFFF);
		seek1.setMax(0xFFFF);
		seek1.setValue(0xFFF);
		
		SeekBarPreference seek2 = (SeekBarPreference) findPreference("timeLongPressTrigger");
		seek2.setMin(100);
		seek2.setMax(1000);
		seek2.setValue(500);
	}
}
