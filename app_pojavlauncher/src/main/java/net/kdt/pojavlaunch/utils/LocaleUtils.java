package net.kdt.pojavlaunch.utils;

import static net.kdt.pojavlaunch.prefs.LauncherPreferences.PREF_LANGUAGE;

import android.content.*;
import android.content.res.*;
import android.os.Build;

import androidx.preference.*;
import java.util.*;
import net.kdt.pojavlaunch.prefs.*;

public class LocaleUtils {

    private static Locale CURRENT_LOCALE;

    public static Locale getLocale(){
        return Locale.getDefault();
    }
    
    public static Context setLocale(Context context) {
        if (LauncherPreferences.DEFAULT_PREF == null) {
            LauncherPreferences.DEFAULT_PREF = PreferenceManager.getDefaultSharedPreferences(context);
            LauncherPreferences.loadPreferences(context);
        }


        if (LauncherPreferences.PREF_LANGUAGE.equals("default")) {
            CURRENT_LOCALE = getLocale();
        } else {
            if(CURRENT_LOCALE == null || !PREF_LANGUAGE.equalsIgnoreCase(CURRENT_LOCALE.toString())){
                String[] localeString;
                if(PREF_LANGUAGE.contains("_")){
                    localeString = PREF_LANGUAGE.split("_");
                }else{
                    localeString = new String[]{PREF_LANGUAGE, ""};
                }
                CURRENT_LOCALE = new Locale(localeString[0], localeString[1]);
            }

        }


        
        Locale.setDefault(CURRENT_LOCALE);

        Resources res = context.getResources();
        Configuration config = res.getConfiguration();

        if (Build.VERSION.SDK_INT >= 24) {
            config.setLocale(CURRENT_LOCALE);
            context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
        } else {
            config.locale = CURRENT_LOCALE;
            context.getApplicationContext().createConfigurationContext(config);
        }

        return context;
    }
}
