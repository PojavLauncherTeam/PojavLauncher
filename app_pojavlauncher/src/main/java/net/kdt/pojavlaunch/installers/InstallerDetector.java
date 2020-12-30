package net.kdt.pojavlaunch.installers;

import java.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;

import com.google.gson.*;
import net.kdt.pojavlaunch.value.*;

public class InstallerDetector
{
    public static boolean isFabric(BaseInstaller installer) {
        return installer.mJarFile.getEntry("net/fabricmc/installer/Main.class") != null;
    }
    public static boolean isOptiFine(BaseInstaller installer) {
        Enumeration e = installer.mJarFile.entries();
        return installer.mJarFile.getEntry("optifine/Installer.class") != null;
    }
    // Forge Legacy: for 1.12.1 and below
    public static boolean isForgeLegacy(BaseInstaller installer) throws IOException, JsonSyntaxException {
        ForgeInstallProfile profile = LegacyForgeInstaller.readInstallProfile(installer);
        return profile != null && profile.versionInfo != null;
    }
    
    // Forge for 1.12.2 only
    public static boolean isForge1p12p2(BaseInstaller installer) throws IOException, JsonSyntaxException {
        ForgeInstallProfile profile = LegacyForgeInstaller.readInstallProfile(installer);
        // Forge 1.12.2 install_profile.json has same format as Forge 1.13+
        return isForgeNew(installer) && profile.minecraft.equals("1.12.2");
    }
    
    // Forge New: for 1.13 and above
    public static boolean isForgeNew(BaseInstaller installer) throws IOException, JsonSyntaxException {
        ForgeInstallProfile profile = LegacyForgeInstaller.readInstallProfile(installer);
        return profile != null && profile.version != null;
    }
}
