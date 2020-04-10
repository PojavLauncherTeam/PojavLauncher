package net.kdt.pojavlaunch.value.launcherprofiles;
import com.google.gson.*;
import net.kdt.pojavlaunch.*;
import java.io.*;

public class LauncherProfiles
{
	public static MinecraftLauncherProfiles mainProfileJson;
	public static File launcherProfilesFile = new File(Tools.MAIN_PATH + "/launcher_profiles.json");
	public static MinecraftLauncherProfiles getJson() {
		try {
			if (launcherProfilesFile.exists()) {
				if (mainProfileJson == null) {
					mainProfileJson = new Gson().fromJson(Tools.read(launcherProfilesFile.getAbsolutePath()), MinecraftLauncherProfiles.class);
				}
			} else {
				mainProfileJson = new MinecraftLauncherProfiles();
			}
			// insertMissing();
			return mainProfileJson;
		} catch (Throwable th) {
			throw new RuntimeException(th);
		}
	}
	/*
	public static String insert;
	
	private static void insertMissing() {
		if (mainProfileJson.authenticationDatabase == null) {
			MinecraftAuthenticationDatabase mad = new MinecraftAuthenticationDatabase();
			mainProfileJson.authenticationDatabase = mad;
		}
	}
	*/
}
