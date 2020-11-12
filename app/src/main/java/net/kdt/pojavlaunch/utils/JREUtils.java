package net.kdt.pojavlaunch.utils;

import android.content.*;
import android.support.annotation.*;
import android.system.*;
import android.util.*;
import java.io.*;
import java.util.*;
import net.kdt.pojavlaunch.*;
import net.kdt.pojavlaunch.prefs.*;
import org.lwjgl.glfw.*;

public class JREUtils
{
    private JREUtils() {}
    
    public static String LD_LIBRARY_PATH;
    private static String nativeLibDir;
    
    public static String findInLdLibPath(String libName) {
        for (String libPath : Os.getenv("LD_LIBRARY_PATH").split(":")) {
            File f = new File(libPath, libName);
            if (f.exists() && f.isFile()) {
                return f.getAbsolutePath();
            }
        }
        return libName;
    }
    
    public static void initJavaRuntime() {
        dlopen(findInLdLibPath("libjli.so"));
        dlopen(findInLdLibPath("libjvm.so"));
        dlopen(findInLdLibPath("libverify.so"));
        dlopen(findInLdLibPath("libjava.so"));
        // dlopen(findInLdLibPath("libjsig.so"));
        dlopen(findInLdLibPath("libnet.so"));
        dlopen(findInLdLibPath("libnio.so"));
        dlopen(findInLdLibPath("libawt.so"));
        dlopen(findInLdLibPath("libawt_headless.so"));

        dlopen(nativeLibDir + "/libopenal.so");
        
        if (LauncherPreferences.PREF_CUSTOM_OPENGL_LIBNAME.equals("libgl04es.so")) {
            LauncherPreferences.PREF_CUSTOM_OPENGL_LIBNAME = nativeLibDir + "/libgl04es.so";
        }
        if (!dlopen(LauncherPreferences.PREF_CUSTOM_OPENGL_LIBNAME)) {
            System.err.println("Failed to load custom OpenGL library " + LauncherPreferences.PREF_CUSTOM_OPENGL_LIBNAME + ". Fallbacking to GL4ES.");
            dlopen(nativeLibDir + "/libgl04es.so");
        }
    }
    
    private static boolean checkAccessTokenLeak = true;
    public static void redirectAndPrintJRELog(final LoggableActivity act, final String accessToken) {
        JREUtils.redirectLogcat();
        Log.v("jrelog","Log starts here");
        Thread t = new Thread(new Runnable(){
            int failTime = 0;
            ProcessBuilder logcatPb;
            @Override
            public void run() {
                try {
                    if (logcatPb == null) {
                        logcatPb = new ProcessBuilder().command("logcat", /* "-G", "1mb", */ "-v", "brief", "*:S").redirectErrorStream(true);
                    }
                    
                    Log.i("jrelog-logcat","Clearing logcat");
                    new ProcessBuilder().command("logcat", "-c").redirectErrorStream(true).start();
                    Log.i("jrelog-logcat","Starting logcat");
                    java.lang.Process p = logcatPb.start();

                    // idk which better, both have a bug that printf(\n) in a single line
                    /*
                     BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                     String line;
                     while ((line = reader.readLine()) != null) {
                     act.appendlnToLog(line);
                     }
                     reader.close();
                     */

                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = p.getInputStream().read(buf)) != -1) {
                        String currStr = new String(buf, 0, len);
                        
                        // Avoid leaking access token to log by replace it.
                        // Also, Minecraft will just print it once.
                        if (checkAccessTokenLeak) {
                            checkAccessTokenLeak = false;
                            if (accessToken != null && accessToken.length() > 5 && currStr.contains(accessToken)) {
                                currStr = currStr.replace(accessToken, "ACCESS_TOKEN_HIDDEN");
                            }
                        }
                        
                        act.appendToLog(currStr);
                    }
                    
                    if (p.waitFor() != 0) {
                        Log.e("jrelog-logcat", "Logcat exited with code " + p.exitValue());
                        failTime++;
                        Log.i("jrelog-logcat", (failTime <= 10 ? "Restarting logcat" : "Too many restart fails") + " (attempt " + failTime + "/10");
                        if (failTime <= 10) {
                            run();
                        } else {
                            act.appendlnToLog("ERROR: Unable to get more log.");
                        }
                        return;
                    }
                } catch (Throwable e) {
                    Log.e("jrelog-logcat", "Exception on logging thread");
                    e.printStackTrace();

                    act.appendlnToLog("Exception on logging thread:\n" + Log.getStackTraceString(e));
                }
            }
        });
        t.start();
        Log.i("jrelog-logcat","Logcat thread started");
    }
    
    public static void relocateLibPath(Context ctx) {
        nativeLibDir = ctx.getApplicationInfo().nativeLibraryDir;

        for (String arch : Tools.currentArch.split("/")) {
            File f = new File(Tools.homeJreDir, "lib/" + arch);
            if (f.exists() && f.isDirectory()) {
                Tools.homeJreLib = "lib/" + arch;
                break;
            }
        }
        
        String libName = Tools.currentArch.contains("64") ? "lib64" : "lib";
        StringBuilder ldLibraryPath = new StringBuilder();
        ldLibraryPath.append(
            // To make libjli.so ignore re-execute
            Tools.homeJreDir + "/" + Tools.homeJreLib + "/server:" +
            Tools.homeJreDir + "/" +  Tools.homeJreLib + "/jli:" +
            Tools.homeJreDir + "/" + Tools.homeJreLib + ":"
        );
        ldLibraryPath.append(
            "/system/" + libName + ":" +
            "/vendor/" + libName + ":" +
            "/vendor/" + libName + "/hw:" +

            nativeLibDir
        );
        
        LD_LIBRARY_PATH = ldLibraryPath.toString();
    }
    
    public static void setJavaEnvironment(Context ctx, @Nullable ShellProcessOperation shell) throws Throwable {
        Map<String, String> envMap = new ArrayMap<>();
        envMap.put("JAVA_HOME", Tools.homeJreDir);
        envMap.put("HOME", Tools.MAIN_PATH);
        envMap.put("TMPDIR", ctx.getCacheDir().getAbsolutePath());
        envMap.put("LIBGL_MIPMAP", "3");
        envMap.put("MESA_GLSL_CACHE_DIR", ctx.getCacheDir().getAbsolutePath());
        envMap.put("LD_LIBRARY_PATH", LD_LIBRARY_PATH);
        envMap.put("PATH", Tools.homeJreDir + "/bin:" + Os.getenv("PATH"));
        
        envMap.put("REGAL_GL_VENDOR", "Android");
        envMap.put("REGAL_GL_RENDERER", "Regal");
        envMap.put("REGAL_GL_VERSION", "4.5");

        envMap.put("AWTSTUB_WIDTH", Integer.toString(CallbackBridge.windowWidth));
        envMap.put("AWTSTUB_HEIGHT", Integer.toString(CallbackBridge.windowHeight));
        
        File customEnvFile = new File(Tools.MAIN_PATH, "custom_env.txt");
        if (customEnvFile.exists() && customEnvFile.isFile()) {
            BufferedReader reader = new BufferedReader(new FileReader(customEnvFile));
            String line;
            while ((line = reader.readLine()) != null) {
                // Not use split() as only split first one
                int index = line.indexOf("=");
                envMap.put(line.substring(0, index), line.substring(index + 1));
            }
            reader.close();
        }
        
        for (Map.Entry<String, String> env : envMap.entrySet()) {
            if (shell == null) {
                Os.setenv(env.getKey(), env.getValue(), true);
            } else {
                shell.writeToProcess("export " + env.getKey() + "=" + env.getValue());
            }
        }
        
        if (shell == null) {
            setLdLibraryPath(LD_LIBRARY_PATH);
        }
        
        // return ldLibraryPath;
    }

    public static native int chdir(String path);
    public static native boolean dlopen(String libPath);
    public static native void redirectLogcat();
    public static native void setLdLibraryPath(String ldLibraryPath);
    public static native void setupBridgeWindow(Object surface);
    
    // TODO AWT Android port
    public static native int[] renderAWTScreenFrame(/* Object canvas, int width, int height */);
    
    // BEFORE Load and execute PIE binary using dlopen and dlsym("main")
    // AFTER: [Deprecated]
    @Deprecated
    public static native int executeBinary(String[] args);

    static {
        System.loadLibrary("pojavexec");
    }
}
