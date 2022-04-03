package net.kdt.pojavlaunch.authenticator.mojang;

public interface LoginListener
{
    public void onBeforeLogin();
    public void onLoginDone(String[] result);
}