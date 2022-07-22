package net.kdt.pojavlaunch.prefs;

import android.content.*;
import net.kdt.pojavlaunch.*;
import net.kdt.pojavlaunch.multirt.MultiRTUtils;
import net.kdt.pojavlaunch.utils.JREUtils;

public class LauncherPreferences {
    public static final String PREF_KEY_CURRENT_PROFILE = "currentProfile";
    public static SharedPreferences DEFAULT_PREF;
    public static String PREF_RENDERER = "opengles2";

	public static boolean PREF_VERTYPE_RELEASE = true;
	public static boolean PREF_VERTYPE_SNAPSHOT = false;
	public static boolean PREF_VERTYPE_OLDALPHA = false;
	public static boolean PREF_VERTYPE_OLDBETA = false;
	public static boolean PREF_HIDE_SIDEBAR = false;
	public static boolean PREF_IGNORE_NOTCH = false;
	public static int PREF_NOTCH_SIZE = 0;
	public static float PREF_BUTTONSIZE = 100f;
	public static float PREF_MOUSESCALE = 100f;
	public static int PREF_LONGPRESS_TRIGGER = 300;
	public static String PREF_DEFAULTCTRL_PATH = Tools.CTRLDEF_FILE;
	public static String PREF_CUSTOM_JAVA_ARGS;
    public static String PREF_LANGUAGE = "default";
    public static String PREF_VERSION_REPOS = "https://piston-meta.mojang.com/mc/game/version_manifest_v2.json";
    public static boolean PREF_CHECK_LIBRARY_SHA = true;
    public static boolean PREF_DISABLE_GESTURES = false;
    public static float PREF_MOUSESPEED = 1f;
    public static int PREF_RAM_ALLOCATION;
    public static String PREF_DEFAULT_RUNTIME;
    public static int PREF_CONTROL_TOP_OFFSET = 0;
    public static int PREF_CONTROL_RIGHT_OFFSET = 0;
    public static int PREF_CONTROL_BOTTOM_OFFSET = 0;
    public static int PREF_CONTROL_LEFT_OFFSET = 0;
    public static boolean PREF_SUSTAINED_PERFORMANCE = false;
    public static boolean PREF_VIRTUAL_MOUSE_START = false;
    public static boolean PREF_ARC_CAPES = false;
    public static boolean PREF_USE_ALTERNATE_SURFACE = true;
    public static int PREF_SCALE_FACTOR = 100;


    public static void loadPreferences(Context ctx) {
        //Required for the data folder.
        Tools.initContextConstants(ctx);

        PREF_RENDERER = DEFAULT_PREF.getString("renderer", "opengles2");

		PREF_BUTTONSIZE = DEFAULT_PREF.getInt("buttonscale", 100);
		PREF_MOUSESCALE = DEFAULT_PREF.getInt("mousescale", 100);
		PREF_MOUSESPEED = ((float)DEFAULT_PREF.getInt("mousespeed",100))/100f;
		PREF_HIDE_SIDEBAR = DEFAULT_PREF.getBoolean("hideSidebar", false);
		PREF_IGNORE_NOTCH = DEFAULT_PREF.getBoolean("ignoreNotch", false);
		PREF_VERTYPE_RELEASE = DEFAULT_PREF.getBoolean("vertype_release", true);
		PREF_VERTYPE_SNAPSHOT = DEFAULT_PREF.getBoolean("vertype_snapshot", false);
		PREF_VERTYPE_OLDALPHA = DEFAULT_PREF.getBoolean("vertype_oldalpha", false);
		PREF_VERTYPE_OLDBETA = DEFAULT_PREF.getBoolean("vertype_oldbeta", false);
		PREF_LONGPRESS_TRIGGER = DEFAULT_PREF.getInt("timeLongPressTrigger", 300);
		PREF_DEFAULTCTRL_PATH = DEFAULT_PREF.getString("defaultCtrl", Tools.CTRLDEF_FILE);
        PREF_LANGUAGE = DEFAULT_PREF.getString("language", "default");
        PREF_CHECK_LIBRARY_SHA = DEFAULT_PREF.getBoolean("checkLibraries",true);
        PREF_DISABLE_GESTURES = DEFAULT_PREF.getBoolean("disableGestures",false);
        PREF_RAM_ALLOCATION = DEFAULT_PREF.getInt("allocation", findBestRAMAllocation(ctx));
        PREF_CUSTOM_JAVA_ARGS = DEFAULT_PREF.getString("javaArgs", "");
        PREF_CONTROL_TOP_OFFSET = DEFAULT_PREF.getInt("controlTopOffset", 0);
        PREF_CONTROL_RIGHT_OFFSET = DEFAULT_PREF.getInt("controlRightOffset", 0);
        PREF_CONTROL_BOTTOM_OFFSET = DEFAULT_PREF.getInt("controlBottomOffset", 0);
        PREF_CONTROL_LEFT_OFFSET = DEFAULT_PREF.getInt("controlLeftOffset", 0);
        PREF_SUSTAINED_PERFORMANCE = DEFAULT_PREF.getBoolean("sustainedPerformance", false);
        PREF_VIRTUAL_MOUSE_START = DEFAULT_PREF.getBoolean("mouse_start", false);
        PREF_ARC_CAPES = DEFAULT_PREF.getBoolean("arc_capes",false);
        PREF_USE_ALTERNATE_SURFACE = DEFAULT_PREF.getBoolean("alternate_surface", false);
        PREF_SCALE_FACTOR = DEFAULT_PREF.getInt("resolutionRatio", 100);

/*
        if (PREF_CUSTOM_JAVA_ARGS.isEmpty()) {
            String DEFAULT_JAVA_ARGS = "";
                "-Xms" + (androidHeap > 800 ? 800 : androidHeap) + "m " +
                // (32bit) More than 800mb may make JVM not allocateable and crash
                "-Xmx" + (doubleAndroidHeap > 800 ? 800 : doubleAndroidHeap) + "m" +
                "-XX:+UseG1GC " +
                "-XX:+ParallelRefProcEnabled " +
                "-XX:MaxGCPauseMillis=200 " +
                "-XX:+UnlockExperimentalVMOptions " +
                "-XX:+AlwaysPreTouch " +
		"-XX:G1NewSizePercent=30 " +
		"-XX:G1MaxNewSizePercent=40 " +
		"-XX:G1HeapRegionSize=8M " +
		"-XX:G1ReservePercent=20 " +
		"-XX:G1HeapWastePercent=5 " +
	        "-XX:G1MixedGCCountTarget=4 " +
		"-XX:InitiatingHeapOccupancyPercent=15 " +
		"-XX:G1MixedGCLiveThresholdPercent=90 " +
		"-XX:G1RSetUpdatingPauseTimePercent=5 " +
		"-XX:SurvivorRatio=32 " +
		"-XX:+PerfDisableSharedMem " +
                "-XX:MaxTenuringThreshold=1";
            PREF_CUSTOM_JAVA_ARGS = DEFAULT_JAVA_ARGS;
            DEFAULT_PREF.edit().putString("javaArgs", DEFAULT_JAVA_ARGS).commit();
        }
*/

        String argLwjglLibname = "-Dorg.lwjgl.opengl.libname=";
        for (String arg : JREUtils.parseJavaArguments(PREF_CUSTOM_JAVA_ARGS)) {
            if (arg.startsWith(argLwjglLibname)) {
                // purge arg
                DEFAULT_PREF.edit().putString("javaArgs",
                    PREF_CUSTOM_JAVA_ARGS.replace(arg, "")).commit();
            }
        }
        if(DEFAULT_PREF.contains("defaultRuntime")) {
            PREF_DEFAULT_RUNTIME = DEFAULT_PREF.getString("defaultRuntime","");
        }else{
            if(MultiRTUtils.getRuntimes().size() < 1) {
                PREF_DEFAULT_RUNTIME = "";
                return;
            }
            PREF_DEFAULT_RUNTIME = MultiRTUtils.getRuntimes().get(0).name;
            LauncherPreferences.DEFAULT_PREF.edit().putString("defaultRuntime",LauncherPreferences.PREF_DEFAULT_RUNTIME).apply();
        }
    }

    /**
     * This functions aims at finding the best default RAM amount,
     * according to the RAM amount of the physical device.
     * Put not enough RAM ? Minecraft will lag and crash.
     * Put too much RAM ?
     * The GC will lag, android won't be able to breathe properly.
     * @param ctx Context needed to get the total memory of the device.
     * @return The best default value found.
     */
    private static int findBestRAMAllocation(Context ctx){
        int deviceRam = Tools.getTotalDeviceMemory(ctx);
        if (deviceRam < 1024) return 300;
        if (deviceRam < 1536) return 450;
        if (deviceRam < 2048) return 600;
        if (deviceRam < 3064) return 936;
        if (deviceRam < 4096) return 1148;
        if (deviceRam < 6144) return 1536;
        return 2048; //Default RAM allocation for 64 bits
    }
}
