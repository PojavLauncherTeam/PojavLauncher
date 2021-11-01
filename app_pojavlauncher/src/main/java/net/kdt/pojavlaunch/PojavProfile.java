package net.kdt.pojavlaunch;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

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
	public static String PROFILE_PREF_TEMP_CONTENT = "tempContent";

	public static SharedPreferences getPrefs(Context ctx) {
		return ctx.getSharedPreferences(PROFILE_PREF, Context.MODE_PRIVATE);
	}
	
	public static MinecraftAccount getCurrentProfileContent(Context ctx) throws JsonSyntaxException {
		MinecraftAccount build = MinecraftAccount.load(getCurrentProfileName(ctx));
        if (build == null) {
            System.out.println("isTempProfile null? " + (getTempProfileContent() == null));
            return getTempProfileContent();
        }
        return build;
	}

    public static MinecraftAccount getTempProfileContent() {
	    try {
            MinecraftAccount acc = MinecraftAccount.parse(Tools.read(Tools.DIR_DATA+"/cache/tempacc.json"));
            if (acc.accessToken == null) {
                acc.accessToken = "0";
            }
            if (acc.clientToken == null) {
                acc.clientToken = "0";
            }
            if (acc.profileId == null) {
                acc.profileId = "00000000-0000-0000-0000-000000000000";
            }
            if (acc.username == null) {
                acc.username = "0";
            }
            if (acc.selectedVersion == null) {
                acc.selectedVersion = "1.7.10";
            }
            if (acc.msaRefreshToken == null) {
                acc.msaRefreshToken = "0";
            }
            return acc;
        }catch (IOException e) {
            Log.e(MinecraftAccount.class.getName(), "Caught an exception while loading the temporary profile",e);
            return null;
        }
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

        Intent intent = new Intent(ctx, PojavLauncherActivity.class); //MCLauncherActivity.class);
        ctx.startActivity(intent);
    }

    public static void updateTokens(final Activity ctx, final String name, RefreshListener listen) throws Exception {
        new RefreshTokenTask(ctx, listen).execute(name);
    }
}
