package net.kdt.pojavlaunch.modloaders.modpacks.models;

public class CurseManifest {
    public String name;
    public String version;
    public String author;
    public String manifestType;
    public int manifestVersion;
    public CurseFile[] files;
    public CurseMinecraft minecraft;
    public String overrides;
    public static class CurseFile {
        public long projectID;
        public long fileID;
    }
    public static class CurseMinecraft {
        public String version;
        public CurseModLoader[] modLoaders;
    }
    public static class CurseModLoader {
        public String id;
        public boolean primary;
    }
}
