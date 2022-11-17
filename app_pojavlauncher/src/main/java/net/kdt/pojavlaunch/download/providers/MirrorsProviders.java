package net.kdt.pojavlaunch.download.providers;

import net.kdt.pojavlaunch.download.providers.BMCLProvider;
import net.kdt.pojavlaunch.download.providers.DefaultProvider;
import net.kdt.pojavlaunch.download.providers.MirrorsProvider;

public class MirrorsProviders {

    public static final DefaultProvider DEFAULT_PROVIDER = new DefaultProvider();
    public static final BMCLProvider BMCL_PROVIDER = new BMCLProvider();

	public static final MirrorsProvider[] PROVIDERS = new MirrorsProvider[] {
		DEFAULT_PROVIDER, BMCL_PROVIDER
	};
}
