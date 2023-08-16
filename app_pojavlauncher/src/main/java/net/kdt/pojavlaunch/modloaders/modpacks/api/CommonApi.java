package net.kdt.pojavlaunch.modloaders.modpacks.api;

import androidx.annotation.NonNull;

import net.kdt.pojavlaunch.PojavApplication;
import net.kdt.pojavlaunch.modloaders.modpacks.models.Constants;
import net.kdt.pojavlaunch.modloaders.modpacks.models.ModDetail;
import net.kdt.pojavlaunch.modloaders.modpacks.models.ModItem;
import net.kdt.pojavlaunch.modloaders.modpacks.models.SearchFilters;

import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

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

        Future<?>[] futures = new Future<?>[mModpackApis.length];
        for(int i = 0; i < mModpackApis.length; i++) {
            futures[i] = PojavApplication.sExecutorService.submit(new ApiDownloadTask(i, searchFilters));
        }
        if(Thread.interrupted()) {
            cancelAllFutures(futures);
            return null;
        }
        for(int i = 0; i < mModpackApis.length; i++) {
            try {
                items[i] = (ModItem[]) futures[i].get();
                totalSize += items[i].length;
            }catch (Exception e) {
                cancelAllFutures(futures);
                e.printStackTrace();
                return null;
            }
        }
        // Then build an array with all the mods
        ModItem[] concatenatedItems = new ModItem[totalSize];
        int copyOffset = 0;
        for(ModItem[] apiItems : items) {
            System.arraycopy(apiItems, 0, concatenatedItems, copyOffset, apiItems.length);
            copyOffset += apiItems.length;
        }
        if(Thread.interrupted()) return null;
        Arrays.sort(concatenatedItems, (modItem, t1) -> modItem.title.compareToIgnoreCase(t1.title));
        if(Thread.interrupted()) return null;
        return concatenatedItems;
    }

    @Override
    public ModDetail getModDetails(ModItem item) {
        return getModpackApi(item.apiSource).getModDetails(item);
    }

    @Override
    public ModLoader installMod(ModDetail modDetail, int selectedVersion) {
        return getModpackApi(modDetail.apiSource).installMod(modDetail, selectedVersion);
    }

    private @NonNull ModpackApi getModpackApi(int apiSource) {
        switch (apiSource) {
            case Constants.SOURCE_MODRINTH:
                return mModrinthApi;
            case Constants.SOURCE_CURSEFORGE:
                return mCurseforgeApi;
            default:
                throw new UnsupportedOperationException("Unknown API source: " + apiSource);
        }
    }

    private void cancelAllFutures(Future<?>[] futures) {
        for(Future<?> future : futures) {
            if(future == null) continue;
            future.cancel(true);
        }
    }

    private class ApiDownloadTask implements Callable<ModItem[]> {
        private final int mModApi;
        private final SearchFilters mSearchFilters;

        private ApiDownloadTask(int modApi, SearchFilters searchFilters) {
            this.mModApi = modApi;
            this.mSearchFilters = searchFilters;
        }

        @Override
        public ModItem[] call() {
            return mModpackApis[mModApi].searchMod(mSearchFilters);
        }
    }
}
