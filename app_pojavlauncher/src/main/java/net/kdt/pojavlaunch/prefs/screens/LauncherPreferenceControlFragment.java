package net.kdt.pojavlaunch.prefs.screens;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.preference.PreferenceCategory;
import androidx.preference.SwitchPreference;

import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.prefs.CustomSeekBarPreference;
import net.kdt.pojavlaunch.prefs.LauncherPreferences;

public class LauncherPreferenceControlFragment extends LauncherPreferenceFragment {
    private boolean mGyroAvailable = false;
    @Override
    public void onCreatePreferences(Bundle b, String str) {
        // Get values
        int longPressTrigger = LauncherPreferences.PREF_LONGPRESS_TRIGGER;
        int prefButtonSize = (int) LauncherPreferences.PREF_BUTTONSIZE;
        int mouseScale = (int) LauncherPreferences.PREF_MOUSESCALE;
        int gyroSampleRate = LauncherPreferences.PREF_GYRO_SAMPLE_RATE;
        float mouseSpeed = LauncherPreferences.PREF_MOUSESPEED;
        float gyroSpeed = LauncherPreferences.PREF_GYRO_SENSITIVITY;

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

        Context context = getContext();
        if(context != null) {
            mGyroAvailable = ((SensorManager)context.getSystemService(Context.SENSOR_SERVICE)).getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null;
        }
        PreferenceCategory gyroCategory =  (PreferenceCategory) findPreference("gyroCategory");
        gyroCategory.setVisible(mGyroAvailable);

        CustomSeekBarPreference gyroSensitivitySeek = findPreference("gyroSensitivity");
        gyroSensitivitySeek.setRange(25, 300);
        gyroSensitivitySeek.setValue((int) (gyroSpeed*100f));
        gyroSensitivitySeek.setSuffix(" %");
        CustomSeekBarPreference gyroSampleRateSeek = findPreference("gyroSampleRate");
        gyroSampleRateSeek.setRange(5, 50);
        gyroSampleRateSeek.setValue(gyroSampleRate);
        gyroSampleRateSeek.setSuffix(" ms");
        computeVisibility();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences p, String s) {
        super.onSharedPreferenceChanged(p, s);
        computeVisibility();
    }

    private void computeVisibility(){
        findPreference("timeLongPressTrigger").setVisible(!LauncherPreferences.PREF_DISABLE_GESTURES);
        findPreference("gyroSensitivity").setVisible(LauncherPreferences.PREF_ENALBE_GYRO);
        findPreference("gyroSampleRate").setVisible(LauncherPreferences.PREF_ENALBE_GYRO);
        findPreference("gyroInvertX").setVisible(LauncherPreferences.PREF_ENALBE_GYRO);
        findPreference("gyroInvertY").setVisible(LauncherPreferences.PREF_ENALBE_GYRO);
    }

}
