package net.kdt.pojavlaunch.modloaders.modpacks.api;


import net.kdt.pojavlaunch.PojavApplication;
import net.kdt.pojavlaunch.modloaders.modpacks.models.ModDetail;
import net.kdt.pojavlaunch.modloaders.modpacks.models.ModItem;
import net.kdt.pojavlaunch.modloaders.modpacks.models.SearchFilters;
import net.kdt.pojavlaunch.progresskeeper.ProgressKeeper;

import org.jetbrains.annotations.Nullable;

/**
 *
 */
public interface ModpackApi {

    /**
     * @param searchFilters Filters
     * @return A list of mod items
     */
    ModItem[] searchMod(SearchFilters searchFilters);

    /**
     * Fetch the mod details
     * @param item The moditem that was selected
     * @return Detailed data about a mod(pack)
     */
    ModDetail getModDetails(ModItem item);

    /**
     * Download and install the mod(pack)
     * @param modDetail The mod detail data
     * @param selectedVersion The selected version
     */
    default void handleInstallation(ModDetail modDetail, int selectedVersion) {
        PojavApplication.sExecutorService.execute(() -> {
            installMod(modDetail, selectedVersion);
        });
    }

    /**
     * Install the mod(pack).
     * May require the download of additional files.
     * May requires launching the installation of a modloader
     * @param modDetail The mod detail data
     * @param selectedVersion The selected version
     */
    void installMod(ModDetail modDetail, int selectedVersion);
}
