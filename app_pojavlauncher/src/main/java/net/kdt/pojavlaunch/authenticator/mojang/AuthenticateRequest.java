
package net.kdt.pojavlaunch.authenticator.mojang.yggdrasil;

import java.util.UUID;

public class AuthenticateRequest {
    public AgentInfo agent = new AgentInfo();
    public UUID clientToken;
    public String password;
    public String username;

    public static class AgentInfo {
        public String name;
        public int version;
    }

    public AuthenticateRequest(String username, String password, UUID clientToken, String clientName, int clientVersion) {
        this.username = username;
        this.password = password;
        this.clientToken = clientToken;
        this.agent.name = clientName;
        this.agent.version = clientVersion;
    }
}