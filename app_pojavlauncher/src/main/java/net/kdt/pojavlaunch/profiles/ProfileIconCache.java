package net.kdt.pojavlaunch.profiles;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.util.Log;

import net.kdt.pojavlaunch.R;

import java.util.HashMap;
import java.util.Map;

public class ProfileIconCache {
    private static final String BASE64_PNG_HEADER = "data:image/png;base64,";
    private static final Map<String, BitmapDrawable> sIconCache = new HashMap<>();
    private static BitmapDrawable sDefaultIcon;

    public static void initDefault(Context context) {
        if(sDefaultIcon == null)
            sDefaultIcon = new BitmapDrawable(context.getResources(),
                    BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_menu_java));
    }

    public static void clearIconCache() {
        for(String key : sIconCache.keySet()) {
            BitmapDrawable bitmapDrawable = sIconCache.get(key);
            if(bitmapDrawable != sDefaultIcon && bitmapDrawable != null) {
                bitmapDrawable.getBitmap().recycle();
            }
        }
        sIconCache.clear();
    }

    public static BitmapDrawable getCachedIcon(String key) {
        return sIconCache.get(key);
    }

    public static BitmapDrawable submitIcon(String key, String base64) {
        byte[] pngBytes = Base64.decode(base64, Base64.DEFAULT);
        BitmapDrawable cachedIcon = new BitmapDrawable(BitmapFactory.decodeByteArray(pngBytes,0, pngBytes.length));
        sIconCache.put(key, cachedIcon);
        return cachedIcon;
    }

    public static BitmapDrawable tryResolveIcon(String profileName, String b64Icon) {
        BitmapDrawable icon;
        if (b64Icon != null && b64Icon.startsWith(BASE64_PNG_HEADER)) {
            icon = ProfileIconCache.submitIcon(profileName, b64Icon.substring(BASE64_PNG_HEADER.length()));
        }else{
            Log.i("IconParser","Unsupported icon: "+b64Icon);
            icon = ProfileIconCache.pushDefaultIcon(profileName);
        }
        return icon;
    }

    public static BitmapDrawable pushDefaultIcon(String key) {
        sIconCache.put(key, sDefaultIcon);
        return sDefaultIcon;
    }
}
