package net.kdt.pojavlaunch.prefs;

import android.app.ActivityManager;
import android.os.*;

import androidx.preference.*;

import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.Tools;

import android.content.*;

import static net.kdt.pojavlaunch.Architecture.is32BitsDevice;
import static net.kdt.pojavlaunch.prefs.LauncherPreferences.PREF_NOTCH_SIZE;

public class LauncherPreferenceFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener
{
    @Override
    public void onCreatePreferences(Bundle b, String str) {
        addPreferencesFromResource(R.xml.pref_main);
		
        // Disable freeform mode in Android 6.0 and below.
        findPreference("freeform").setVisible(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N);
        //Disable notch checking behavior on android 8.1 and below.
        findPreference("ignoreNotch").setVisible(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && PREF_NOTCH_SIZE != 0);
		
        CustomSeekBarPreference seek2 = findPreference("timeLongPressTrigger");
        seek2.setRange(100, 1000);
        seek2.setValue(LauncherPreferences.PREF_LONGPRESS_TRIGGER);
        seek2.setSuffix(" ms");
        
        CustomSeekBarPreference seek3 = findPreference("buttonscale");
        seek3.setRange(80, 250);
        seek3.setValue((int) LauncherPreferences.PREF_BUTTONSIZE);
        seek3.setSuffix(" %");

        CustomSeekBarPreference seek4 = findPreference("mousescale");
        seek4.setRange(25, 300);
        seek4.setValue((int) LauncherPreferences.PREF_MOUSESCALE);
        seek4.setSuffix(" %");

        CustomSeekBarPreference seek5 = findPreference("resolutionRatio");
        seek5.setMin(25);
        seek5.setSuffix(" %");

	    CustomSeekBarPreference seek6 = findPreference("mousespeed");
	    seek6.setRange(25, 300);
	    seek6.setValue((int)(LauncherPreferences.PREF_MOUSESPEED*100f));
        seek6.setSuffix(" %");


        int maxRAM;
        int freeMem = (int) (Runtime.getRuntime().freeMemory() / 1048576L);
        ActivityManager actManager = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        actManager.getMemoryInfo(memInfo);

        CustomSeekBarPreference seek7 = findPreference("allocation");
        seek7.setMin(256);

        if(is32BitsDevice()) maxRAM = Math.min(1100, (int)(memInfo.totalMem /1024 /1024));
        else maxRAM = freeMem > 4096 ? freeMem : (int)(memInfo.totalMem /1024 /1024);

        seek7.setMax(maxRAM);
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
