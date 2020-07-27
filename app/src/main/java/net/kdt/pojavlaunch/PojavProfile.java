package net.kdt.pojavlaunch;
import java.io.*;
import android.content.*;

public class PojavProfile
{
	private static String PROFILE_PREF = "pojav_profile";
	private static String PROFILE_PREF_FILE = "file";
	private static String PROFILE_PREF_CONTENT = "content";
	
	private static SharedPreferences getPrefs(Context ctx) {
		return ctx.getSharedPreferences(PROFILE_PREF, Context.MODE_PRIVATE);
	}
	
	public static MCProfile.Builder getCurrentProfileContent(Context ctx) {
		return MCProfile.parse(getPrefs(ctx).getString(PROFILE_PREF_CONTENT, ""));
	}
	
	public static String getCurrentProfilePath(Context ctx) {
		return getPrefs(ctx).getString(PROFILE_PREF_FILE, "");
	}
	
	public static boolean setCurrentProfile(Context ctx, Object obj) {
		SharedPreferences.Editor pref = getPrefs(ctx).edit();
		
		try {
			if (obj instanceof MCProfile.Builder) {
				pref.putString(PROFILE_PREF_CONTENT, MCProfile.toString((MCProfile.Builder) obj));
			} else if (obj instanceof String) {
				pref.putString(PROFILE_PREF_FILE, (String) obj);
				pref.putString(PROFILE_PREF_CONTENT, MCProfile.toString((String) obj));
			} else if (obj == null) {
				pref.putString(PROFILE_PREF_FILE, "");
				pref.putString(PROFILE_PREF_CONTENT, "");
			} else {
				throw new IllegalArgumentException("Profile must be MCProfile.Builder.class, String.class or null");
			}
		} finally {
			return pref.commit();
		}
	}
	
	public static boolean isFileType(Context ctx) {
		String profilePath = PojavProfile.getCurrentProfilePath(ctx);
		String profileCon = MCProfile.toString(getCurrentProfileContent(ctx));
		if (profileCon.equals(":::::")) {
			throw new RuntimeException("Profile not set or reset.");
		}
		
		return new File(profilePath).exists();
	}
}
