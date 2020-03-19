package com.kdt.mojangauth;

public interface RefreshListener
{
	public void onFailed(Throwable e);
	public void onSuccess();
}
