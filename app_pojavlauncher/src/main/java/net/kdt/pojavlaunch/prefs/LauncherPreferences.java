package net.kdt.pojavlaunch.prefs;

import android.content.*;
import net.kdt.pojavlaunch.*;

public class LauncherPreferences
{
	public static SharedPreferences DEFAULT_PREF;
	public static boolean PREF_VERTYPE_RELEASE = true;
	public static boolean PREF_VERTYPE_SNAPSHOT = false;
	public static boolean PREF_VERTYPE_OLDALPHA = false;
	public static boolean PREF_VERTYPE_OLDBETA = false;
	public static boolean PREF_FREEFORM = false;
	public static boolean PREF_HIDE_SIDEBAR = false;
	public static boolean PREF_IGNORE_NOTCH = false;
	public static int PREF_NOTCH_SIZE = 0;
	public static float PREF_BUTTONSIZE = 100f;
	public static float PREF_MOUSESCALE = 100f;
	public static int PREF_LONGPRESS_TRIGGER = 500;
	public static String PREF_DEFAULTCTRL_PATH = Tools.CTRLDEF_FILE;
	public static String PREF_CUSTOM_JAVA_ARGS;
    public static String PREF_CUSTOM_OPENGL_LIBNAME = "libgl04es.so";
    public static String PREF_LANGUAGE = "default";
	
	public static void loadPreferences() {
		PREF_BUTTONSIZE = DEFAULT_PREF.getInt("buttonscale", 100);
		PREF_MOUSESCALE = DEFAULT_PREF.getInt("mousescale", 100);
		PREF_FREEFORM = DEFAULT_PREF.getBoolean("freeform", false);
		PREF_HIDE_SIDEBAR = DEFAULT_PREF.getBoolean("hideSidebar", false);
		PREF_IGNORE_NOTCH = DEFAULT_PREF.getBoolean("ignoreNotch", false);
		PREF_VERTYPE_RELEASE = DEFAULT_PREF.getBoolean("vertype_release", true);
		PREF_VERTYPE_SNAPSHOT = DEFAULT_PREF.getBoolean("vertype_snapshot", false);
		PREF_VERTYPE_OLDALPHA = DEFAULT_PREF.getBoolean("vertype_oldalpha", false);
		PREF_VERTYPE_OLDBETA = DEFAULT_PREF.getBoolean("vertype_oldbeta", false);
		PREF_LONGPRESS_TRIGGER = DEFAULT_PREF.getInt("timeLongPressTrigger", 500);
		PREF_DEFAULTCTRL_PATH = DEFAULT_PREF.getString("defaultCtrl", Tools.CTRLDEF_FILE);
        PREF_LANGUAGE = DEFAULT_PREF.getString("language", "default");
        
		// Get double of max Android heap to set default heap size
		int androidHeap = (int) (Runtime.getRuntime().maxMemory() / 1024l / 512l);
        int doubleAndroidHeap = androidHeap * 2;
		PREF_CUSTOM_JAVA_ARGS = DEFAULT_PREF.getString("javaArgs", "");
        if (PREF_CUSTOM_JAVA_ARGS.isEmpty()) {
            String DEFAULT_JAVA_ARGS =
                "-Xms" + (androidHeap > 800 ? 800 : androidHeap) + "m " +
                // (32bit) More than 800mb may make JVM not allocateable and crash
                "-Xmx" + (doubleAndroidHeap > 800 ? 800 : doubleAndroidHeap) + "m"; /* "m " +

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
*/
            
            PREF_CUSTOM_JAVA_ARGS = DEFAULT_JAVA_ARGS;
            DEFAULT_PREF.edit().putString("javaArgs", DEFAULT_JAVA_ARGS).commit();
        }
        
        String argLwjglLibname = "-Dorg.lwjgl.opengl.libname=";
        for (String arg : PREF_CUSTOM_JAVA_ARGS.split(" ")) {
            if (arg.startsWith(argLwjglLibname)) {
                PREF_CUSTOM_OPENGL_LIBNAME = arg.substring(argLwjglLibname.length());
            }
        }
	}
}
