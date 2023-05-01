package net.kdt.pojavlaunch.extra;

import androidx.annotation.NonNull;

/**
 * Listener class for the ExtraCore
 * An ExtraListener can listen to a virtually unlimited amount of values
 */
public interface ExtraListener<T> {

    /**
     * Called upon a new value being set
     * @param key The name of the value
     * @param value The new value as a string
     * @return Whether you consume the Listener (stop listening)
     */
    @SuppressWarnings("SameReturnValue")
    boolean onValueSet(String key, @NonNull T value);

}