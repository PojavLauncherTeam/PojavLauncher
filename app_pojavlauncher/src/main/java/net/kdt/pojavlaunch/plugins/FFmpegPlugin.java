package net.kdt.pojavlaunch.plugins;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.io.File;

public class FFmpegPlugin {
    public static boolean isAvailable = false;
    public static String libraryPath;
    public static String executablePath;
    public static void discover(Context context) {
        PackageManager manager = context.getPackageManager();
        try {
            PackageInfo ffmpegPluginInfo = manager.getPackageInfo("net.kdt.pojavlaunch.ffmpeg", PackageManager.GET_SHARED_LIBRARY_FILES);
            libraryPath = ffmpegPluginInfo.applicationInfo.nativeLibraryDir;
            File ffmpegExecutable = new File(libraryPath, "libffmpeg.so");
            executablePath = ffmpegExecutable.getAbsolutePath();
            // Older plugin versions still have the old executable location
            isAvailable = ffmpegExecutable.exists();
        }catch (Exception e) {
            Log.i("FFmpegPlugin", "Failed to discover plugin", e);
        }
    }
}
