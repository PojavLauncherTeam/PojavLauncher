package net.kdt.pojavlaunch;

import android.app.*;
import android.content.*;
import android.net.*;
import android.os.*;
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

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.lwjgl.glfw.*;
import android.view.*;
import android.widget.Toast;

import static android.os.Build.VERSION_CODES.P;
import static net.kdt.pojavlaunch.prefs.LauncherPreferences.PREF_IGNORE_NOTCH;
import static net.kdt.pojavlaunch.prefs.LauncherPreferences.PREF_NOTCH_SIZE;

public final class Tools
{
    public static final boolean ENABLE_DEV_FEATURES = BuildConfig.DEBUG;

    public static String APP_NAME = "null";
    
    public static final Gson GLOBAL_GSON = new GsonBuilder().setPrettyPrinting().create();
    
    public static final String URL_HOME = "https://pojavlauncherteam.github.io/PojavLauncher";
    public static String DIR_DATA = "/data/data/" + BuildConfig.APPLICATION_ID;
    public static String CURRENT_ARCHITECTURE;

    // New since 3.3.1
    public static String DIR_ACCOUNT_NEW;
    public static String DIR_ACCOUNT_OLD;
    public static final String DIR_GAME_HOME = Environment.getExternalStorageDirectory().getAbsolutePath() + "/games/PojavLauncher";
    public static final String DIR_GAME_NEW = DIR_GAME_HOME + "/.minecraft";
    public static final String DIR_GAME_OLD = Environment.getExternalStorageDirectory().getAbsolutePath() + "/games/.minecraft";
    
    // New since 3.0.0
    public static String DIR_HOME_JRE;
    public static String DIRNAME_HOME_JRE = "lib";

    // New since 2.4.2
    public static final String DIR_HOME_VERSION = DIR_GAME_NEW + "/versions";
    public static final String DIR_HOME_LIBRARY = DIR_GAME_NEW + "/libraries";

    public static final String DIR_HOME_CRASH = DIR_GAME_NEW + "/crash-reports";

    public static final String ASSETS_PATH = DIR_GAME_NEW + "/assets";
    public static final String OBSOLETE_RESOURCES_PATH= DIR_GAME_NEW + "/resources";
    public static final String CTRLMAP_PATH = DIR_GAME_HOME + "/controlmap";
    public static final String CTRLDEF_FILE = DIR_GAME_HOME + "/controlmap/default.json";
    
    public static final String LIBNAME_OPTIFINE = "optifine:OptiFine";

    public static void launchMinecraft(final LoggableActivity ctx, MinecraftAccount profile, String versionName) throws Throwable {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ((ActivityManager)ctx.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryInfo(mi);
        if(LauncherPreferences.PREF_RAM_ALLOCATION > (mi.availMem/1048576L)) {
            ctx.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    androidx.appcompat.app.AlertDialog.Builder b = new androidx.appcompat.app.AlertDialog.Builder(ctx)
                            .setMessage(ctx.getString(R.string.memory_warning_msg,(mi.availMem/1048576L),LauncherPreferences.PREF_RAM_ALLOCATION))
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {}
                            });
                    b.show();
                }
            });
        }

        JMinecraftVersionList.Version versionInfo = Tools.getVersionInfo(null,versionName);
        PerVersionConfig.update();
        PerVersionConfig.VersionConfig pvcConfig = PerVersionConfig.configMap.get(versionName);

        String gamedirPath;
        if(pvcConfig != null && pvcConfig.gamePath != null && !pvcConfig.gamePath.isEmpty()) gamedirPath = pvcConfig.gamePath;
        else gamedirPath = Tools.DIR_GAME_NEW;
        if(pvcConfig != null && pvcConfig.jvmArgs != null && !pvcConfig.jvmArgs.isEmpty()) LauncherPreferences.PREF_CUSTOM_JAVA_ARGS = pvcConfig.jvmArgs;
        PojavLoginActivity.disableSplash(gamedirPath);
        String[] launchArgs = getMinecraftArgs(profile, versionInfo, gamedirPath);

        // ctx.appendlnToLog("Minecraft Args: " + Arrays.toString(launchArgs));

        String launchClassPath = generateLaunchClassPath(versionInfo,versionName);

        List<String> javaArgList = new ArrayList<String>();

        // Only Java 8 supports headful AWT for now
        if (ctx.jreReleaseList.get("JAVA_VERSION").equals("1.8.0")) {
            getCacioJavaArgs(javaArgList, false);
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

        javaArgList.add("-cp");
        javaArgList.add(getLWJGL3ClassPath() + ":" + launchClassPath);

        javaArgList.add(versionInfo.mainClass);
        javaArgList.addAll(Arrays.asList(launchArgs));
        // ctx.appendlnToLog("full args: "+javaArgList.toString());
        JREUtils.launchJavaVM(ctx, javaArgList);
    }
    
    public static void getCacioJavaArgs(List<String> javaArgList, boolean isHeadless) {
        javaArgList.add("-Djava.awt.headless="+isHeadless);
        // Caciocavallo config AWT-enabled version
        javaArgList.add("-Dcacio.managed.screensize=" + CallbackBridge.physicalWidth + "x" + CallbackBridge.physicalHeight);
        // javaArgList.add("-Dcacio.font.fontmanager=net.java.openjdk.cacio.ctc.CTCFontManager");
        javaArgList.add("-Dcacio.font.fontmanager=sun.awt.X11FontManager");
        javaArgList.add("-Dcacio.font.fontscaler=sun.font.FreetypeFontScaler");
        javaArgList.add("-Dswing.defaultlaf=javax.swing.plaf.metal.MetalLookAndFeel");
        javaArgList.add("-Dawt.toolkit=net.java.openjdk.cacio.ctc.CTCToolkit");
        javaArgList.add("-Djava.awt.graphicsenv=net.java.openjdk.cacio.ctc.CTCGraphicsEnvironment");

        StringBuilder cacioClasspath = new StringBuilder();
        cacioClasspath.append("-Xbootclasspath/a");
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

    public static void getJavaArgs(Context ctx, List<String> javaArgList) {
        List<String> overrideableArgList = new ArrayList<String>();

        overrideableArgList.add("-Djava.home=" + Tools.DIR_HOME_JRE);
        overrideableArgList.add("-Djava.io.tmpdir=" + ctx.getCacheDir().getAbsolutePath());
        
        overrideableArgList.add("-Duser.home=" + new File(Tools.DIR_GAME_NEW).getParent());
        overrideableArgList.add("-Duser.language=" + System.getProperty("user.language"));
        // overrideableArgList.add("-Duser.timezone=GMT");

        overrideableArgList.add("-Dos.name=Linux");
        overrideableArgList.add("-Dos.version=Android-" + Build.VERSION.RELEASE);
        overrideableArgList.add("-Dpojav.path.minecraft=" + DIR_GAME_NEW);
        overrideableArgList.add("-Dpojav.path.private.account=" + DIR_ACCOUNT_NEW);
        
        // javaArgList.add("-Dorg.lwjgl.libname=liblwjgl3.so");
        // javaArgList.add("-Dorg.lwjgl.system.jemalloc.libname=libjemalloc.so");
       
        overrideableArgList.add("-Dorg.lwjgl.opengl.libname=" + LauncherPreferences.PREF_CUSTOM_OPENGL_LIBNAME);
        // overrideableArgList.add("-Dorg.lwjgl.opengl.libname=libgl4es_115.so");
        
        // javaArgList.add("-Dorg.lwjgl.opengl.libname=libRegal.so");

        // Enable LWJGL3 debug
        // overrideableArgList.add("-Dorg.lwjgl.util.Debug=true");
        // overrideableArgList.add("-Dorg.lwjgl.util.DebugFunctions=true");
        // overrideableArgList.add("-Dorg.lwjgl.util.DebugLoader=true");

        // GLFW Stub width height
        overrideableArgList.add("-Dglfwstub.windowWidth=" + CallbackBridge.windowWidth);
        overrideableArgList.add("-Dglfwstub.windowHeight=" + CallbackBridge.windowHeight);
        overrideableArgList.add("-Dglfwstub.initEgl=false");

        overrideableArgList.add("-Dnet.minecraft.clientmodname=" + Tools.APP_NAME);
        
        // Disable FML Early Loading Screen to get Forge 1.14+ works
        overrideableArgList.add("-Dfml.earlyprogresswindow=false");
        
        // Override args
        for (String argOverride : LauncherPreferences.PREF_CUSTOM_JAVA_ARGS.split(" ")) {
            for (int i = overrideableArgList.size() - 1; i >= 0; i--) {
                String arg = overrideableArgList.get(i);
                // Currently, only java property is supported overridable argument, other such as "-X:" are handled by the JVM.
                // Althought java properties are also handled by JVM, but duplicate bug from parser may occurs, so replace them.
                if (arg.startsWith("-D") && argOverride.startsWith(arg.substring(0, arg.indexOf('=') + 1))) {
                    // Override the matched argument
                    overrideableArgList.set(i, argOverride);
                    break;
                } else if (!argOverride.isEmpty() && i == 0) {
                    // Overridable argument has mismatched, so add the custom argument to overridable argument list
                    javaArgList.add(argOverride);
                }
            }
        }

        javaArgList.addAll(overrideableArgList);
    }

    public static String[] getMinecraftArgs(MinecraftAccount profile, JMinecraftVersionList.Version versionInfo, String strGameDir) {
        String username = profile.username;
        String versionName = versionInfo.id;
        if (versionInfo.inheritsFrom != null) {
            versionName = versionInfo.inheritsFrom;
        }
        
        String userType = "mojang";

        File gameDir = new File(strGameDir);
        gameDir.mkdirs();

        Map<String, String> varArgMap = new ArrayMap<String, String>();
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
        minecraftArgs.add("--width");
        minecraftArgs.add(Integer.toString(CallbackBridge.windowWidth));
        minecraftArgs.add("--height");
        minecraftArgs.add(Integer.toString(CallbackBridge.windowHeight));
        minecraftArgs.add("--fullscreenWidth");
        minecraftArgs.add(Integer.toString(CallbackBridge.windowWidth));
        minecraftArgs.add("--fullscreenHeight");
        minecraftArgs.add(Integer.toString(CallbackBridge.windowHeight));
        
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
        strList.add("--fullscreen");
        return strList.toArray(new String[0]);
    }

    public static String artifactToPath(String group, String artifact, String version) {
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

    public static DisplayMetrics getDisplayMetrics(Activity ctx) {
        DisplayMetrics displayMetrics = new DisplayMetrics();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && (ctx.isInMultiWindowMode() || ctx.isInPictureInPictureMode())){
            //For devices with free form/split screen, we need window size, not screen size.
            displayMetrics = ctx.getResources().getDisplayMetrics();
        }else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                ctx.getDisplay().getRealMetrics(displayMetrics);
            } else {
                 ctx.getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
            }
            if(!PREF_IGNORE_NOTCH){
                //Remove notch width when it isn't ignored.
                displayMetrics.widthPixels -= PREF_NOTCH_SIZE;
            }
        }
        return displayMetrics;
    }

    public static void setFullscreen(Activity act) {
        final View decorView = act.getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener (new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                        decorView.setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                    }
                }
            });
    }

    public static DisplayMetrics currentDisplayMetrics;

    public static void updateWindowSize(Activity ctx) {
        currentDisplayMetrics = getDisplayMetrics(ctx);

        CallbackBridge.physicalWidth = (int) (currentDisplayMetrics.widthPixels);
        CallbackBridge.physicalHeight = (int) (currentDisplayMetrics.heightPixels);
    }

    public static float dpToPx(float dp) {
        // 921600 = 1280 * 720, default scale
        // TODO better way to scaling
        float scaledDp = dp; // / DisplayMetrics.DENSITY_XHIGH * currentDisplayMetrics.densityDpi;
        return (scaledDp * currentDisplayMetrics.density);
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
/*
    public static void extractAssetFolder(Activity ctx, String path, String output) throws Exception {
        extractAssetFolder(ctx, path, output, false);
    }

    public static void extractAssetFolder(Activity ctx, String path, String output, boolean overwrite) throws Exception {
        AssetManager assetManager = ctx.getAssets();
        String assets[] = null;
        try {
            assets = assetManager.list(path);
            if (assets.length == 0) {
                Tools.copyAssetFile(ctx, path, output, overwrite);
            } else {
                File dir = new File(output, path);
                if (!dir.exists())
                    dir.mkdirs();
                for (String sub : assets) {
                    extractAssetFolder(ctx, path + "/" + sub, output, overwrite);
                }
            }
        } catch (Exception e) {
            showError(ctx, e);
        }
    }
*/
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
        
        Runnable runnable = new Runnable(){

            @Override
            public void run()
            {
                final String errMsg = showMore ? Log.getStackTraceString(e): e.getMessage();
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) ctx)
                    .setTitle(titleId)
                    .setMessage(errMsg)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){

                        @Override
                        public void onClick(DialogInterface p1, int p2)
                        {
                            if(exitIfOk) {
                                if (ctx instanceof BaseMainActivity) {
                                    BaseMainActivity.fullyExit();
                                } else if (ctx instanceof Activity) {
                                    ((Activity) ctx).finish();
                                }
                            }
                        }
                    })
                    .setNegativeButton(showMore ? R.string.error_show_less : R.string.error_show_more, new DialogInterface.OnClickListener(){

                        @Override
                        public void onClick(DialogInterface p1, int p2)
                        {
                            showError(ctx, titleId, e, exitIfOk, !showMore);
                        }
                    })
                    .setNeutralButton(android.R.string.copy, new DialogInterface.OnClickListener(){

                        @Override
                        public void onClick(DialogInterface p1, int p2)
                        {
                            android.content.ClipboardManager mgr = (android.content.ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
                            mgr.setPrimaryClip(ClipData.newPlainText("error", Log.getStackTraceString(e)));
                            if(exitIfOk) {
                                if (ctx instanceof BaseMainActivity) {
                                    BaseMainActivity.fullyExit();
                                } else {
                                    ((Activity) ctx).finish();
                                }
                            }
                        }
                    })
                    //.setNegativeButton("Report (not available)", null)
                    .setCancelable(!exitIfOk);
                try {
                    builder.show();
                } catch (Throwable th) {
                    th.printStackTrace();
                }
            }
        };

        if (ctx instanceof Activity) {
            ((Activity) ctx).runOnUiThread(runnable);
        } else {
            runnable.run();
        }
    }

    public static void dialogOnUiThread(final Activity ctx, final CharSequence title, final CharSequence message) {
        ctx.runOnUiThread(new Runnable(){

                @Override
                public void run() {
                    new AlertDialog.Builder(ctx)
                        .setTitle(title)
                        .setMessage(message)
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
                }
            });

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

    public static String[] generateLibClasspath(JMinecraftVersionList.Version info) {
        List<String> libDir = new ArrayList<String>();

        for (DependentLibrary libItem: info.libraries) {
            String[] libInfos = libItem.name.split(":");
            libDir.add(Tools.DIR_HOME_LIBRARY + "/" + Tools.artifactToPath(libInfos[0], libInfos[1], libInfos[2]));
        }
        return libDir.toArray(new String[0]);
    }

    public static JMinecraftVersionList.Version getVersionInfo(BaseLauncherActivity bla, String versionName) {
        try {
            JMinecraftVersionList.Version customVer = Tools.GLOBAL_GSON.fromJson(read(DIR_HOME_VERSION + "/" + versionName + "/" + versionName + ".json"), JMinecraftVersionList.Version.class);
            for (DependentLibrary lib : customVer.libraries) {
                if (lib.name.startsWith(LIBNAME_OPTIFINE)) {
                    customVer.optifineLib = lib;
                }
            }
            if (customVer.inheritsFrom == null || customVer.inheritsFrom.equals(customVer.id)) {
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
                inheritsVer.inheritsFrom = inheritsVer.id;
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
        String out = "";
        int len;
        byte[] buf = new byte[512];
        while((len = inputStream.read(buf))!=-1) {
            out += new String(buf,0,len,charset);
        }
        return out;
    }

    public static File lastFileModified(String dir) {
        File fl = new File(dir);

        File[] files = fl.listFiles(new FileFilter() {          
                public boolean accept(File file) {
                    return file.isFile();
                }
            });

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
        String out = "";
        int len;
        byte[] buf = new byte[512];
        while((len = is.read(buf))!=-1) {
            out += new String(buf,0,len);
        }
        return out;
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
    public static class ZipTool
    {
        private ZipTool(){}
        public static void zip(List<File> files, File zipFile) throws IOException {
            final int BUFFER_SIZE = 2048;

            BufferedInputStream origin = null;
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)));

            try {
                byte data[] = new byte[BUFFER_SIZE];

                for (File file : files) {
                    FileInputStream fileInputStream = new FileInputStream( file );

                    origin = new BufferedInputStream(fileInputStream, BUFFER_SIZE);

                    try {
                        ZipEntry entry = new ZipEntry(file.getName());

                        out.putNextEntry(entry);

                        int count;
                        while ((count = origin.read(data, 0, BUFFER_SIZE)) != -1) {
                            out.write(data, 0, count);
                        }
                    }
                    finally {
                        origin.close();
                    }
                }
            } finally {
                out.close();
            }
        }
        public static void unzip(File zipFile, File targetDirectory) throws IOException {
            final int BUFFER_SIZE = 1024;
            ZipInputStream zis = new ZipInputStream(
                new BufferedInputStream(new FileInputStream(zipFile)));
            try {
                ZipEntry ze;
                int count;
                byte[] buffer = new byte[BUFFER_SIZE];
                while ((ze = zis.getNextEntry()) != null) {
                    File file = new File(targetDirectory, ze.getName());
                    File dir = ze.isDirectory() ? file : file.getParentFile();
                    if (!dir.isDirectory() && !dir.mkdirs())
                        throw new FileNotFoundException("Failed to ensure directory: " +
                                                        dir.getAbsolutePath());
                    if (ze.isDirectory())
                        continue;
                    FileOutputStream fout = new FileOutputStream(file);
                    try {
                        while ((count = zis.read(buffer)) != -1)
                            fout.write(buffer, 0, count);
                    } finally {
                        fout.close();
                    }
                    /* if time should be restored as well
                     long time = ze.getTime();
                     if (time > 0)
                     file.setLastModified(time);
                     */
                }
            } finally {
                zis.close();
            }
        }
    }

    public static void ignoreNotch(boolean shouldIgnore, Activity ctx){
        if (Build.VERSION.SDK_INT >= P) {
            if (shouldIgnore) {
                ctx.getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            } else {
                ctx.getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER;
            }
            ctx.getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
            Tools.updateWindowSize(ctx);
        }
    }

}
