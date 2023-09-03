package net.kdt.pojavlaunch.profiles;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.util.Log;

import androidx.core.content.res.ResourcesCompat;

import net.kdt.pojavlaunch.R;

import java.util.HashMap;
import java.util.Map;

public class ProfileIconCache {
    // Data header format: data:<mime>;<encoding>,<data>
    private static final String DATA_HEADER = "data:";
    private static final String FALLBACK_ICON_NAME = "default";
    private static final Map<String, Drawable> sIconCache = new HashMap<>();
    private static final Map<String, Drawable> sStaticIconCache = new HashMap<>();


    public static Drawable fetchIcon(Resources resources, String key, String icon) {
        Drawable cachedIcon = sIconCache.get(key);
        if(cachedIcon != null) return cachedIcon;
        if(icon != null && icon.startsWith(DATA_HEADER)) return fetchDataIcon(resources, key, icon);
        else return fetchStaticIcon(resources, key, icon);
    }

    private static Drawable fetchDataIcon(Resources resources, String key, String icon) {
        Drawable dataIcon = readDataIcon(resources, icon);
        if(dataIcon == null) dataIcon = fetchFallbackIcon(resources);
        sIconCache.put(key, dataIcon);
        return dataIcon;
    }

    private static Drawable fetchStaticIcon(Resources resources, String key, String icon) {
        Drawable staticIcon = sStaticIconCache.get(icon);
        if(staticIcon == null) {
            if(icon != null) staticIcon = getStaticIcon(resources, icon);
            if(staticIcon == null) staticIcon = fetchFallbackIcon(resources);
            sStaticIconCache.put(icon, staticIcon);
        }
        sIconCache.put(key, staticIcon);
        return staticIcon;
    }

    private static Drawable fetchFallbackIcon(Resources resources) {
        Drawable fallbackIcon = sStaticIconCache.get(FALLBACK_ICON_NAME);
        if(fallbackIcon == null) {
            fallbackIcon = getStaticIcon(resources, FALLBACK_ICON_NAME);
            sStaticIconCache.put(FALLBACK_ICON_NAME, fallbackIcon);
        }
        return fallbackIcon;
    }

    private static Drawable getStaticIcon(Resources resources, String icon) {
        int staticIconResource = getStaticIconResource(icon);
        if(staticIconResource == -1) return null;
        return ResourcesCompat.getDrawable(resources, staticIconResource, null);
    }

    private static int getStaticIconResource(String icon) {
        switch (icon) {
            case "default": return R.drawable.ic_pojav_full;
            case "fabric": return R.drawable.ic_fabric;
            case "quilt": return R.drawable.ic_quilt;
            default: return -1;
        }
    }

    private static Drawable readDataIcon(Resources resources, String icon) {
        byte[] iconData = extractIconData(icon);
        if(iconData == null) return null;
        Bitmap iconBitmap = BitmapFactory.decodeByteArray(iconData, 0, iconData.length);
        if(iconBitmap == null) return null;
        return new BitmapDrawable(resources, iconBitmap);
    }

    private static byte[] extractIconData(String inputString) {
        int firstSemicolon = inputString.indexOf(';');
        int commaAfterSemicolon = inputString.indexOf(',');
        if(firstSemicolon == -1 || commaAfterSemicolon == -1) return null;
        String dataEncoding = inputString.substring(firstSemicolon+1, commaAfterSemicolon);
        if(!dataEncoding.equals("base64")) return null;
        return Base64.decode(inputString.substring(commaAfterSemicolon+1), 0);
    }


    /*public static void initDefault(Context context) {
        if(sDefaultIcon != null) return;
        sDefaultIcon = ResourcesCompat.getDrawable(context.getResources(), R.mipmap.ic_launcher_foreground, null);
        if(sDefaultIcon != null) sDefaultIcon.setBounds(0, 0, 10, 10);
    }

    public static Drawable getCachedIcon(String key) {
        return sIconCache.get(key);
    }

    public static Drawable submitIcon(Resources resources, String key, String base64) {
        byte[] pngBytes = Base64.decode(base64, Base64.DEFAULT);
        Drawable drawable = new BitmapDrawable(resources, BitmapFactory.decodeByteArray(pngBytes, 0, pngBytes.length));
        sIconCache.put(key, drawable);
        return drawable;
    }

    public static Drawable tryResolveIcon(Resources resources, String profileName, String b64Icon) {
        Drawable icon;
        if (b64Icon != null && b64Icon.startsWith(BASE64_PNG_HEADER)) {
            icon = ProfileIconCache.submitIcon(resources, profileName, b64Icon.substring(BASE64_PNG_HEADER.length()));
        }else{
            Log.i("IconParser","Unsupported icon: "+b64Icon);
            icon = ProfileIconCache.pushDefaultIcon(profileName);
        }
        return icon;
    }

    public static Drawable pushDefaultIcon(String key) {
        sIconCache.put(key, sDefaultIcon);

        return sDefaultIcon;
    }*/
}
