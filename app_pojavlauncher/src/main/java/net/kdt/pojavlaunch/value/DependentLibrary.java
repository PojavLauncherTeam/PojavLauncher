package net.kdt.pojavlaunch.value;

import net.kdt.pojavlaunch.JMinecraftVersionList.Arguments.ArgValue.ArgRules;

public class DependentLibrary {
    public ArgRules[] rules;
    public String name;
    public LibraryDownloads downloads;
    public String url;
    
    public static class LibraryDownloads {
        public MinecraftLibraryArtifact artifact;
            public LibraryDownloads(MinecraftLibraryArtifact artifact) {
                this.artifact = artifact;
            }
    }
}

