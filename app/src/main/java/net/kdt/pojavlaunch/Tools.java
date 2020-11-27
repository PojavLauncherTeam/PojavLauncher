package net.kdt.pojavlaunch;

import android.app.*;
import android.content.*;
import android.content.res.*;
import android.net.*;
import android.os.*;
import android.system.*;
import android.util.*;
import com.google.gson.*;
import com.oracle.dalvik.*;
import java.io.*;
import java.lang.reflect.*;
import java.nio.charset.*;
import java.util.*;
import java.util.zip.*;
import net.kdt.pojavlaunch.prefs.*;
import net.kdt.pojavlaunch.utils.*;
import net.kdt.pojavlaunch.value.*;
import org.lwjgl.glfw.*;
import android.view.*;

public final class Tools
{
    public static final boolean enableDevFeatures = BuildConfig.DEBUG;

    public static String APP_NAME = "null";
    public static final String MAIN_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/games/.minecraft";
    public static final String ASSETS_PATH = MAIN_PATH + "/assets";
    public static final String CTRLMAP_PATH = MAIN_PATH + "/controlmap";
    public static final String CTRLDEF_FILE = MAIN_PATH + "/controlmap/default.json";

    public static final Gson GLOBAL_GSON = new GsonBuilder().setPrettyPrinting().create();
    
    public static final String mhomeUrl = "https://pojavlauncherteam.github.io/PojavLauncher";
    public static int usingVerCode = 1;
    public static String usingVerName = "3.2.0";
    public static String datapath = "/data/data/net.kdt.pojavlaunch";
    public static String worksDir = datapath + "/app_working_dir";
    public static String currentArch;

    // New since 3.0.0
    public static String homeJreDir = datapath + "/jre_runtime";
    public static String homeJreLib = "lib";

    // New since 2.4.2
    public static final String versnDir = MAIN_PATH + "/versions";
    public static final String libraries = MAIN_PATH + "/libraries";
    public static final String optifineDir = MAIN_PATH + "/optifine";

    public static final String crashPath = MAIN_PATH + "/crash-reports";
    public static String mpProfiles = datapath + "/Users";

    public static final String optifineLib = "optifine:OptiFine";

    private static int exitCode = 0;
    public static void launchMinecraft(final LoggableActivity ctx, MCProfile.Builder profile, JMinecraftVersionList.Version versionInfo) throws Throwable {
        String[] launchArgs = getMinecraftArgs(profile, versionInfo);

        // ctx.appendlnToLog("Minecraft Args: " + Arrays.toString(launchArgs));

        String launchClassPath = generateLaunchClassPath(profile.getVersion());
        System.out.println("Java Classpath: " + launchClassPath);

        List<String> javaArgList = new ArrayList<String>();
        
        javaArgList.add("-cp");
        javaArgList.add(getLWJGL3ClassPath() + ":" + launchClassPath);

        javaArgList.add(versionInfo.mainClass);
        javaArgList.addAll(Arrays.asList(launchArgs));

        launchJavaVM(ctx, javaArgList);
    }
    
    public static void launchJavaVM(final LoggableActivity ctx, final List<String> args) throws Throwable {
        JREUtils.relocateLibPath(ctx);
        ctx.appendlnToLog("LD_LIBRARY_PATH = " + JREUtils.LD_LIBRARY_PATH);
        
        List<String> javaArgList = new ArrayList<String>();
        javaArgList.add(Tools.homeJreDir + "/bin/java");
        getJavaArgs(ctx, javaArgList);
        javaArgList.addAll(args);
        
        JREUtils.setJavaEnvironment(ctx, null);
        JREUtils.initJavaRuntime();
        JREUtils.chdir(Tools.MAIN_PATH);

        if (new File(Tools.MAIN_PATH, "strace.txt").exists()) {
            startStrace(android.os.Process.myTid());
        }

        exitCode = VMLauncher.launchJVM(javaArgList.toArray(new String[0]));
        ctx.appendlnToLog("Java Exit code: " + exitCode);
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

    public static void getJavaArgs(Context ctx, List<String> javaArgList) {
        List<String> overrideableArgList = new ArrayList<String>();

        overrideableArgList.add("-Djava.home=" + Tools.homeJreDir);
        overrideableArgList.add("-Djava.io.tmpdir=" + ctx.getCacheDir().getAbsolutePath());
        // overrideableArgList.add("-Djava.library.path=" + JREUtils.LD_LIBRARY_PATH);
        overrideableArgList.add("-Duser.home=" + new File(Tools.MAIN_PATH).getParent());
        overrideableArgList.add("-Duser.language=" + System.getProperty("user.language"));
        // overrideableArgList.add("-Duser.timezone=GMT");

        // Should be compatible?
        // overrideableArgList.add("-Dos.name=Android");
        overrideableArgList.add("-Dos.name=Linux");
        overrideableArgList.add("-Dos.version=Android-" + Build.VERSION.RELEASE);

        // javaArgList.add("-Dorg.lwjgl.libname=liblwjgl3.so");
        // javaArgList.add("-Dorg.lwjgl.system.jemalloc.libname=libjemalloc.so");
        overrideableArgList.add("-Dorg.lwjgl.opengl.libname=libgl04es.so");
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
        
        // Override args
        for (String argOverride : LauncherPreferences.PREF_CUSTOM_JAVA_ARGS.split(" ")) {
            for (int i = 0; i < overrideableArgList.size(); i++) {
                String arg = overrideableArgList.get(i);
                if (arg.startsWith("-D") && argOverride.startsWith(arg.substring(0, arg.indexOf('=') + 1))) {
                    overrideableArgList.set(i, argOverride);
                    break;
                } else if (i+1 == overrideableArgList.size()) {
                    javaArgList.add(argOverride);
                }
            }
        }

        javaArgList.addAll(overrideableArgList);
    }

    public static String[] getMinecraftArgs(MCProfile.Builder profile, JMinecraftVersionList.Version versionInfo) {
        String username = profile.getUsername();
        String versionName = versionInfo.id;
        if (versionInfo.inheritsFrom != null) {
            versionName = versionInfo.inheritsFrom;
        }
        
        String userType = "mojang";

        File gameDir = new File(Tools.MAIN_PATH);
        gameDir.mkdirs();

        Map<String, String> varArgMap = new ArrayMap<String, String>();
        varArgMap.put("auth_access_token", profile.getAccessToken());
        varArgMap.put("auth_player_name", username);
        varArgMap.put("auth_uuid", profile.getProfileID());
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

    private static void startStrace(int pid) throws Exception {
        String[] straceArgs = new String[] {"/system/bin/strace",
            "-o", new File(Tools.MAIN_PATH, "strace.txt").getAbsolutePath(), "-f", "-p", "" + pid};
        System.out.println("strace args: " + Arrays.toString(straceArgs));
        Runtime.getRuntime().exec(straceArgs);
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
        return versnDir + "/" + version + "/" + version + ".jar";
    }

    private static String getLWJGL3ClassPath() {
        StringBuilder libStr = new StringBuilder();
        File lwjgl3Folder = new File(Tools.MAIN_PATH, "lwjgl3");
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
    public static String generateLaunchClassPath(String version) {
        StringBuilder libStr = new StringBuilder(); //versnDir + "/" + version + "/" + version + ".jar:";

        JMinecraftVersionList.Version info = getVersionInfo(version);
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
            libStr.append(getPatchedFile(version));
        }
        for (String perJar : classpath) {
            if (!new File(perJar).exists()) {
                System.out.println("ClassPathGen: ignored non-exists file: " + perJar);
                continue;
            }
            libStr.append((isClientFirst ? ":" : "") + perJar + (!isClientFirst ? ":" : ""));
        }
        if (!isClientFirst) {
            libStr.append(getPatchedFile(version));
        }

        return libStr.toString();
    }

    public static DisplayMetrics getDisplayMetrics(Activity ctx) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ctx.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
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
        CallbackBridge.windowWidth = currentDisplayMetrics.widthPixels;
        CallbackBridge.windowHeight = currentDisplayMetrics.heightPixels;
    }

    public static float dpToPx(float dp) {
        // 921600 = 1280 * 720, default scale
        // TODO better way to scaling
        float scaledDp = dp; // / DisplayMetrics.DENSITY_XHIGH * currentDisplayMetrics.densityDpi;
        return (scaledDp * currentDisplayMetrics.density);
    }

    public static void copyAssetFile(Context ctx, String fileName, String output, boolean overwrite) throws Exception
    {
        copyAssetFile(ctx, fileName, output, fileName, overwrite);
    }

    public static void copyAssetFile(Context ctx, String fileName, String output, String outputName, boolean overwrite) throws Exception
    {
        try {
            File file = new File(output);
            if(!file.exists()) {
                file.mkdirs();
            }
            File file2 = new File(output, outputName);
            if(!file2.exists() || overwrite){
                write(file2.getAbsolutePath(), loadFromAssetToByte(ctx, fileName));
            }
        } catch (Throwable th) {
            throw new RuntimeException("Unable to copy " + fileName + " to " + output + "/" + outputName, th);
        }
    }

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
        Runnable runnable = new Runnable(){

            @Override
            public void run()
            {
                final String errMsg = showMore ? Log.getStackTraceString(e): e.getMessage();
                new AlertDialog.Builder((Context) ctx)
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
                    .setCancelable(!exitIfOk)
                    .show();
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
            libDir.add(Tools.libraries + "/" + Tools.artifactToPath(libInfos[0], libInfos[1], libInfos[2]));
        }
        return libDir.toArray(new String[0]);
    }

    public static JMinecraftVersionList.Version getVersionInfo(String versionName) {
        try {
            JMinecraftVersionList.Version customVer = Tools.GLOBAL_GSON.fromJson(read(versnDir + "/" + versionName + "/" + versionName + ".json"), JMinecraftVersionList.Version.class);
            for (DependentLibrary lib : customVer.libraries) {
                if (lib.name.startsWith(optifineLib)) {
                    customVer.optifineLib = lib;
                }
            }
            if (customVer.inheritsFrom == null || customVer.inheritsFrom.equals(customVer.id)) {
                return customVer;
            } else {
                JMinecraftVersionList.Version inheritsVer = Tools.GLOBAL_GSON.fromJson(read(versnDir + "/" + customVer.inheritsFrom + "/" + customVer.inheritsFrom + ".json"), JMinecraftVersionList.Version.class);
                inheritsVer.inheritsFrom = inheritsVer.id;
                
                insertSafety(inheritsVer, customVer,
                             "assetIndex", "assets", "id",
                             "mainClass", "minecraftArguments",
                             "optifineLib", "releaseTime", "time", "type"
                             );

                List<DependentLibrary> libList = new ArrayList<DependentLibrary>(Arrays.asList(inheritsVer.libraries));
                try {
                    for (DependentLibrary lib : customVer.libraries) {
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
                System.err.println("Unable to insert " + key + "=" + value);
                th.printStackTrace();
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

    public static void deleteRecursive(File fileOrDirectory) {
        try {
            if (fileOrDirectory.isDirectory()) {
                for (File child : fileOrDirectory.listFiles()) {
                    deleteRecursive(child);
                }
            }
        } finally {
            fileOrDirectory.delete();
        }
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

        FileOutputStream fos = new FileOutputStream(path);
        fos.write(content);
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
}
