package net.kdt.pojavlaunch.extra;

/**
 * Listener class for the ExtraCore
 * An ExtraListener can listen to a virtually unlimited amount of values
 */
public abstract class ExtraListener {

    /**
     * Called by the ExtraCore after a value is set.
     * Technically, it can be triggered from outside but is seems pointless
     */
    public final void notifyDataChanged(String key, String value){
        if(onValueSet(key, value)){
            ExtraCore.removeExtraListenerFromValue(key, this);
        }
    }

    /**
     * Called upon a new value being set
     * @param key The name of the value
     * @param value The new value as a string
     * @return Whether you consume the Listener (stop listening)
     */
    public abstract boolean onValueSet(String key, String value);
}
