package net.kdt.pojavlaunch;

import android.content.*;
import android.system.*;
import android.util.*;
import java.io.*;
import net.kdt.pojavlaunch.prefs.*;

public class JREUtils
{
	private JREUtils() {}
    
    private static String nativeLibDir;
	
	public static void initJavaRuntime() {
		dlopen(Tools.homeJreDir + "/lib/jli/libjli.so");
		dlopen(Tools.homeJreDir + "/lib/server/libjvm.so");
		dlopen(Tools.homeJreDir + "/lib/libverify.so");
		dlopen(Tools.homeJreDir + "/lib/libjava.so");
		dlopen(Tools.homeJreDir + "/lib/libnet.so");
		dlopen(Tools.homeJreDir + "/lib/libnio.so");
		dlopen(Tools.homeJreDir + "/lib/libawt.so");
		dlopen(Tools.homeJreDir + "/lib/libawt_headless.so");
		
        dlopen(nativeLibDir + "/libopenal.so");
        
        if (LauncherPreferences.PREF_CUSTOM_OPENGL_LIBNAME.equals("libgl04es.so")) {
            LauncherPreferences.PREF_CUSTOM_OPENGL_LIBNAME = nativeLibDir + "/libgl04es.so";
        }
        if (!dlopen(LauncherPreferences.PREF_CUSTOM_OPENGL_LIBNAME)) {
            System.err.println("Failed to load custom OpenGL library " + LauncherPreferences.PREF_CUSTOM_OPENGL_LIBNAME + ". Fallbacking to GL4ES.");
            dlopen(nativeLibDir + "/libgl04es.so");
        }
	}
	
    public static void redirectAndPrintJRELog(LoggableActivity act) {
        JREUtils.redirectLogcat();
        Log.v("jrelog","Log starts here");
        Thread t = new Thread(() -> {
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
            */
                
                byte[] buf = new byte[512];
                int len;
                while ((len = p.getInputStream().read(buf)) != -1) {
                    act.appendToLog(new String(buf, 0, len));
                }
            } catch (IOException e) {
                Log.e("jrelog-logcat", "IOException on logging thread");
                e.printStackTrace();

                act.appendlnToLog("IOException on logging thread:\n" + Log.getStackTraceString(e));
            }
        });
        t.start();
		Log.i("jrelog-logcat","Logcat thread started");
    }
    
	public static void setJavaEnvironment(Context ctx, int launchType) throws Throwable {
        nativeLibDir = ctx.getApplicationInfo().nativeLibraryDir;
		String libName = System.getProperty("os.arch").contains("64") ? "lib64" : "lib";
		String ldLibraryPath = (
			// To make libjli.so ignore re-execute
			Tools.homeJreDir + "/lib/server:" +

			"/system/" + libName + ":" +
			"/vendor/" + libName + ":" +
			"/vendor/" + libName + "/hw:" +

			nativeLibDir + ":" +

			Tools.homeJreDir + "/lib/jli:" +
			Tools.homeJreDir + "/lib"
		);
		
		setEnvironment(launchType, "JAVA_HOME", Tools.homeJreDir);
		setEnvironment(launchType, "HOME", Tools.MAIN_PATH);
		setEnvironment(launchType, "TMPDIR", ctx.getCacheDir().getAbsolutePath());
		// setEnvironment(launchType, "LIBGL_MIPMAP", "3");
		setEnvironment(launchType, "MESA_GLSL_CACHE_DIR", ctx.getCacheDir().getAbsolutePath());
		setEnvironment(launchType, "LD_LIBRARY_PATH", ldLibraryPath);
		setEnvironment(launchType, "PATH", Tools.homeJreDir + "/bin:" + libcore.io.Libcore.os.getenv("PATH"));
        
        setEnvironment(launchType, "REGAL_GL_VENDOR", "Android");
        setEnvironment(launchType, "REGAL_GL_RENDERER", "Regal");
        setEnvironment(launchType, "REGAL_GL_VERSION", "4.5");
        // REGAL_GL_EXTENSIONS
        
		setLdLibraryPath(ldLibraryPath);
		
		// return ldLibraryPath;
	}
	
	private static void setEnvironment(int launchType, String name, String value) throws Throwable {
		if (launchType == Tools.LTYPE_PROCESS) {
			Tools.mLaunchShell.writeToProcess("export " + name + "=" + value);
		} else {
            // Libcore one support all Android versions
            libcore.io.Libcore.os.setenv(name, value, true);
            // Class.forName("libcore.io.Os").getMethod("setenv", String.class, String.class, boolean.class).invoke(null, name, value, true);
/*
            if (Build.VERSION.SDK_INT < 21) {
                Class.forName("libcore.io.Os").getMethod("setenv").invoke(null, name, value, true);
            } else {
                Class.forName("android.system.Os").getMethod("setenv").invoke(null, name, value, true);
            }
*/
		}
	}
	
	public static native int chdir(String path);
	public static native boolean dlopen(String libPath);
    public static native void redirectLogcat();
	public static native void setLdLibraryPath(String ldLibraryPath);
	public static native void setupBridgeWindow(Object surface);
	
	public static native void setupBridgeSurfaceAWT(long surface);
	
	// BEFORE Load and execute PIE binary using dlopen and dlsym("main")
	// AFTER: Execute a binary in forked process
	public static native int executeBinary(String[] args);

	static {
		System.loadLibrary("pojavexec");
	}
}
