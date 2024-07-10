package net.kdt.pojavlaunch.prefs.screens;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.preference.PreferenceCategory;

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
        float joystickDeadzone = LauncherPreferences.PREF_DEADZONE_SCALE;
        boolean invertXYIngame = LauncherPreferences.PREF_INVERT_XY_INGAME;
        boolean invertXYMenu = LauncherPreferences.PREF_INVERT_XY_MENU;


        //Triggers a write for some reason which resets the value
        addPreferencesFromResource(R.xml.pref_control);

        CustomSeekBarPreference seek2 = requirePreference("timeLongPressTrigger",
                CustomSeekBarPreference.class);
        seek2.setRange(100, 1000);
        seek2.setValue(longPressTrigger);
        seek2.setSuffix(" ms");

        CustomSeekBarPreference seek3 = requirePreference("buttonscale",
                CustomSeekBarPreference.class);
        seek3.setRange(80, 250);
        seek3.setValue(prefButtonSize);
        seek3.setSuffix(" %");

        CustomSeekBarPreference seek4 = requirePreference("mousescale",
                CustomSeekBarPreference.class);
        seek4.setRange(25, 300);
        seek4.setValue(mouseScale);
        seek4.setSuffix(" %");

        CustomSeekBarPreference seek6 = requirePreference("mousespeed",
                CustomSeekBarPreference.class);
        seek6.setRange(25, 300);
        seek6.setValue((int)(mouseSpeed *100f));
        seek6.setSuffix(" %");

        CustomSeekBarPreference deadzoneSeek = requirePreference("gamepad_deadzone_scale",
                CustomSeekBarPreference.class);
        deadzoneSeek.setRange(50, 200);
        deadzoneSeek.setValue((int) (joystickDeadzone * 100f));
        deadzoneSeek.setSuffix(" %");


        Context context = getContext();
        if(context != null) {
            mGyroAvailable = ((SensorManager)context.getSystemService(Context.SENSOR_SERVICE)).getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null;
        }
        PreferenceCategory gyroCategory =  requirePreference("gyroCategory",
                PreferenceCategory.class);
        gyroCategory.setVisible(mGyroAvailable);

        CustomSeekBarPreference gyroSensitivitySeek = requirePreference("gyroSensitivity",
                CustomSeekBarPreference.class);
        gyroSensitivitySeek.setRange(25, 300);
        gyroSensitivitySeek.setValue((int) (gyroSpeed*100f));
        gyroSensitivitySeek.setSuffix(" %");

        CustomSeekBarPreference gyroSampleRateSeek = requirePreference("gyroSampleRate",
                CustomSeekBarPreference.class);
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
        requirePreference("timeLongPressTrigger").setVisible(!LauncherPreferences.PREF_DISABLE_GESTURES);
        requirePreference("gyroSensitivity").setVisible(LauncherPreferences.PREF_ENABLE_GYRO);
        requirePreference("gyroSampleRate").setVisible(LauncherPreferences.PREF_ENABLE_GYRO);
        requirePreference("gyroInvertX").setVisible(LauncherPreferences.PREF_ENABLE_GYRO);
        requirePreference("gyroInvertY").setVisible(LauncherPreferences.PREF_ENABLE_GYRO);
        requirePreference("gyroSmoothing").setVisible(LauncherPreferences.PREF_ENABLE_GYRO);
    }
}
