package net.kdt.pojavlaunch.value;

import java.util.Map;

public class MinecraftVersion
{
	public String assets;
	public Map<String, MinecraftClientInfo> downloads;
    public DependentLibrary[] libraries;
    public String mainClass;
    public String minecraftArguments;
    public int minimumLauncherVersion;
}
