package net.kdt.pojavlaunch.authenticator.mojang.yggdrasil;

import java.util.UUID;

public class RefreshRequest {
    public String accessToken;
    public UUID clientToken;

    public RefreshRequest(String accessToken, UUID clientToken) {
        this.accessToken = accessToken;
        this.clientToken = clientToken;
    }
}

