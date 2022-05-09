package net.kdt.pojavlaunch.value;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.value.launcherprofiles.MinecraftLauncherProfiles;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class PerVersionConfig {
    static File pvcFile;
    public static HashMap<String,VersionConfig> configMap;
    public static void update() throws IOException {
        if(configMap == null) {
            pvcFile = new File(Tools.DIR_GAME_HOME,"per-version-config.json");
            if(pvcFile.exists()) {
                try {
                    configMap = Tools.GLOBAL_GSON.fromJson(Tools.read(pvcFile.getAbsolutePath()), new TypeToken<HashMap<String, VersionConfig>>() {
                    }.getType());
                }catch(JsonSyntaxException ex) {
                    ex.printStackTrace();
                    configMap = new HashMap<>();
                }
            }else{
                configMap = new HashMap<>();
            }
        }else{
            Tools.write(pvcFile.getAbsolutePath(),Tools.GLOBAL_GSON.toJson(configMap));
        }
    }
    public static boolean erase() {
        return new File(Tools.DIR_GAME_HOME,"per-version-config.json").delete();
    }
    public static boolean exists() {
        return new File(Tools.DIR_GAME_HOME,"per-version-config.json").exists();
    }
    public static class VersionConfig {
        public String jvmArgs;
        public String gamePath;
        public String selectedRuntime;
        public String renderer;
    }
}
