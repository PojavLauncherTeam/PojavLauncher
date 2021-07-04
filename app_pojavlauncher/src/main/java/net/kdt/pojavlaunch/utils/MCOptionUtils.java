package net.kdt.pojavlaunch.utils;
import java.io.*;
import java.util.*;
import android.util.*;
import net.kdt.pojavlaunch.*;

public class MCOptionUtils
{
    private static final HashMap<String,String> parameterMap = new HashMap<>();
    
    public static void load() {
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
}
