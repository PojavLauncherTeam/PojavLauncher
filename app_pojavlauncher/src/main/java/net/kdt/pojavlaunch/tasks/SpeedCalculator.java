package net.kdt.pojavlaunch.tasks;

/**
 * A simple class to calculate the average Internet speed using a simple moving average.
 */
public class SpeedCalculator {
    private long mLastMillis;
    private long mLastBytes;
    private int mIndex;
    private final double[] mPreviousInputs;
    private double mSum;

    public SpeedCalculator() {
        this(4);
    }

    public SpeedCalculator(int averageDepth) {
        mPreviousInputs = new double[averageDepth];
    }

    private double addToAverage(double speed) {
        mSum -= mPreviousInputs[mIndex];
        mSum += speed;
        mPreviousInputs[mIndex] = speed;
        if(++mIndex == mPreviousInputs.length) mIndex = 0;
        double dLength = mPreviousInputs.length;
        return (mSum + (dLength / 2d)) / dLength;
    }

    public double feed(long newBytes) {
        long millis = System.currentTimeMillis();
        long deltaBytes = newBytes - mLastBytes;
        long deltaMillis = millis - mLastMillis;
        mLastBytes = deltaBytes;
        mLastMillis = millis;
        double speed = (double)deltaBytes / (double)deltaMillis;
        return addToAverage(speed);
    }
}
