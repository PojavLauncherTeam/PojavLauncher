package net.kdt.pojavlaunch.value;

public class DependentLibrary {
    public String name;
	public LibraryDownloads downloads;
	
	public static class LibraryDownloads
	{
		public MinecraftLibraryArtifact artifact;
		public LibraryDownloads(MinecraftLibraryArtifact artifact) {
			this.artifact = artifact;
		}
	}
}

