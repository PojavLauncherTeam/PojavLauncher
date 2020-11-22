package net.kdt.pojavlaunch.value;

import net.kdt.pojavlaunch.*;

public class ForgeInstallProfile {
    public ForgeInstallProperties install;
    public JMinecraftVersionList.Version versionInfo;
    
    public static class ForgeInstallProperties {
        public String profileName;
        public String target;
        public String path;
        public String version;
        public String filePath; // universal file .jar
        public String minecraft; // target Minecraft version
    }
}
