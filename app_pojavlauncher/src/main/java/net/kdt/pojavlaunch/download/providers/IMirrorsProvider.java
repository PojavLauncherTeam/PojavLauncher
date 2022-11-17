package net.kdt.pojavlaunch.download.providers;

import net.kdt.pojavlaunch.JMinecraftVersionList;

//Provide the URL of the source
public interface IMirrorsProvider {
	
    String getAssetsURL();
	String getAssetsIndexURL();
    String getLibrariesURL();
	
	String getVersionManifestURL();
	String getDisplayName();
}
