package net.kdt.pojavlaunch.utils;

import android.content.*;
import android.content.res.*;
import android.os.Build;

import androidx.preference.*;
import java.util.*;
import net.kdt.pojavlaunch.prefs.*;

public class LocaleUtils {
    public static final Locale DEFAULT_LOCALE;
    
    static {
        DEFAULT_LOCALE = Locale.getDefault();
    }
    
    public static Context setLocale(Context context) {
        if (LauncherPreferences.DEFAULT_PREF == null) {
            LauncherPreferences.DEFAULT_PREF = PreferenceManager.getDefaultSharedPreferences(context);
			LauncherPreferences.loadPreferences(context);
        }
        
        Locale locale;
        if (LauncherPreferences.PREF_LANGUAGE.equals("default")) {
            locale = DEFAULT_LOCALE;
        } else {
            locale = new Locale(LauncherPreferences.PREF_LANGUAGE);
        }
        
        Locale.setDefault(locale);

        Resources res = context.getResources();
        Configuration config = new Configuration(res.getConfiguration());
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(locale);
            context = context.createConfigurationContext(config);
        }else {
            config.locale = locale;
            res.updateConfiguration(config,res.getDisplayMetrics());
        }
        return context;
    }
}
