package net.kdt.pojavlaunch.prefs.screens;

import android.os.Build;
import android.os.Bundle;

import androidx.preference.SwitchPreference;

import net.kdt.pojavlaunch.R;

public class LauncherPreferenceMiscellaneousFragment extends LauncherPreferenceFragment {
    @Override
    public void onCreatePreferences(Bundle b, String str) {
        addPreferencesFromResource(R.xml.pref_misc);

        // Sustained performance is only available since Nougat
        SwitchPreference sustainedPerfSwitch = findPreference("sustainedPerformance");
        sustainedPerfSwitch.setVisible(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N);
    }
}
