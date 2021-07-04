package net.kdt.pojavlaunch.utils;

import java.util.*;
import java.io.*;
import android.util.*;
import net.kdt.pojavlaunch.*;

public class MCOptionUtils
{
    private static final List<String> mLineList = Collections.synchronizedList(new ArrayList<>());
    
    public static void load() {
        mLineList.clear();
        
        try {
            BufferedReader reader = new BufferedReader(new FileReader(Tools.DIR_GAME_NEW + "/options.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                mLineList.add(line);
            }
            reader.close();
        } catch (IOException e) {
            Log.w(Tools.APP_NAME, "Could not load options.txt", e);
        }
    }
    
    public static void set(String key, String value) {
        for (int i = 0; i < mLineList.size(); i++) {
            String line = mLineList.get(i);
            if (line.startsWith(key + ":")) {
                mLineList.set(i, key + ":" + value);
                return;
            }
        }
        
        mLineList.add(key + ":" + value);
    }

    public static String get(String key){
        if(mLineList.isEmpty()) load();
        String searchedLine=null;
        for(String line : mLineList){
            if(line.startsWith(key + ":")) {
                searchedLine = line;
                break;
            }
        }
        if(searchedLine != null) return searchedLine.substring(searchedLine.indexOf(':') + 1);
        else return null;
    }
    
    public static void save() {
        StringBuilder result = new StringBuilder();
        for(String line : mLineList)
            result.append(line).append('\n');
        
        try {
            Tools.write(Tools.DIR_GAME_NEW + "/options.txt", result.toString());
        } catch (IOException e) {
            Log.w(Tools.APP_NAME, "Could not save options.txt", e);
        }
    }
}
