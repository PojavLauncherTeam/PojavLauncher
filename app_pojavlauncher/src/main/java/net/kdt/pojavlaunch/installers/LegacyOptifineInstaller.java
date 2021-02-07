package net.kdt.pojavlaunch.installers;


import java.io.*;
import net.kdt.pojavlaunch.*;

public class LegacyOptifineInstaller extends BaseInstaller {
    public LegacyOptifineInstaller(BaseInstaller i) {
        from(i);
    }

    @Override
    public int install(JavaGUILauncherActivity ctx) throws IOException {
        mJarFile.close();
        ctx.appendlnToLog("Launching JVM");
        return ctx.launchJavaRuntime(null,
                "-jar "+Tools.DIR_GAME_HOME+"/config/OptiInst.jar " + mFile.getAbsolutePath() +" .");
    }
}
