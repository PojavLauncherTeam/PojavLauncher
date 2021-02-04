package net.kdt.pojavlaunch;

import android.content.*;
import android.net.*;
import androidx.browser.customtabs.*;

public class CustomTabs {

    public static void openTab(Context context, String url) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setShowTitle(true);
        
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(context, Uri.parse(url));
    }
}
