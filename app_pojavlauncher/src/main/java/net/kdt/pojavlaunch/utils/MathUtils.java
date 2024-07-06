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
     * @return the RankedValue that wraps the object which has the closest value to targetValue, or null if values of all
     *         objects are less than targetValue
     * @param <T> the object type that is used for the search.
     */
    public static <T> RankedValue<T> findNearestPositive(int targetValue, List<T> objects, ValueProvider<T> valueProvider) {
        int delta = Integer.MAX_VALUE;
        T selectedObject = null;
        for(T object : objects) {
            int objectValue = valueProvider.getValue(object);
            if(objectValue < targetValue) continue;

            int currentDelta = objectValue - targetValue;
            if(currentDelta == 0) return new RankedValue<>(object, 0);
            if(currentDelta >= delta) continue;

            selectedObject = object;
            delta = currentDelta;
        }
        if(selectedObject == null) return null;
        return new RankedValue<>(selectedObject, delta);
    }

    public interface ValueProvider<T> {
        int getValue(T object);
    }

    public static final class RankedValue<T> {
        public final T value;
        public final int rank;
        public RankedValue(T value, int rank) {
            this.value = value;
            this.rank = rank;
        }
    }

    /**
     * Out of two objects, select one with the lowest value.
     * @param object1 Object 1 for comparsion
     * @param object2 Object 2 for comparsion
     * @param valueProvider Value provider for the objects
     * @return If value of object 1 is lower than or equal to object 2, returns object 1
     *         Otherwise, returns object 2
     * @param <T> Type of objects
     */
    public static <T> T objectMin(T object1, T object2, ValueProvider<T> valueProvider) {
        if(object1 == null) return object2;
        if(object2 == null) return object1;
        int value1 = valueProvider.getValue(object1);
        int value2 = valueProvider.getValue(object2);
        if(value1 <= value2) {
            return object1;
        } else {
            return object2;
        }
    }
}
