package net.kdt.pojavlaunch.profiles;

import static net.kdt.pojavlaunch.profiles.ProfileAdapter.CREATE_PROFILE_MAGIC;

import android.content.Context;
import android.content.res.Resources;
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
    private static final String BASE64_PNG_HEADER = "data:image/png;base64,";
    private static final Map<String, Drawable> sIconCache = new HashMap<>();
    private static Drawable sDefaultIcon;


    public static void initDefault(Context context) {
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
            if(profileName.equals(CREATE_PROFILE_MAGIC)){
                icon = ProfileIconCache.pushAddProfileIcon(ResourcesCompat.getDrawable(resources, R.drawable.ic_add, null));
            }else{
                icon = ProfileIconCache.pushDefaultIcon(profileName);
            }

        }
        return icon;
    }

    public static Drawable pushAddProfileIcon(Drawable drawable){
        sIconCache.put(CREATE_PROFILE_MAGIC, drawable);
        return drawable;
    }

    public static Drawable pushDefaultIcon(String key) {
        sIconCache.put(key, sDefaultIcon);

        return sDefaultIcon;
    }
}
