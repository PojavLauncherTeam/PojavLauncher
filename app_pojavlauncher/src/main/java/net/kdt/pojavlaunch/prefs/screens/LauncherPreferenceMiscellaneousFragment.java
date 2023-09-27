package net.kdt.pojavlaunch.prefs.screens;

import android.os.Bundle;

import androidx.preference.Preference;

import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.Tools;

public class LauncherPreferenceMiscellaneousFragment extends LauncherPreferenceFragment {
    @Override
    public void onCreatePreferences(Bundle b, String str) {
        addPreferencesFromResource(R.xml.pref_misc);
        Preference driverPreference = findPreference("zinkPreferSystemDriver");
        if(!Tools.checkVulkanSupport(driverPreference.getContext().getPackageManager())) {
            driverPreference.setVisible(false);
        }
    }
}
