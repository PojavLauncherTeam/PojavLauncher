package net.kdt.pojavlaunch;
import java.util.*;

public class DisplayableLocale {
    public final Locale mLocale;
    public final CharSequence mName;
    
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
