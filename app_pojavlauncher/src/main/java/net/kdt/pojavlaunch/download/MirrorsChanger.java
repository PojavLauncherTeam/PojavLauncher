package net.kdt.pojavlaunch.download;

import android.util.Log;
import java.util.Map;
import net.kdt.pojavlaunch.JAssets;
import net.kdt.pojavlaunch.JMinecraftVersionList;
import net.kdt.pojavlaunch.download.providers.IMirrorsProvider;
import net.kdt.pojavlaunch.download.providers.MirrorsProviders;
import net.kdt.pojavlaunch.value.DependentLibrary;
import net.kdt.pojavlaunch.value.MinecraftClientInfo;

import static net.kdt.pojavlaunch.download.providers.MirrorsProviders.DEFAULT_PROVIDER;

public class MirrorsChanger {
	
    private static MirrorsChanger instance = new MirrorsChanger();
	
	private IMirrorsProvider provider;
	
	private MirrorsChanger() {
		this.provider = MirrorsProviders.BMCL_PROVIDER;
		Log.i("Mirror", "change mirror provider to " + provider.getDisplayName());
	}

	//To facilitate management, a single is used
	//On the other hand, users have only one source setting globally
	public static MirrorsChanger getInstance() {
		if (instance == null) {
			synchronized(MirrorsChanger.class) {
				if (instance == null)
			       instance = new MirrorsChanger();
			}
		}
		return instance;
	}
	
	public void inject(JMinecraftVersionList.Version version) {
		
        if (DEFAULT_PROVIDER.equals(provider) || version == null) return;
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
	
	public String getMinecraftResourceUrl() {
		return provider.getAssetsURL();
	}
	
	public String getMinecraftVersionManifestUrl() {
		return provider.getVersionManifestURL();
    }
	
	public String getMinecraftLibrariesUrl() {
		return provider.getLibrariesURL();
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
		return url != null ? url//Prevent NullPointerException
		  .replace(DEFAULT_PROVIDER.getAssetsIndexURL(), provider.getAssetsIndexURL())
		  .replace(DEFAULT_PROVIDER.getLibrariesURL(), provider.getLibrariesURL())
		  .replace(DEFAULT_PROVIDER.getVersionManifestURL(), provider.getVersionManifestURL())
		  : null;
	}
	
	//getters and setters
	public void setProvider(IMirrorsProvider provider) {
		this.provider = provider;
		Log.i("Mirror", "change mirror provider to " + provider.getDisplayName());
		Log.v("Mirror", "Notify listener");
	}

	public IMirrorsProvider getProvider() {
		return provider;
	}

}
