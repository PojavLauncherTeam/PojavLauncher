package net.kdt.pojavlaunch.utils;
import static org.lwjgl.glfw.CallbackBridge.windowHeight;
import static org.lwjgl.glfw.CallbackBridge.windowWidth;

import android.os.Build;
import android.os.FileObserver;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.kdt.pojavlaunch.Tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MCOptionUtils {
    private static final HashMap<String,String> sParameterMap = new HashMap<>();
    private static final ArrayList<WeakReference<MCOptionListener>> sOptionListeners = new ArrayList<>();
    private static FileObserver sFileObserver;
    private static String sOptionFolderPath = null;
    public interface MCOptionListener {
        /** Called when an option is changed. Don't know which one though */
        void onOptionChanged();
    }


    public static void load(){
        load(sOptionFolderPath == null
                ? Tools.DIR_GAME_NEW
                : sOptionFolderPath);
    }

    public static void load(@NonNull String folderPath) {
        File optionFile = new File(folderPath + "/options.txt");
        if(!optionFile.exists()) {
            try { // Needed for new instances I guess  :think:
                optionFile.createNewFile();
            } catch (IOException e) { e.printStackTrace(); }
        }

        if(sFileObserver == null || !Objects.equals(sOptionFolderPath, folderPath)){
            sOptionFolderPath = folderPath;
            setupFileObserver();
        }
        sOptionFolderPath = folderPath; // Yeah I know, it may be redundant

        sParameterMap.clear();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(optionFile));
            String line;
            while ((line = reader.readLine()) != null) {
                int firstColonIndex = line.indexOf(':');
                if(firstColonIndex < 0) {
                    Log.w(Tools.APP_NAME, "No colon on line \""+line+"\", skipping");
                    continue;
                }
                sParameterMap.put(line.substring(0,firstColonIndex), line.substring(firstColonIndex+1));
            }
            reader.close();
        } catch (IOException e) {
            Log.w(Tools.APP_NAME, "Could not load options.txt", e);
        }
    }

    public static void set(String key, String value) {
        sParameterMap.put(key,value);
    }

    /** Set an array of String, instead of a simple value. Not supported on all options */
    public static void set(String key, List<String> values){
        sParameterMap.put(key, values.toString());
    }

    public static String get(String key){
        return sParameterMap.get(key);
    }

    /** @return A list of values from an array stored as a string */
    public static List<String> getAsList(String key){
        String value = get(key);

        // Fallback if the value doesn't exist
        if (value == null) return new ArrayList<>();

        // Remove the edges
        value = value.replace("[", "").replace("]", "");
        if (value.isEmpty()) return new ArrayList<>();

        return Arrays.asList(value.split(","));
    }

    public static void save() {
        StringBuilder result = new StringBuilder();
        for(String key : sParameterMap.keySet())
            result.append(key)
                    .append(':')
                    .append(sParameterMap.get(key))
                    .append('\n');

        try {
            sFileObserver.stopWatching();
            Tools.write(sOptionFolderPath + "/options.txt", result.toString());
            sFileObserver.startWatching();
        } catch (IOException e) {
            Log.w(Tools.APP_NAME, "Could not save options.txt", e);
        }
    }

    /** @return The stored Minecraft GUI scale, also auto-computed if on auto-mode or improper setting */
    public static int getMcScale() {
        String str = MCOptionUtils.get("guiScale");
        int guiScale = (str == null ? 0 :Integer.parseInt(str));

        int scale = Math.max(Math.min(windowWidth / 320, windowHeight / 240), 1);
        if(scale < guiScale || guiScale == 0){
            guiScale = scale;
        }

        return guiScale;
    }

    /** Add a file observer to reload options on file change
     * Listeners get notified of the change */
    private static void setupFileObserver(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            sFileObserver = new FileObserver(new File(sOptionFolderPath + "/options.txt"), FileObserver.MODIFY) {
                @Override
                public void onEvent(int i, @Nullable String s) {
                    MCOptionUtils.load();
                    notifyListeners();
                }
            };
        }else{
            sFileObserver = new FileObserver(sOptionFolderPath + "/options.txt", FileObserver.MODIFY) {
                @Override
                public void onEvent(int i, @Nullable String s) {
                    MCOptionUtils.load();
                    notifyListeners();
                }
            };
        }

        sFileObserver.startWatching();
    }

    /** Notify the option listeners */
    public static void notifyListeners(){
        for(WeakReference<MCOptionListener> weakReference : sOptionListeners){
            MCOptionListener optionListener = weakReference.get();
            if(optionListener == null) continue;

            optionListener.onOptionChanged();
        }
    }

    /** Add an option listener, notice how we don't have a reference to it */
    public static void addMCOptionListener(MCOptionListener listener){
        sOptionListeners.add(new WeakReference<>(listener));
    }

    /** Remove a listener from existence, or at least, its reference here */
    public static void removeMCOptionListener(MCOptionListener listener){
        for(WeakReference<MCOptionListener> weakReference : sOptionListeners){
            MCOptionListener optionListener = weakReference.get();
            if(optionListener == null) continue;
            if(optionListener == listener){
                sOptionListeners.remove(weakReference);
                return;
            }
        }
    }

}
