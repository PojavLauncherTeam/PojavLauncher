package net.kdt.pojavlaunch.modloaders.modpacks.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kdt.mcgui.ProgressLayout;

import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.modloaders.modpacks.imagecache.ModIconCache;
import net.kdt.pojavlaunch.modloaders.modpacks.models.Constants;
import net.kdt.pojavlaunch.modloaders.modpacks.models.ModDetail;
import net.kdt.pojavlaunch.modloaders.modpacks.models.ModItem;
import net.kdt.pojavlaunch.modloaders.modpacks.models.ModrinthIndex;
import net.kdt.pojavlaunch.modloaders.modpacks.models.SearchFilters;
import net.kdt.pojavlaunch.progresskeeper.DownloaderProgressWrapper;
import net.kdt.pojavlaunch.utils.DownloadUtils;
import net.kdt.pojavlaunch.utils.ZipUtils;
import net.kdt.pojavlaunch.value.launcherprofiles.LauncherProfiles;
import net.kdt.pojavlaunch.value.launcherprofiles.MinecraftProfile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.zip.ZipFile;

public class ModrinthApi implements ModpackApi{
    private final ApiHandler mApiHandler;
    public ModrinthApi(){
        mApiHandler = new ApiHandler("https://api.modrinth.com/v2");
    }

    @Override
    public ModItem[] searchMod(SearchFilters searchFilters) {
        HashMap<String, Object> params = new HashMap<>();

        // Build the facets filters
        StringBuilder facetString = new StringBuilder();
        facetString.append("[");
        facetString.append(String.format("[\"project_type:%s\"]", searchFilters.isModpack ? "modpack" : "mod"));
        if(searchFilters.mcVersion != null && !searchFilters.mcVersion.isEmpty())
            facetString.append(String.format(",[\"versions:%s\"]", searchFilters.mcVersion));
        facetString.append("]");
        params.put("facets", facetString.toString());
        params.put("query", searchFilters.name);
        params.put("limit", 100);

        JsonObject response = mApiHandler.get("search", params, JsonObject.class);
        if(response == null) return null;
        JsonArray responseHits = response.getAsJsonArray("hits");
        if(responseHits == null) return null;

        ModItem[] items = new ModItem[responseHits.size()];
        for(int i=0; i<responseHits.size(); ++i){
            JsonObject hit = responseHits.get(i).getAsJsonObject();
            items[i] = new ModItem(
                    Constants.SOURCE_MODRINTH,
                    hit.get("project_type").getAsString().equals("modpack"),
                    hit.get("project_id").getAsString(),
                    hit.get("title").getAsString(),
                    hit.get("description").getAsString(),
                    hit.get("icon_url").getAsString()
            );
        }

        return items;
    }

    @Override
    public ModDetail getModDetails(ModItem item) {

        JsonArray response = mApiHandler.get(String.format("project/%s/version", item.id), JsonArray.class);
        if(response == null) return null;
        System.out.println(response);
        String[] names = new String[response.size()];
        String[] mcNames = new String[response.size()];
        String[] urls = new String[response.size()];

        for (int i=0; i<response.size(); ++i) {
            JsonObject version = response.get(i).getAsJsonObject();
            names[i] = version.get("name").getAsString();
            mcNames[i] = version.get("game_versions").getAsJsonArray().get(0).getAsString();
            urls[i] = version.get("files").getAsJsonArray().get(0).getAsJsonObject().get("url").getAsString();

            if (!names[i].contains(mcNames[i]))
                names[i] += " - " + mcNames[i];
        }

        return new ModDetail(item, names, mcNames, urls);
    }

    @Override
    public void installMod(ModDetail modDetail, int selectedVersion) {
        //TODO considering only modpacks for now
        String versionUrl = modDetail.versionUrls[selectedVersion];
        String modpackName = modDetail.title.toLowerCase(Locale.ROOT).trim().replace(" ", "_" );

        // Build a new minecraft instance, folder first

        // Get the mrpack
        File modpackFile = new File(Tools.DIR_CACHE, modpackName + ".mrpack");
        ModLoaderInfo modLoaderInfo;
        try {
            byte[] downloadBuffer = new byte[8192];
            DownloadUtils.downloadFileMonitored(versionUrl, modpackFile, downloadBuffer,
                    new DownloaderProgressWrapper(R.string.modpack_download_downloading_metadata,
                            ProgressLayout.INSTALL_MODPACK));
            ModrinthIndex modrinthIndex = installMrpack(modpackFile, new File(Tools.DIR_GAME_HOME, "custom_instances/"+modpackName));
            modLoaderInfo = createInfo(modrinthIndex);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            modpackFile.delete();
            ProgressLayout.clearProgress(ProgressLayout.INSTALL_MODPACK);
        }
        if(modLoaderInfo == null) {
            return;
        }

        // Create the instance
        MinecraftProfile profile = new MinecraftProfile();
        profile.gameDir = "./custom_instances/" + modpackName;
        profile.name = modDetail.title;
        profile.lastVersionId = modLoaderInfo.getVersionId();
        profile.icon = ModIconCache.getBase64Image(modDetail.getIconCacheTag());


        LauncherProfiles.mainProfileJson.profiles.put(modpackName, profile);
        LauncherProfiles.update();
    }

    private static ModLoaderInfo createInfo(ModrinthIndex modrinthIndex) {
        if(modrinthIndex == null) return null;
        Map<String, String> dependencies = modrinthIndex.dependencies;
        String mcVersion = dependencies.get("minecraft");
        if(mcVersion == null) return null;
        String modLoaderVersion;
        if((modLoaderVersion = dependencies.get("forge")) != null) {
            return new ModLoaderInfo(ModLoaderInfo.MOD_LOADER_FORGE, modLoaderVersion, mcVersion);
        }
        if((modLoaderVersion = dependencies.get("fabric-loader")) != null) {
            return new ModLoaderInfo(ModLoaderInfo.MOD_LOADER_FABRIC, modLoaderVersion, mcVersion);
        }
        if((modLoaderVersion = dependencies.get("quilt-loader")) != null) {
            throw new RuntimeException("Quilt is gay af");
            //return new ModLoaderInfo(ModLoaderInfo.MOD_LOADER_QUILT, modLoaderVersion, mcVersion);
        }
        return null;
    }

    private ModrinthIndex installMrpack(File mrpackFile, File instanceDestination) throws IOException {
        try (ZipFile modpackZipFile = new ZipFile(mrpackFile)){
            ModrinthIndex modrinthIndex = Tools.GLOBAL_GSON.fromJson(
                    Tools.read(ZipUtils.getEntryStream(modpackZipFile, "modrinth.index.json")),
                    ModrinthIndex.class);
            
            ModDownloader modDownloader = new ModDownloader(instanceDestination);
            for(ModrinthIndex.ModrinthIndexFile indexFile : modrinthIndex.files) {
                modDownloader.submitDownload(indexFile.fileSize, indexFile.path, indexFile.downloads);
            }
            modDownloader.awaitFinish(new DownloaderProgressWrapper(R.string.modpack_download_downloading_mods, ProgressLayout.INSTALL_MODPACK));
            ProgressLayout.setProgress(ProgressLayout.INSTALL_MODPACK, 0, R.string.modpack_download_applying_overrides, 1, 2);
            ZipUtils.zipExtract(modpackZipFile, "overrides/", instanceDestination);
            ProgressLayout.setProgress(ProgressLayout.INSTALL_MODPACK, 50, R.string.modpack_download_applying_overrides, 2, 2);
            ZipUtils.zipExtract(modpackZipFile, "client-overrides/", instanceDestination);
            return modrinthIndex;
        }
    }
}
