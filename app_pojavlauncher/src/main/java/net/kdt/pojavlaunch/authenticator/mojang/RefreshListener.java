package net.kdt.pojavlaunch.authenticator.mojang;

import net.kdt.pojavlaunch.*;
import net.kdt.pojavlaunch.value.*;

public interface RefreshListener
{
	void onStart();
	void onProgressed(double progress);
	void onFailed(Throwable e);
	void onSuccess(MinecraftAccount profile);
}
