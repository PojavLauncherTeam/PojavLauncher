package net.kdt.pojavlaunch.modloaders;

import androidx.annotation.NonNull;

public class ForgeVersion {
    public final String versionString;
    public final ForgeForks fork;

    enum ForgeForks {
        FORGE(""),
        NEOFORGE("NeoForged");
        public final String name;
        ForgeForks(String forkName) {
            this.name = forkName;
        }
    }
    public ForgeVersion(String versionString, ForgeForks fork) {
        this.versionString = versionString;
        this.fork = fork;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format("%s %s", versionString, fork.name);
    }
}
