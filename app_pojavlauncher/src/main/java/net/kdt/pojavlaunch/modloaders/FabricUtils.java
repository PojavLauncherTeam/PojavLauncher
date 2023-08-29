package net.kdt.pojavlaunch.modloaders;

import android.content.Intent;

import com.google.gson.JsonSyntaxException;

import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.utils.DownloadUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class FabricUtils {
    private static final String FABRIC_LOADER_METADATA_URL = "https://meta.fabricmc.net/v2/versions/loader/%s";
    private static final String FABRIC_GAME_METADATA_URL = "https://meta.fabricmc.net/v2/versions/game";

    private static final String FABRIC_JSON_DOWNLOAD_URL = "https://meta.fabricmc.net/v2/versions/loader/%s/%s/profile/json";

    public static FabricVersion[] downloadGameVersions() throws IOException{
        try {
            return DownloadUtils.downloadStringCached(FABRIC_GAME_METADATA_URL, "fabric_game_versions",
                    FabricUtils::deserializeRawVersions
            );
        }catch (DownloadUtils.ParseException ignored) {}
        return null;
    }

    public static FabricVersion[] downloadLoaderVersions(String gameVersion) throws IOException{
        try {
            String urlEncodedGameVersion = URLEncoder.encode(gameVersion, "UTF-8");
            return DownloadUtils.downloadStringCached(String.format(FABRIC_LOADER_METADATA_URL, urlEncodedGameVersion),
                    "fabric_loader_versions."+urlEncodedGameVersion,
                    (input)->{ try {
                        return deserializeLoaderVersions(input);
                    }catch (JSONException e) {
                        throw new DownloadUtils.ParseException(e);
                    }});

        }catch (DownloadUtils.ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String createJsonDownloadUrl(String gameVersion, String loaderVersion) {
        try {
            gameVersion = URLEncoder.encode(gameVersion, "UTF-8");
            loaderVersion = URLEncoder.encode(loaderVersion, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return String.format(FABRIC_JSON_DOWNLOAD_URL, gameVersion, loaderVersion);
    }

    private static FabricVersion[] deserializeLoaderVersions(String input) throws JSONException {
        JSONArray jsonArray = new JSONArray(input);
        FabricVersion[] fabricVersions = new FabricVersion[jsonArray.length()];
        for(int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i).getJSONObject("loader");
            FabricVersion fabricVersion = new FabricVersion();
            fabricVersion.version = jsonObject.getString("version");
            fabricVersion.stable = jsonObject.getBoolean("stable");
            fabricVersions[i] = fabricVersion;
        }
        return fabricVersions;
    }

    private static FabricVersion[] deserializeRawVersions(String jsonArrayIn) throws DownloadUtils.ParseException {
        try {
            return Tools.GLOBAL_GSON.fromJson(jsonArrayIn, FabricVersion[].class);
        }catch (JsonSyntaxException e) {
            e.printStackTrace();
            throw new DownloadUtils.ParseException(null);
        }
    }

    public static void addAutoInstallArgs(Intent intent, File modInstalllerJar,
                                          String gameVersion, String loaderVersion,
                                          boolean isSnapshot, boolean createProfile) {
        intent.putExtra("javaArgs", "-jar " + modInstalllerJar.getAbsolutePath() + " client -dir "+ Tools.DIR_GAME_NEW
        + " -mcversion "+gameVersion +" -loader "+loaderVersion +
                (isSnapshot ? " -snapshot" : "") +
                (createProfile ? "" : " -noprofile"));
        intent.putExtra("openLogOutput", true);

    }
}
