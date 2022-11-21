package net.kdt.pojavlaunch.prefs.screens;

import android.os.Build;
import android.os.Bundle;

import androidx.preference.SwitchPreference;

import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.prefs.LauncherPreferences;
import net.kdt.pojavlaunch.prefs.CustomSeekBarPreference;

public class LauncherPreferenceMiscellaneousFragment extends LauncherPreferenceFragment {
    @Override
    public void onCreatePreferences(Bundle b, String str) {
        addPreferencesFromResource(R.xml.pref_misc);
		
		int redownloadAfter = LauncherPreferences.PREF_DOWNLOAD_RETRY_AFTER_MS;
		int redownloadTimes = LauncherPreferences.PREF_DOWNLOAD_RETRY_TIMES;
		
		CustomSeekBarPreference seek1 = findPreference("redownloadTimes");
		seek1.setValue(redownloadTimes);
		seek1.setRange(0, 50);
		
		CustomSeekBarPreference seek2 = findPreference("redownloadAfterMs");
		seek2.setSuffix(" ms");
		seek2.setValue(redownloadAfter);
		seek2.setRange(100, 5000);
    }
}
