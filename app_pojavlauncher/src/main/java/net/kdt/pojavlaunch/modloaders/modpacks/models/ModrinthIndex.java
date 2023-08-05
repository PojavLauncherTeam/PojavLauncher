package net.kdt.pojavlaunch.modloaders.modpacks.models;


import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

/**
 * POJO to represent the modrinth index inside mrpacks
 */
public class ModrinthIndex {


    public int formatVersion;
    public String game;
    public String versionId;
    public String name;
    public String summary;

    public ModrinthIndexFile[] files;


    public static class ModrinthIndexFile {
        public String path;
        public String[] downloads;
        public int fileSize;

        public ModrinthIndexFileHashes hashes;

        @Nullable public ModrinthIndexFileEnv env;

        @Override
        public String toString() {
            return "ModrinthIndexFile{" +
                    "path='" + path + '\'' +
                    ", downloads=" + Arrays.toString(downloads) +
                    ", fileSize=" + fileSize +
                    ", hashes=" + hashes +
                    '}';
        }

        public static class ModrinthIndexFileHashes {
            public String sha1;
            public String sha512;

            @Override
            public String toString() {
                return "ModrinthIndexFileHashes{" +
                        "sha1='" + sha1 + '\'' +
                        ", sha512='" + sha512 + '\'' +
                        '}';
            }
        }

        public static class ModrinthIndexFileEnv {
            public String client;
            public String server;

            @Override
            public String toString() {
                return "ModrinthIndexFileEnv{" +
                        "client='" + client + '\'' +
                        ", server='" + server + '\'' +
                        '}';
            }
        }
    }


    public static class ModrinthIndexDependencies {
        @Nullable public String minecraft;
        @Nullable public String forge;
        @SerializedName("fabric-loader")
        @Nullable public String fabricLoader;
        @SerializedName("quilt-loader")
        @Nullable public String quiltLoader;

        @Override
        public String toString() {
            return "ModrinthIndexDependencies{" +
                    "minecraft='" + minecraft + '\'' +
                    ", forge='" + forge + '\'' +
                    ", fabricLoader='" + fabricLoader + '\'' +
                    ", quiltLoader='" + quiltLoader + '\'' +
                    '}';
        }

    }

    @Override
    public String toString() {
        return "ModrinthIndex{" +
                "formatVersion=" + formatVersion +
                ", game='" + game + '\'' +
                ", versionId='" + versionId + '\'' +
                ", name='" + name + '\'' +
                ", summary='" + summary + '\'' +
                ", files=" + Arrays.toString(files) +
                '}';
    }

}
