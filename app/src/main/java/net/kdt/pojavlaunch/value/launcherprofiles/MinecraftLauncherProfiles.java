package net.kdt.pojavlaunch.value.launcherprofiles;
import com.google.gson.*;
import net.kdt.pojavlaunch.*;

public class MinecraftLauncherProfiles
{
	public MinecraftProfile[] profiles;
	public String clientToken;
	public MinecraftAuthenticationDatabase[] authenticationDatabase;
	// public Map launcherVersion;
	public MinecraftLauncherSettings settings;
	// public Map analyticsToken;
	public int analyticsFailcount;
	public MinecraftSelectedUser selectedUser;
    
    public String toJson() {
        return Tools.GLOBAL_GSON.toJson(this);
    }
}
