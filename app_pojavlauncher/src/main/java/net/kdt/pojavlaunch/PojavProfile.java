package net.kdt.pojavlaunch;
import java.io.*;
import android.content.*;
import net.kdt.pojavlaunch.value.*;
import com.google.gson.*;

public class PojavProfile
{
	private static String PROFILE_PREF = "pojav_profile";
	private static String PROFILE_PREF_FILE = "file";
	private static String PROFILE_PREF_TEMP_CONTENT = "tempContent";
	
	private static SharedPreferences getPrefs(Context ctx) {
		return ctx.getSharedPreferences(PROFILE_PREF, Context.MODE_PRIVATE);
	}
	
	public static MinecraftAccount getCurrentProfileContent(Context ctx) throws IOException, JsonSyntaxException {
		MinecraftAccount build = MinecraftAccount.load(getPrefs(ctx).getString(PROFILE_PREF_FILE, ""));
        if (build == null) {
            getTempProfileContent(ctx);
        }
        return build;
	}

    public static MinecraftAccount getTempProfileContent(Context ctx) {
        return MinecraftAccount.parse(getPrefs(ctx).getString(PROFILE_PREF_TEMP_CONTENT, ""));
    }
	
	public static String getCurrentProfilePath(Context ctx) {
		return getPrefs(ctx).getString(PROFILE_PREF_FILE, "");
	}
	
	public static boolean setCurrentProfile(Context ctx, Object obj) {
		SharedPreferences.Editor pref = getPrefs(ctx).edit();
		
		try {
			if (obj instanceof MinecraftAccount) {
                try {
                    MinecraftAccount.saveTempAccount((MinecraftAccount) obj);
                } catch (IOException e) {
                    Tools.showError(ctx, e);
                }
			} else if (obj instanceof String) {
                String acc = (String) obj;
				pref.putString(PROFILE_PREF_FILE,
                    acc.startsWith("/") ?
                        acc : 
                        Tools.DIR_ACCOUNT_NEW + "/" + acc + ".json");
                MinecraftAccount.clearTempAccount();
			} else if (obj == null) {
				pref.putString(PROFILE_PREF_FILE, "");
			} else {
				throw new IllegalArgumentException("Profile must be MinecraftAccount.class, String.class or null");
			}
		} finally {
			return pref.commit();
		}
	}
	
	public static boolean isFileType(Context ctx) {
		return new File(PojavProfile.getCurrentProfilePath(ctx)).exists();
	}
}
