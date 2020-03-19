package net.kdt.pojavlaunch;

import java.util.Map;

public class JMinecraftVersionList {
    public static final String TYPE_OLD_ALPHA = "old_alpha";
    public static final String TYPE_OLD_BETA = "old_beta";
    public static final String TYPE_RELEASE = "release";
    public static final String TYPE_SNAPSHOT = "snapshot";
    public Map<String, String> latest;
    public Version[] versions;

    public static class Version extends OfflineVersion {
        public String url;
    }
	
	public static class OfflineVersion {
        public String id;
        public String releaseTime;
        public String time;
        public String type;
    }
}

