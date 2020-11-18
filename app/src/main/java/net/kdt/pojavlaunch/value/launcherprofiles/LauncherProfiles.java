package net.kdt.pojavlaunch.value.launcherprofiles;
import com.google.gson.*;
import net.kdt.pojavlaunch.*;
import java.io.*;

public class LauncherProfiles
{
    public static MinecraftLauncherProfiles mainProfileJson;
    public static File launcherProfilesFile = new File(Tools.MAIN_PATH + "/launcher_profiles.json");
    public static MinecraftLauncherProfiles update() {
        try {
            if (mainProfileJson == null) {
                if (launcherProfilesFile.exists()) {
                    mainProfileJson = Tools.GLOBAL_GSON.fromJson(Tools.read(launcherProfilesFile.getAbsolutePath()), MinecraftLauncherProfiles.class);
                } else {
                    mainProfileJson = new MinecraftLauncherProfiles();
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
