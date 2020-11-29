package net.kdt.pojavlaunch.installers;

import java.io.*;
import com.google.gson.*;
import net.kdt.pojavlaunch.value.*;

public class InstallerDetector
{
    public static boolean isFabric(BaseInstaller installer) {
        return installer.mJarFile.getEntry("net/fabricmc/installer/Main.class") != null;
    }
    
    // Forge Legacy: for 1.12.1 and below
    public static boolean isForgeLegacy(BaseInstaller installer) throws IOException, JsonSyntaxException {
        ForgeInstallProfile profile = LegacyForgeInstaller.readInstallProfile(installer);
        return profile != null && profile.versionInfo != null;
    }
    
    // Forge New: for 1.12.2 and above
    public static boolean isForgeNew(BaseInstaller installer) throws IOException, JsonSyntaxException {
        ForgeInstallProfile profile = LegacyForgeInstaller.readInstallProfile(installer);
        return profile != null && profile.version != null;
    }
}
