package net.kdt.pojavlaunch.utils;

import java.util.*;
import java.io.*;
import android.util.*;
import net.kdt.pojavlaunch.*;

public class MCOptionUtils
{
    public static final MCOptionUtils INSTANCE = new MCOptionUtils();
    
    private List<String> mLineList;
    
    private MCOptionUtils() {
        mLineList = new ArrayList<String>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(Tools.MAIN_PATH + "/options.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                mLineList.add(line);
            }
            reader.close();
        } catch (IOException e) {
            Log.w(Tools.APP_NAME, "Could not load options.txt", e);
        }
    }
    
    public void set(String key, String value) {
        for (int i = 0; i < mLineList.size(); i++) {
            String line = mLineList.get(i);
            if (line.startsWith(key + ":")) {
                mLineList.set(i, key + ":" + value);
                return;
            }
        }
        
        mLineList.add(key + ":" + value);
    }
    
    public void save() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < mLineList.size(); i++) {
            result.append(mLineList.get(i));
            if (i + 1 < mLineList.size()) {
                result.append("\n");
            }
        }
        
        try {
            Tools.write(Tools.MAIN_PATH + "/options.txt", result.toString());
        } catch (IOException e) {
            Log.w(Tools.APP_NAME, "Could not save options.txt", e);
        }
    }
}
