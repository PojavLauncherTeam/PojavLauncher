package net.kdt.pojavlaunch.installers;
import android.content.*;
import java.io.*;
import java.util.jar.*;
import net.kdt.pojavlaunch.*;
import java.nio.charset.*;
import net.kdt.pojavlaunch.value.*;
import org.apache.commons.io.*;

public class ForgeInstaller extends BaseInstaller {
    @Override
    public void install(Context ctx) throws IOException {
        JarFile file = new JarFile(mJarFile);
        ForgeInstallProfile profile =
            Tools.GLOBAL_GSON.fromJson(
                Tools.convertStream(file.getInputStream(
                    file.getEntry("install_profile.json")),
                    Charset.forName("UTF-8")
                ),
            ForgeInstallProfile.class
        );
        
        // Write the json file
        File versionFile = new File(Tools.versnDir, profile.install.target);
        versionFile.mkdir();
        Tools.write(
            versionFile.getAbsolutePath() + "/" + profile.install.target + ".json",
            Tools.GLOBAL_GSON.toJson(profile.versionInfo)
        );
        
        // Extract Forge universal
        String[] libInfos = profile.install.path.split(":");
        File libraryFile = new File(Tools.libraries, Tools.artifactToPath(libInfos[0], libInfos[1], libInfos[2]));
        libraryFile.mkdirs();
        FileOutputStream out = new FileOutputStream(libraryFile.getAbsolutePath() + "/" + profile.install.filePath.replace("-universal", ""));
        IOUtils.copy(file.getInputStream(file.getEntry(profile.install.filePath)), out);
        out.close();
    }
}
