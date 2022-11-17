package net.kdt.pojavlaunch.download;

import net.kdt.pojavlaunch.download.providers.*;
import net.kdt.pojavlaunch.JMinecraftVersionList;

import static net.kdt.pojavlaunch.download.providers.MirrorsProviders.DEFAULT_PROVIDER;
import java.util.Map;
import net.kdt.pojavlaunch.value.MinecraftClientInfo;
import net.kdt.pojavlaunch.value.DependentLibrary;
import android.util.Log;

public class MirrorsChanger {
	
    public static final MirrorsChanger GLOBAL_URL_CHANGER = new MirrorsChanger();
	
	private MirrorsProvider provider;
	
	private MirrorsChanger() {
		this.provider = MirrorsProviders.DEFAULT_PROVIDER;
		Log.i("Mirror", "change mirror provider to " + provider.getDisplayName());
	}
	
	public void inject(JMinecraftVersionList.Version version) {
		
        if (DEFAULT_PROVIDER.equals(provider) && version == null) return;
		Log.i("Mirror", "Changing mirror to " + provider.getDisplayName());
		if (version.assetIndex != null) 
			version.assetIndex.url = injectUrl(version.assetIndex.url);
		if (version.downloads != null) 
			injectDownloads(version);
		if (version.libraries != null)
			injectLibs(version);
		if (version.logging != null) 
			version.logging.client.file.url = injectUrl(version.logging.client.file.url);
		version.url = injectUrl(version.url);
	}
	
	private void injectLibs(JMinecraftVersionList.Version ver) {
		for (DependentLibrary lib: ver.libraries) {
			lib.downloads.artifact.url = injectUrl(lib.downloads.artifact.url);
			lib.url = injectUrl(lib.url);
		}
	}
	
	private void injectDownloads(JMinecraftVersionList.Version ver) {
		for (Map.Entry<String, MinecraftClientInfo> entry: ver.downloads.entrySet()) {
			MinecraftClientInfo info = entry.getValue();
			info.url = injectUrl(info.url);
		}
	}
	
	//It only replaces one time
	//TODO: make it faster
	private String injectUrl(String url) {
		return url != null ? url
		  .replace(DEFAULT_PROVIDER.getAssetsIndexURL(), provider.getAssetsIndexURL())
		  .replace(DEFAULT_PROVIDER.getAssetsURL(), provider.getAssetsURL())
		  .replace(DEFAULT_PROVIDER.getLibrariesURL(), provider.getLibrariesURL())
		  .replace(DEFAULT_PROVIDER.getVersionManifestURL(), provider.getVersionManifestURL())
		  : null;
	}
	
	//getters and setters
	
	public void setProvider(MirrorsProvider provider) {
		this.provider = provider;
		Log.i("Mirror", "change mirror provider to " + provider.getDisplayName());
	}

	public MirrorsProvider getProvider() {
		return provider;
	}
}
