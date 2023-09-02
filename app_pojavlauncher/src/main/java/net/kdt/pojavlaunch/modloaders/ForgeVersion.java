package net.kdt.pojavlaunch.modloaders;

import androidx.annotation.NonNull;

public class ForgeVersion {
    String versionString;
    ForgeVersionListHandler.ForgeForks fork;

    public ForgeVersion(String versionString, ForgeVersionListHandler.ForgeForks fork) {
        this.versionString = versionString;
        this.fork = fork;
    }

    @NonNull
    @Override
    public String toString() {
        return versionString + (fork == ForgeVersionListHandler.ForgeForks.NEOFORGE ? " (NeoForge)" : "");
    }
}
