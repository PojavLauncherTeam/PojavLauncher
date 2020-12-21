package net.kdt.pojavlaunch.installers;

import android.content.*;
import java.io.*;
import java.util.jar.*;
import net.kdt.pojavlaunch.*;
import java.nio.charset.*;
import net.kdt.pojavlaunch.value.*;
import org.apache.commons.io.*;
import com.google.gson.*;
import java.util.zip.*;

public class Legacy1p12p2ForgeInstaller extends BaseInstaller {
    public Legacy1p12p2ForgeInstaller(BaseInstaller i) {
        from(i);
    }

    @Override
    public int install(JavaGUILauncherActivity ctx) throws IOException {
        String target;

        ctx.appendlnToLog("Reading install_profile.json");
        ForgeInstallProfile profile = readInstallProfile(this);

        // Write the json file
        File versionFile = new File(Tools.DIR_HOME_VERSION, profile.version);
        versionFile.mkdir();
        target = versionFile.getAbsolutePath() + "/" + profile.version + ".json";
        ctx.appendlnToLog("Writing " + target + " from " + profile.json);
        ZipEntry versionJson = mJarFile.getEntry(profile.json==null ? "version.json" : profile.json.substring(profile.json.indexOf("/")+1,profile.json.length()));
        Tools.write(
            target,
            Tools.convertStream(mJarFile.getInputStream(versionJson))
        );

        // Forge 1.12.2+ installer does not include universal, so download
        // Users are already go throught Forge ads to download installer, so not again.
        String[] libInfos = profile.path.split(":");
        File libraryFile = new File(Tools.DIR_HOME_LIBRARY, Tools.artifactToPath(libInfos[0], libInfos[1], libInfos[2]));
        libraryFile.getParentFile().mkdirs();
        target = libraryFile.getAbsolutePath();
        String downloadPath = "https://files.minecraftforge.net/maven/" + profile.path.replace(":", "/").replace("net.minecraftforge","net/minecraftforge") + "/forge-" + libInfos[2] + "-universal.jar";
        ctx.appendlnToLog("Downloading " + target);
        Tools.downloadFile(downloadPath, target);
        
        mJarFile.close();
        
        return 0;
    }

    public static ForgeInstallProfile readInstallProfile(BaseInstaller base) throws IOException, JsonSyntaxException {
        ZipEntry entry = base.mJarFile.getEntry("install_profile.json");
        return entry == null ? null : Tools.GLOBAL_GSON.fromJson(
            Tools.convertStream(
                base.mJarFile.getInputStream(entry)
            ),
            ForgeInstallProfile.class
        );
    }
}
