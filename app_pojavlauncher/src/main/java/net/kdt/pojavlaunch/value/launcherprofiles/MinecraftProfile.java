package net.kdt.pojavlaunch.value.launcherprofiles;

import net.kdt.pojavlaunch.Tools;

public class MinecraftProfile
{
	public String name = "Default";
	public String type;
	public String created;
	public String lastUsed;
	public String icon = null;
	public String lastVersionId = "1.12.2";
	public String gameDir = Tools.DIR_GAME_DEFAULT;
	public String javaDir;
	public String javaArgs;
	public String logConfig;
	public boolean logConfigIsXML;
	public MinecraftResolution[] resolution;
}
