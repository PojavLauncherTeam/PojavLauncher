package net.kdt.pojavlaunch;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.Surface;
import android.view.WindowManager;

import net.kdt.pojavlaunch.prefs.LauncherPreferences;

import org.lwjgl.glfw.CallbackBridge;

public class GyroControl implements SensorEventListener, GrabListener {
    private final SensorManager mSensorManager;
    private final Sensor mSensor;
    private final Context ctx;
    private boolean mShouldHandleEvents;
    private boolean mFirstPass;
    private long mPreviousTimestamp;

    public GyroControl(Context context) {
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        ctx = context;
    }

    public void enable() {
        if(mSensor == null) return;
        mFirstPass = true;
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
            float factor = LauncherPreferences.PREF_GYRO_SENSITIVITY;
            if(!mFirstPass) {
                factor *= (sensorEvent.timestamp - mPreviousTimestamp) * 0.000001;
            }else mFirstPass = false;
            WindowManager windowManager = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
            if(windowManager.getDefaultDisplay().getRotation() == Surface.ROTATION_90) {
                CallbackBridge.mouseX -= sensorEvent.values[0] * factor;
                CallbackBridge.mouseY += sensorEvent.values[1] * factor;
            }
            else {
                CallbackBridge.mouseX += sensorEvent.values[0] * factor;
                CallbackBridge.mouseY -= sensorEvent.values[1] * factor;
            }
            CallbackBridge.sendCursorPos(CallbackBridge.mouseX, CallbackBridge.mouseY);
            mPreviousTimestamp = sensorEvent.timestamp;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onGrabState(boolean isGrabbing) {
        mFirstPass = true;
        mShouldHandleEvents = isGrabbing;
    }
}
