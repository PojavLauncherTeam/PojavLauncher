package net.kdt.pojavlaunch;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.P;
import static net.kdt.pojavlaunch.PojavApplication.sExecutorService;
import static net.kdt.pojavlaunch.prefs.LauncherPreferences.PREF_IGNORE_NOTCH;
import static net.kdt.pojavlaunch.prefs.LauncherPreferences.PREF_NOTCH_SIZE;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.DocumentsContract;
import android.provider.OpenableColumns;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.kdt.pojavlaunch.extra.ExtraConstants;
import net.kdt.pojavlaunch.extra.ExtraCore;
import net.kdt.pojavlaunch.multirt.MultiRTUtils;
import net.kdt.pojavlaunch.multirt.Runtime;
import net.kdt.pojavlaunch.prefs.LauncherPreferences;
import net.kdt.pojavlaunch.utils.DownloadUtils;
import net.kdt.pojavlaunch.utils.JREUtils;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.lwjgl.glfw.CallbackBridge;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@SuppressWarnings("IOStreamConstructor")
public final class Tools {
    public static final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());
    public static String APP_NAME = "null";


    public static final String RT4_MAIN_CLASS = "rt4.client";
    public static final Gson GLOBAL_GSON = new GsonBuilder().setPrettyPrinting().create();

    public static final String URL_HOME = "https://pojavlauncherteam.github.io";
    public static String NATIVE_LIB_DIR;
    public static String DIR_DATA; //Initialized later to get context
    public static File DIR_CACHE;
    public static String MULTIRT_HOME;
    public static String LOCAL_RENDERER = null;
    public static int DEVICE_ARCHITECTURE;
    public static final String LAUNCHERPROFILES_RTPREFIX = "pojav://";

    // New since 3.3.1
    public static String DIR_ACCOUNT_NEW;
    public static String DIR_GAME_HOME = Environment.getExternalStorageDirectory().getAbsolutePath() + "/games/PojavLauncher";
    public static String DIR_GAME_NEW;

    // New since 3.0.0
    public static String DIRNAME_HOME_JRE = "lib";

    // New since 2.4.2
    public static String DIR_HOME_VERSION;
    public static String DIR_HOME_LIBRARY;

    public static String DIR_HOME_CRASH;

    public static String ASSETS_PATH;
    public static String OBSOLETE_RESOURCES_PATH;
    public static String CTRLMAP_PATH;
    public static String CTRLDEF_FILE;
    public static final int RUN_MOD_INSTALLER = 2050;


    private static File getPojavStorageRoot(Context ctx) {
        if(SDK_INT >= 29) {
            return ctx.getExternalFilesDir(null);
        }else{
            return new File(Environment.getExternalStorageDirectory(),"games/PojavLauncher");
        }
    }

    /**
     * Checks if the Pojav's storage root is accessible and read-writable
     * @param context context to get the storage root if it's not set yet
     * @return true if storage is fine, false if storage is not accessible
     */
    public static boolean checkStorageRoot(Context context) {
        File externalFilesDir = DIR_GAME_HOME  == null ? Tools.getPojavStorageRoot(context) : new File(DIR_GAME_HOME);
        //externalFilesDir == null when the storage is not mounted if it was obtained with the context call
        return externalFilesDir != null && Environment.getExternalStorageState(externalFilesDir).equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * Since some constant requires the use of the Context object
     * You can call this function to initialize them.
     * Any value (in)directly dependant on DIR_DATA should be set only here.
     */
    public static void initContextConstants(Context ctx){
        DIR_CACHE = ctx.getCacheDir();
        DIR_DATA = ctx.getFilesDir().getParent();
        MULTIRT_HOME = DIR_DATA+"/runtimes";
        DIR_GAME_HOME = getPojavStorageRoot(ctx).getAbsolutePath();
        DIR_GAME_NEW = DIR_GAME_HOME + "/.minecraft";
        DIR_HOME_VERSION = DIR_GAME_NEW + "/versions";
        DIR_HOME_LIBRARY = DIR_GAME_NEW + "/libraries";
        DIR_HOME_CRASH = DIR_GAME_NEW + "/crash-reports";
        ASSETS_PATH = DIR_GAME_NEW + "/assets";
        OBSOLETE_RESOURCES_PATH= DIR_GAME_NEW + "/resources";
        CTRLMAP_PATH = DIR_GAME_HOME + "/controlmap";
        CTRLDEF_FILE = DIR_GAME_HOME + "/controlmap/default.json";
        NATIVE_LIB_DIR = ctx.getApplicationInfo().nativeLibraryDir;
    }


    public static void launchGLJRE(final Activity activity) throws Throwable {

        Runtime runtime = MultiRTUtils.forceReread("Internal");
        File gamedir = new File(Tools.DIR_DATA);

        ExtraCore.setValue(ExtraConstants.OPEN_GL_VERSION, "2");

        List<String> javaArgList = new ArrayList<>();
        javaArgList.add("-Dorg.lwjgl.util.NoChecks=true");

        getCacioJavaArgs(javaArgList, runtime.javaVersion == 8);

        javaArgList.add("-DconfigFile="+Tools.DIR_DATA + "/config.json");
        javaArgList.add("-DpluginDir="+ Tools.DIR_DATA + "/plugins/");
        javaArgList.add("-DclientHomeOverride="+gamedir);
        javaArgList.add("-DglfwWidth="+CallbackBridge.windowWidth);
        javaArgList.add("-DglfwHeight="+CallbackBridge.windowHeight);
        javaArgList.add("-cp");
        javaArgList.add(getLWJGL3ClassPath()+":"+Tools.DIR_DATA+"/rt4.jar");
        javaArgList.add(RT4_MAIN_CLASS);
        String args = LauncherPreferences.PREF_CUSTOM_JAVA_ARGS;
        JREUtils.launchJavaVM(activity, runtime, gamedir, javaArgList, args);
    }


    public static void buildNotificationChannel(Context context){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return;
        NotificationChannel channel = new NotificationChannel(
                context.getString(R.string.notif_channel_id),
                context.getString(R.string.notif_channel_name), NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        manager.createNotificationChannel(channel);
    }
    public static void disableSplash(File dir) {
        File configDir = new File(dir, "config");
        if(configDir.exists() || configDir.mkdirs()) {
            File forgeSplashFile = new File(dir, "config/splash.properties");
            String forgeSplashContent = "enabled=true";
            try {
                if (forgeSplashFile.exists()) {
                    forgeSplashContent = Tools.read(forgeSplashFile.getAbsolutePath());
                }
                if (forgeSplashContent.contains("enabled=true")) {
                    Tools.write(forgeSplashFile.getAbsolutePath(),
                            forgeSplashContent.replace("enabled=true", "enabled=false"));
                }
            } catch (IOException e) {
                Log.w(Tools.APP_NAME, "Could not disable Forge 1.12.2 and below splash screen!", e);
            }
        } else {
            Log.w(Tools.APP_NAME, "Failed to create the configuration directory");
        }
    }

    public static void getCacioJavaArgs(List<String> javaArgList, boolean isJava8) {
        // Caciocavallo config AWT-enabled version
        javaArgList.add("-Djava.awt.headless=false");
        javaArgList.add("-Dcacio.managed.screensize=" + AWTCanvasView.AWT_CANVAS_WIDTH + "x" + AWTCanvasView.AWT_CANVAS_HEIGHT);
        javaArgList.add("-Dcacio.font.fontmanager=sun.awt.X11FontManager");
        javaArgList.add("-Dcacio.font.fontscaler=sun.font.FreetypeFontScaler");
        javaArgList.add("-Dswing.defaultlaf=javax.swing.plaf.metal.MetalLookAndFeel");
        if (isJava8) {
            javaArgList.add("-Dawt.toolkit=net.java.openjdk.cacio.ctc.CTCToolkit");
            javaArgList.add("-Djava.awt.graphicsenv=net.java.openjdk.cacio.ctc.CTCGraphicsEnvironment");
        } else {
            javaArgList.add("-Dawt.toolkit=com.github.caciocavallosilano.cacio.ctc.CTCToolkit");
            javaArgList.add("-Djava.awt.graphicsenv=com.github.caciocavallosilano.cacio.ctc.CTCGraphicsEnvironment");
            javaArgList.add("-Djava.system.class.loader=com.github.caciocavallosilano.cacio.ctc.CTCPreloadClassLoader");

            javaArgList.add("--add-exports=java.desktop/java.awt=ALL-UNNAMED");
            javaArgList.add("--add-exports=java.desktop/java.awt.peer=ALL-UNNAMED");
            javaArgList.add("--add-exports=java.desktop/sun.awt.image=ALL-UNNAMED");
            javaArgList.add("--add-exports=java.desktop/sun.java2d=ALL-UNNAMED");
            javaArgList.add("--add-exports=java.desktop/java.awt.dnd.peer=ALL-UNNAMED");
            javaArgList.add("--add-exports=java.desktop/sun.awt=ALL-UNNAMED");
            javaArgList.add("--add-exports=java.desktop/sun.awt.event=ALL-UNNAMED");
            javaArgList.add("--add-exports=java.desktop/sun.awt.datatransfer=ALL-UNNAMED");
            javaArgList.add("--add-exports=java.desktop/sun.font=ALL-UNNAMED");
            javaArgList.add("--add-exports=java.base/sun.security.action=ALL-UNNAMED");
            javaArgList.add("--add-opens=java.base/java.util=ALL-UNNAMED");
            javaArgList.add("--add-opens=java.desktop/java.awt=ALL-UNNAMED");
            javaArgList.add("--add-opens=java.desktop/sun.font=ALL-UNNAMED");
            javaArgList.add("--add-opens=java.desktop/sun.java2d=ALL-UNNAMED");
            javaArgList.add("--add-opens=java.base/java.lang.reflect=ALL-UNNAMED");

            // Opens the java.net package to Arc DNS injector on Java 9+
            javaArgList.add("--add-opens=java.base/java.net=ALL-UNNAMED");
        }

        StringBuilder cacioClasspath = new StringBuilder();
        cacioClasspath.append("-Xbootclasspath/").append(isJava8 ? "p" : "a");
        File cacioDir = new File(DIR_GAME_HOME + "/caciocavallo" + (isJava8 ? "" : "17"));
        File[] cacioFiles = cacioDir.listFiles();
        if (cacioFiles != null) {
            for (File file : cacioFiles) {
                if (file.getName().endsWith(".jar")) {
                    cacioClasspath.append(":").append(file.getAbsolutePath());
                }
            }
        }
        javaArgList.add(cacioClasspath.toString());
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
        List<String> strList = new ArrayList<>();
        for (String arg : argStr.split(" ")) {
            if (!arg.isEmpty()) {
                strList.add(arg);
            }
        }
        //strList.add("--fullscreen");
        return strList.toArray(new String[0]);
    }

    public static String getPatchedFile(String version) {
        return DIR_HOME_VERSION + "/" + version + "/" + version + ".jar";
    }

    private static String getLWJGL3ClassPath() {
        StringBuilder libStr = new StringBuilder();
        File lwjgl3Folder = new File(Tools.DIR_GAME_HOME, "lwjgl3");
        File[] lwjgl3Files = lwjgl3Folder.listFiles();
        if (lwjgl3Files != null) {
            for (File file: lwjgl3Files) {
                if (file.getName().endsWith(".jar")) {
                    libStr.append(file.getAbsolutePath()).append(":");
                }
            }
        }
        // Remove the ':' at the end
        libStr.setLength(libStr.length() - 1);
        return libStr.toString();
    }

    private final static boolean isClientFirst = false;

    public static DisplayMetrics getDisplayMetrics(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();

        if(SDK_INT >= Build.VERSION_CODES.N && (activity.isInMultiWindowMode() || activity.isInPictureInPictureMode())){
            //For devices with free form/split screen, we need window size, not screen size.
            displayMetrics = activity.getResources().getDisplayMetrics();
        }else{
            if (SDK_INT >= Build.VERSION_CODES.R) {
                activity.getDisplay().getRealMetrics(displayMetrics);
            } else { // Removed the clause for devices with unofficial notch support, since it also ruins all devices with virtual nav bars before P
                activity.getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
            }
            if(!PREF_IGNORE_NOTCH){
                //Remove notch width when it isn't ignored.
                if(activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                    displayMetrics.heightPixels -= PREF_NOTCH_SIZE;
                else
                    displayMetrics.widthPixels -= PREF_NOTCH_SIZE;
            }
        }
        currentDisplayMetrics = displayMetrics;
        return displayMetrics;
    }

    public static void setFullscreen(Activity activity, boolean fullscreen) {
        final View decorView = activity.getWindow().getDecorView();
        View.OnSystemUiVisibilityChangeListener visibilityChangeListener = visibility -> {
            if(fullscreen){
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                }
            }else{
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            }

        };
        decorView.setOnSystemUiVisibilityChangeListener(visibilityChangeListener);
        visibilityChangeListener.onSystemUiVisibilityChange(decorView.getSystemUiVisibility()); //call it once since the UI state may not change after the call, so the activity wont become fullscreen
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

    public static void copyAssetFile(Context ctx, String fileName, String output, String outputName, boolean overwrite) throws IOException {
        File parentFolder = new File(output);
        if(!parentFolder.exists() && !parentFolder.mkdirs()) {
            throw new IOException("Failed to create parent directory");
        }
        File destinationFile = new File(output, outputName);
        if(!destinationFile.exists() || overwrite){
            try(InputStream inputStream = ctx.getAssets().open(fileName)) {
                try (OutputStream outputStream = new FileOutputStream(destinationFile)){
                    IOUtils.copy(inputStream, outputStream);
                }
            }
        }
    }

    public static String printToString(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        throwable.printStackTrace(printWriter);
        printWriter.close();
        return stringWriter.toString();
    }

    public static void showError(Context ctx, Throwable e) {
        showError(ctx, e, false);
    }

    public static void showError(final Context ctx, final Throwable e, final boolean exitIfOk) {
        showError(ctx, R.string.global_error, null ,e, exitIfOk, false);
    }
    public static void showError(final Context ctx, final int rolledMessage, final Throwable e) {
        showError(ctx, R.string.global_error, ctx.getString(rolledMessage), e, false, false);
    }
    public static void showError(final Context ctx, final String rolledMessage, final Throwable e) {
        showError(ctx, R.string.global_error, rolledMessage, e, false, false);
    }

    public static void showError(final Context ctx, final int titleId, final Throwable e, final boolean exitIfOk) {
        showError(ctx, titleId, null, e, exitIfOk, false);
    }

    private static void showError(final Context ctx, final int titleId, final String rolledMessage, final Throwable e, final boolean exitIfOk, final boolean showMore) {
        e.printStackTrace();

        Runnable runnable = () -> {
            final String errMsg = showMore ? printToString(e) : rolledMessage != null ? rolledMessage : e.getMessage();
            AlertDialog.Builder builder = new AlertDialog.Builder(ctx)
                    .setTitle(titleId)
                    .setMessage(errMsg)
                    .setPositiveButton(android.R.string.ok, (p1, p2) -> {
                        if(exitIfOk) {
                            if (ctx instanceof MainActivity) {
                                MainActivity.fullyExit();
                            } else if (ctx instanceof Activity) {
                                ((Activity) ctx).finish();
                            }
                        }
                    })
                    .setNegativeButton(showMore ? R.string.error_show_less : R.string.error_show_more, (p1, p2) -> showError(ctx, titleId, rolledMessage, e, exitIfOk, !showMore))
                    .setNeutralButton(android.R.string.copy, (p1, p2) -> {
                        ClipboardManager mgr = (ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
                        mgr.setPrimaryClip(ClipData.newPlainText("error", Log.getStackTraceString(e)));
                        if(exitIfOk) {
                            if (ctx instanceof MainActivity) {
                                MainActivity.fullyExit();
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
        activity.runOnUiThread(()->dialog(activity, title, message));
    }

    public static void dialog(final Context context, final CharSequence title, final CharSequence message) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    public static void openURL(Activity act, String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        act.startActivity(browserIntent);
    }
    public static String read(InputStream is) throws IOException {
        String readResult = IOUtils.toString(is, StandardCharsets.UTF_8);
        is.close();
        return readResult;
    }

    public static String read(String path) throws IOException {
        return read(new FileInputStream(path));
    }

    public static void write(String path, String content) throws IOException {
        File file = new File(path);
        File parent = file.getParentFile();
        if(parent != null && !parent.exists()) {
            if(!parent.mkdirs()) throw new IOException("Failed to create parent directory");
        }
        try(FileOutputStream outStream = new FileOutputStream(file)) {
            IOUtils.write(content, outStream);
        }
    }

    public static void downloadFile(String urlInput, String nameOutput) throws IOException {
        File file = new File(nameOutput);
        DownloadUtils.downloadFile(urlInput, file);
    }

    public interface DownloaderFeedback {
        void updateProgress(int curr, int max);
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
                }
            } finally {
                zis.close();
            }
        }
    }


    public static boolean compareSHA1(File f, String sourceSHA) {
        try {
            String sha1_dst;
            try (InputStream is = new FileInputStream(f)) {
                sha1_dst = new String(Hex.encodeHex(org.apache.commons.codec.digest.DigestUtils.sha1(is)));
            }
            if(sourceSHA != null) {
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
        Cursor c = ctx.getContentResolver().query(uri, null, null, null, null);
        if(c == null) return uri.getLastPathSegment(); // idk myself but it happens on asus file manager
        c.moveToFirst();
        int columnIndex = c.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        if(columnIndex == -1) return uri.getLastPathSegment();
        String fileName = c.getString(columnIndex);
        c.close();
        return fileName;
    }


    /** Remove the current fragment */
    public static void removeCurrentFragment(FragmentActivity fragmentActivity){
        fragmentActivity.getSupportFragmentManager().popBackStackImmediate();
    }

    public static void installMod(Activity activity, boolean customJavaArgs) {
        if (MultiRTUtils.getExactJreName(8) == null) {
            Toast.makeText(activity, R.string.multirt_nojava8rt, Toast.LENGTH_LONG).show();
            return;
        }

        if(!customJavaArgs){ // Launch the intent to get the jar file
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("jar");
            if(mimeType == null) mimeType = "*/*";
            intent.setType(mimeType);
            activity.startActivityForResult(intent, RUN_MOD_INSTALLER);
            return;
        }

        // install mods with custom arguments
        final EditText editText = new EditText(activity);
        editText.setSingleLine();
        editText.setHint("-jar/-cp /path/to/file.jar ...");

        AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                .setTitle(R.string.alerttitle_installmod)
                .setNegativeButton(android.R.string.cancel, null)
                .setView(editText)
                .setPositiveButton(android.R.string.ok, (di, i) -> {
                    Intent intent = new Intent(activity, JavaGUILauncherActivity.class);
                    intent.putExtra("skipDetectMod", true);
                    intent.putExtra("javaArgs", editText.getText().toString());
                    activity.startActivity(intent);
                });
        builder.show();
    }

    /** Display and return a progress dialog, instructing to wait */
    private static ProgressDialog getWaitingDialog(Context ctx){
        final ProgressDialog barrier = new ProgressDialog(ctx);
        barrier.setMessage(ctx.getString(R.string.global_waiting));
        barrier.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        barrier.setCancelable(false);
        barrier.show();

        return barrier;
    }

    /** Copy the mod file, and launch the mod installer activity */
    public static void launchModInstaller(Activity activity, @NonNull Intent data){
        final ProgressDialog alertDialog = getWaitingDialog(activity);

        final Uri uri = data.getData();
        alertDialog.setMessage(activity.getString(R.string.multirt_progress_caching));
        sExecutorService.execute(() -> {
            try {
                final String name = getFileName(activity, uri);
                final File modInstallerFile = new File(Tools.DIR_CACHE, name);
                FileOutputStream fos = new FileOutputStream(modInstallerFile);
                InputStream input = activity.getContentResolver().openInputStream(uri);
                IOUtils.copy(input, fos);
                input.close();
                fos.close();
                activity.runOnUiThread(() -> {
                    alertDialog.dismiss();
                    Intent intent = new Intent(activity, JavaGUILauncherActivity.class);
                    intent.putExtra("modFile", modInstallerFile);
                    activity.startActivity(intent);
                });
            }catch(IOException e) {
                Tools.showError(activity, e);
            }
        });
    }


    public static void installRuntimeFromUri(Activity activity, Uri uri){
        sExecutorService.execute(() -> {
            try {
                String name = getFileName(activity, uri);
                MultiRTUtils.installRuntimeNamed(
                        NATIVE_LIB_DIR,
                        activity.getContentResolver().openInputStream(uri),
                        name);

                MultiRTUtils.postPrepare(name);
            } catch (IOException e) {
                Tools.showError(activity, e);
            }
        });
    }

    public static String extractUntilCharacter(String input, String whatFor, char terminator) {
        int whatForStart = input.indexOf(whatFor);
        if(whatForStart == -1) return null;
        whatForStart += whatFor.length();
        int terminatorIndex = input.indexOf(terminator, whatForStart);
        if(terminatorIndex == -1) return null;
        return input.substring(whatForStart, terminatorIndex);
    }

    public static boolean isValidString(String string) {
        return string != null && !string.isEmpty();
    }

    public static String getRuntimeName(String prefixedName) {
        if(prefixedName == null) return prefixedName;
        if(!prefixedName.startsWith(Tools.LAUNCHERPROFILES_RTPREFIX)) return null;
        return prefixedName.substring(Tools.LAUNCHERPROFILES_RTPREFIX.length());
    }

    public static void runOnUiThread(Runnable runnable) {
        MAIN_HANDLER.post(runnable);
    }

    /** Triggers the share intent chooser, with the latestlog file attached to it */
    public static void shareLog(Context context){
        Uri contentUri = DocumentsContract.buildDocumentUri(context.getString(R.string.storageProviderAuthorities), Tools.DIR_GAME_HOME + "/latestlog.txt");

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shareIntent.setType("text/plain");

        Intent sendIntent = Intent.createChooser(shareIntent, "latestlog.txt");
        context.startActivity(sendIntent);
    }
}
