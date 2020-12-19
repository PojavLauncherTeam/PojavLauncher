package net.kdt.pojavlaunch.utils;

import android.content.*;
import android.content.res.*;
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
			LauncherPreferences.loadPreferences();
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
        config.setLocale(locale);
        context = context.createConfigurationContext(config);
        return context;
    }
}
