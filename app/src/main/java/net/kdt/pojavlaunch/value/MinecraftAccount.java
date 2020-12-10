package net.kdt.pojavlaunch.value;

public class MinecraftAccount
{
    public String accessToken; // access token
    public String clientToken; // clientID: refresh and invalidate
    public String profileId; // authenticate UUID
    public String username;
    public String selectedVersion = "1.7.10";
    public boolean isMicrosoft;
}
