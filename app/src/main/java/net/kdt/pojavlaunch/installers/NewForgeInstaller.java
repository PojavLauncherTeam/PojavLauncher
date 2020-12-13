package net.kdt.pojavlaunch.installers;

import java.io.*;
import net.kdt.pojavlaunch.*;

public class NewForgeInstaller extends BaseInstaller
 {
    public NewForgeInstaller(BaseInstaller i) {
        from(i);
    }

    @Override
    public int install(JavaGUILauncherActivity ctx) throws IOException {
        // Unused ZipFile
        mJarFile.close();

        ctx.appendlnToLog("Launching JVM");
        return ctx.launchJavaRuntime(null,
            "-cp " + Tools.MAIN_PATH + "/config/forge-installer-headless.jar:" + mFile.getAbsolutePath() + " me.xfl03.HeadlessInstaller --installClient .");
    }
}
