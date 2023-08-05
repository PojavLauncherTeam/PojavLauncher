package net.kdt.pojavlaunch.modloaders.modpacks.api;


import net.kdt.pojavlaunch.PojavApplication;
import net.kdt.pojavlaunch.modloaders.modpacks.models.ModDetail;
import net.kdt.pojavlaunch.modloaders.modpacks.models.ModItem;
import net.kdt.pojavlaunch.progresskeeper.ProgressKeeper;

/**
 *
 */
public interface ModpackApi {

    /**
     * @param searchModpack Whether we are searching mod or modpacks
     * @param minecraftVersion The minecraft version we want
     * @param name The name of the mod(pack)
     * @return A list of mod items
     */
    ModItem[] searchMod(boolean searchModpack, String minecraftVersion, String name);

    /**
     * Fetch the mod details
     * @param item The moditem that was selected
     * @param targetMcVersion The desired version
     * @return Detailed data about a mod(pack)
     */
    ModDetail getModDetails(ModItem item, String targetMcVersion);

    /**
     * Download and install the mod(pack)
     * @param modDetail The mod detail data
     * @param versionUrl The version that has been selected by the user
     * @param mcVersion The selected mc version
     */
    default void handleInstallation(ModDetail modDetail, String versionUrl, String mcVersion) {
        PojavApplication.sExecutorService.execute(() -> {
            installMod(modDetail, versionUrl, mcVersion);
        });
    }

    /**
     * Install the mod(pack).
     * May require the download of additional files.
     * May requires launching the installation of a modloader
     * @param modDetail The mod detail data
     * @param versionUrl The version that has been selected by the user
     * @param mcVersion The selected mc version
     */
    void installMod(ModDetail modDetail, String versionUrl, String mcVersion);
}
