package net.kdt.pojavlaunch;

import androidx.annotation.Keep;

import java.util.Map;
import net.kdt.pojavlaunch.value.*;
import java.util.*;

@Keep
public class JMinecraftVersionList {
    public static final String TYPE_OLD_ALPHA = "old_alpha";
    public static final String TYPE_OLD_BETA = "old_beta";
    public static final String TYPE_RELEASE = "release";
    public static final String TYPE_SNAPSHOT = "snapshot";
    public Map<String, String> latest;
    public Version[] versions;

    @Keep
    public static class Version {
        // Since 1.13, so it's one of ways to check
        public Arguments arguments;

        public AssetIndex assetIndex;

        public String assets;
        public Map<String, MinecraftClientInfo> downloads;
        public String id;
        public String inheritsFrom;
        public JavaVersionInfo javaVersion;
        public DependentLibrary[] libraries;
        public String mainClass;
        public String minecraftArguments;
        public int minimumLauncherVersion;
        public DependentLibrary optifineLib;
        public String releaseTime;
        public String time;
        public String type;
        public String url;
        public String sha1;
    }
    @Keep
    public static class JavaVersionInfo {
        public String component;
        public int majorVersion;
    }

    // Since 1.13
    @Keep
    public static class Arguments {
        public Object[] game;
        public Object[] jvm;

        @Keep
        public static class ArgValue {
            public ArgRules[] rules;
            public String value;

            // TLauncher styled argument...
            public String[] values;

            @Keep
            public static class ArgRules {
                public String action;
                public String features;
            }
        }
    }
    @Keep
    public static class AssetIndex {
        public String id, sha1, url;
        public long size, totalSize;
    }
}

