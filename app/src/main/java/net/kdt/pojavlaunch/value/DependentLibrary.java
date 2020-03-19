package net.kdt.pojavlaunch.value;

public class DependentLibrary {
    public String name;
	public MDownloads downloads;
	
	public static class MDownloads
	{
		public MinecraftLibraryArtifact artifact;
	}
}

