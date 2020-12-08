package net.kdt.pojavlaunch;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.net.*;
import android.support.customtabs.*;
import android.support.v4.content.*;
import android.widget.*;
import net.kdt.pojavlaunch.*;

public class CustomTabs {

    public static void openTab(Context context, String url) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setShowTitle(true);
        
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(context, Uri.parse(url));
    }

    public static class CopyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String url = intent.getDataString();

            ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData data = ClipData.newPlainText("Link", url);
            clipboardManager.setPrimaryClip(data);

            Toast.makeText(context, "Copied " + url, Toast.LENGTH_SHORT).show();
        }
    }
}
