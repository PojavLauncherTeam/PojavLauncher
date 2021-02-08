package net.kdt.pojavlaunch.value;
import android.util.Log;

import net.kdt.pojavlaunch.*;
import java.io.*;
import com.google.gson.*;
import android.graphics.Bitmap;
import android.util.Base64;

public class MinecraftAccount
{
    public String accessToken = "0"; // access token
    public String clientToken = "0"; // clientID: refresh and invalidate
    public String profileId = "0"; // authenticate UUID
    public String username = "Steve";
    //public String selectedVersion = "1.7.10";
    public String selectedProfile = "(Default)";
    public boolean isMicrosoft = false;
    public String msaRefreshToken = "0";
    public String skinFaceBase64;
    
    public void updateSkinFace() {
        try {
            Bitmap bSkin = AccountSkin.getSkin(profileId);
            if (bSkin.getWidth() != 64 || bSkin.getHeight() != 64) {
                Log.w("SkinLoader", "Only skin size 64x64 is currently supported, this skin is " + bSkin.getWidth() + "x" + bSkin.getHeight());
                return;
            }
            
            int[] pixels = new int[8 * 8];
            bSkin.getPixels(pixels, 0, 8, 8, 8, 8, 8); 
            bSkin.recycle();
            
            ByteArrayOutputStream outByteArr = new ByteArrayOutputStream();
            Bitmap bFace = Bitmap.createBitmap(pixels, 8, 8, Bitmap.Config.ARGB_8888);
            bFace.compress(Bitmap.CompressFormat.PNG, 100, outByteArr);
            bFace.recycle();
            skinFaceBase64 = Base64.encodeToString(outByteArr.toByteArray(), Base64.DEFAULT);
            outByteArr.close();
            
            Log.i("SkinLoader", "Update skin face success");
        } catch (IOException e) {
            // Skin refresh limit, no internet connection, etc...
            // Simply ignore updating skin face
            Log.w("SkinLoader", "Could not update skin face", e);
        }
    }
    
    public String save(String outPath) throws IOException {
        Tools.write(outPath, Tools.GLOBAL_GSON.toJson(this));
        return username;
    }
    
    public String save() throws IOException {
        return save(Tools.DIR_ACCOUNT_NEW + "/" + username + ".json");
    }
    
    public static MinecraftAccount parse(String content) throws JsonSyntaxException {
        return Tools.GLOBAL_GSON.fromJson(content, MinecraftAccount.class);
    }
    
    public static MinecraftAccount load(String name) throws JsonSyntaxException {
        try {
            MinecraftAccount acc = parse(Tools.read(Tools.DIR_ACCOUNT_NEW + "/" + name + ".json"));
            if (acc.accessToken == null) {
                acc.accessToken = "0";
            }
            if (acc.clientToken == null) {
                acc.clientToken = "0";
            }
            if (acc.profileId == null) {
                acc.profileId = "0";
            }
            if (acc.username == null) {
                acc.username = "0";
            }
            if (acc.selectedProfile == null) {
                acc.selectedProfile = "1.7.10";
            }
            if (acc.msaRefreshToken == null) {
                acc.msaRefreshToken = "0";
            }
            return acc;
        } catch(IOException e) {
            Log.e(MinecraftAccount.class.getName(), "Caught an exception while loading the profile",e);
            return null;
        }
    }
    
    public static void clearTempAccount() {
        File tempAccFile = new File(Tools.DIR_DATA, "cache/tempacc.json");
        tempAccFile.delete();
    }
    
    public static void saveTempAccount(MinecraftAccount acc) throws IOException {
        File tempAccFile = new File(Tools.DIR_DATA, "cache/tempacc.json");
        tempAccFile.delete();
        acc.save(tempAccFile.getAbsolutePath());
    }
}
