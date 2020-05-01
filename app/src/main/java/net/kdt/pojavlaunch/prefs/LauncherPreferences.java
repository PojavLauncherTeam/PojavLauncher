package net.kdt.pojavlaunch.prefs;

import android.content.*;
public class LauncherPreferences
{
	public static boolean PREF_VERTYPE_RELEASE = true;
	public static boolean PREF_VERTYPE_SNAPSHOT = false;
	public static boolean PREF_VERTYPE_OLDALPHA = false;
	public static boolean PREF_VERTYPE_OLDBETA = false;
	public static boolean PREF_FREEFORM = false;
	public static boolean PREF_RUNASROOT = false;
	public static float PREF_BUTTONSIZE = 1.0f;
	public static int PREF_LONGPRESS_TRIGGER = 500;
	
	public static void loadPreferences(Context ctx) {
		SharedPreferences mainPreference = ctx.getSharedPreferences(ctx.getPackageName() + "_preferences", Context.MODE_PRIVATE);
		PREF_BUTTONSIZE = mainPreference.getFloat("controlSize", 1f);
		PREF_FREEFORM = mainPreference.getBoolean("freeform", false);
		PREF_RUNASROOT = mainPreference.getBoolean("runAsRoot", false);
		PREF_VERTYPE_RELEASE = mainPreference.getBoolean("vertype_release", true);
		PREF_VERTYPE_SNAPSHOT = mainPreference.getBoolean("vertype_snapshot", false);
		PREF_VERTYPE_OLDALPHA = mainPreference.getBoolean("vertype_oldalpha", false);
		PREF_VERTYPE_OLDBETA = mainPreference.getBoolean("vertype_oldbeta", false);
		PREF_LONGPRESS_TRIGGER = mainPreference.getInt("timeLongPressTrigger", 500);
	}
}
