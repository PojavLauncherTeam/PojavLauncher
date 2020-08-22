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
	public static float PREF_BUTTONSIZE = 1.0f;
	public static int PREF_LONGPRESS_TRIGGER = 500;
	public static String PREF_DEFAULTCTRL_PATH = Tools.CTRLDEF_FILE;
	
	public static void loadPreferences(Context ctx) {
		PREF_BUTTONSIZE = DEFAULT_PREF.getFloat("controlSize", 1f);
		PREF_FREEFORM = DEFAULT_PREF.getBoolean("freeform", false);
		PREF_VERTYPE_RELEASE = DEFAULT_PREF.getBoolean("vertype_release", true);
		PREF_VERTYPE_SNAPSHOT = DEFAULT_PREF.getBoolean("vertype_snapshot", false);
		PREF_VERTYPE_OLDALPHA = DEFAULT_PREF.getBoolean("vertype_oldalpha", false);
		PREF_VERTYPE_OLDBETA = DEFAULT_PREF.getBoolean("vertype_oldbeta", false);
		PREF_LONGPRESS_TRIGGER = DEFAULT_PREF.getInt("timeLongPressTrigger", 500);
		PREF_DEFAULTCTRL_PATH = DEFAULT_PREF.getString("defaultCtrl", Tools.CTRLDEF_FILE);
	}
}
