package net.kdt.pojavlaunch.installers;


import java.io.*;
import net.kdt.pojavlaunch.*;

public class FabricInstaller extends BaseInstaller {
    public FabricInstaller(BaseInstaller i) {
        from(i);
    }

    @Override
    public int install(JavaGUILauncherActivity ctx) throws IOException {
        // Unused ZipFile
        mJarFile.close();
        
        String mcversion = ctx.dialogInput("Fabric installer", R.string.main_version);
        
        ctx.appendlnToLog("Launching JVM");
        return ctx.launchJavaRuntime(null,
            "-jar " + mFile.getAbsolutePath() + " client -dir . -mcversion " + mcversion);
    }
}
