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
