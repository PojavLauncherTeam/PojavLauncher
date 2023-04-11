package net.kdt.pojavlaunch.value.launcherprofiles;
import android.util.Log;

import com.google.gson.*;
import net.kdt.pojavlaunch.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class LauncherProfiles {
    public static MinecraftLauncherProfiles mainProfileJson;
    public static File launcherProfilesFile = new File(Tools.DIR_GAME_NEW + "/launcher_profiles.json");
    public static MinecraftLauncherProfiles update() {
        try {
            if (mainProfileJson == null) {
                if (launcherProfilesFile.exists()) {
                    mainProfileJson = Tools.GLOBAL_GSON.fromJson(Tools.read(launcherProfilesFile.getAbsolutePath()), MinecraftLauncherProfiles.class);
                    if(mainProfileJson.profiles == null) mainProfileJson.profiles = new HashMap<>();
                    else if(LauncherProfiles.normalizeProfileIds(mainProfileJson)){
                        LauncherProfiles.update();
                    }
                } else {
                    mainProfileJson = new MinecraftLauncherProfiles();
                    mainProfileJson.profiles = new HashMap<>();
                }
            } else {
                Tools.write(launcherProfilesFile.getAbsolutePath(), mainProfileJson.toJson());
            }

            // insertMissing();
            return mainProfileJson;
        } catch (Throwable th) {
            throw new RuntimeException(th);
        }
    }

    /**
     * For all keys to be UUIDs, effectively isolating profile created by installers
     * This avoids certain profiles to be erased by the installer
     * @return Whether some profiles have been normalized
     */
    private static boolean normalizeProfileIds(MinecraftLauncherProfiles launcherProfiles){
        boolean hasNormalized = false;
        ArrayList<String> keys = new ArrayList<>();

        // Detect denormalized keys
        for(String profileKey : launcherProfiles.profiles.keySet()){
            try{
                UUID.fromString(profileKey);
            }catch (IllegalArgumentException exception){
                keys.add(profileKey);
                Log.w(LauncherProfiles.class.toString(), "Illegal profile uuid: " + profileKey);
            }
        }

        // Swap the new keys
        for(String profileKey: keys){
            String uuid = UUID.randomUUID().toString();
            while(launcherProfiles.profiles.containsKey(uuid)) {
                uuid = UUID.randomUUID().toString();
            }

            launcherProfiles.profiles.put(uuid, launcherProfiles.profiles.get(profileKey));
            launcherProfiles.profiles.remove(profileKey);
            hasNormalized = true;
        }

        return hasNormalized;
    }
    /*
     public static String insert;

     private static void insertMissing() {
     if (mainProfileJson.authenticationDatabase == null) {
     MinecraftAuthenticationDatabase mad = new MinecraftAuthenticationDatabase();
     mainProfileJson.authenticationDatabase = mad;
     }
     }
     */
}
