package net.kdt.pojavlaunch;
import java.io.*;
import android.content.*;

public class PojavProfile
{
	private static String PROFILE_PREF = "pojav_profile";
	private static String PROFILE_PREF_FILE = "file";
	private static String PROFILE_PREF_CONTENT = "content";
	private static String PROFILE_PREF_TEMP_CONTENT = "tempContent";
	
	private static SharedPreferences getPrefs(Context ctx) {
		return ctx.getSharedPreferences(PROFILE_PREF, Context.MODE_PRIVATE);
	}
	
	public static MCProfile.Builder getCurrentProfileContent(Context ctx) {
		MCProfile.Builder build = MCProfile.parse(getPrefs(ctx).getString(PROFILE_PREF_CONTENT, ""));
        if (build == null) {
            getTempProfileContent(ctx);
        }
        return build;
	}

    public static MCProfile.Builder getTempProfileContent(Context ctx) {
        return MCProfile.parse(getPrefs(ctx).getString(PROFILE_PREF_TEMP_CONTENT, ""));
    }
	
	public static String getCurrentProfilePath(Context ctx) {
		return getPrefs(ctx).getString(PROFILE_PREF_FILE, "");
	}
	
	public static boolean setCurrentProfile(Context ctx, Object obj) {
		SharedPreferences.Editor pref = getPrefs(ctx).edit();
		
		try {
			if (obj instanceof MCProfile.Builder) {
                String str = MCProfile.toString((MCProfile.Builder) obj);
				pref.putString(PROFILE_PREF_CONTENT, str);
                pref.putString(PROFILE_PREF_TEMP_CONTENT, str);
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
