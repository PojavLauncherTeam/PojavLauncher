package net.kdt.pojavlaunch.download.providers;

import net.kdt.pojavlaunch.download.providers.IMirrorsProvider;

public class MirrorsProviders {

    public static final MirrorsProvider DEFAULT_PROVIDER = new MirrorsProvider(
		"https://resources.download.minecraft.net"//assert url
		, "https://launcher.mojang.com"//assetIndexUrl
		, "https://libraries.minecraft.net"//librariesUrl
		, "http://launchermeta.mojang.com/mc/game/version_manifest.json"//versionManifestUrl
		, "Default"//display name 
	);
    public static final MirrorsProvider BMCL_PROVIDER = new MirrorsProvider(
		"https://bmclapi2.bangbang93.com/assets"
		,"https://bmclapi2.bangbang93.com"
		,"https://bmclapi2.bangbang93.com/maven"
		,"https://bmclapi2.bangbang93.com/mc/game/version_manifest.json"
		,"BMCL"
	);

	public static final IMirrorsProvider[] PROVIDERS = new IMirrorsProvider[] {
		DEFAULT_PROVIDER, BMCL_PROVIDER
	};
}
