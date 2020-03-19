package com.kdt.mojangauth;

public interface LoginListener
{
	public void onBeforeLogin();
	public void onLoginDone(String[] result);
}
