package net.kdt.pojavlaunch;

import androidx.annotation.Keep;
import java.util.*;
import net.kdt.pojavlaunch.value.*;

@Keep
@SuppressWarnings("unused") // all unused fields here are parts of JSON structures
public class JMinecraftVersionList {
    public Map<String, String> latest;
    public Version[] versions;

    @Keep
    public static class FileProperties {
        public String id, sha1, url;
        public long size;
    }

    @Keep
    public static class Version extends FileProperties {
        // Since 1.13, so it's one of ways to check
        public Arguments arguments;
        public AssetIndex assetIndex;

        public String assets;
        public Map<String, MinecraftClientInfo> downloads;
        public String inheritsFrom;
        public JavaVersionInfo javaVersion;
        public DependentLibrary[] libraries;
        public LoggingConfig logging;
        public String mainClass;
        public String minecraftArguments;
        public int minimumLauncherVersion;
        public String releaseTime;
        public String time;
        public String type;
    }
    @Keep
    public static class JavaVersionInfo {
        public String component;
        public int majorVersion;
    }
    @Keep
    public static class LoggingConfig {
        public LoggingClientConfig client;

        @Keep
        public static class LoggingClientConfig {
            public String argument;
            public FileProperties file;
            public String type;
        }
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
                public ArgOS os;

                @Keep
                public static class ArgOS {
                    public String name;
                    public String version;
                }
            }
        }
    }
    @Keep
    public static class AssetIndex extends FileProperties {
        public long totalSize;
    }
}

