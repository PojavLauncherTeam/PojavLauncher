package net.kdt.pojavlaunch.utils;

import android.app.*;
import android.content.*;
import android.opengl.EGL14;
import android.opengl.EGLExt;
import android.opengl.GLES10;
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

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;

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
        if(!dlopen("libjvm.so")){
            Log.w("DynamicLoader","Failed to load with no path, trying with full path");
            dlopen(jvmLibraryPath+"/libjvm.so");
        }
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
    public static String jvmLibraryPath;
    public static void redirectAndPrintJRELog(final LoggableActivity act) {
        Log.v("jrelog","Log starts here");
        JREUtils.logToActivity(act);
        Thread t = new Thread(new Runnable(){
            int failTime = 0;
            ProcessBuilder logcatPb;
            @Override
            public void run() {
                try {
                    if (logcatPb == null) {
                        logcatPb = new ProcessBuilder().command("logcat", /* "-G", "1mb", */ "-v", "brief", "-s", "jrelog:I", "LIBGL:I").redirectErrorStream(true);
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
    
    public static void relocateLibPath(final Context ctx) throws IOException {
        if (JRE_ARCHITECTURE == null) {
            JRE_ARCHITECTURE = readJREReleaseProperties().get("OS_ARCH");
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
    
    public static void setJavaEnvironment(LoggableActivity ctx) throws Throwable {
        Map<String, String> envMap = new ArrayMap<>();
        envMap.put("JAVA_HOME", Tools.DIR_HOME_JRE);
        envMap.put("HOME", Tools.DIR_GAME_NEW);
        envMap.put("TMPDIR", ctx.getCacheDir().getAbsolutePath());
        envMap.put("LIBGL_MIPMAP", "3");
        
        // Fix white color on banner and sheep, since GL4ES 1.1.5
        envMap.put("LIBGL_NORMALIZE", "1");
   
        envMap.put("MESA_GLSL_CACHE_DIR", ctx.getCacheDir().getAbsolutePath());
        envMap.put("MESA_GL_VERSION_OVERRIDE", "4.6");
        envMap.put("MESA_GLSL_VERSION_OVERRIDE", "460");
        envMap.put("force_glsl_extensions_warn", "true");
        envMap.put("allow_higher_compat_version", "true");
        envMap.put("allow_glsl_extension_directive_midshader", "true");
        envMap.put("MESA_LOADER_DRIVER_OVERRIDE", "zink");

        envMap.put("LD_LIBRARY_PATH", LD_LIBRARY_PATH);
        envMap.put("PATH", Tools.DIR_HOME_JRE + "/bin:" + Os.getenv("PATH"));
        
        envMap.put("REGAL_GL_VENDOR", "Android");
        envMap.put("REGAL_GL_RENDERER", "Regal");
        envMap.put("REGAL_GL_VERSION", "4.5");
        if(Tools.LOCAL_RENDERER != null) {
            envMap.put("POJAV_RENDERER", Tools.LOCAL_RENDERER);
        }
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
        if(!envMap.containsKey("LIBGL_ES") && Tools.LOCAL_RENDERER != null) {
            int glesMajor = getDetectedVersion();
            Log.i("glesDetect","GLES version detected: "+glesMajor);

            if (glesMajor < 3) {
                //fallback to 2 since it's the minimum for the entire app
                envMap.put("LIBGL_ES","2");
            } else if (Tools.LOCAL_RENDERER.startsWith("opengles")) {
                envMap.put("LIBGL_ES", Tools.LOCAL_RENDERER.replace("opengles", "").replace("_5", ""));
            } else {
                // TODO if can: other backends such as Vulkan.
                // Sure, they should provide GLES 3 support.
                envMap.put("LIBGL_ES", "3");
            }
        }
        for (Map.Entry<String, String> env : envMap.entrySet()) {
            ctx.appendlnToLog("Added custom env: " + env.getKey() + "=" + env.getValue());
            Os.setenv(env.getKey(), env.getValue(), true);
        }

        File serverFile = new File(Tools.DIR_HOME_JRE + "/" + Tools.DIRNAME_HOME_JRE + "/server/libjvm.so");
        jvmLibraryPath = Tools.DIR_HOME_JRE + "/" + Tools.DIRNAME_HOME_JRE + "/" + (serverFile.exists() ? "server" : "client");
        Log.d("DynamicLoader","Base LD_LIBRARY_PATH: "+LD_LIBRARY_PATH);
        Log.d("DynamicLoader","Internal LD_LIBRARY_PATH: "+jvmLibraryPath+":"+LD_LIBRARY_PATH);
        setLdLibraryPath(jvmLibraryPath+":"+LD_LIBRARY_PATH);

        // return ldLibraryPath;
    }
    
    public static int launchJavaVM(final LoggableActivity ctx, final List<String> args) throws Throwable {
        JREUtils.relocateLibPath(ctx);
        // ctx.appendlnToLog("LD_LIBRARY_PATH = " + JREUtils.LD_LIBRARY_PATH);
        final String graphicsLib;
        if(Tools.LOCAL_RENDERER != null){
            switch (Tools.LOCAL_RENDERER) {
                case "opengles2":
                    graphicsLib = "libgl4es_114.so";
                    break;
                case "opengles2_5":
                case "opengles3":
                    graphicsLib = "libgl4es_115.so";
                    break;
                case "vulkan_zink":
                    graphicsLib = "libOSMesa_8.so";
                    break;
                case "opengles3_vgpu_1.2_beta":
                    graphicsLib = "libvgpu_12.so";
                    break;
                default:
                    throw new RuntimeException("Undefined renderer: " + Tools.LOCAL_RENDERER);
            }
            if (!dlopen(graphicsLib) && !dlopen(findInLdLibPath(graphicsLib))) {
                System.err.println("Failed to load renderer " + graphicsLib + ". Falling back to GL4ES 1.1.4");
                Tools.LOCAL_RENDERER = "opengles2";
                dlopen(nativeLibDir + "/libgl4es_114.so");
            }
        }else{
            graphicsLib = null;
        }

        List<String> javaArgList = new ArrayList<String>();
        javaArgList.add(Tools.DIR_HOME_JRE + "/bin/java");
        Tools.getJavaArgs(ctx, javaArgList,graphicsLib);
            purgeArg(javaArgList,"-Xms");
            purgeArg(javaArgList,"-Xmx");
            /*if(Tools.CURRENT_ARCHITECTURE.contains("32") && ((mi.availMem / 1048576L)-50) > 300) {
                javaArgList.add("-Xms300M");
                javaArgList.add("-Xmx300M");
            }else {*/
                javaArgList.add("-Xms" + LauncherPreferences.PREF_RAM_ALLOCATION + "M");
                javaArgList.add("-Xmx" + LauncherPreferences.PREF_RAM_ALLOCATION + "M");
            //}
            ctx.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(ctx, ctx.getString(R.string.autoram_info_msg,LauncherPreferences.PREF_RAM_ALLOCATION), Toast.LENGTH_SHORT).show();
                }
            });
            System.out.println(javaArgList);
        javaArgList.addAll(args);
        
        // For debugging only!
/*
        StringBuilder sbJavaArgs = new StringBuilder();
        for (String s : javaArgList) {
            sbJavaArgs.append(s + " ");
        }
        ctx.appendlnToLog("Executing JVM: \"" + sbJavaArgs.toString() + "\"");
*/

        setJavaEnvironment(ctx);
        initJavaRuntime();
        setupExitTrap(ctx.getApplication());
        chdir(Tools.DIR_GAME_NEW);

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
    private static final int EGL_OPENGL_ES_BIT = 0x0001;
    private static final int EGL_OPENGL_ES2_BIT = 0x0004;
    private static final int EGL_OPENGL_ES3_BIT_KHR = 0x0040;
    private static boolean hasExtension(String extensions, String name) {
        int start = extensions.indexOf(name);
        while (start >= 0) {
            // check that we didn't find a prefix of a longer extension name
            int end = start + name.length();
            if (end == extensions.length() || extensions.charAt(end) == ' ') {
                return true;
            }
            start = extensions.indexOf(name, end);
        }
        return false;
    }
    private static int getDetectedVersion() {
        /*
         * Get all the device configurations and check the EGL_RENDERABLE_TYPE attribute
         * to determine the highest ES version supported by any config. The
         * EGL_KHR_create_context extension is required to check for ES3 support; if the
         * extension is not present this test will fail to detect ES3 support. This
         * effectively makes the extension mandatory for ES3-capable devices.
         */
        EGL10 egl = (EGL10) EGLContext.getEGL();
        EGLDisplay display = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        int[] numConfigs = new int[1];
        if (egl.eglInitialize(display, null)) {
            try {
                boolean checkES3 = hasExtension(egl.eglQueryString(display, EGL10.EGL_EXTENSIONS),
                        "EGL_KHR_create_context");
                if (egl.eglGetConfigs(display, null, 0, numConfigs)) {
                    EGLConfig[] configs = new EGLConfig[numConfigs[0]];
                    if (egl.eglGetConfigs(display, configs, numConfigs[0], numConfigs)) {
                        int highestEsVersion = 0;
                        int[] value = new int[1];
                        for (int i = 0; i < numConfigs[0]; i++) {
                            if (egl.eglGetConfigAttrib(display, configs[i],
                                    EGL10.EGL_RENDERABLE_TYPE, value)) {
                                if (checkES3 && ((value[0] & EGL_OPENGL_ES3_BIT_KHR) ==
                                        EGL_OPENGL_ES3_BIT_KHR)) {
                                    if (highestEsVersion < 3) highestEsVersion = 3;
                                } else if ((value[0] & EGL_OPENGL_ES2_BIT) == EGL_OPENGL_ES2_BIT) {
                                    if (highestEsVersion < 2) highestEsVersion = 2;
                                } else if ((value[0] & EGL_OPENGL_ES_BIT) == EGL_OPENGL_ES_BIT) {
                                    if (highestEsVersion < 1) highestEsVersion = 1;
                                }
                            } else {
                                Log.w("glesDetect", "Getting config attribute with "
                                        + "EGL10#eglGetConfigAttrib failed "
                                        + "(" + i + "/" + numConfigs[0] + "): "
                                        + egl.eglGetError());
                            }
                        }
                        return highestEsVersion;
                    } else {
                        Log.e("glesDetect", "Getting configs with EGL10#eglGetConfigs failed: "
                                + egl.eglGetError());
                        return -1;
                    }
                } else {
                    Log.e("glesDetect", "Getting number of configs with EGL10#eglGetConfigs failed: "
                            + egl.eglGetError());
                    return -2;
                }
            } finally {
                egl.eglTerminate(display);
            }
        } else {
            Log.e("glesDetect", "Couldn't initialize EGL.");
            return -3;
        }
    }
    public static native int chdir(String path);
    public static native void logToActivity(final LoggableActivity a);
    public static native boolean dlopen(String libPath);
    public static native void setLdLibraryPath(String ldLibraryPath);
    public static native void setupBridgeWindow(Object surface);
    public static native void setupExitTrap(Context context);
    // Obtain AWT screen pixels to render on Android SurfaceView
    public static native int[] renderAWTScreenFrame(/* Object canvas, int width, int height */);

    static {
        System.loadLibrary("pojavexec");
        System.loadLibrary("pojavexec_awt");
        dlopen("libxhook.so");
        System.loadLibrary("istdio");
    }
}
