package net.kdt.pojavlaunch.prefs;

import android.os.*;
import androidx.core.app.*;
import androidx.preference.*;

import net.kdt.pojavlaunch.R;
import android.content.*;

public class LauncherPreferenceFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener
{
    @Override
    public void onCreatePreferences(Bundle b, String str) {
        addPreferencesFromResource(R.xml.pref_main);
		
        // Disable freeform mode in Android 6.0 and below.
        findPreference("freeform").setEnabled(Build.VERSION.SDK_INT >= 24);
		
        SeekBarPreference seek2 = (SeekBarPreference) findPreference("timeLongPressTrigger");
        seek2.setMin(100);
        seek2.setMax(1000);
        seek2.setValue(LauncherPreferences.PREF_LONGPRESS_TRIGGER);
        
        SeekBarPreference seek3 = (SeekBarPreference) findPreference("buttonscale");
        seek3.setMin(20);
        seek3.setMax(500);
        seek3.setValue((int) LauncherPreferences.PREF_BUTTONSIZE);
        
        SeekBarPreference seek4 = (SeekBarPreference) findPreference("mousescale");
        seek4.setMin(20);
        seek4.setMax(500);
        seek4.setValue((int) LauncherPreferences.PREF_MOUSESCALE);
        
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
