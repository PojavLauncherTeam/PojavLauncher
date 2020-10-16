package net.kdt.pojavlaunch;

import android.content.*;
import android.os.*;
import android.system.*;
import android.util.*;

import java.io.*;
import java.lang.reflect.*;

import net.kdt.pojavlaunch.prefs.*;

import org.lwjgl.glfw.*;

public class JREUtils
{
    private JREUtils() {}
    
    private static String nativeLibDir;
    
    private static String findInLdLibPath(String libName) {
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
/*
        dlopen(findInLdLibPath("libjvm.so"));
        dlopen(findInLdLibPath("libverify.so"));
        dlopen(findInLdLibPath("libjava.so"));
        // dlopen(findInLdLibPath("/lib/libjsig.so"));
        dlopen(findInLdLibPath("libnet.so"));
        dlopen(findInLdLibPath("libnio.so"));
        dlopen(findInLdLibPath("libawt.so"));
        dlopen(findInLdLibPath("libawt_headless.so"));
*/
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
            @Override
            public void run() {
                try {
                    Log.i("jrelog-logcat","Clearing logcat");
                    new ProcessBuilder().command("logcat", "-c").redirectErrorStream(true).start();
                    Log.i("jrelog-logcat","Starting logcat");
                    java.lang.Process p = new ProcessBuilder().command("logcat", /* "-G", "1mb", */ "-v", "brief", "*:S").redirectErrorStream(true).start();

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
                    
                    if (p.exitValue() != 0) {
                        Log.e("jrelog-logcat", "Logcat exited with code " + p.exitValue());
                        failTime++;
                        Log.i("jrelog-logcat", (failTime <= 10 ? "Restarting logcat" : "Too many restart fails") + " (attempt " + failTime + "/10");
                        if (failTime <= 10) {
                            run();
                        }
                        return;
                    }
                } catch (IOException e) {
                    Log.e("jrelog-logcat", "IOException on logging thread");
                    e.printStackTrace();

                    act.appendlnToLog("IOException on logging thread:\n" + Log.getStackTraceString(e));
                }
            }
        });
        t.start();
        Log.i("jrelog-logcat","Logcat thread started");
    }
    
    public static void setJavaEnvironment(Context ctx, int launchType) throws Throwable {
        nativeLibDir = ctx.getApplicationInfo().nativeLibraryDir;
        String libName = System.getProperty("os.arch").contains("64") ? "lib64" : "lib";
        
        StringBuilder ldLibraryPath = new StringBuilder();
        
        for (String arch : Tools.currentArch.split("/")) {
            File f = new File(Tools.homeJreDir + "/lib/" + arch);
            if (f.exists() && f.isDirectory()) {
                ldLibraryPath.append(Tools.homeJreDir + "/lib/" + arch + "/server:");
                ldLibraryPath.append(Tools.homeJreDir + "/lib/" + arch + "/jli:");
                ldLibraryPath.append(Tools.homeJreDir + "/lib/" + arch + ":");
            }
        }
        
        ldLibraryPath.append(
            // To make libjli.so ignore re-execute
            Tools.homeJreDir + "/lib/server:" +
            Tools.homeJreDir + "/lib/jli:" +
            Tools.homeJreDir + "/lib:" +

            "/system/" + libName + ":" +
            "/vendor/" + libName + ":" +
            "/vendor/" + libName + "/hw:" +

            nativeLibDir
        );
        
        setEnvironment(launchType, "JAVA_HOME", Tools.homeJreDir);
        setEnvironment(launchType, "HOME", Tools.MAIN_PATH);
        setEnvironment(launchType, "TMPDIR", ctx.getCacheDir().getAbsolutePath());
        setEnvironment(launchType, "LIBGL_MIPMAP", "3");
        setEnvironment(launchType, "MESA_GLSL_CACHE_DIR", ctx.getCacheDir().getAbsolutePath());
        setEnvironment(launchType, "LD_LIBRARY_PATH", ldLibraryPath.toString());
        setEnvironment(launchType, "PATH", Tools.homeJreDir + "/bin:" + Os.getenv("PATH"));
        
        setEnvironment(launchType, "REGAL_GL_VENDOR", "Android");
        setEnvironment(launchType, "REGAL_GL_RENDERER", "Regal");
        setEnvironment(launchType, "REGAL_GL_VERSION", "4.5");

        setEnvironment(launchType, "AWTSTUB_WIDTH", Integer.toString(CallbackBridge.windowWidth));
        setEnvironment(launchType, "AWTSTUB_HEIGHT", Integer.toString(CallbackBridge.windowHeight));
        
        File customEnvFile = new File(Tools.MAIN_PATH, "custom_env.txt");
        if (customEnvFile.exists() && customEnvFile.isFile()) {
            BufferedReader reader = new BufferedReader(new FileReader(customEnvFile));
            String line;
            while ((line = reader.readLine()) != null) {
                // Not use split() as only split first one
                int index = line.indexOf("=");
                setEnvironment(launchType, line.substring(0, index), line.substring(index + 1));
            }
            reader.close();
        }
        
        // REGAL_GL_EXTENSIONS
        
        setLdLibraryPath(ldLibraryPath.toString());
        
        // return ldLibraryPath;
    }
    
    private static void setEnvironment(int launchType, String name, String value) throws Throwable {
        if (launchType == Tools.LTYPE_PROCESS) {
            Tools.mLaunchShell.writeToProcess("export " + name + "=" + value);
        }
        Os.setenv(name, value, true);
    }

    public static native int chdir(String path);
    public static native boolean dlopen(String libPath);
    public static native void redirectLogcat();
    public static native void setLdLibraryPath(String ldLibraryPath);
    public static native void setupBridgeWindow(Object surface);
    
    // TODO AWT Android port
    public static native void setupBridgeSurfaceAWT(long surface);
    
    // BEFORE Load and execute PIE binary using dlopen and dlsym("main")
    // AFTER: [Deprecated]
    @Deprecated
    public static native int executeBinary(String[] args);

    static {
        System.loadLibrary("pojavexec");
    }
}
