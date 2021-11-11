package net.kdt.pojavlaunch.prefs.screens;

import android.content.SharedPreferences;
import android.os.Bundle;

import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.prefs.CustomSeekBarPreference;
import net.kdt.pojavlaunch.prefs.LauncherPreferences;

public class LauncherPreferenceControlFragment extends LauncherPreferenceFragment {

    @Override
    public void onCreatePreferences(Bundle b, String str) {
        // Get values
        int longPressTrigger = LauncherPreferences.PREF_LONGPRESS_TRIGGER;
        int prefButtonSize = (int) LauncherPreferences.PREF_BUTTONSIZE;
        int mouseScale = (int) LauncherPreferences.PREF_MOUSESCALE;
        float mouseSpeed = LauncherPreferences.PREF_MOUSESPEED;

        //Triggers a write for some reason which resets the value
        addPreferencesFromResource(R.xml.pref_control);

        CustomSeekBarPreference seek2 = findPreference("timeLongPressTrigger");
        seek2.setRange(100, 1000);
        seek2.setValue(longPressTrigger);
        seek2.setSuffix(" ms");

        CustomSeekBarPreference seek3 = findPreference("buttonscale");
        seek3.setRange(80, 250);
        seek3.setValue(prefButtonSize);
        seek3.setSuffix(" %");

        CustomSeekBarPreference seek4 = findPreference("mousescale");
        seek4.setRange(25, 300);
        seek4.setValue(mouseScale);
        seek4.setSuffix(" %");

        CustomSeekBarPreference seek6 = findPreference("mousespeed");
        seek6.setRange(25, 300);
        seek6.setValue((int)(mouseSpeed *100f));
        seek6.setSuffix(" %");


        computeVisibility();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences p, String s) {
        super.onSharedPreferenceChanged(p, s);
        computeVisibility();
    }

    private void computeVisibility(){
        CustomSeekBarPreference seek2 = findPreference("timeLongPressTrigger");
        seek2.setVisible(!LauncherPreferences.PREF_DISABLE_GESTURES);
    }

}
