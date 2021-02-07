package net.kdt.pojavlaunch.utils;

import android.app.*;
import android.content.*;
import android.system.*;
import android.util.*;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.oracle.dalvik.*;
import java.io.*;
import java.util.*;
import net.kdt.pojavlaunch.*;
import net.kdt.pojavlaunch.prefs.*;
import org.lwjgl.glfw.*;

public class JREUtils
{
    private JREUtils() {}
    
    public static String JRE_ARCHITECTURE;
    
    public static String LD_LIBRARY_PATH;
    private static String nativeLibDir;

    public static void checkJavaArchitecture(LoggableActivity act, String jreArch) throws Exception {
        String[] argName = Tools.CURRENT_ARCHITECTURE.split("/");
        act.appendlnToLog("Architecture: " + Tools.CURRENT_ARCHITECTURE);
        if (!(jreArch.contains(argName[0]) || jreArch.contains(argName[1]))) {
            // x86 check workaround
            if (jreArch.startsWith("i") && jreArch.endsWith("86") && Tools.CURRENT_ARCHITECTURE.contains("x86") && !Tools.CURRENT_ARCHITECTURE.contains("64")) {
                return;
            }

            act.appendlnToLog("Architecture " + Tools.CURRENT_ARCHITECTURE + " is incompatible with Java Runtime " + jreArch);
            throw new RuntimeException(act.getString(R.string.mcn_check_fail_incompatiblearch, Tools.CURRENT_ARCHITECTURE, jreArch));
        }
    }
    
    public static String findInLdLibPath(String libName) {
        for (String libPath : Os.getenv("LD_LIBRARY_PATH").split(":")) {
            File f = new File(libPath, libName);
            if (f.exists() && f.isFile()) {
                return f.getAbsolutePath();
            }
        }
        return libName;
    }
    public static ArrayList<File> locateLibs(File path) {
        ArrayList<File> ret = new ArrayList<>();
        File[] list = path.listFiles();
        if(list != null) {for(File f : list) {
            if(f.isFile() && f.getName().endsWith(".so")) {
                ret.add(f);
            }else if(f.isDirectory()) {
                ret.addAll(locateLibs(f));
            }
        }}
        return ret;
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
        dlopen(findInLdLibPath("libfreetype.so"));
        dlopen(findInLdLibPath("libfontmanager.so"));
        for(File f : locateLibs(new File(Tools.DIR_HOME_JRE + "/" + Tools.DIRNAME_HOME_JRE))) {
            dlopen(f.getAbsolutePath());
        }
        dlopen(nativeLibDir + "/libopenal.so");
        
        if (LauncherPreferences.PREF_CUSTOM_OPENGL_LIBNAME.equals("libgl04es.so")) {
            LauncherPreferences.PREF_CUSTOM_OPENGL_LIBNAME = nativeLibDir + "/libgl04es.so";
        }
        if (!dlopen(LauncherPreferences.PREF_CUSTOM_OPENGL_LIBNAME) && !dlopen(findInLdLibPath(LauncherPreferences.PREF_CUSTOM_OPENGL_LIBNAME))) {
            System.err.println("Failed to load custom OpenGL library " + LauncherPreferences.PREF_CUSTOM_OPENGL_LIBNAME + ". Fallbacking to GL4ES.");
            dlopen(nativeLibDir + "/libgl04es.so");
        }
    }

    public static Map<String, String> readJREReleaseProperties() throws IOException {
        Map<String, String> jreReleaseMap = new ArrayMap<>();
        BufferedReader jreReleaseReader = new BufferedReader(new FileReader(Tools.DIR_HOME_JRE + "/release"));
        String currLine;
        while ((currLine = jreReleaseReader.readLine()) != null) {
            if (!currLine.isEmpty() || currLine.contains("=")) {
                String[] keyValue = currLine.split("=");
                jreReleaseMap.put(keyValue[0], keyValue[1].replace("\"", ""));
            }
        }
        jreReleaseReader.close();
        return jreReleaseMap;
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
                        logcatPb = new ProcessBuilder().command("logcat", /* "-G", "1mb", */ "-v", "brief", "-s", "jrelog:I").redirectErrorStream(true);
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
                            if (accessToken != null && accessToken.length() > 5 && currStr.contains(accessToken)) {
                                checkAccessTokenLeak = false;
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
                    Log.e("jrelog-logcat", "Exception on logging thread", e);
                    act.appendlnToLog("Exception on logging thread:\n" + Log.getStackTraceString(e));
                }
            }
        });
        t.start();
        Log.i("jrelog-logcat","Logcat thread started");
    }
    
    public static void relocateLibPath(Context ctx) throws Exception {
        if (JRE_ARCHITECTURE == null) {
            Map<String, String> jreReleaseList = JREUtils.readJREReleaseProperties();
            JRE_ARCHITECTURE = jreReleaseList.get("OS_ARCH");
            if (JRE_ARCHITECTURE.startsWith("i") && JRE_ARCHITECTURE.endsWith("86") && Tools.CURRENT_ARCHITECTURE.contains("x86") && !Tools.CURRENT_ARCHITECTURE.contains("64")) {
                JRE_ARCHITECTURE = "i386/i486/i586";
            }
        }
        
        nativeLibDir = ctx.getApplicationInfo().nativeLibraryDir;

        for (String arch : JRE_ARCHITECTURE.split("/")) {
            File f = new File(Tools.DIR_HOME_JRE, "lib/" + arch);
            if (f.exists() && f.isDirectory()) {
                Tools.DIRNAME_HOME_JRE = "lib/" + arch;
            }
        }
        
        String libName = Tools.CURRENT_ARCHITECTURE.contains("64") ? "lib64" : "lib";
        StringBuilder ldLibraryPath = new StringBuilder();
        File serverFile = new File(Tools.DIR_HOME_JRE + "/" + Tools.DIRNAME_HOME_JRE + "/server/libjvm.so");
        // To make libjli.so ignore re-execute
        ldLibraryPath.append(
            Tools.DIR_HOME_JRE + "/" + Tools.DIRNAME_HOME_JRE + "/" + (serverFile.exists() ? "server" : "client") + ":");
        ldLibraryPath.append(
            Tools.DIR_HOME_JRE + "/" +  Tools.DIRNAME_HOME_JRE + "/jli:" +
            Tools.DIR_HOME_JRE + "/" + Tools.DIRNAME_HOME_JRE + ":"
        );
        ldLibraryPath.append(
            "/system/" + libName + ":" +
            "/vendor/" + libName + ":" +
            "/vendor/" + libName + "/hw:" +

            nativeLibDir
        );
        
        LD_LIBRARY_PATH = ldLibraryPath.toString();
    }
    
    public static void setJavaEnvironment(LoggableActivity ctx, @Nullable ShellProcessOperation shell) throws Throwable {
        Map<String, String> envMap = new ArrayMap<>();
        envMap.put("JAVA_HOME", Tools.DIR_HOME_JRE);
        envMap.put("HOME", Tools.DIR_GAME_NEW);
        envMap.put("TMPDIR", ctx.getCacheDir().getAbsolutePath());
        envMap.put("LIBGL_MIPMAP", "3");
        
        // Fix white color on banner and sheep, since GL4ES 1.1.5
        envMap.put("LIBGL_NORMALIZE", "1");
   
        envMap.put("MESA_GLSL_CACHE_DIR", ctx.getCacheDir().getAbsolutePath());
        envMap.put("LD_LIBRARY_PATH", LD_LIBRARY_PATH);
        envMap.put("PATH", Tools.DIR_HOME_JRE + "/bin:" + Os.getenv("PATH"));
        
        envMap.put("REGAL_GL_VENDOR", "Android");
        envMap.put("REGAL_GL_RENDERER", "Regal");
        envMap.put("REGAL_GL_VERSION", "4.5");

        envMap.put("AWTSTUB_WIDTH", Integer.toString(CallbackBridge.windowWidth > 0 ? CallbackBridge.windowWidth : CallbackBridge.physicalWidth));
        envMap.put("AWTSTUB_HEIGHT", Integer.toString(CallbackBridge.windowHeight > 0 ? CallbackBridge.windowHeight : CallbackBridge.physicalHeight));
        
        File customEnvFile = new File(Tools.DIR_GAME_HOME, "custom_env.txt");
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
            try {
                if (shell == null) {
                    Os.setenv(env.getKey(), env.getValue(), true);
                } else {
                    shell.writeToProcess("export " + env.getKey() + "=" + env.getValue());
                }
            } catch (Throwable th) {
                ctx.appendlnToLog(Log.getStackTraceString(th));
            }
        }
        
        if (shell == null) {
            setLdLibraryPath(LD_LIBRARY_PATH);
        }
        
        // return ldLibraryPath;
    }
    
    public static int launchJavaVM(final LoggableActivity ctx, final List<String> args) throws Throwable {
        JREUtils.relocateLibPath(ctx);
        // ctx.appendlnToLog("LD_LIBRARY_PATH = " + JREUtils.LD_LIBRARY_PATH);

        List<String> javaArgList = new ArrayList<String>();
        javaArgList.add(Tools.DIR_HOME_JRE + "/bin/java");
        Tools.getJavaArgs(ctx, javaArgList);
        if(LauncherPreferences.DEFAULT_PREF.getBoolean("autoRam",true)) {
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            ((ActivityManager)ctx.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryInfo(mi);
            purgeArg(javaArgList,"-Xms");
            purgeArg(javaArgList,"-Xmx");
            if(Tools.CURRENT_ARCHITECTURE.contains("32") && ((mi.availMem / 1048576L)-50) > 750) {
                javaArgList.add("-Xms750M");
                javaArgList.add("-Xmx750M");
            }else {
                javaArgList.add("-Xms" + ((mi.availMem / 1048576L) - 50) + "M");
                javaArgList.add("-Xmx" + ((mi.availMem / 1048576L) - 50) + "M");
            }
            ctx.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(ctx, ctx.getString(R.string.autoram_info_msg,((mi.availMem / 1048576L)-50)), Toast.LENGTH_SHORT).show();
                }
            });
            System.out.println(javaArgList);
        }
        javaArgList.addAll(args);
        
        // For debugging only!
/*
        StringBuilder sbJavaArgs = new StringBuilder();
        for (String s : javaArgList) {
            sbJavaArgs.append(s + " ");
        }
        ctx.appendlnToLog("Executing JVM: \"" + sbJavaArgs.toString() + "\"");
*/

        JREUtils.setJavaEnvironment(ctx, null);
        JREUtils.initJavaRuntime();
        JREUtils.chdir(Tools.DIR_GAME_NEW);

        final int exitCode = VMLauncher.launchJVM(javaArgList.toArray(new String[0]));
        ctx.appendlnToLog("Java Exit code: " + exitCode);
        if (exitCode != 0) {
            ctx.runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(ctx);
                        dialog.setMessage(ctx.getString(R.string.mcn_exit_title, exitCode));
                        dialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){

                                @Override
                                public void onClick(DialogInterface p1, int p2){
                                    BaseMainActivity.fullyExit();
                                }
                            });
                        dialog.show();
                    }
                });
        }
        return exitCode;
    }
    private static void purgeArg(List<String> argList, String argStart) {
        for(int i = 0; i < argList.size(); i++) {
            final String arg = argList.get(i);
            if(arg.startsWith(argStart)) {
                argList.remove(i);
            }
        }
    }
    public static native int chdir(String path);
    public static native boolean dlopen(String libPath);
    public static native void redirectLogcat();
    public static native void setLdLibraryPath(String ldLibraryPath);
    public static native void setupBridgeWindow(Object surface);
    
    // Obtain AWT screen pixels to render on Android SurfaceView
    public static native int[] renderAWTScreenFrame(/* Object canvas, int width, int height */);

    static {
        System.loadLibrary("pojavexec");
        System.loadLibrary("pojavexec_awt");
    }
}
