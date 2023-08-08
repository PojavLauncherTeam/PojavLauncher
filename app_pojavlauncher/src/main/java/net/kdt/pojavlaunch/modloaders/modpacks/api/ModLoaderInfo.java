package net.kdt.pojavlaunch.modloaders.modpacks.api;

public class ModLoaderInfo {
    public static final int MOD_LOADER_FORGE = 0;
    public static final int MOD_LOADER_FABRIC = 1;
    public static final int MOD_LOADER_QUILT = 2;
    public final int modLoaderType;
    public final String modLoaderVersion;
    public final String minecraftVersion;

    public ModLoaderInfo(int modLoaderType, String modLoaderVersion, String minecraftVersion) {
        this.modLoaderType = modLoaderType;
        this.modLoaderVersion = modLoaderVersion;
        this.minecraftVersion = minecraftVersion;
    }

    public String getVersionId() {
        switch (modLoaderType) {
            case MOD_LOADER_FORGE:
                return minecraftVersion+"-forge-"+modLoaderType;
            case MOD_LOADER_FABRIC:
                return "fabric-loader-"+modLoaderVersion+"-"+minecraftVersion;
            case MOD_LOADER_QUILT:
                // TODO
            default:
                return null;
        }
    }
}
