package net.kdt.pojavlaunch;

import android.app.*;
import android.content.*;
import android.database.Cursor;
import android.net.*;
import android.os.*;
import android.provider.OpenableColumns;
import android.system.*;
import android.util.*;
import com.google.gson.*;
import com.oracle.dalvik.*;
import java.io.*;
import java.lang.reflect.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.*;
import net.kdt.pojavlaunch.prefs.*;
import net.kdt.pojavlaunch.utils.*;
import net.kdt.pojavlaunch.value.*;
import net.kdt.pojavlaunch.value.launcherprofiles.LauncherProfiles;
import net.kdt.pojavlaunch.value.launcherprofiles.MinecraftProfile;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.lwjgl.glfw.*;
import android.view.*;
import android.widget.Toast;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.P;
import static android.os.Build.VERSION_CODES.Q;
import static net.kdt.pojavlaunch.prefs.LauncherPreferences.PREF_IGNORE_NOTCH;
import static net.kdt.pojavlaunch.prefs.LauncherPreferences.PREF_NOTCH_SIZE;

public final class Tools {
    public static final boolean ENABLE_DEV_FEATURES = BuildConfig.DEBUG;

    public static String APP_NAME = "null";
    
    public static final Gson GLOBAL_GSON = new GsonBuilder().setPrettyPrinting().create();
    
    public static final String URL_HOME = "https://pojavlauncherteam.github.io/PojavLauncher";

    public static String DIR_DATA; //Initialized later to get context
    public static String MULTIRT_HOME;
    public static String LOCAL_RENDERER = null;
    public static int DEVICE_ARCHITECTURE;
    public static String LAUNCHERPROFILES_RTPREFIX = "pojav://";

    // New since 3.3.1
    public static String DIR_ACCOUNT_NEW;
    public static String DIR_ACCOUNT_OLD;
    public static String DIR_GAME_HOME = Environment.getExternalStorageDirectory().getAbsolutePath() + "/games/PojavLauncher";
    public static String DIR_GAME_NEW;
    public static String DIR_GAME_OLD = Environment.getExternalStorageDirectory().getAbsolutePath() + "/games/.minecraft";
    
    // New since 3.0.0
    public static String DIR_HOME_JRE;
    public static String DIRNAME_HOME_JRE = "lib";

    // New since 2.4.2
    public static String DIR_HOME_VERSION;
    public static String DIR_HOME_LIBRARY;

    public static String DIR_HOME_CRASH;

    public static String ASSETS_PATH;
    public static String OBSOLETE_RESOURCES_PATH;
    public static String CTRLMAP_PATH;
    public static String CTRLDEF_FILE;
    
    public static final String LIBNAME_OPTIFINE = "optifine:OptiFine";

    /**
     * Since some constant requires the use of the Context object
     * You can call this function to initialize them.
     * Any value (in)directly dependant on DIR_DATA should be set only here.
     */
    public static void initContextConstants(Context ctx){
        DIR_DATA = ctx.getFilesDir().getParent();
        MULTIRT_HOME = DIR_DATA+"/runtimes";
        if(SDK_INT >= 29) {
            DIR_GAME_HOME = ctx.getExternalFilesDir(null).getAbsolutePath();
        }else{
            DIR_GAME_HOME = new File(Environment.getExternalStorageDirectory(),"games/PojavLauncher").getAbsolutePath();
        }
        DIR_GAME_NEW = DIR_GAME_HOME + "/.minecraft";
        DIR_HOME_VERSION = DIR_GAME_NEW + "/versions";
        DIR_HOME_LIBRARY = DIR_GAME_NEW + "/libraries";
        DIR_HOME_CRASH = DIR_GAME_NEW + "/crash-reports";
        ASSETS_PATH = DIR_GAME_NEW + "/assets";
        OBSOLETE_RESOURCES_PATH= DIR_GAME_NEW + "/resources";
        CTRLMAP_PATH = DIR_GAME_HOME + "/controlmap";
        CTRLDEF_FILE = DIR_GAME_HOME + "/controlmap/default.json";
    }


    public static void launchMinecraft(final Activity activity, MinecraftAccount profile, String versionName) throws Throwable {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ((ActivityManager)activity.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryInfo(mi);
        if(LauncherPreferences.PREF_RAM_ALLOCATION > (mi.availMem/1048576L)) {
            Object memoryErrorLock = new Object();
            activity.runOnUiThread(() -> {
                androidx.appcompat.app.AlertDialog.Builder b = new androidx.appcompat.app.AlertDialog.Builder(activity)
                        .setMessage(activity.getString(R.string.memory_warning_msg,(mi.availMem/1048576L),LauncherPreferences.PREF_RAM_ALLOCATION))
                        .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {synchronized(memoryErrorLock){memoryErrorLock.notifyAll();}})
                        .setOnCancelListener((i) -> {synchronized(memoryErrorLock){memoryErrorLock.notifyAll();}});
                b.show();
            });
            synchronized (memoryErrorLock) {
                memoryErrorLock.wait();
            }
        }

        JMinecraftVersionList.Version versionInfo = Tools.getVersionInfo(null,versionName);
        String gamedirPath = Tools.DIR_GAME_NEW;
            if(activity instanceof BaseMainActivity) {
                LauncherProfiles.update();
                MinecraftProfile minecraftProfile = ((BaseMainActivity)activity).minecraftProfile;
                if(minecraftProfile == null) throw new Exception("Launching empty Profile");
                if(minecraftProfile.gameDir != null && minecraftProfile.gameDir.startsWith(Tools.LAUNCHERPROFILES_RTPREFIX))
                    gamedirPath = minecraftProfile.gameDir.replace(Tools.LAUNCHERPROFILES_RTPREFIX,Tools.DIR_GAME_HOME+"/");
                if(minecraftProfile.javaArgs != null && !minecraftProfile.javaArgs.isEmpty())
                    LauncherPreferences.PREF_CUSTOM_JAVA_ARGS = minecraftProfile.javaArgs;
            }
        PojavLoginActivity.disableSplash(gamedirPath);
        String[] launchArgs = getMinecraftClientArgs(profile, versionInfo, gamedirPath);

        // Select the appropriate openGL version
        OldVersionsUtils.selectOpenGlVersion(versionInfo);

        // ctx.appendlnToLog("Minecraft Args: " + Arrays.toString(launchArgs));

        String launchClassPath = generateLaunchClassPath(versionInfo,versionName);

        List<String> javaArgList = new ArrayList<String>();

        // Only Java 8 supports headful AWT for now
        if (JREUtils.jreReleaseList.get("JAVA_VERSION").equals("1.8.0")) {
            getCacioJavaArgs(javaArgList, false);
        } else if (LauncherPreferences.PREF_ARC_CAPES) {
            // Opens the java.net package to Arc DNS injector on Java 9+
            javaArgList.add("--add-opens=java.base/java.net=ALL-UNNAMED");
        }

/*
        int mcReleaseDate = Integer.parseInt(versionInfo.releaseTime.substring(0, 10).replace("-", ""));
        // 13w17a: 20130425
        // 13w18a: 20130502
        if (mcReleaseDate < 20130502 && versionInfo.minimumLauncherVersion < 9){
            ctx.appendlnToLog("AWT-enabled version detected! ("+mcReleaseDate+")");
            getCacioJavaArgs(javaArgList,false);
        }else{
            getCacioJavaArgs(javaArgList,false); // true
            ctx.appendlnToLog("Headless version detected! ("+mcReleaseDate+")");
        }
*/

        if (versionInfo.logging != null) {
            String configFile = Tools.DIR_DATA + "/" + versionInfo.logging.client.file.id.replace("client", "log4j-rce-patch");
            if (!new File(configFile).exists()) {
                configFile = Tools.DIR_GAME_NEW + "/" + versionInfo.logging.client.file.id;
            }
            javaArgList.add("-Dlog4j.configurationFile=" + configFile);
        }
        javaArgList.addAll(Arrays.asList(getMinecraftJVMArgs(versionName, gamedirPath)));
        javaArgList.add("-cp");
        javaArgList.add(getLWJGL3ClassPath() + ":" + launchClassPath);

        javaArgList.add(versionInfo.mainClass);
        javaArgList.addAll(Arrays.asList(launchArgs));
        // ctx.appendlnToLog("full args: "+javaArgList.toString());
        JREUtils.launchJavaVM(activity, javaArgList);
    }
    
    public static void getCacioJavaArgs(List<String> javaArgList, boolean isHeadless) {
        javaArgList.add("-Djava.awt.headless="+isHeadless);
        // Caciocavallo config AWT-enabled version
        javaArgList.add("-Dcacio.managed.screensize=" + AWTCanvasView.AWT_CANVAS_WIDTH + "x" + AWTCanvasView.AWT_CANVAS_HEIGHT);
        // javaArgList.add("-Dcacio.font.fontmanager=net.java.openjdk.cacio.ctc.CTCFontManager");
        javaArgList.add("-Dcacio.font.fontmanager=sun.awt.X11FontManager");
        javaArgList.add("-Dcacio.font.fontscaler=sun.font.FreetypeFontScaler");
        javaArgList.add("-Dswing.defaultlaf=javax.swing.plaf.metal.MetalLookAndFeel");
        javaArgList.add("-Dawt.toolkit=net.java.openjdk.cacio.ctc.CTCToolkit");
        javaArgList.add("-Djava.awt.graphicsenv=net.java.openjdk.cacio.ctc.CTCGraphicsEnvironment");

        StringBuilder cacioClasspath = new StringBuilder();
        cacioClasspath.append("-Xbootclasspath/p");
        File cacioDir = new File(DIR_GAME_HOME + "/caciocavallo");
        if (cacioDir.exists() && cacioDir.isDirectory()) {
            for (File file : cacioDir.listFiles()) {
                if (file.getName().endsWith(".jar")) {
                    cacioClasspath.append(":" + file.getAbsolutePath());
                }
            }
        }
        javaArgList.add(cacioClasspath.toString());
    }

    public static String[] getMinecraftJVMArgs(String versionName, String strGameDir) {
        JMinecraftVersionList.Version versionInfo = Tools.getVersionInfo(null, versionName, true);
        // Parse Forge 1.17+ additional JVM Arguments
        if (versionInfo.inheritsFrom == null || versionInfo.arguments == null || versionInfo.arguments.jvm == null) {
            return new String[0];
        }

        Map<String, String> varArgMap = new ArrayMap<>();
        varArgMap.put("classpath_separator", ":");
        varArgMap.put("library_directory", strGameDir + "/libraries");
        varArgMap.put("version_name", versionInfo.id);

        List<String> minecraftArgs = new ArrayList<String>();
        if (versionInfo.arguments != null) {
            for (Object arg : versionInfo.arguments.jvm) {
                if (arg instanceof String) {
                    minecraftArgs.add((String) arg);
                } else {
                    /*
                     JMinecraftVersionList.Arguments.ArgValue argv = (JMinecraftVersionList.Arguments.ArgValue) arg;
                     if (argv.values != null) {
                     minecraftArgs.add(argv.values[0]);
                     } else {

                     for (JMinecraftVersionList.Arguments.ArgValue.ArgRules rule : arg.rules) {
                     // rule.action = allow
                     // TODO implement this
                     }

                     }
                     */
                }
            }
        }

        String[] argsFromJson = JSONUtils.insertJSONValueList(minecraftArgs.toArray(new String[0]), varArgMap);
        // Tools.dialogOnUiThread(this, "Result args", Arrays.asList(argsFromJson).toString());
        return argsFromJson;
    }

    public static String[] getMinecraftClientArgs(MinecraftAccount profile, JMinecraftVersionList.Version versionInfo, String strGameDir) {
        String username = profile.username;
        String versionName = versionInfo.id;
        if (versionInfo.inheritsFrom != null) {
            versionName = versionInfo.inheritsFrom;
        }
        
        String userType = "mojang";

        File gameDir = new File(strGameDir);
        gameDir.mkdirs();

        Map<String, String> varArgMap = new ArrayMap<>();
        varArgMap.put("auth_session", profile.accessToken); // For legacy versions of MC
        varArgMap.put("auth_access_token", profile.accessToken);
        varArgMap.put("auth_player_name", username);
        varArgMap.put("auth_uuid", profile.profileId);
        varArgMap.put("assets_root", Tools.ASSETS_PATH);
        varArgMap.put("assets_index_name", versionInfo.assets);
        varArgMap.put("game_assets", Tools.ASSETS_PATH);
        varArgMap.put("game_directory", gameDir.getAbsolutePath());
        varArgMap.put("user_properties", "{}");
        varArgMap.put("user_type", userType);
        varArgMap.put("version_name", versionName);
        varArgMap.put("version_type", versionInfo.type);

        List<String> minecraftArgs = new ArrayList<String>();
        if (versionInfo.arguments != null) {
            // Support Minecraft 1.13+
            for (Object arg : versionInfo.arguments.game) {
                if (arg instanceof String) {
                    minecraftArgs.add((String) arg);
                } else {
                    /*
                    JMinecraftVersionList.Arguments.ArgValue argv = (JMinecraftVersionList.Arguments.ArgValue) arg;
                    if (argv.values != null) {
                        minecraftArgs.add(argv.values[0]);
                    } else {
                        
                         for (JMinecraftVersionList.Arguments.ArgValue.ArgRules rule : arg.rules) {
                         // rule.action = allow
                         // TODO implement this
                         }
                         
                    }
                    */
                }
            }
        }
        /*
        minecraftArgs.add("--width");
        minecraftArgs.add(Integer.toString(CallbackBridge.windowWidth));
        minecraftArgs.add("--height");
        minecraftArgs.add(Integer.toString(CallbackBridge.windowHeight));

        minecraftArgs.add("--fullscreenWidth");
        minecraftArgs.add(Integer.toString(CallbackBridge.windowWidth));
        minecraftArgs.add("--fullscreenHeight");
        minecraftArgs.add(Integer.toString(CallbackBridge.windowHeight));
        */
        
        String[] argsFromJson = JSONUtils.insertJSONValueList(
            splitAndFilterEmpty(
                versionInfo.minecraftArguments == null ?
                fromStringArray(minecraftArgs.toArray(new String[0])):
                versionInfo.minecraftArguments
            ), varArgMap
        );
        // Tools.dialogOnUiThread(this, "Result args", Arrays.asList(argsFromJson).toString());
        return argsFromJson;
    }

    public static String fromStringArray(String[] strArr) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < strArr.length; i++) {
            if (i > 0) builder.append(" ");
            builder.append(strArr[i]);
        }

        return builder.toString();
    }

    private static String[] splitAndFilterEmpty(String argStr) {
        List<String> strList = new ArrayList<String>();
        for (String arg : argStr.split(" ")) {
            if (!arg.isEmpty()) {
                strList.add(arg);
            }
        }
        //strList.add("--fullscreen");
        return strList.toArray(new String[0]);
    }

    public static String artifactToPath(String name) {
        int idx = name.indexOf(":");
        assert idx != -1;
        int idx2 = name.indexOf(":", idx+1);
        assert idx2 != -1;
        String group = name.substring(0, idx);
        String artifact = name.substring(idx+1, idx2);
        String version = name.substring(idx2+1).replace(':','-');
        return group.replaceAll("\\.", "/") + "/" + artifact + "/" + version + "/" + artifact + "-" + version + ".jar";
    }

    public static String getPatchedFile(String version) {
        return DIR_HOME_VERSION + "/" + version + "/" + version + ".jar";
    }

    private static String getLWJGL3ClassPath() {
        StringBuilder libStr = new StringBuilder();
        File lwjgl3Folder = new File(Tools.DIR_GAME_HOME, "lwjgl3");
        if (/* info.arguments != null && */ lwjgl3Folder.exists()) {
            for (File file: lwjgl3Folder.listFiles()) {
                if (file.getName().endsWith(".jar")) {
                    libStr.append(file.getAbsolutePath() + ":");
                }
            }
        }
        // Remove the ':' at the end
        libStr.setLength(libStr.length() - 1);
        return libStr.toString();
    }

    private static boolean isClientFirst = false;
    public static String generateLaunchClassPath(JMinecraftVersionList.Version info,String actualname) {
        StringBuilder libStr = new StringBuilder(); //versnDir + "/" + version + "/" + version + ".jar:";

        String[] classpath = generateLibClasspath(info);

        // Debug: LWJGL 3 override
        // File lwjgl2Folder = new File(Tools.MAIN_PATH, "lwjgl2");

        /*
         File lwjgl3Folder = new File(Tools.MAIN_PATH, "lwjgl3");
         if (lwjgl3Folder.exists()) {
         for (File file: lwjgl3Folder.listFiles()) {
         if (file.getName().endsWith(".jar")) {
         libStr.append(file.getAbsolutePath() + ":");
         }
         }
         } else if (lwjgl2Folder.exists()) {
         for (File file: lwjgl2Folder.listFiles()) {
         if (file.getName().endsWith(".jar")) {
         libStr.append(file.getAbsolutePath() + ":");
         }
         }
         }
         */

        if (isClientFirst) {
            libStr.append(getPatchedFile(actualname));
        }
        for (String perJar : classpath) {
            if (!new File(perJar).exists()) {
                Log.d(APP_NAME, "Ignored non-exists file: " + perJar);
                continue;
            }
            libStr.append((isClientFirst ? ":" : "") + perJar + (!isClientFirst ? ":" : ""));
        }
        if (!isClientFirst) {
            libStr.append(getPatchedFile(actualname));
        }

        return libStr.toString();
    }

    public static DisplayMetrics getDisplayMetrics(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();

        if(SDK_INT >= Build.VERSION_CODES.N && (activity.isInMultiWindowMode() || activity.isInPictureInPictureMode())){
            //For devices with free form/split screen, we need window size, not screen size.
            displayMetrics = activity.getResources().getDisplayMetrics();
        }else{
            if (SDK_INT >= Build.VERSION_CODES.R) {
                activity.getDisplay().getRealMetrics(displayMetrics);
            } else if(SDK_INT >= P) {
                 activity.getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
            }else{ // Some old devices can have a notch despite it not being officially supported
                activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            }
            if(!PREF_IGNORE_NOTCH){
                //Remove notch width when it isn't ignored.
                displayMetrics.widthPixels -= PREF_NOTCH_SIZE;
            }
        }
        currentDisplayMetrics = displayMetrics;

        return displayMetrics;
    }

    public static void setFullscreen(Activity activity) {
        final View decorView = activity.getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener (visibility -> {
            if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }
        });
    }

    public static DisplayMetrics currentDisplayMetrics;

    public static void updateWindowSize(Activity activity) {
        currentDisplayMetrics = getDisplayMetrics(activity);

        CallbackBridge.physicalWidth = currentDisplayMetrics.widthPixels;
        CallbackBridge.physicalHeight = currentDisplayMetrics.heightPixels;
    }

    public static float dpToPx(float dp) {
        //Better hope for the currentDisplayMetrics to be good
        return dp * currentDisplayMetrics.density;
    }

    public static float pxToDp(float px){
        //Better hope for the currentDisplayMetrics to be good
        return px / currentDisplayMetrics.density;
    }

    public static void copyAssetFile(Context ctx, String fileName, String output, boolean overwrite) throws IOException {
        copyAssetFile(ctx, fileName, output, new File(fileName).getName(), overwrite);
    }

    public static void copyAssetFile(Context ctx, String fileName, String output, String outputName, boolean overwrite) throws IOException
    {
        File file = new File(output);
        if(!file.exists()) {
            file.mkdirs();
        }
        File file2 = new File(output, outputName);
        if(!file2.exists() || overwrite){
            write(file2.getAbsolutePath(), loadFromAssetToByte(ctx, fileName));
        }
    }

    public static void showError(Context ctx, Throwable e) {
        showError(ctx, e, false);
    }

    public static void showError(final Context ctx, final Throwable e, final boolean exitIfOk) {
        showError(ctx, R.string.global_error, e, exitIfOk, false);
    }

    public static void showError(final Context ctx, final int titleId, final Throwable e, final boolean exitIfOk) {
        showError(ctx, titleId, e, exitIfOk, false);
    }

    private static void showError(final Context ctx, final int titleId, final Throwable e, final boolean exitIfOk, final boolean showMore) {
        e.printStackTrace();
        
        Runnable runnable = () -> {
            final String errMsg = showMore ? Log.getStackTraceString(e): e.getMessage();
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) ctx)
                .setTitle(titleId)
                .setMessage(errMsg)
                .setPositiveButton(android.R.string.ok, (DialogInterface.OnClickListener) (p1, p2) -> {
                    if(exitIfOk) {
                        if (ctx instanceof BaseMainActivity) {
                            BaseMainActivity.fullyExit();
                        } else if (ctx instanceof Activity) {
                            ((Activity) ctx).finish();
                        }
                    }
                })
                .setNegativeButton(showMore ? R.string.error_show_less : R.string.error_show_more, (DialogInterface.OnClickListener) (p1, p2) -> showError(ctx, titleId, e, exitIfOk, !showMore))
                .setNeutralButton(android.R.string.copy, (DialogInterface.OnClickListener) (p1, p2) -> {
                    ClipboardManager mgr = (ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
                    mgr.setPrimaryClip(ClipData.newPlainText("error", Log.getStackTraceString(e)));
                    if(exitIfOk) {
                        if (ctx instanceof BaseMainActivity) {
                            BaseMainActivity.fullyExit();
                        } else {
                            ((Activity) ctx).finish();
                        }
                    }
                })
                .setCancelable(!exitIfOk);
            try {
                builder.show();
            } catch (Throwable th) {
                th.printStackTrace();
            }
        };

        if (ctx instanceof Activity) {
            ((Activity) ctx).runOnUiThread(runnable);
        } else {
            runnable.run();
        }
    }

    public static void dialogOnUiThread(final Activity activity, final CharSequence title, final CharSequence message) {
        activity.runOnUiThread(() -> new AlertDialog.Builder(activity)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(android.R.string.ok, null)
            .show());
    }

    public static void moveInside(String from, String to) {
        File fromFile = new File(from);
        for (File fromInside : fromFile.listFiles()) {
            moveRecursive(fromInside.getAbsolutePath(), to);
        }
        fromFile.delete();
    }

    public static void moveRecursive(String from, String to) {
        moveRecursive(new File(from), new File(to));
    }

    public static void moveRecursive(File from, File to) {
        File toFrom = new File(to, from.getName());
        try {
            if (from.isDirectory()) {
                for (File child : from.listFiles()) {
                    moveRecursive(child, toFrom);
                }
            }
        } finally {
            from.getParentFile().mkdirs();
            from.renameTo(toFrom);
        }
    }

    public static void openURL(Activity act, String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        act.startActivity(browserIntent);
    }
    private static boolean checkRules(JMinecraftVersionList.Arguments.ArgValue.ArgRules[] rules) {
        if(rules == null) return true; // always allow
        for (JMinecraftVersionList.Arguments.ArgValue.ArgRules rule : rules) {
            if (rule.action.equals("allow") && rule.os != null && rule.os.name.equals("osx")) {
                return false; //disallow
            }
        }
        return true; // allow if none match
    }
    public static String[] generateLibClasspath(JMinecraftVersionList.Version info) {
        List<String> libDir = new ArrayList<String>();
        for (DependentLibrary libItem: info.libraries) {
            if(!checkRules(libItem.rules)) continue;
            libDir.add(Tools.DIR_HOME_LIBRARY + "/" + Tools.artifactToPath(libItem.name));
        }
        return libDir.toArray(new String[0]);
    }

    public static JMinecraftVersionList.Version getVersionInfo(BaseLauncherActivity bla, String versionName) {
        return getVersionInfo(bla, versionName, false);
    }

    public static JMinecraftVersionList.Version getVersionInfo(BaseLauncherActivity bla, String versionName, boolean skipInheriting) {
        try {
            JMinecraftVersionList.Version customVer = Tools.GLOBAL_GSON.fromJson(read(DIR_HOME_VERSION + "/" + versionName + "/" + versionName + ".json"), JMinecraftVersionList.Version.class);
            for (DependentLibrary lib : customVer.libraries) {
                if (lib.name.startsWith(LIBNAME_OPTIFINE)) {
                    customVer.optifineLib = lib;
                }
            }
            if (skipInheriting || customVer.inheritsFrom == null || customVer.inheritsFrom.equals(customVer.id)) {
                return customVer;
            } else {
                JMinecraftVersionList.Version inheritsVer = null;
                if(bla != null) if (bla.mVersionList != null) {
                    for (JMinecraftVersionList.Version valueVer : bla.mVersionList.versions) {
                        if (valueVer.id.equals(customVer.inheritsFrom) && (!new File(DIR_HOME_VERSION + "/" + customVer.inheritsFrom + "/" + customVer.inheritsFrom + ".json").exists()) && (valueVer.url != null)) {
                            Tools.downloadFile(valueVer.url,DIR_HOME_VERSION + "/" + customVer.inheritsFrom + "/" + customVer.inheritsFrom + ".json");
                        }
                    }
                }//If it won't download, just search for it
                   try{
                      inheritsVer = Tools.GLOBAL_GSON.fromJson(read(DIR_HOME_VERSION + "/" + customVer.inheritsFrom + "/" + customVer.inheritsFrom + ".json"), JMinecraftVersionList.Version.class);
                   }catch(IOException e) {
                       throw new RuntimeException("Can't find the source version for "+ versionName +" (req version="+customVer.inheritsFrom+")");
                   }
                //inheritsVer.inheritsFrom = inheritsVer.id;
                insertSafety(inheritsVer, customVer,
                             "assetIndex", "assets", "id",
                             "mainClass", "minecraftArguments",
                             "optifineLib", "releaseTime", "time", "type"
                             );

                List<DependentLibrary> libList = new ArrayList<DependentLibrary>(Arrays.asList(inheritsVer.libraries));
                try {
                    loop_1:
                    for (DependentLibrary lib : customVer.libraries) {
                        String libName = lib.name.substring(0, lib.name.lastIndexOf(":"));
                        for (int i = 0; i < libList.size(); i++) {
                            DependentLibrary libAdded = libList.get(i);
                            String libAddedName = libAdded.name.substring(0, libAdded.name.lastIndexOf(":"));
                            
                            if (libAddedName.equals(libName)) {
                                Log.d(APP_NAME, "Library " + libName + ": Replaced version " + 
                                    libName.substring(libName.lastIndexOf(":") + 1) + " with " +
                                    libAddedName.substring(libAddedName.lastIndexOf(":") + 1));
                                libList.set(i, lib);
                                continue loop_1;
                            }
                        }

                        libList.add(lib);
                    }
                } finally {
                    inheritsVer.libraries = libList.toArray(new DependentLibrary[0]);
                }

                // Inheriting Minecraft 1.13+ with append custom args
                if (inheritsVer.arguments != null && customVer.arguments != null) {
                    List totalArgList = new ArrayList();
                    totalArgList.addAll(Arrays.asList(inheritsVer.arguments.game));
                    
                    int nskip = 0;
                    for (int i = 0; i < customVer.arguments.game.length; i++) {
                        if (nskip > 0) {
                            nskip--;
                            continue;
                        }
                        
                        Object perCustomArg = customVer.arguments.game[i];
                        if (perCustomArg instanceof String) {
                            String perCustomArgStr = (String) perCustomArg;
                            // Check if there is a duplicate argument on combine
                            if (perCustomArgStr.startsWith("--") && totalArgList.contains(perCustomArgStr)) {
                                perCustomArg = customVer.arguments.game[i + 1];
                                if (perCustomArg instanceof String) {
                                    perCustomArgStr = (String) perCustomArg;
                                    // If the next is argument value, skip it
                                    if (!perCustomArgStr.startsWith("--")) {
                                        nskip++;
                                    }
                                }
                            } else {
                                totalArgList.add(perCustomArgStr);
                            }
                        } else if (!totalArgList.contains(perCustomArg)) {
                            totalArgList.add(perCustomArg);
                        }
                    }

                    inheritsVer.arguments.game = totalArgList.toArray(new Object[0]);
                }

                return inheritsVer;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Prevent NullPointerException
    private static void insertSafety(JMinecraftVersionList.Version targetVer, JMinecraftVersionList.Version fromVer, String... keyArr) {
        for (String key : keyArr) {
            Object value = null;
            try {
                Field fieldA = fromVer.getClass().getDeclaredField(key);
                value = fieldA.get(fromVer);
                if (((value instanceof String) && !((String) value).isEmpty()) || value != null) {
                    Field fieldB = targetVer.getClass().getDeclaredField(key);
                    fieldB.set(targetVer, value);
                }
            } catch (Throwable th) {
                Log.w(Tools.APP_NAME, "Unable to insert " + key + "=" + value, th);
            }
        }
    }
    
    public static String convertStream(InputStream inputStream) throws IOException {
        return convertStream(inputStream, Charset.forName("UTF-8"));
    }
    
    public static String convertStream(InputStream inputStream, Charset charset) throws IOException {
        StringBuilder out = new StringBuilder();
        int len;
        byte[] buf = new byte[512];
        while((len = inputStream.read(buf))!=-1) {
            out.append(new String(buf, 0, len, charset));
        }
        return out.toString();
    }

    public static File lastFileModified(String dir) {
        File fl = new File(dir);

        File[] files = fl.listFiles(File::isFile);
        if(files == null) {
            return null;
            // The patch was a bit wrong...
            // So, this may be null, why? Because this folder may not exist yet
            // Or it may not have any files...
            // Doesn't matter. We must check for that in the crash fragment.
        }
        long lastMod = Long.MIN_VALUE;
        File choice = null;
        for (File file : files) {
             if (file.lastModified() > lastMod) {
                 choice = file;
                 lastMod = file.lastModified();
            }
        }
        return choice;
    }


    public static String read(InputStream is) throws IOException {
        StringBuilder out = new StringBuilder();
        int len;
        byte[] buf = new byte[512];
        while((len = is.read(buf))!=-1) {
            out.append(new String(buf, 0, len));
        }
        return out.toString();
    }

    public static String read(String path) throws IOException {
        return read(new FileInputStream(path));
    }

    public static void write(String path, byte[] content) throws IOException
    {
        File outPath = new File(path);
        outPath.getParentFile().mkdirs();
        outPath.createNewFile();

        BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(path));
        fos.write(content, 0, content.length);
        fos.close();
    }

    public static void write(String path, String content) throws IOException {
        write(path, content.getBytes());
    }

    public static byte[] loadFromAssetToByte(Context ctx, String inFile) {
        byte[] buffer = null;

        try {
            InputStream stream = ctx.getAssets().open(inFile);

            int size = stream.available();
            buffer = new byte[size];
            stream.read(buffer);
            stream.close();
        } catch (IOException e) {
            // Handle exceptions here
            e.printStackTrace();
        }
        return buffer;
    }

    public static void downloadFile(String urlInput, String nameOutput) throws IOException {
        File file = new File(nameOutput);
        DownloadUtils.downloadFile(urlInput, file);
    }
    public abstract static class DownloaderFeedback {
        public abstract void updateProgress(int curr, int max);
    }

    public static void downloadFileMonitored(String urlInput,String nameOutput, DownloaderFeedback monitor) throws IOException {
        File nameOutputFile = new File(nameOutput);
        if (!nameOutputFile.exists()) {
            nameOutputFile.getParentFile().mkdirs();
        }
        HttpURLConnection conn = (HttpURLConnection) new URL(urlInput).openConnection();
        InputStream readStr = conn.getInputStream();
        FileOutputStream fos = new FileOutputStream(nameOutputFile);
        int cur = 0;
        int oval = 0;
        int len = conn.getContentLength();
        byte[] buf = new byte[65535];
        while ((cur = readStr.read(buf)) != -1) {
            oval += cur;
            fos.write(buf, 0, cur);
            monitor.updateProgress(oval, len);
        }
        fos.close();
        conn.disconnect();
    }
    public static boolean compareSHA1(File f, String sourceSHA) {
        try {
            String sha1_dst;
            try (InputStream is = new FileInputStream(f)) {
                 sha1_dst = new String(Hex.encodeHex(org.apache.commons.codec.digest.DigestUtils.sha1(is)));
            }
            if(sha1_dst != null && sourceSHA != null) {
                return sha1_dst.equalsIgnoreCase(sourceSHA);
            } else{
                return true; // fake match
            }
        }catch (IOException e) {
            Log.i("SHA1","Fake-matching a hash due to a read error",e);
            return true;
        }
    }

    public static void ignoreNotch(boolean shouldIgnore, Activity ctx){
        if (SDK_INT >= P) {
            if (shouldIgnore) {
                ctx.getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            } else {
                ctx.getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER;
            }
            ctx.getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
            Tools.updateWindowSize(ctx);
        }
    }

    public static int getTotalDeviceMemory(Context ctx){
        ActivityManager actManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        actManager.getMemoryInfo(memInfo);
        return (int) (memInfo.totalMem / 1048576L);
    }

    public static int getFreeDeviceMemory(Context ctx){
        ActivityManager actManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        actManager.getMemoryInfo(memInfo);
        return (int) (memInfo.availMem / 1048576L);
    }

    public static int getDisplayFriendlyRes(int displaySideRes, float scaling){
        displaySideRes *= scaling;
        if(displaySideRes % 2 != 0) displaySideRes ++;
        return displaySideRes;
    }

    public static String getFileName(Context ctx, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = ctx.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}
