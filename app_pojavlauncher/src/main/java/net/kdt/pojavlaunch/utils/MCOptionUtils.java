package net.kdt.pojavlaunch.utils;
import static org.lwjgl.glfw.CallbackBridge.windowHeight;
import static org.lwjgl.glfw.CallbackBridge.windowWidth;

import android.os.Build;
import android.os.FileObserver;
import android.util.Log;

import androidx.annotation.Nullable;

import net.kdt.pojavlaunch.Tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

public class MCOptionUtils
{
    private static final HashMap<String,String> parameterMap = new HashMap<>();
    private static final ArrayList<WeakReference<MCOptionListener>> optionListeners = new ArrayList<>();
    private static FileObserver fileObserver;
    public interface MCOptionListener {
         /** Called when an option is changed. Don't know which one though */
        void onOptionChanged();
    }
    
    public static void load() {
        if(fileObserver == null){
            setupFileObserver();
        }

        parameterMap.clear();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(Tools.DIR_GAME_NEW + "/options.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                int firstColonIndex = line.indexOf(':');
                if(firstColonIndex < 0) {
                    Log.w(Tools.APP_NAME, "No colon on line \""+line+"\", skipping");
                    continue;
                }
                parameterMap.put(line.substring(0,firstColonIndex), line.substring(firstColonIndex+1));
            }
            reader.close();
        } catch (IOException e) {
            Log.w(Tools.APP_NAME, "Could not load options.txt", e);
        }
    }
    
    public static void set(String key, String value) {
        parameterMap.put(key,value);
    }

    public static String get(String key){
        return parameterMap.get(key);
    }
    
    public static void save() {
        StringBuilder result = new StringBuilder();
        for(String key : parameterMap.keySet())
            result.append(key)
                    .append(':')
                    .append(parameterMap.get(key))
                    .append('\n');
        
        try {
            Tools.write(Tools.DIR_GAME_NEW + "/options.txt", result.toString());
        } catch (IOException e) {
            Log.w(Tools.APP_NAME, "Could not save options.txt", e);
        }
    }

    /** @return The stored Minecraft GUI scale, also auto-computed if on auto-mode or improper setting */
    public static int getMcScale() {
        MCOptionUtils.load();
        String str = MCOptionUtils.get("guiScale");
        int guiScale = (str == null ? 0 :Integer.parseInt(str));

        int scale = Math.max(Math.min(windowWidth / 320, windowHeight / 240), 1);
        if(scale < guiScale || guiScale == 0){
            guiScale = scale;
        }

        return guiScale;
    }

    private static void setupFileObserver(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            fileObserver = new FileObserver(new File(Tools.DIR_GAME_NEW + "/options.txt"), FileObserver.MODIFY) {
                @Override
                public void onEvent(int i, @Nullable String s) {
                    MCOptionUtils.load();
                    notifyListeners();
                }
            };
        }else{
            fileObserver = new FileObserver(Tools.DIR_GAME_NEW + "/options.txt", FileObserver.MODIFY) {
                @Override
                public void onEvent(int i, @Nullable String s) {
                    MCOptionUtils.load();
                    notifyListeners();
                }
            };
        }

        fileObserver.startWatching();
    }

    public static void notifyListeners(){
        for(WeakReference<MCOptionListener> weakReference : optionListeners){
            MCOptionListener optionListener = weakReference.get();
            if(optionListener == null) continue;

            optionListener.onOptionChanged();
        }
    }

    public static void addMCOptionListener(MCOptionListener listener){
        optionListeners.add(new WeakReference<>(listener));
    }

    public static void removeMCOptionListener(MCOptionListener listener){
        for(WeakReference<MCOptionListener> weakReference : optionListeners){
            MCOptionListener optionListener = weakReference.get();
            if(optionListener == null) continue;
            if(optionListener == listener){
                optionListeners.remove(weakReference);
                return;
            }
        }
    }

}
