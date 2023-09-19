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

        // Add preferences from resource
        addPreferencesFromResource(R.xml.pref_control);

        // Find preferences
        CustomSeekBarPreference seek2 = findPreference("timeLongPressTrigger");
        CustomSeekBarPreference seek3 = findPreference("buttonscale");
        CustomSeekBarPreference seek4 = findPreference("mousescale");
        CustomSeekBarPreference seek6 = findPreference("mousespeed");
        CustomSeekBarPreference deadzoneSeek = findPreference("gamepad_deadzone_scale");
        PreferenceCategory gyroCategory = findPreference("gyroCategory");
        CustomSeekBarPreference gyroSensitivitySeek = findPreference("gyroSensitivity");
        CustomSeekBarPreference gyroSampleRateSeek = findPreference("gyroSampleRate");

        // Set preferences
        seek2.setRange(100, 1000);
        seek2.setValue(longPressTrigger);
        seek2.setSuffix(" ms");

        seek3.setRange(80, 250);
        seek3.setValue(prefButtonSize);
        seek3.setSuffix(" %");

        seek4.setRange(25, 300);
        seek4.setValue(mouseScale);
        seek4.setSuffix(" %");

        seek6.setRange(25, 300);
        seek6.setValue((int) (mouseSpeed * 100f));
        seek6.setSuffix(" %");

        deadzoneSeek.setRange(50, 200);
        deadzoneSeek.setValue((int) joystickDeadzone * 100);
        deadzoneSeek.setSuffix(" %");

        // Check if gyro is available
        Context context = getContext();
        if (context != null) {
            mGyroAvailable = ((SensorManager) context.getSystemService(Context.SENSOR_SERVICE)).getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null;
        }

        // Set visibility of gyro category
        gyroCategory.setVisible(mGyroAvailable);

        // Set visibility of gyro preferences
        gyroSensitivitySeek.setVisible(LauncherPreferences.PREF_ENABLE_GYRO);
        gyroSampleRateSeek.setVisible(LauncherPreferences.PREF_ENABLE_GYRO);

        // Compute visibility
        computeVisibility();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences p, String s) {
        super.onSharedPreferenceChanged(p, s);
        computeVisibility();
    }

    private void computeVisibility() {
        findPreference("timeLongPressTrigger").setVisible(!LauncherPreferences.PREF_DISABLE_GESTURES);
    }
}
