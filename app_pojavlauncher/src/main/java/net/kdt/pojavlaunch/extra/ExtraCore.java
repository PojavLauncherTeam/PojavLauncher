package net.kdt.pojavlaunch.extra;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Class providing callback across all of a program
 * to allow easy thread safe implementations of UI update without context leak
 *
 * This class uses a singleton pattern to simplify access to it
 */
public final class ExtraCore {
    // No unwanted instantiation
    private ExtraCore(){}

    // Store the key-value pair
    private final Map<String, String> valueMap = new ConcurrentHashMap<>();

    // Store what each ExtraListener listen to
    private final Map<String, ConcurrentLinkedQueue<WeakReference<ExtraListener>>> listenerMap = new ConcurrentHashMap<>();

    // Inner class for singleton implementation
    private static class ExtraCoreSingleton {
        private static final ExtraCore extraCore = new ExtraCore();
    }

    // All public methods will pass through this one
    private static ExtraCore getInstance(){
        return ExtraCoreSingleton.extraCore;
    }

    /**
     * Set the value associated to a key and trigger all listeners
     * @param key The key
     * @param value The value
     */
    public static void setValue(String key, String value){
        getInstance().valueMap.put(key, value);
        ConcurrentLinkedQueue<WeakReference<ExtraListener>> extraListenerList = getInstance().listenerMap.get(key);
        for(WeakReference<ExtraListener> listener : extraListenerList){
            if(listener.get() == null){
                extraListenerList.remove(listener);
                continue;
            }
            listener.get().notifyDataChanged(key, value);
        }
    }

    /** @return The value behind the key */
    public static String getValue(String key){
        return getInstance().valueMap.get(key);
    }

    /** Remove the key and its value from the valueMap */
    public static void removeValue(String key){
        getInstance().valueMap.remove(key);
    }

    /** Remove all values */
    public static void removeAllValues(){
        getInstance().valueMap.clear();
    }

    /**
     * Link an ExtraListener to a value
     * @param key The value key to look for
     * @param listener The ExtraListener to link
     */
    public static void addExtraListener(String key, ExtraListener listener){
        ConcurrentLinkedQueue<WeakReference<ExtraListener>> listenerList = getInstance().listenerMap.get(key);
        // Look for new sets
        if(listenerList == null){
            listenerList = new ConcurrentLinkedQueue<>();
            getInstance().listenerMap.put(key, listenerList);
        }

        // This is kinda naive, I should look for duplicates
        listenerList.add(new WeakReference<>(listener));
    }

    /**
     * Unlink an ExtraListener from a value.
     * Unlink null references found along the way
     * @param key The value key to ignore now
     * @param listener The ExtraListener to unlink
     */
    public static void removeExtraListenerFromValue(String key, ExtraListener listener){
        ConcurrentLinkedQueue<WeakReference<ExtraListener>> listenerList = getInstance().listenerMap.get(key);
        // Look for new sets
        if(listenerList == null){
            listenerList = new ConcurrentLinkedQueue<>();
            getInstance().listenerMap.put(key, listenerList);
        }

        // Removes all occurrences of ExtraListener and all null references
        for(WeakReference<ExtraListener> listenerWeakReference : listenerList){
            ExtraListener actualListener = listenerWeakReference.get();

            if(actualListener == null || actualListener == listener){
                listenerList.remove(listenerWeakReference);
            }
        }
    }

    /**
     * Unlink all ExtraListeners from a value
     * @param key The key to which ExtraListener are linked
     */
    public static void removeAllExtraListenersFromValue(String key){
        ConcurrentLinkedQueue<WeakReference<ExtraListener>> listenerList = getInstance().listenerMap.get(key);
        // Look for new sets
        if(listenerList == null){
            listenerList = new ConcurrentLinkedQueue<>();
            getInstance().listenerMap.put(key, listenerList);
        }

        listenerList.clear();
    }

    /**
     * Remove all ExtraListeners from listening to any value
     */
    public static void removeAllExtraListeners(){
        getInstance().listenerMap.clear();
    }

}
