package net.kdt.pojavlaunch.utils;

import java.util.List;

public class MathUtils {

    //Ported from https://www.arduino.cc/reference/en/language/functions/math/map/
    public static float map(float x, float in_min, float in_max, float out_min, float out_max) {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }

    /** Returns the distance between two points. */
    public static float dist(float x1, float y1, float x2, float y2) {
        final float x = (x2 - x1);
        final float y = (y2 - y1);
        return (float) Math.hypot(x, y);
    }

    /**
     * Find the object T with the closest (or higher) value compared to targetValue
     * @param targetValue the target value
     * @param objects the list of objects that the search will be performed on
     * @param valueProvider the provider for each values
     * @return the object which has the closest value to targetValue, or null if values of all
     *         objects are less than targetValue
     * @param <T> the object type that is used for the search.
     */
    public static <T> T findNearestPositive(int targetValue, List<T> objects, ValueProvider<T> valueProvider) {
        int delta = Integer.MAX_VALUE;
        T selectedObject = null;
        for(T object : objects) {
            int objectValue = valueProvider.getValue(object);
            if(objectValue < targetValue) continue;

            int currentDelta = objectValue - targetValue;
            if(currentDelta == 0) return object;
            if(currentDelta >= delta) continue;

            selectedObject = object;
            delta = currentDelta;
        }
        return selectedObject;
    }

    public interface ValueProvider<T> {
        int getValue(T object);
    }
}
