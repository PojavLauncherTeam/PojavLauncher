package net.kdt.pojavlaunch;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.OrientationEventListener;

import net.kdt.pojavlaunch.prefs.LauncherPreferences;

import org.lwjgl.glfw.CallbackBridge;

public class GyroControl implements SensorEventListener, GrabListener{
    private final SensorManager mSensorManager;
    private final Sensor mSensor;
    private final OrientationCorrectionListener mCorrectionListener;
    private boolean mShouldHandleEvents;
    private boolean mFirstPass;
    private long mPreviousTimestamp;
    private float xFactor; // -1 or 1 depending on device orientation
    private float yFactor;
    private boolean mSwapXY;

    private final float[] mPreviousRotation = new float[16];
    private final float[] mCurrentRotation = new float[16];
    private final float[] mAngleDifference = new float[3];

    public GyroControl(Context context) {
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
        mCorrectionListener = new OrientationCorrectionListener(context);
    }

    public void enable() {
        if(mSensor == null) return;
        mFirstPass = true;
        mSensorManager.registerListener(this, mSensor, 1000 * LauncherPreferences.PREF_GYRO_SAMPLE_RATE);
        mCorrectionListener.enable();
        mShouldHandleEvents = CallbackBridge.isGrabbing();
        CallbackBridge.addGrabListener(this);
    }

    public void disable() {
        if(mSensor == null) return;
        mSensorManager.unregisterListener(this);
        mCorrectionListener.disable();
        CallbackBridge.removeGrabListener(this);
    }
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (!mShouldHandleEvents) return;
        // Copy the old array content
        System.arraycopy(mCurrentRotation, 0, mPreviousRotation, 0, 16);
        SensorManager.getRotationMatrixFromVector(mCurrentRotation, sensorEvent.values);

        if(mFirstPass){  // Setup initial position
            mFirstPass = false;
            mPreviousTimestamp = sensorEvent.timestamp;
            return;
        }
        SensorManager.getAngleChange(mAngleDifference, mCurrentRotation, mPreviousRotation);

        CallbackBridge.mouseX -= (mAngleDifference[mSwapXY ? 2 : 1] * 1000 * LauncherPreferences.PREF_GYRO_SENSITIVITY * xFactor);
        CallbackBridge.mouseY += (mAngleDifference[mSwapXY ? 1 : 2] * 1000  * LauncherPreferences.PREF_GYRO_SENSITIVITY * yFactor);
        CallbackBridge.sendCursorPos(CallbackBridge.mouseX, CallbackBridge.mouseY);

        mPreviousTimestamp = sensorEvent.timestamp;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onGrabState(boolean isGrabbing) {
        mFirstPass = true;
        mShouldHandleEvents = isGrabbing;
    }

    class OrientationCorrectionListener extends OrientationEventListener {

        public OrientationCorrectionListener(Context context) {
            super(context, SensorManager.SENSOR_DELAY_NORMAL);
        }

        @Override
        public void onOrientationChanged(int i) {
            if((315 < i && i <= 360) || (i < 45) ) {
                mSwapXY = true;
                xFactor = 1;
                yFactor = 1;
            }else if(45 < i && i < 135) {
                mSwapXY = false;
                xFactor = 1;
                yFactor = -1;
            }else if(135 < i && i < 225) {
                mSwapXY = true;
                xFactor = -1;
                yFactor = -1;
            }else if(225 <  i && i < 315) {
                mSwapXY = false;
                xFactor = -1;
                yFactor = 1;
            }
            if(LauncherPreferences.PREF_GYRO_INVERT_X) xFactor *= -1;
            if(LauncherPreferences.PREF_GYRO_INVERT_Y) yFactor *= -1;
        }
    }
}
