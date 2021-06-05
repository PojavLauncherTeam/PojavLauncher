package net.kdt.pojavlaunch.prefs;

import android.os.*;

import androidx.preference.*;

import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.Tools;

import android.content.*;

public class LauncherPreferenceFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener
{
    @Override
    public void onCreatePreferences(Bundle b, String str) {
        addPreferencesFromResource(R.xml.pref_main);
		
        // Disable freeform mode in Android 6.0 and below.
        findPreference("freeform").setVisible(Build.VERSION.SDK_INT >= 24);
        //Disable notch checking behavior on android 8.1 and below.
        findPreference("ignoreNotch").setVisible(Build.VERSION.SDK_INT >= 28);
		
        CustomSeekBarPreference seek2 = (CustomSeekBarPreference) findPreference("timeLongPressTrigger");
        seek2.setMin(100);
        seek2.setMax(1000);
        seek2.setValue(LauncherPreferences.PREF_LONGPRESS_TRIGGER);
        seek2.setSuffix(" ms");
        
        CustomSeekBarPreference seek3 = (CustomSeekBarPreference) findPreference("buttonscale");
        seek3.setMin(80);
        seek3.setMax(250);
        seek3.setValue((int) LauncherPreferences.PREF_BUTTONSIZE);
        seek3.setSuffix(" %");

        CustomSeekBarPreference seek4 = (CustomSeekBarPreference) findPreference("mousescale");
        seek4.setMin(25);
        seek4.setMax(300);
        seek4.setValue((int) LauncherPreferences.PREF_MOUSESCALE);
        seek4.setSuffix(" %");

        CustomSeekBarPreference seek5 = (CustomSeekBarPreference) findPreference("resolutionRatio");
        seek5.setMin(25);
        seek5.setSuffix(" %");

	    CustomSeekBarPreference seek6 = (CustomSeekBarPreference) findPreference("mousespeed");
        seek6.setMin(25);
        seek6.setValue((int)(LauncherPreferences.PREF_MOUSESPEED*100f));
        seek6.setMax(300);
        seek6.setSuffix(" %");

        int freeMem = (int) (Runtime.getRuntime().freeMemory() / 1048576l);

        CustomSeekBarPreference seek7 = (CustomSeekBarPreference) findPreference("allocation");
        seek7.setMin(256);
        if(Tools.CURRENT_ARCHITECTURE.contains("32")) seek7.setMax(1100);
        else seek7.setMax(freeMem > 4096 ? freeMem : 4096);
        seek7.setValue(LauncherPreferences.PREF_RAM_ALLOCATION);
        seek7.setSuffix(" MB");

	// #724 bug fix
        if (seek5.getValue() < 25) {
            seek5.setValue(100);
        }
        
        EditTextPreference editJVMArgs = findPreference("javaArgs");
        if (editJVMArgs != null) {
            editJVMArgs.setOnBindEditTextListener((editText) -> editText.setSingleLine());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences p, String s) {
        LauncherPreferences.loadPreferences();
    }
}
