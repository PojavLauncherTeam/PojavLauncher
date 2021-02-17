package net.kdt.pojavlaunch.authenticator.mojang;

import net.kdt.pojavlaunch.*;
import net.kdt.pojavlaunch.value.*;

public interface RefreshListener
{
	public void onFailed(Throwable e);
	public void onSuccess(MinecraftAccount profile);
}
