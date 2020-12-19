package net.kdt.pojavlaunch.value;

import net.kdt.pojavlaunch.*;

public class ForgeInstallProfile {
    // ----- < 1.12.2 Forge Install Profile -----
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
    
    // ----- 1.12.2+ Forge Install Profile -----
    public String version;
    public String json;
    public String path;
    public String minecraft; // target Minecraft version
}
