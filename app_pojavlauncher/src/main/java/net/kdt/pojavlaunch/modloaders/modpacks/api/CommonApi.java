package net.kdt.pojavlaunch.modloaders.modpacks.api;

import net.kdt.pojavlaunch.modloaders.modpacks.models.Constants;
import net.kdt.pojavlaunch.modloaders.modpacks.models.ModDetail;
import net.kdt.pojavlaunch.modloaders.modpacks.models.ModItem;
import net.kdt.pojavlaunch.modloaders.modpacks.models.SearchFilters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Group all apis under the same umbrella, as another layer of abstraction
 */
public class CommonApi implements ModpackApi {

    private final ModpackApi mCurseforgeApi =  new CurseforgeApi();
    private final ModpackApi mModrinthApi =  new ModrinthApi();
    private final ModpackApi[] mModpackApis = new ModpackApi[]{mModrinthApi, mCurseforgeApi};
    @Override
    public ModItem[] searchMod(SearchFilters searchFilters) {
        ModItem[][] items = new ModItem[mModpackApis.length][];
        int totalSize = 0;
        // TODO unoptimized as hell

        for(int i=0; i<mModpackApis.length; i++) {
            items[i] = mModpackApis[i].searchMod(searchFilters);
            totalSize += items[i].length;
        }

        // Then build an array with all the mods
        ModItem[] concatenatedItems = new ModItem[totalSize];
        int copyOffset = 0;
        for(ModItem[] apiItems : items) {
            System.arraycopy(apiItems, 0, concatenatedItems, copyOffset, apiItems.length);
            copyOffset += apiItems.length;
        }

        return concatenatedItems;
    }

    @Override
    public ModDetail getModDetails(ModItem item) {
        switch (item.apiSource) {
            case Constants.SOURCE_MODRINTH:
                return mModrinthApi.getModDetails(item);
            case Constants.SOURCE_CURSEFORGE:
                return mCurseforgeApi.getModDetails(item);
            default:
                throw new UnsupportedOperationException("Unknown API source: " + item.apiSource);
        }
    }

    @Override
    public ModLoader installMod(ModDetail modDetail, int selectedVersion) {
        switch (modDetail.apiSource) {
            case Constants.SOURCE_MODRINTH:
                return mModrinthApi.installMod(modDetail, selectedVersion);
            case Constants.SOURCE_CURSEFORGE:
                return mCurseforgeApi.installMod(modDetail, selectedVersion);
            default:
                throw new UnsupportedOperationException("Unknown API source: " + modDetail.apiSource);
        }
    }
}
