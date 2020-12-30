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

public class LegacyOptifineInstaller extends BaseInstaller {
    public LegacyOptifineInstaller(BaseInstaller i) {
        from(i);
    }

    @Override
    public int install(JavaGUILauncherActivity ctx) throws IOException {
        mJarFile.close();
        ctx.appendlnToLog("Launching JVM");
        return ctx.launchJavaRuntime(null,
                "-jar "+Tools.DIR_GAME_NEW+"/config/OptiInst.jar " + mFile.getAbsolutePath() +" .");
    }
}
