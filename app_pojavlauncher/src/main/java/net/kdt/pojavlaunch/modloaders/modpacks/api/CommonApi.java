package net.kdt.pojavlaunch.modloaders.modpacks.api;

import android.util.Log;

import androidx.annotation.NonNull;

import net.kdt.pojavlaunch.PojavApplication;
import net.kdt.pojavlaunch.modloaders.modpacks.models.Constants;
import net.kdt.pojavlaunch.modloaders.modpacks.models.ModDetail;
import net.kdt.pojavlaunch.modloaders.modpacks.models.ModItem;
import net.kdt.pojavlaunch.modloaders.modpacks.models.SearchFilters;
import net.kdt.pojavlaunch.modloaders.modpacks.models.SearchResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * Group all apis under the same umbrella, as another layer of abstraction
 */
public class CommonApi implements ModpackApi {

    private final ModpackApi mCurseforgeApi;
    private final ModpackApi mModrinthApi;
    private final ModpackApi[] mModpackApis;

    public CommonApi(String curseforgeApiKey) {
        mCurseforgeApi = new CurseforgeApi(curseforgeApiKey);
        mModrinthApi = new ModrinthApi();
        mModpackApis = new ModpackApi[]{mModrinthApi, mCurseforgeApi};
    }

    @Override
    public SearchResult searchMod(SearchFilters searchFilters, SearchResult previousPageResult) {
        CommonApiSearchResult commonApiSearchResult = (CommonApiSearchResult) previousPageResult;
        // If there are no previous page results, create a new array. Otherwise, use the one from the previous page
        SearchResult[] results = commonApiSearchResult == null ?
                new SearchResult[mModpackApis.length] : commonApiSearchResult.searchResults;

        int totalSize = 0;

        Future<?>[] futures = new Future<?>[mModpackApis.length];
        for(int i = 0; i < mModpackApis.length; i++) {
            // If there is an array and its length is zero, this means that we've exhausted the results for this
            // search query and we don't need to actually do the search
            if(results[i] != null && results[i].results.length == 0) continue;
            // If the previous page result is not null (aka the arrays aren't fresh)
            // and the previous result is null, it means that na error has occured on the previous
            // page. We lost contingency anyway, so don't bother requesting.
            if(previousPageResult != null && results[i] == null) continue;
            futures[i] = PojavApplication.sExecutorService.submit(new ApiDownloadTask(i, searchFilters,
                    results[i]));
        }

        if(Thread.interrupted()) {
            cancelAllFutures(futures);
            return null;
        }
        boolean hasSuccessful = false;
        // Count up all the results
        for(int i = 0; i < mModpackApis.length; i++) {
            Future<?> future = futures[i];
            if(future == null) continue;
            try {
                SearchResult searchResult = results[i] = (SearchResult) future.get();
                if(searchResult != null) hasSuccessful = true;
                else continue;
                totalSize += searchResult.totalResultCount;
            }catch (Exception e) {
                cancelAllFutures(futures);
                e.printStackTrace();
                return null;
            }
        }
        if(!hasSuccessful) {
            return null;
        }
        // Then build an array with all the mods
        ArrayList<ModItem[]> filteredResults = new ArrayList<>(results.length);

        // Sanitize returned values
        for(SearchResult result : results) {
            if(result == null) continue;
            ModItem[] searchResults = result.results;
            // If the length is zero, we don't need to perform needless copies
            if(searchResults.length == 0) continue;
            filteredResults.add(searchResults);
        }
        filteredResults.trimToSize();
        if(Thread.interrupted()) return null;

        ModItem[] concatenatedItems = buildFusedResponse(filteredResults);
        if(Thread.interrupted()) return null;
        // Recycle or create new search result
        if(commonApiSearchResult == null) commonApiSearchResult = new CommonApiSearchResult();
        commonApiSearchResult.searchResults = results;
        commonApiSearchResult.totalResultCount = totalSize;
        commonApiSearchResult.results = concatenatedItems;
        return commonApiSearchResult;
    }

    @Override
    public ModDetail getModDetails(ModItem item) {
        Log.i("CommonApi", "Invoking getModDetails on item.apiSource="+item.apiSource +" item.title="+item.title);
        return getModpackApi(item.apiSource).getModDetails(item);
    }

    @Override
    public ModLoader installMod(ModDetail modDetail, int selectedVersion) throws IOException {
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

    /** Fuse the arrays in a way that's fair for every endpoint */
    private ModItem[] buildFusedResponse(List<ModItem[]> modMatrix){
        int totalSize = 0;

        // Calculate the total size of the merged array
        for (ModItem[] array : modMatrix) {
            totalSize += array.length;
        }

        ModItem[] fusedItems = new ModItem[totalSize];

        int mergedIndex = 0;
        int maxLength = 0;

        // Find the maximum length of arrays
        for (ModItem[] array : modMatrix) {
            if (array.length > maxLength) {
                maxLength = array.length;
            }
        }

        // Populate the merged array
        for (int i = 0; i < maxLength; i++) {
            for (ModItem[] matrix : modMatrix) {
                if (i < matrix.length) {
                    fusedItems[mergedIndex] = matrix[i];
                    mergedIndex++;
                }
            }
        }

        return fusedItems;
    }

    private void cancelAllFutures(Future<?>[] futures) {
        for(Future<?> future : futures) {
            if(future == null) continue;
            future.cancel(true);
        }
    }

    private class ApiDownloadTask implements Callable<SearchResult> {
        private final int mModApi;
        private final SearchFilters mSearchFilters;
        private final SearchResult mPreviousPageResult;

        private ApiDownloadTask(int modApi, SearchFilters searchFilters, SearchResult previousPageResult) {
            this.mModApi = modApi;
            this.mSearchFilters = searchFilters;
            this.mPreviousPageResult = previousPageResult;
        }

        @Override
        public SearchResult call() {
            return mModpackApis[mModApi].searchMod(mSearchFilters, mPreviousPageResult);
        }
    }

    class CommonApiSearchResult extends SearchResult {
        SearchResult[] searchResults = new SearchResult[mModpackApis.length];
    }
}
