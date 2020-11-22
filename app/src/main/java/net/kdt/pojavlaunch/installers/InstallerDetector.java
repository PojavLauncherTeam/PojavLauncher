package net.kdt.pojavlaunch.installers;

import java.io.*;
import com.google.gson.*;
import net.kdt.pojavlaunch.value.*;

public class InstallerDetector
{
    // Forge Legacy: for 1.12.2 and below
    public static boolean isForgeLegacy(BaseInstaller installer) throws IOException, JsonSyntaxException {
        ForgeInstallProfile profile = ForgeInstaller.readInstallProfile(installer);
        return profile != null && profile.versionInfo != null;
    }
}
