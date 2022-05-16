package net.kdt.pojavlaunch.utils;

import static net.kdt.pojavlaunch.Architecture.ARCH_X86;
import static net.kdt.pojavlaunch.Architecture.is64BitsDevice;
import static net.kdt.pojavlaunch.Tools.LOCAL_RENDERER;
import static net.kdt.pojavlaunch.Tools.currentDisplayMetrics;

import android.app.*;
import android.content.*;
import android.os.Build;
import android.system.*;
import android.util.*;
import android.widget.Toast;

import com.oracle.dalvik.*;
import java.io.*;
import java.util.*;
import net.kdt.pojavlaunch.*;
import net.kdt.pojavlaunch.extra.ExtraConstants;
import net.kdt.pojavlaunch.extra.ExtraCore;
import net.kdt.pojavlaunch.prefs.*;
import org.lwjgl.glfw.*;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;

public class JREUtils {
    private JREUtils() {}

    public static String LD_LIBRARY_PATH;
    public static Map<String, String> jreReleaseList;
    public static String jvmLibraryPath;
    private static String sNativeLibDir;

    public static String findInLdLibPath(String libName) {
        if(Os.getenv("LD_LIBRARY_PATH")==null) {
            try {
                if (LD_LIBRARY_PATH != null) {
                    Os.setenv("LD_LIBRARY_PATH", LD_LIBRARY_PATH, true);
                }
            }catch (ErrnoException e) {
                e.printStackTrace();
            }
            return libName;
        }
        for (String libPath : Os.getenv("LD_LIBRARY_PATH").split(":")) {
            File f = new File(libPath, libName);
            if (f.exists() && f.isFile()) {
                return f.getAbsolutePath();
            }
        }
        return libName;
    }

    public static ArrayList<File> locateLibs(File path) {
        ArrayList<File> returnValue = new ArrayList<>();
        File[] list = path.listFiles();
        if(list != null) {
            for(File f : list) {
                if(f.isFile() && f.getName().endsWith(".so")) {
                    returnValue.add(f);
                }else if(f.isDirectory()) {
                    returnValue.addAll(locateLibs(f));
                }
            }
        }
        return returnValue;
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
        dlopen(sNativeLibDir + "/libopenal.so");
    }

    public static Map<String, String> readJREReleaseProperties() throws IOException {
        return readJREReleaseProperties(Tools.DIR_HOME_JRE);
    }

    public static Map<String, String> readJREReleaseProperties(String name) throws IOException {
        Map<String, String> jreReleaseMap = new ArrayMap<>();
        if (!name.contains("/")) {
            name = Tools.MULTIRT_HOME + "/" + name;
        }
        BufferedReader jreReleaseReader = new BufferedReader(new FileReader(name + "/release"));
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

    public static void redirectAndPrintJRELog() {
        Log.v("jrelog","Log starts here");
        JREUtils.logToLogger(Logger.getInstance());
        new Thread(new Runnable(){
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

                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = p.getInputStream().read(buf)) != -1) {
                        String currStr = new String(buf, 0, len);
                        Logger.getInstance().appendToLog(currStr);
                    }
                    
                    if (p.waitFor() != 0) {
                        Log.e("jrelog-logcat", "Logcat exited with code " + p.exitValue());
                        failTime++;
                        Log.i("jrelog-logcat", (failTime <= 10 ? "Restarting logcat" : "Too many restart fails") + " (attempt " + failTime + "/10");
                        if (failTime <= 10) {
                            run();
                        } else {
                            Logger.getInstance().appendToLog("ERROR: Unable to get more log.");
                        }
                        return;
                    }
                } catch (Throwable e) {
                    Log.e("jrelog-logcat", "Exception on logging thread", e);
                    Logger.getInstance().appendToLog("Exception on logging thread:\n" + Log.getStackTraceString(e));
                }
            }
        }).start();
        Log.i("jrelog-logcat","Logcat thread started");
    }
    
    public static void relocateLibPath(final Context ctx) throws IOException {
        String JRE_ARCHITECTURE = readJREReleaseProperties().get("OS_ARCH");
        if (Architecture.archAsInt(JRE_ARCHITECTURE) == ARCH_X86){
            JRE_ARCHITECTURE = "i386/i486/i586";
        }
        
        sNativeLibDir = ctx.getApplicationInfo().nativeLibraryDir;

        for (String arch : JRE_ARCHITECTURE.split("/")) {
            File f = new File(Tools.DIR_HOME_JRE, "lib/" + arch);
            if (f.exists() && f.isDirectory()) {
                Tools.DIRNAME_HOME_JRE = "lib/" + arch;
            }
        }
        
        String libName = is64BitsDevice() ? "lib64" : "lib";
        StringBuilder ldLibraryPath = new StringBuilder();
        ldLibraryPath.append(
            Tools.DIR_HOME_JRE + "/" +  Tools.DIRNAME_HOME_JRE + "/jli:" +
            Tools.DIR_HOME_JRE + "/" + Tools.DIRNAME_HOME_JRE + ":"
        );
        ldLibraryPath.append(
            "/system/" + libName + ":" +
            "/vendor/" + libName + ":" +
            "/vendor/" + libName + "/hw:" +
                    sNativeLibDir
        );
        LD_LIBRARY_PATH = ldLibraryPath.toString();
    }
    
    public static void setJavaEnvironment(Activity activity) throws Throwable {
        Map<String, String> envMap = new ArrayMap<>();
        envMap.put("POJAV_NATIVEDIR", activity.getApplicationInfo().nativeLibraryDir);
        envMap.put("JAVA_HOME", Tools.DIR_HOME_JRE);
        envMap.put("HOME", Tools.DIR_GAME_NEW);
        envMap.put("TMPDIR", activity.getCacheDir().getAbsolutePath());
        envMap.put("LIBGL_MIPMAP", "3");

        // On certain GLES drivers, overloading default functions shader hack fails, so disable it
        envMap.put("LIBGL_NOINTOVLHACK", "1");

        // Fix white color on banner and sheep, since GL4ES 1.1.5
        envMap.put("LIBGL_NORMALIZE", "1");

        // The OPEN GL version is changed according
        envMap.put("LIBGL_ES", (String) ExtraCore.getValue(ExtraConstants.OPEN_GL_VERSION));
   
        envMap.put("MESA_GLSL_CACHE_DIR", activity.getCacheDir().getAbsolutePath());
        if (LOCAL_RENDERER != null) {
            envMap.put("MESA_GL_VERSION_OVERRIDE", LOCAL_RENDERER.equals("opengles3_virgl")?"4.3":"4.6");
            envMap.put("MESA_GLSL_VERSION_OVERRIDE", LOCAL_RENDERER.equals("opengles3_virgl")?"430":"460");
        }
        envMap.put("force_glsl_extensions_warn", "true");
        envMap.put("allow_higher_compat_version", "true");
        envMap.put("allow_glsl_extension_directive_midshader", "true");
        envMap.put("MESA_LOADER_DRIVER_OVERRIDE", "zink");
        envMap.put("VTEST_SOCKET_NAME", activity.getCacheDir().getAbsolutePath() + "/.virgl_test");

        envMap.put("LD_LIBRARY_PATH", LD_LIBRARY_PATH);
        envMap.put("PATH", Tools.DIR_HOME_JRE + "/bin:" + Os.getenv("PATH"));
        
        envMap.put("REGAL_GL_VENDOR", "Android");
        envMap.put("REGAL_GL_RENDERER", "Regal");
        envMap.put("REGAL_GL_VERSION", "4.5");
        if(LOCAL_RENDERER != null) {
            envMap.put("POJAV_RENDERER", LOCAL_RENDERER);
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
        if(!envMap.containsKey("LIBGL_ES") && LOCAL_RENDERER != null) {
            int glesMajor = getDetectedVersion();
            Log.i("glesDetect","GLES version detected: "+glesMajor);

            if (glesMajor < 3) {
                //fallback to 2 since it's the minimum for the entire app
                envMap.put("LIBGL_ES","2");
            } else if (LOCAL_RENDERER.startsWith("opengles")) {
                envMap.put("LIBGL_ES", LOCAL_RENDERER.replace("opengles", "").replace("_5", ""));
            } else {
                // TODO if can: other backends such as Vulkan.
                // Sure, they should provide GLES 3 support.
                envMap.put("LIBGL_ES", "3");
            }
        }
        for (Map.Entry<String, String> env : envMap.entrySet()) {
            Logger.getInstance().appendToLog("Added custom env: " + env.getKey() + "=" + env.getValue());
            try {
                Os.setenv(env.getKey(), env.getValue(), true);
            }catch (NullPointerException exception){
                Log.e("JREUtils", exception.toString());
            }
        }

        File serverFile = new File(Tools.DIR_HOME_JRE + "/" + Tools.DIRNAME_HOME_JRE + "/server/libjvm.so");
        jvmLibraryPath = Tools.DIR_HOME_JRE + "/" + Tools.DIRNAME_HOME_JRE + "/" + (serverFile.exists() ? "server" : "client");
        Log.d("DynamicLoader","Base LD_LIBRARY_PATH: "+LD_LIBRARY_PATH);
        Log.d("DynamicLoader","Internal LD_LIBRARY_PATH: "+jvmLibraryPath+":"+LD_LIBRARY_PATH);
        setLdLibraryPath(jvmLibraryPath+":"+LD_LIBRARY_PATH);

        // return ldLibraryPath;
    }
    
    public static int launchJavaVM(final Activity activity,final List<String> JVMArgs) throws Throwable {
        JREUtils.relocateLibPath(activity);
        // For debugging only!
/*
        StringBuilder sbJavaArgs = new StringBuilder();
        for (String s : javaArgList) {
            sbJavaArgs.append(s + " ");
        }
        ctx.appendlnToLog("Executing JVM: \"" + sbJavaArgs.toString() + "\"");
*/

        setJavaEnvironment(activity);
        
        final String graphicsLib = loadGraphicsLibrary();
         List<String> userArgs = getJavaArgs(activity);

        //Remove arguments that can interfere with the good working of the launcher
        purgeArg(userArgs,"-Xms");
        purgeArg(userArgs,"-Xmx");
        purgeArg(userArgs,"-d32");
        purgeArg(userArgs,"-d64");
        purgeArg(userArgs, "-Dorg.lwjgl.opengl.libname");

        //Add automatically generated args
        userArgs.add("-Xms" + LauncherPreferences.PREF_RAM_ALLOCATION + "M");
        userArgs.add("-Xmx" + LauncherPreferences.PREF_RAM_ALLOCATION + "M");
        if(LOCAL_RENDERER != null) userArgs.add("-Dorg.lwjgl.opengl.libname=" + graphicsLib);

        userArgs.addAll(JVMArgs);
        activity.runOnUiThread(() -> Toast.makeText(activity, activity.getString(R.string.autoram_info_msg,LauncherPreferences.PREF_RAM_ALLOCATION), Toast.LENGTH_SHORT).show());
        System.out.println(JVMArgs);
        
        initJavaRuntime();
        setupExitTrap(activity.getApplication());
        chdir(Tools.DIR_GAME_NEW);
        userArgs.add(0,"java"); //argv[0] is the program name according to C standard.

        final int exitCode = VMLauncher.launchJVM(userArgs.toArray(new String[0]));
        Logger.getInstance().appendToLog("Java Exit code: " + exitCode);
        if (exitCode != 0) {
            activity.runOnUiThread(() -> {
                AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
                dialog.setMessage(activity.getString(R.string.mcn_exit_title, exitCode));

                dialog.setPositiveButton(android.R.string.ok, (p1, p2) -> BaseMainActivity.fullyExit());
                dialog.show();
            });
        }
        return exitCode;
    }

    /**
     *  Gives an argument list filled with both the user args
     *  and the auto-generated ones (eg. the window resolution).
     * @param ctx The application context
     * @return A list filled with args.
     */
    public static List<String> getJavaArgs(Context ctx) {
        List<String> userArguments = parseJavaArguments(LauncherPreferences.PREF_CUSTOM_JAVA_ARGS);
        String resolvFile;
            resolvFile = new File(Tools.DIR_DATA,"resolv.conf").getAbsolutePath();

        ArrayList<String> overridableArguments = new ArrayList<>(Arrays.asList(
                "-Djava.home=" + Tools.DIR_HOME_JRE,
                "-Djava.io.tmpdir=" + ctx.getCacheDir().getAbsolutePath(),
                "-Duser.home=" + new File(Tools.DIR_GAME_NEW).getParent(),
                "-Duser.language=" + System.getProperty("user.language"),
                "-Dos.name=Linux",
                "-Dos.version=Android-" + Build.VERSION.RELEASE,
                "-Dpojav.path.minecraft=" + Tools.DIR_GAME_NEW,
                "-Dpojav.path.private.account=" + Tools.DIR_ACCOUNT_NEW,

                //LWJGL 3 DEBUG FLAGS
                //"-Dorg.lwjgl.util.Debug=true",
                //"-Dorg.lwjgl.util.DebugFunctions=true",
                //"-Dorg.lwjgl.util.DebugLoader=true",
                // GLFW Stub width height
                "-Dglfwstub.windowWidth=" + Tools.getDisplayFriendlyRes(currentDisplayMetrics.widthPixels, LauncherPreferences.PREF_SCALE_FACTOR/100F),
                "-Dglfwstub.windowHeight=" + Tools.getDisplayFriendlyRes(currentDisplayMetrics.heightPixels, LauncherPreferences.PREF_SCALE_FACTOR/100F),
                "-Dglfwstub.initEgl=false",
                "-Dext.net.resolvPath=" +resolvFile,
                "-Dlog4j2.formatMsgNoLookups=true", //Log4j RCE mitigation

                "-Dnet.minecraft.clientmodname=" + Tools.APP_NAME,
                "-Dfml.earlyprogresswindow=false" //Forge 1.14+ workaround
        ));
        if(LauncherPreferences.PREF_ARC_CAPES) {
            overridableArguments.add("-javaagent:"+new File(Tools.DIR_DATA,"arc_dns_injector.jar").getAbsolutePath()+"=23.95.137.176");
        }
        List<String> additionalArguments = new ArrayList<>();
        for(String arg : overridableArguments) {
            String strippedArg = arg.substring(0,arg.indexOf('='));
            boolean add = true;
            for(String uarg : userArguments) {
                if(uarg.startsWith(strippedArg)) {
                    add = false;
                    break;
                }
            }
            if(add)
                additionalArguments.add(arg);
            else
                Log.i("ArgProcessor","Arg skipped: "+arg);
        }

        //Add all the arguments
        userArguments.addAll(additionalArguments);
        return userArguments;
    }

    /**
     * Parse and separate java arguments in a user friendly fashion
     * It supports multi line and absence of spaces between arguments
     * The function also supports auto-removal of improper arguments, although it may miss some.
     *
     * @param args The un-parsed argument list.
     * @return Parsed args as an ArrayList
     */
    public static ArrayList<String> parseJavaArguments(String args){
        ArrayList<String> parsedArguments = new ArrayList<>(0);
        args = args.trim().replace(" ", "");
        //For each prefixes, we separate args.
        for(String prefix : new String[]{"-XX:-","-XX:+", "-XX:","--","-"}){
            while (true){
                int start = args.indexOf(prefix);
                if(start == -1) break;
                //Get the end of the current argument
                int end = args.indexOf("-", start + prefix.length());
                if(end == -1) end = args.length();

                //Extract it
                String parsedSubString = args.substring(start, end);
                args = args.replace(parsedSubString, "");

                //Check if two args aren't bundled together by mistake
                if(parsedSubString.indexOf('=') == parsedSubString.lastIndexOf('=')) {
                    int arraySize = parsedArguments.size();
                    if(arraySize > 0){
                        String lastString = parsedArguments.get(arraySize - 1);
                        // Looking for list elements
                        if(lastString.charAt(lastString.length() - 1) == ',' ||
                                parsedSubString.contains(",")){
                            parsedArguments.set(arraySize - 1, lastString + parsedSubString);
                            continue;
                        }
                    }
                    parsedArguments.add(parsedSubString);
                }
                else Log.w("JAVA ARGS PARSER", "Removed improper arguments: " + parsedSubString);
            }
        }
        return parsedArguments;
    }

    /**
     * Open the render library in accordance to the settings.
     * It will fallback if it fails to load the library.
     * @return The name of the loaded library
     */
    public static String loadGraphicsLibrary(){
        if(LOCAL_RENDERER == null) return null;
        String renderLibrary;
        switch (LOCAL_RENDERER){
            case "opengles2":
            case "opengles2_5":
            case "opengles3":
                renderLibrary = "libgl4es_114.so"; break;
            case "opengles3_virgl":
            case "vulkan_zink": renderLibrary = "libOSMesa_8.so"; break;
            default:
                Log.w("RENDER_LIBRARY", "No renderer selected, defaulting to opengles2");
                renderLibrary = "libgl4es_114.so";
                break;
        }

        if (!dlopen(renderLibrary) && !dlopen(findInLdLibPath(renderLibrary))) {
            Log.e("RENDER_LIBRARY","Failed to load renderer " + renderLibrary + ". Falling back to GL4ES 1.1.4");
            LOCAL_RENDERER = "opengles2";
            renderLibrary = "libgl4es_114.so";
            dlopen(sNativeLibDir + "/libgl4es_114.so");
        }
        return renderLibrary;
    }

    /**
     * Remove the argument from the list, if it exists
     * If the argument exists multiple times, they will all be removed.
     * @param argList The argument list to purge
     * @param argStart The argument to purge from the list.
     */
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

    public static int getDetectedVersion() {
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
    public static native void logToLogger(final Logger logger);
    public static native boolean dlopen(String libPath);
    public static native void setLdLibraryPath(String ldLibraryPath);
    public static native void setupBridgeWindow(Object surface);
    public static native void releaseBridgeWindow();
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
