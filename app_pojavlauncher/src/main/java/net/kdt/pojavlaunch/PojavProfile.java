package net.kdt.pojavlaunch;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import com.google.gson.JsonSyntaxException;
import java.io.File;
import java.io.IOException;
import net.kdt.pojavlaunch.authenticator.mojang.RefreshListener;
import net.kdt.pojavlaunch.authenticator.mojang.RefreshTokenTask;
import net.kdt.pojavlaunch.value.MinecraftAccount;

public class PojavProfile
{
	private static String PROFILE_PREF = "pojav_profile";
	private static String PROFILE_PREF_FILE = "file";
	private static String PROFILE_PREF_TEMP_CONTENT = "tempContent";
	
	private static SharedPreferences getPrefs(Context ctx) {
		return ctx.getSharedPreferences(PROFILE_PREF, Context.MODE_PRIVATE);
	}
	
	public static MinecraftAccount getCurrentProfileContent(Context ctx) throws IOException, JsonSyntaxException {
		MinecraftAccount build = MinecraftAccount.load(getCurrentProfileName(ctx));
        if (build == null) {
            return getTempProfileContent(ctx);
        }
        return build;
	}

    public static MinecraftAccount getTempProfileContent(Context ctx) {
        return MinecraftAccount.parse(getPrefs(ctx).getString(PROFILE_PREF_TEMP_CONTENT, ""));
    }
	
    public static String getCurrentProfileName(Context ctx) {
        String name = getPrefs(ctx).getString(PROFILE_PREF_FILE, "");
        // A dirty fix
        if (!name.isEmpty() && name.startsWith(Tools.DIR_ACCOUNT_NEW) && name.endsWith(".json")) {
            name = name.substring(0, name.length() - 5).replace(Tools.DIR_ACCOUNT_NEW, "").replace(".json", "");
            setCurrentProfile(ctx, name);
        }
        return name;
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
				pref.putString(PROFILE_PREF_FILE, acc);
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
		return new File(Tools.DIR_ACCOUNT_NEW + "/" + PojavProfile.getCurrentProfileName(ctx) + ".json").exists();
	}
	

    public static void launch(Activity ctx, Object o) {
        PojavProfile.setCurrentProfile(ctx, o);

        Intent intent = new Intent(ctx, PojavV2ActivityManager.getLauncherRemakeVer(ctx)); //MCLauncherActivity.class);
        ctx.startActivity(intent);
    }

    public static void updateTokens(final Activity ctx, final String name, RefreshListener listen) throws Exception {
        new RefreshTokenTask(ctx, listen).execute(Tools.DIR_ACCOUNT_NEW + "/" + name + ".json");
    }
}
