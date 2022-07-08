package net.kdt.pojavlaunch;

import static net.kdt.pojavlaunch.Architecture.archAsString;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import net.kdt.pojavlaunch.multirt.MultiRTUtils;
import net.kdt.pojavlaunch.multirt.Runtime;

import java.io.IOException;

public class JRE17Util {
    public static final String NEW_JRE_NAME = "Internal-17";
    public static boolean checkInternalNewJre(Context context, MultiRTUtils.RuntimeProgressReporter reporter) {
        AssetManager assetManager = context.getAssets();
        String launcher_jre17_version;
        String installed_jre17_version = MultiRTUtils.__internal__readBinpackVersion(NEW_JRE_NAME);
        try {
            launcher_jre17_version = Tools.read(assetManager.open("components/jre-new/version"));
        }catch (IOException exc) {
            //we don't have a runtime included!
            return installed_jre17_version != null; //if we have one installed -> return true -> proceed (no updates but the current one should be functional)
            //if we don't -> return false -> Cannot find compatible Java runtime
        }
        if(!launcher_jre17_version.equals(installed_jre17_version))  // this implicitly checks for null, so it will unpack the runtime even if we don't have one installed
            return unpackJre17(context, assetManager, launcher_jre17_version, reporter);
        else return true;
    }
    private static boolean unpackJre17(Context context, AssetManager assetManager, String rt_version, MultiRTUtils.RuntimeProgressReporter feedback) {
        try {
            MultiRTUtils.installRuntimeNamedBinpack(context.getApplicationInfo().nativeLibraryDir,
                    assetManager.open("components/jre-new/universal.tar.xz"),
                    assetManager.open("components/jre-new/bin-" + archAsString(Tools.DEVICE_ARCHITECTURE) + ".tar.xz"),
                    "Internal-17", rt_version, feedback);
            MultiRTUtils.postPrepare(context,"Internal-17");
            return true;
        }catch (IOException e) {
            Log.e("JRE17Auto", "Internal JRE unpack failed", e);
            return false;
        }
    }
    public static boolean isInternalNewJRE(String s_runtime) {
        Runtime runtime = MultiRTUtils.read(s_runtime);
        if(runtime == null) return false;
        return NEW_JRE_NAME.equals(runtime.name);
    }
}