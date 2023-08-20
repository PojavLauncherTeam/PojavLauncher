package net.kdt.pojavlaunch.modloaders.modpacks.api;

import android.content.Context;
import android.content.Intent;

import net.kdt.pojavlaunch.JavaGUILauncherActivity;
import net.kdt.pojavlaunch.modloaders.FabricDownloadTask;
import net.kdt.pojavlaunch.modloaders.FabricUtils;
import net.kdt.pojavlaunch.modloaders.ForgeDownloadTask;
import net.kdt.pojavlaunch.modloaders.ForgeUtils;
import net.kdt.pojavlaunch.modloaders.ModloaderDownloadListener;

import java.io.File;

public class ModLoader {
    public static final int MOD_LOADER_FORGE = 0;
    public static final int MOD_LOADER_FABRIC = 1;
    public static final int MOD_LOADER_QUILT = 2;
    public final int modLoaderType;
    public final String modLoaderVersion;
    public final String minecraftVersion;

    public ModLoader(int modLoaderType, String modLoaderVersion, String minecraftVersion) {
        this.modLoaderType = modLoaderType;
        this.modLoaderVersion = modLoaderVersion;
        this.minecraftVersion = minecraftVersion;
    }

    /**
     * Get the Version ID (the name of the mod loader in the versions/ folder)
     * @return the Version ID as a string
     */
    public String getVersionId() {
        switch (modLoaderType) {
            case MOD_LOADER_FORGE:
                return minecraftVersion+"-forge-"+modLoaderVersion;
            case MOD_LOADER_FABRIC:
                return "fabric-loader-"+modLoaderVersion+"-"+minecraftVersion;
            case MOD_LOADER_QUILT:
                throw new RuntimeException("Quilt is not supported");
            default:
                return null;
        }
    }

    /**
     * Get the Runnable that needs to run in order to download the mod loader
     * @param listener the listener that gets notified of the installation status
     * @return the task Runnable that needs to be ran
     */
    public Runnable getDownloadTask(ModloaderDownloadListener listener) {
        switch (modLoaderType) {
            case MOD_LOADER_FORGE:
                return new ForgeDownloadTask(listener, minecraftVersion, modLoaderVersion);
            case MOD_LOADER_FABRIC:
                return new FabricDownloadTask(listener);
            case MOD_LOADER_QUILT:
                throw new RuntimeException("Quilt is not supported");
            default:
                return null;
        }
    }

    /**
     * Get the Intent to start the graphical installation of the mod loader.
     * This method should only be ran after the download task of the specified mod loader finishes.
     * @param context the package resolving Context (can be the base context)
     * @param modInstallerJar the JAR file of the mod installer, provided by ModloaderDownloadListener after the installation
     *                        finishes.
     * @return the Intent which the launcher needs to start in order to install the mod loader
     */
    public Intent getInstallationIntent(Context context, File modInstallerJar) {
        Intent baseIntent = new Intent(context, JavaGUILauncherActivity.class);
        switch (modLoaderType) {
            case MOD_LOADER_FORGE:
                ForgeUtils.addAutoInstallArgs(baseIntent, modInstallerJar, getVersionId());
                return baseIntent;
            case MOD_LOADER_FABRIC:
                FabricUtils.addAutoInstallArgs(baseIntent, modInstallerJar, minecraftVersion, modLoaderVersion, false, false);
                return baseIntent;
            case MOD_LOADER_QUILT:
                throw new RuntimeException("Quilt is not supported");
            default:
                return null;
        }
    }
}
