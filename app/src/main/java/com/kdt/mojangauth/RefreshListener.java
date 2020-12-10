package com.kdt.mojangauth;

import net.kdt.pojavlaunch.*;

public interface RefreshListener
{
	public void onFailed(Throwable e);
	public void onSuccess(MCProfile.Builder profile);
}
