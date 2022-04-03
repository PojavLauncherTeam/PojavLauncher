package net.kdt.pojavlaunch.authenticator.mojang.yggdrasil;

import java.util.UUID;

public class AuthenticateResponse {
    public String accessToken;
    public Profile[] availableProfiles;
    public UUID clientToken;
    public Profile selectedProfile;
}