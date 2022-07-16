package net.kdt.pojavlaunch;
import java.util.*;

public class DisplayableLocale {
    public final Locale mLocale;
    public final CharSequence mName;
    
    private static Locale processStringLocale(String locale) {
        if (locale.contains("-")) {
            String[] split = locale.split("-");
            return new Locale(split[0], split[1].toUpperCase());
        } else {
            return new Locale(locale);
        }
    }
    
    public DisplayableLocale(String locale) {
        this(processStringLocale(locale));
    }
    
    public DisplayableLocale(Locale locale) {
        this(locale, locale.getDisplayName(locale));
    }
    
    public DisplayableLocale(Locale locale, CharSequence name) {
        mLocale = locale;
        mName = name;
    }

    public Locale toLocale() {
        return mLocale;
    }
    
    @Override
    public String toString() {
        return mName.toString();
    }
}
