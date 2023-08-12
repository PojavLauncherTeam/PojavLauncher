package net.kdt.pojavlaunch.modloaders.modpacks.api;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.modloaders.modpacks.models.Constants;
import net.kdt.pojavlaunch.modloaders.modpacks.models.CurseManifest;
import net.kdt.pojavlaunch.modloaders.modpacks.models.ModDetail;
import net.kdt.pojavlaunch.modloaders.modpacks.models.ModItem;
import net.kdt.pojavlaunch.modloaders.modpacks.models.ModrinthIndex;
import net.kdt.pojavlaunch.modloaders.modpacks.models.SearchFilters;
import net.kdt.pojavlaunch.utils.ZipUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.zip.ZipFile;

public class CurseforgeApi implements ModpackApi{
    private static final Pattern sMcVersionPattern = Pattern.compile("([0-9]+)\\.([0-9]+)\\.?([0-9]+)?");
    // Stolen from
    // https://github.com/AnzhiZhang/CurseForgeModpackDownloader/blob/6cb3f428459f0cc8f444d16e54aea4cd1186fd7b/utils/requester.py#L93
    private static final int CURSEFORGE_MINECRAFT_GAME_ID = 432;
    private static final int CURSEFORGE_MODPACK_CLASS_ID = 4471;
    // https://api.curseforge.com/v1/categories?gameId=432 and search for "Mods" (case-sensitive)
    private static final int CURSEFORGE_MOD_CLASS_ID = 6;
    private static final int CURSEFORGE_PAGINATION_SIZE = 50;

    private final ApiHandler mApiHandler = new ApiHandler("https://api.curseforge.com/v1", "$2a$10$Vxkj4kH1Ekf8EsS4Mx8b2eVTHsht107Lk2erVEUtnbqvojsLy.jYq");

    @Override
    public ModItem[] searchMod(SearchFilters searchFilters) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("gameId", CURSEFORGE_MINECRAFT_GAME_ID);
        params.put("classId", searchFilters.isModpack ? CURSEFORGE_MODPACK_CLASS_ID : CURSEFORGE_MOD_CLASS_ID);
        params.put("searchFilter", searchFilters.name);
        if(searchFilters.mcVersion != null && !searchFilters.mcVersion.isEmpty())
            params.put("gameVersion", searchFilters.mcVersion);
        JsonObject response = mApiHandler.get("mods/search", params, JsonObject.class);
        if(response == null) return null;
        JsonArray dataArray = response.getAsJsonArray("data");
        if(dataArray == null) return null;
        ArrayList<ModItem> modItemList = new ArrayList<>(dataArray.size());
        for(int i = 0; i < dataArray.size(); i++) {
            JsonObject dataElement = dataArray.get(i).getAsJsonObject();
            JsonElement allowModDistribution = dataElement.get("allowModDistribution");
            // Gson automatically casts null to false, which leans to issues
            // So, only check the distribution flag if it is non-null
            if(!allowModDistribution.isJsonNull() && !allowModDistribution.getAsBoolean()) {
                Log.i("CurseforgeApi", "Skipping modpack "+dataElement.get("name").getAsString() + " because curseforge sucks");
                continue;
            }
            ModItem modItem = new ModItem(Constants.SOURCE_CURSEFORGE,
                    searchFilters.isModpack,
                    dataElement.get("id").getAsString(),
                    dataElement.get("name").getAsString(),
                    dataElement.get("summary").getAsString(),
                    dataElement.getAsJsonObject("logo").get("thumbnailUrl").getAsString());
            modItemList.add(modItem);
        }
        return modItemList.toArray(new ModItem[0]);
    }

    @Override
    public ModDetail getModDetails(ModItem item) {
        ArrayList<JsonObject> allModDetails = new ArrayList<>();
        int index = 0;
        while(index != -1 && index != -2) {
            index = getPaginatedDetails(allModDetails, index, item.id);
        }
        if(index == -2) return null;
        int length = allModDetails.size();
        String[] versionNames = new String[length];
        String[] mcVersionNames = new String[length];
        String[] versionUrls = new String[length];
        for(int i = 0; i < allModDetails.size(); i++) {
            JsonObject modDetail = allModDetails.get(i);
            versionNames[i] = modDetail.get("displayName").getAsString();
            JsonElement downloadUrl = modDetail.get("downloadUrl");
            versionUrls[i] = downloadUrl.getAsString();
            JsonArray gameVersions = modDetail.getAsJsonArray("gameVersions");
            for(JsonElement jsonElement : gameVersions) {
                String gameVersion = jsonElement.getAsString();
                if(!sMcVersionPattern.matcher(gameVersion).matches()) {
                    continue;
                }
                mcVersionNames[i] = gameVersion;
                break;
            }
        }
        return new ModDetail(item, versionNames, mcVersionNames, versionUrls);
    }

    @Override
    public void installMod(ModDetail modDetail, int selectedVersion) {
        String versionUrl = modDetail.versionUrls[selectedVersion];
        File modpackFile = new File(Tools.DIR_CACHE, modDetail.id+".zip");
    }


    private int getPaginatedDetails(ArrayList<JsonObject> objectList, int index, String modId) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("index", index);
        params.put("pageSize", CURSEFORGE_PAGINATION_SIZE);
        JsonObject response = mApiHandler.get("mods/"+modId+"/files", params, JsonObject.class);
        if(response == null) return -2;
        JsonArray data = response.getAsJsonArray("data");
        if(data == null) return -2;
        for(int i = 0; i < data.size(); i++) {
            JsonObject fileInfo = data.get(i).getAsJsonObject();
            if(fileInfo.get("isServerPack").getAsBoolean()) continue;
            objectList.add(fileInfo);
        }
        if(data.size() < CURSEFORGE_PAGINATION_SIZE) {
            return -1; // we read the remainder! yay!
        }
        return index + data.size();
    }

    private ModLoaderInfo installCurseforgeZip(File zipFile, File instanceDestination) throws IOException {
        try (ZipFile modpackZipFile = new ZipFile(zipFile)){
            CurseManifest curseManifest = Tools.GLOBAL_GSON.fromJson(
                    Tools.read(ZipUtils.getEntryStream(modpackZipFile, "manifest.json")),
                    CurseManifest.class);
        }
        return null;
    }
}
