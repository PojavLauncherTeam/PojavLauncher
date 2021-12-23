package net.kdt.pojavlaunch.value;

import androidx.annotation.Keep;

@Keep
public class DependentLibrary {
    public String name;
	public LibraryDownloads downloads;
    public String url;

    @Keep
	public static class LibraryDownloads {
		public MinecraftLibraryArtifact artifact;
		public LibraryDownloads(MinecraftLibraryArtifact artifact) {
			this.artifact = artifact;
		}
	}
}

