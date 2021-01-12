package net.kdt.pojavlaunch.value;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.utils.DownloadUtils;

public class AccountSkin {
    public static Bitmap getSkin(String uuid) throws IOException {
        Profile p = Tools.GLOBAL_GSON.fromJson(DownloadUtils.downloadString("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid), Profile.class);
        for (Property property : p.properties) {
            if (property.name.equals("textures")) {
                return getSkinFromProperty(Tools.GLOBAL_GSON.fromJson(new String(Base64.decode(property.value, Base64.DEFAULT), "UTF-8"), SkinProperty.class));
            }
        }
        
        return null;
    }
    
    private static Bitmap getSkinFromProperty(SkinProperty p) throws IOException {
        for (Map.Entry<String, Texture> texture : p.textures.entrySet()) {
            if (texture.getKey().equals("SKIN")) {
                String skinFile = File.createTempFile("skin", ".png", new File(Tools.DIR_DATA, "cache")).getAbsolutePath();
                Tools.downloadFile(texture.getValue().url.replace("http://","https://"), skinFile);
                return BitmapFactory.decodeFile(skinFile);
            }
        }
        
        return null;
    }
    
    public static class Texture {
        public String url;
    }
    
    public static class SkinProperty {
        public Map<String, Texture> textures;
    }
    
    public static class Property {
        public String name, value;
    }
    
    public static class Profile {
        public Property[] properties;
    }
}

