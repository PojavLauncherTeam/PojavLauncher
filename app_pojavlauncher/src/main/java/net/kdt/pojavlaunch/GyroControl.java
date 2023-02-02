package net.kdt.pojavlaunch;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import net.kdt.pojavlaunch.prefs.LauncherPreferences;

import org.lwjgl.glfw.CallbackBridge;

public class GyroControl implements SensorEventListener, GrabListener {
    private final SensorManager mSensorManager;
    private final Sensor mSensor;
    private boolean mShouldHandleEvents;

    public GyroControl(Context context) {
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    }

    public void enable() {
        if(mSensor == null) return;
        mSensorManager.registerListener(this, mSensor, 1000 * LauncherPreferences.PREF_GYRO_SAMPLE_RATE);
        mShouldHandleEvents = CallbackBridge.isGrabbing();
        CallbackBridge.addGrabListener(this);
    }

    public void disable() {
        if(mSensor == null) return;
        mSensorManager.unregisterListener(this);
        CallbackBridge.removeGrabListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(mShouldHandleEvents && sensorEvent.sensor == mSensor) {
            CallbackBridge.mouseX += sensorEvent.values[0] * LauncherPreferences.PREF_GYRO_SENSITIVITY * 10;
            CallbackBridge.mouseY += sensorEvent.values[1] * LauncherPreferences.PREF_GYRO_SENSITIVITY * 10;
            CallbackBridge.sendCursorPos(CallbackBridge.mouseX, CallbackBridge.mouseY);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onGrabState(boolean isGrabbing) {
        mShouldHandleEvents = isGrabbing;
    }
}
