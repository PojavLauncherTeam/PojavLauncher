package net.kdt.pojavlaunch;

import android.app.Activity;
import android.app.NativeActivity;
import android.util.Log;

import net.kdt.pojavlaunch.prefs.LauncherPreferences;
import net.kdt.pojavlaunch.utils.JREUtils;
import net.kdt.pojavlaunch.value.DependentLibrary;
import net.kdt.pojavlaunch.value.MinecraftAccount;
import net.kdt.pojavlaunch.value.MinecraftLibraryArtifact;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class QuestMainActivity extends NativeActivity implements ILoggableActivity {
    private static final String TAG = "QuestActivity";

    private static final File APP_JAR = new File(Tools.DIR_GAME_HOME, "test.jar");
    private static final File WORK_DIR = new File(Tools.DIR_GAME_HOME, "workdir");

    // Called from native code
    @SuppressWarnings("unused")
    public void setup() {
        // LauncherPreferences.PREF_CUSTOM_OPENGL_LIBNAME = "libgl4es_114_dbg.so";

        downloadGraphicsTest();

        try {
            JREUtils.relocateLibPath(this);
            JREUtils.setJavaEnvironment(this);
        } catch (Throwable throwable) {
            throw new RuntimeException("Could not setup Java environment", throwable);
        }

        Log.e(TAG, "Calling Java setup");
        JREUtils.initJavaRuntime();
    }

    private void downloadGraphicsTest() {
        try (OutputStream out = new FileOutputStream(APP_JAR)) {
            URL url = new URL("http://10.0.2.24:8001/GraphicsTest.jar");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            if (conn.getResponseCode() != 200)
                throw new IOException("Bad response code " + conn.getResponseCode());

            try (InputStream in = conn.getInputStream()) {
                IOUtils.copy(in, out);
            }
        } catch (IOException e) {
            Log.w(TAG, "Failed to download test.jar", e);
            return;
        }
    }

    /**
     * Get the list of arguments to be supplied to the JVM.
     * <p>
     * Called from native code.
     */
    @SuppressWarnings("unused")
    public String[] getJavaArgs() {
        ArrayList<String> args = new ArrayList<>();
        args.add("java");
        Tools.getJavaArgs(this, args);

        if (true) {
            String versionName = "vivecraft-1.12.2-jrbudda-11-r2";
            // versionName = "1.12.2";
            JMinecraftVersionList.Version versionInfo = Tools.getVersionInfo(null, versionName);
            String launchClassPath = Tools.generateLaunchClassPath(versionInfo, versionName);

            checkLibraries(versionInfo);

            // HACK: Add in Vanilla since vivecraft doesn't include it
            // launchClassPath += ":/sdcard/games/PojavLauncher/.minecraft/versions/1.12.2/1.12.2.jar";

            // FIXME HAAAACK move the tweaker into this repo
            launchClassPath = APP_JAR + ":" + launchClassPath;

            args.add("-cp");
            args.add(Tools.getLWJGL3ClassPath() + ":" + launchClassPath);

            args.add(versionInfo.mainClass);

            MinecraftAccount profile = MinecraftAccount.load("ZNixian");
            String[] launchArgs = Tools.getMinecraftArgs(profile, versionInfo, Tools.DIR_GAME_NEW);
            args.addAll(Arrays.asList(launchArgs));

            args.add("--server");
            args.add("10.0.2.24");

            // Add our custom tweaker to modify JNI so openvr loads
            args.add("--tweakClass");
            args.add("xyz.znix.graphicstest.QuestcraftLaunchTweaker");
        } else {
            args.add("-cp");
            args.add(Tools.getLWJGL3ClassPath() + ":" + APP_JAR);

            args.add("xyz.znix.graphicstest.Main");
        }

        return args.toArray(new String[0]);
    }

    /**
     * Get the directory Java should change to in order to avoid breaking Vivecraft trying
     * to extract some files.
     */
    @SuppressWarnings("unused")
    public String getTargetWorkingDirectory() {
        if (!WORK_DIR.exists()) {
            boolean success = WORK_DIR.mkdirs();
            if (!success) {
                Log.e(TAG, "Failed to create " + WORK_DIR + " - chdir will fail");
            }
        }
        return WORK_DIR.getAbsolutePath();
    }

    @Override
    public Activity asActivity() {
        return this;
    }

    @Override
    public void appendToLog(String text, boolean checkAllow) {
        Log.i(TAG, text);
    }

    private void checkLibraries(JMinecraftVersionList.Version version) {
        // From MinecraftDownloaderTask
        for (final DependentLibrary libItem : version.libraries) {

            if (libItem.name.startsWith("net.java.jinput") || libItem.name.startsWith("org.lwjgl")) {
                // ignore
            } else {
                String[] libInfo = libItem.name.split(":");
                String libArtifact = Tools.artifactToPath(libInfo[0], libInfo[1], libInfo[2]);
                File outLib = new File(Tools.DIR_HOME_LIBRARY + "/" + libArtifact);
                outLib.getParentFile().mkdirs();

                if (!outLib.exists()) {
                    downloadLibrary(libItem, libArtifact, outLib);
                    Log.w(TAG, "should download " + outLib);
                }

                // TODO hash check
            }
        }
    }

    protected void downloadLibrary(DependentLibrary libItem, String libArtifact, File outLib) {
        // From MinecraftDownloaderTask

        String libPathURL;

        if (libItem.downloads == null || libItem.downloads.artifact == null) {
            System.out.println("UnkLib:" + libArtifact);
            MinecraftLibraryArtifact artifact = new MinecraftLibraryArtifact();

            // HACK around Vivecraft URLs, HTTPS doesn't work for them
            String server = libItem.url == null ? "https://libraries.minecraft.net/" : libItem.url;
            if (!server.contains("vivecraft.org"))
                server = server.replace("http://", "https://");

            artifact.url = server + libArtifact;
            libItem.downloads = new DependentLibrary.LibraryDownloads(artifact);
        }

        Log.i(TAG, "Download: " + libItem.downloads.artifact.url);

        try {
            libPathURL = libItem.downloads.artifact.url;
            boolean isFileGood = false;
            byte timesChecked = 0;
            while (!isFileGood) {
                timesChecked++;
                if (timesChecked > 5)
                    throw new RuntimeException("Library download failed after 5 retries");
                Tools.downloadFileMonitored(
                        libPathURL,
                        outLib.getAbsolutePath(),
                        new Tools.DownloaderFeedback() {
                            @Override
                            public void updateProgress(int curr, int max) {
                            }
                        }
                );
                if (libItem.downloads.artifact.sha1 != null) {
                    isFileGood = !LauncherPreferences.PREF_CHECK_LIBRARY_SHA || Tools.compareSHA1(outLib, libItem.downloads.artifact.sha1);
                    if (!isFileGood) {
                        Log.e(TAG, "Bad hash for " + libItem.name);
                        outLib.delete();
                        return;
                    }
                } else {
                    isFileGood = true;
                }
            }
        } catch (Exception th) {
            Log.e(TAG, "Failed downloading libraries", th);
        }
    }
}
