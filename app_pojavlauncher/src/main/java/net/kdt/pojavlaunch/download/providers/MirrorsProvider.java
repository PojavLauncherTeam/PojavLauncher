package net.kdt.pojavlaunch.download.providers;

public class MirrorsProvider implements IMirrorsProvider {

	private final String assetsUrl;
	private final String assetIndexUrl;
	private final String librariesUrl;
	private final String versionManifestUrl;
	private final String displayName;

	protected MirrorsProvider(String assetsUrl, String assetIndexUrl, String librariesUrl, String versionManifestUrl, String displayName) {
		this.assetsUrl = assetsUrl;
		this.assetIndexUrl = assetIndexUrl;
		this.librariesUrl = librariesUrl;
		this.versionManifestUrl = versionManifestUrl;
		this.displayName = displayName;
	}
	
	@Override
	public String getAssetsURL() {
		return assetsUrl;
	}

	@Override
	public String getAssetsIndexURL() {
		return assetIndexUrl;
	}

	@Override
	public String getLibrariesURL() {
		return librariesUrl;
	}

	@Override
	public String getVersionManifestURL() {
		return versionManifestUrl;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}
    
}
