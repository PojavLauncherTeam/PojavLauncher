package net.kdt.pojavlaunch.prefs;

import android.content.*;
import net.kdt.pojavlaunch.*;
import android.os.*;

public class LauncherPreferences
{
	public static SharedPreferences DEFAULT_PREF;
	public static boolean PREF_VERTYPE_RELEASE = true;
	public static boolean PREF_VERTYPE_SNAPSHOT = false;
	public static boolean PREF_VERTYPE_OLDALPHA = false;
	public static boolean PREF_VERTYPE_OLDBETA = false;
	public static boolean PREF_FREEFORM = false;
	public static float PREF_BUTTONSIZE = 1.0f;
	public static int PREF_LONGPRESS_TRIGGER = 500;
	public static String PREF_DEFAULTCTRL_PATH = Tools.CTRLDEF_FILE;
	public static String PREF_CUSTOM_JAVA_ARGS;
	
	public static void loadPreferences() {
		PREF_BUTTONSIZE = DEFAULT_PREF.getFloat("controlSize", 1f);
		PREF_FREEFORM = DEFAULT_PREF.getBoolean("freeform", false);
		PREF_VERTYPE_RELEASE = DEFAULT_PREF.getBoolean("vertype_release", true);
		PREF_VERTYPE_SNAPSHOT = DEFAULT_PREF.getBoolean("vertype_snapshot", false);
		PREF_VERTYPE_OLDALPHA = DEFAULT_PREF.getBoolean("vertype_oldalpha", false);
		PREF_VERTYPE_OLDBETA = DEFAULT_PREF.getBoolean("vertype_oldbeta", false);
		PREF_LONGPRESS_TRIGGER = DEFAULT_PREF.getInt("timeLongPressTrigger", 500);
		PREF_DEFAULTCTRL_PATH = DEFAULT_PREF.getString("defaultCtrl", Tools.CTRLDEF_FILE);
		// Get double of max Android heap to set default heap size
		int androidHeap = (int) (Runtime.getRuntime().maxMemory() / 1024l / 512l);
		PREF_CUSTOM_JAVA_ARGS = DEFAULT_PREF.getString("customJavaArgs",
			"-Xms" + androidHeap + " " +
			"-Xmx" + (androidHeap * 2) + " " +
			"-XX:+UseG1GC " +
			"-Dsun.rmi.dgc.server.gcInterval=2147483646 " +
			"-XX:+UnlockExperimentalVMOptions " +
			"-XX:G1NewSizePercent=20 " +
			"-XX:G1ReservePercent=20 " +
			"-XX:MaxGCPauseMillis=50 " +
			"-XX:G1HeapRegionSize=32M"
		);
	}
}
