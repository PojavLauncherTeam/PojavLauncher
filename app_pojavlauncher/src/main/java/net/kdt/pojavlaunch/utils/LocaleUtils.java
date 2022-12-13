package net.kdt.pojavlaunch.utils;


import static net.kdt.pojavlaunch.prefs.LauncherPreferences.DEFAULT_PREF;

import android.content.*;
import android.content.res.*;
import android.os.Build;

import androidx.preference.*;
import java.util.*;
import net.kdt.pojavlaunch.prefs.*;

public class LocaleUtils {

    public static Locale getLocale(){
        return Locale.getDefault();
    }

    public static Context setLocale(Context context) {
        if (DEFAULT_PREF == null) {
            DEFAULT_PREF = PreferenceManager.getDefaultSharedPreferences(context);
            LauncherPreferences.loadPreferences(context);
        }

        if(DEFAULT_PREF.getBoolean("force_english", false)){
            Locale locale = Locale.ENGLISH;
            Locale.setDefault(locale);
            Configuration config = context.getResources().getConfiguration();
            config.locale = locale;
            context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
        }

        return context;
    }
}
