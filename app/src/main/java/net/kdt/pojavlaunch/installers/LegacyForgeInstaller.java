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

public class LegacyForgeInstaller extends BaseInstaller {
    public LegacyForgeInstaller(BaseInstaller i) {
        from(i);
    }
    
    @Override
    public int install(JavaGUILauncherActivity ctx) throws IOException {
        String target;
        
        ctx.appendlnToLog("Reading install_profile.json");
        ForgeInstallProfile profile = readInstallProfile(this);
            
        // Write the json file
        File versionFile = new File(Tools.versnDir, profile.install.target);
        versionFile.mkdir();
        target = versionFile.getAbsolutePath() + "/" + profile.install.target + ".json";
        ctx.appendlnToLog("Writing " + target);
        Tools.write(
            target,
            Tools.GLOBAL_GSON.toJson(profile.versionInfo)
        );
        
        // Extract Forge universal
        String[] libInfos = profile.install.path.split(":");
        File libraryFile = new File(Tools.libraries, Tools.artifactToPath(libInfos[0], libInfos[1], libInfos[2]));
        libraryFile.getParentFile().mkdirs();
        target = libraryFile.getAbsolutePath().replace("-universal", "");
        ctx.appendlnToLog("Writing " + target);
        FileOutputStream out = new FileOutputStream(target);
        IOUtils.copy(mJarFile.getInputStream(mJarFile.getEntry(profile.install.filePath)), out);
        out.close();
        
        mJarFile.close();
        
        return 0;
    }
    
    public static ForgeInstallProfile readInstallProfile(BaseInstaller base) throws IOException, JsonSyntaxException {
        ZipEntry entry = base.mJarFile.getEntry("install_profile.json");
        return entry == null ? null : Tools.GLOBAL_GSON.fromJson(
                Tools.convertStream(
                    base.mJarFile.getInputStream(entry),
                    Charset.forName("UTF-8")
                ),
            ForgeInstallProfile.class
        );
    }
}
