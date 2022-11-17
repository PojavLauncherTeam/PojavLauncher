package net.kdt.pojavlaunch.download.providers;

public class BMCLProvider implements MirrorsProvider {

	BMCLProvider() {}
	
	@Override
	public String getAssetsURL() {
		return "https://bmclapi2.bangbang93.com/assets";
	}

	@Override
	public String getAssetsIndexURL() {
		return "https://bmclapi2.bangbang93.com";
	}

	@Override
	public String getLibrariesURL() {
		return "https://bmclapi2.bangbang93.com/maven";
	}

	@Override
	public String getVersionManifestURL() {
		return "https://bmclapi2.bangbang93.com/mc/game/version_manifest.json";
	}

	@Override
	public String getDisplayName() {
		return "BMCL";
	}

	//Only one global (in URLProviders)
	@Override
	public boolean equals(Object obj) {
		return obj instanceof BMCLProvider;
	}
}
