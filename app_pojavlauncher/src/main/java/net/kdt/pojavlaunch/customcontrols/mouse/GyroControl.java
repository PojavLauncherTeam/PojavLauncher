package net.kdt.pojavlaunch.customcontrols.mouse;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.WindowManager;

import net.kdt.pojavlaunch.GrabListener;
import net.kdt.pojavlaunch.prefs.LauncherPreferences;

import org.lwjgl.glfw.CallbackBridge;

import java.util.Arrays;

public class GyroControl implements SensorEventListener, GrabListener {
    /* How much distance has to be moved before taking into account the gyro */
    private static final float SINGLE_AXIS_LOW_PASS_THRESHOLD = 1.13F;
    private static final float MULTI_AXIS_LOW_PASS_THRESHOLD = 1.3F;
    // Warmup period of 2 since the first read from the sensor seems to produce a bogus value,
    // which creates a far too large of a difference on the Y axis once actual sensor data comes in
    private static final int ROTATION_VECTOR_WARMUP_PERIOD = 2;

    private final WindowManager mWindowManager;
    private int mSurfaceRotation;
    private final SensorManager mSensorManager;
    private final Sensor mSensor;
    private final OrientationCorrectionListener mCorrectionListener;
    private boolean mShouldHandleEvents;
    private int mWarmup;
    private float xFactor; // -1 or 1 depending on device orientation
    private float yFactor;
    private boolean mSwapXY;

    private final float[] mPreviousRotation = new float[16];
    private final float[] mCurrentRotation = new float[16];
    private final float[] mAngleDifference = new float[3];


    /* Used to average the last values, if smoothing is enabled */
    private final float[][] mAngleBuffer = new float[
            LauncherPreferences.PREF_GYRO_SMOOTHING ? 2 : 1
            ][3];
    private float xTotal = 0;
    private float yTotal = 0;

    private float xAverage = 0;
    private float yAverage = 0;
    private int mHistoryIndex = -1;

    /* Store the gyro movement under the threshold */
    private float mStoredX = 0;
    private float mStoredY = 0;

    public GyroControl(Activity activity) {
        mWindowManager = activity.getWindowManager();
        mSurfaceRotation = -10;
        mSensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
        mCorrectionListener = new OrientationCorrectionListener(activity);
        updateOrientation();
    }

    public void enable() {
        if(mSensor == null) return;
        mWarmup = ROTATION_VECTOR_WARMUP_PERIOD;
        mSensorManager.registerListener(this, mSensor, 1000 * LauncherPreferences.PREF_GYRO_SAMPLE_RATE);
        mCorrectionListener.enable();
        mShouldHandleEvents = CallbackBridge.isGrabbing();
        CallbackBridge.addGrabListener(this);
    }

    public void disable() {
        if(mSensor == null) return;
        mSensorManager.unregisterListener(this);
        mCorrectionListener.disable();
        mStoredX = mStoredY = 0;
        resetDamper();
        CallbackBridge.removeGrabListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (!mShouldHandleEvents) return;
        // Copy the old array content
        System.arraycopy(mCurrentRotation, 0, mPreviousRotation, 0, 16);
        SensorManager.getRotationMatrixFromVector(mCurrentRotation, sensorEvent.values);


        if(mWarmup > 0){  // Setup initial position
            mWarmup--;
            return;
        }
        SensorManager.getAngleChange(mAngleDifference, mCurrentRotation, mPreviousRotation);
        damperValue(mAngleDifference);
        mStoredX += xAverage * 1000 * LauncherPreferences.PREF_GYRO_SENSITIVITY;
        mStoredY += yAverage * 1000 * LauncherPreferences.PREF_GYRO_SENSITIVITY;

        boolean updatePosition = false;
        float absX = Math.abs(mStoredX);
        float absY = Math.abs(mStoredY);

        if(absX + absY > MULTI_AXIS_LOW_PASS_THRESHOLD) {
            CallbackBridge.mouseX -= ((mSwapXY ? mStoredY : mStoredX) * xFactor);
            CallbackBridge.mouseY += ((mSwapXY ? mStoredX : mStoredY) * yFactor);
            mStoredX = 0;
            mStoredY = 0;
            updatePosition = true;
        } else {
            if(Math.abs(mStoredX) > SINGLE_AXIS_LOW_PASS_THRESHOLD){
                CallbackBridge.mouseX -= ((mSwapXY ? mStoredY : mStoredX) * xFactor);
                mStoredX = 0;
                updatePosition = true;
            }

            if(Math.abs(mStoredY) > SINGLE_AXIS_LOW_PASS_THRESHOLD) {
                CallbackBridge.mouseY += ((mSwapXY ? mStoredX : mStoredY) * yFactor);
                mStoredY = 0;
                updatePosition = true;
            }
        }

        if(updatePosition){
            CallbackBridge.sendCursorPos(CallbackBridge.mouseX, CallbackBridge.mouseY);
        }
    }

    /** Update the axis mapping in accordance to activity rotation, used for initial rotation */
    public void updateOrientation(){
        int rotation = mWindowManager.getDefaultDisplay().getRotation();
        mSurfaceRotation = rotation;
        switch (rotation){
            case Surface.ROTATION_0:
                mSwapXY = true;
                xFactor = 1;
                yFactor = 1;
                break;
            case Surface.ROTATION_90:
                mSwapXY = false;
                xFactor = -1;
                yFactor = 1;
                break;
            case Surface.ROTATION_180:
                mSwapXY = true;
                xFactor = -1;
                yFactor = -1;
                break;
            case Surface.ROTATION_270:
                mSwapXY = false;
                xFactor = 1;
                yFactor = -1;
                break;
        }

        if(LauncherPreferences.PREF_GYRO_INVERT_X) xFactor *= -1;
        if(LauncherPreferences.PREF_GYRO_INVERT_Y) yFactor *= -1;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}

    @Override
    public void onGrabState(boolean isGrabbing) {
        mWarmup = ROTATION_VECTOR_WARMUP_PERIOD;
        mShouldHandleEvents = isGrabbing;
    }


    /**
     * Compute the moving average of the gyroscope to reduce jitter
     * @param newAngleDifference The new angle difference
     */
    private void damperValue(float[] newAngleDifference){
        mHistoryIndex ++;
        if(mHistoryIndex >= mAngleBuffer.length) mHistoryIndex = 0;

        xTotal -= mAngleBuffer[mHistoryIndex][1];
        yTotal -= mAngleBuffer[mHistoryIndex][2];

        System.arraycopy(newAngleDifference, 0, mAngleBuffer[mHistoryIndex], 0, 3);

        xTotal += mAngleBuffer[mHistoryIndex][1];
        yTotal += mAngleBuffer[mHistoryIndex][2];

        // compute the moving average
        xAverage = xTotal / mAngleBuffer.length;
        yAverage = yTotal / mAngleBuffer.length;
    }

    /** Reset the moving average data */
    private void resetDamper(){
        mHistoryIndex = -1;
        xTotal = 0;
        yTotal = 0;
        xAverage = 0;
        yAverage = 0;
        for(float[] oldAngle : mAngleBuffer){
            Arrays.fill(oldAngle, 0);
        }
    }

    class OrientationCorrectionListener extends OrientationEventListener {

        public OrientationCorrectionListener(Context context) {
            super(context, SensorManager.SENSOR_DELAY_NORMAL);
        }

        @Override
        public void onOrientationChanged(int i) {
            // Force to wait to be in game before setting factors
            // Theoretically, one could use the whole interface in portrait...
            if(!mShouldHandleEvents) return;

            if(i == OrientationEventListener.ORIENTATION_UNKNOWN) {
                return; //change nothing
            }



            switch (mSurfaceRotation){
                case Surface.ROTATION_90:
                case Surface.ROTATION_270:
                    mSwapXY = false;
                    if(225 <  i && i < 315) {
                        xFactor = -1;
                        yFactor = 1;
                    }else if(45 < i && i < 135) {
                        xFactor = 1;
                        yFactor = -1;
                    }
                    break;

                case Surface.ROTATION_0:
                case Surface.ROTATION_180:
                    mSwapXY = true;
                    if((315 < i && i <= 360) || (i < 45) ) {
                        xFactor = 1;
                        yFactor = 1;
                    }else if(135 < i && i < 225) {
                        xFactor = -1;
                        yFactor = -1;
                    }
                    break;
            }

            if(LauncherPreferences.PREF_GYRO_INVERT_X) xFactor *= -1;
            if(LauncherPreferences.PREF_GYRO_INVERT_Y) yFactor *= -1;
        }
    }
}
