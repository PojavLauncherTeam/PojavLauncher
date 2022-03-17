package net.kdt.pojavlaunch.profiles;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import net.kdt.pojavlaunch.R;

import java.util.HashMap;
import java.util.Map;

public class ProfileIconCache {
    private static final String BASE64_PNG_HEADER = "data:image/png;base64,";
    private static final Map<String, Bitmap> iconCache = new HashMap<>();
    private static Bitmap defaultIcon;
    public static void initDefault(Context context) {
        if(defaultIcon == null)
            defaultIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_menu_java);
    }
    public static void clearIconCache() {
        for(String key : iconCache.keySet()) {
            Bitmap bitmap = iconCache.get(key);
            if(bitmap != defaultIcon && bitmap != null) {
                bitmap.recycle();
            }
        }
        iconCache.clear();
    }
    public static Bitmap getCachedIcon(String key) {
        return iconCache.get(key);
    }
    public static Bitmap submitIcon(String key, String base64) {
        byte[] pngBytes = Base64.decode(base64, Base64.DEFAULT);
        Bitmap cachedIcon = BitmapFactory.decodeByteArray(pngBytes,0,pngBytes.length);
        iconCache.put(key, cachedIcon);
        return cachedIcon;
    }
    public static Bitmap tryResolveIcon(String profileName, String b64Icon) {
        Bitmap icon;
        if (b64Icon != null && b64Icon.startsWith(BASE64_PNG_HEADER)) {
            icon = ProfileIconCache.submitIcon(profileName, b64Icon.substring(BASE64_PNG_HEADER.length()));
        }else{
            Log.i("IconParser","Unsupported icon: "+b64Icon);
            icon = ProfileIconCache.pushDefaultIcon(profileName);
        }
        return icon;
    }
    public static Bitmap pushDefaultIcon(String key) {
        iconCache.put(key, defaultIcon);
        return defaultIcon;
    }
}
