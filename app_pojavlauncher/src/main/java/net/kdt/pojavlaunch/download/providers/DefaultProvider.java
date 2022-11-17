package net.kdt.pojavlaunch.download.providers;

public class DefaultProvider implements MirrorsProvider {

	DefaultProvider() {}
	
	@Override
	public String getDisplayName() {
		return "Default";
	}

	@Override
	public String getAssetsURL() {
		return "https://resources.download.minecraft.net";
	}

	@Override
	public String getAssetsIndexURL() {
		return "https://launcher.mojang.com";
	}

	@Override
	public String getLibrariesURL() {
		return "https://libraries.minecraft.net";
	}

	@Override
	public String getVersionManifestURL() {
		return "http://launchermeta.mojang.com/mc/game/version_manifest.json";
	}

	//Only one global (in URLProviders)
	@Override
	public boolean equals(Object obj) {
		return obj instanceof DefaultProvider;
	}
	
}
