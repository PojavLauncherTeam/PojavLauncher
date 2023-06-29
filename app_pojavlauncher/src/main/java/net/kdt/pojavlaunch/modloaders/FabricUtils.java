package net.kdt.pojavlaunch.modloaders;

import android.content.Intent;

import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.utils.DownloadUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FabricUtils {
    private static final String FABRIC_INSTALLER_METADATA_URL = "https://meta.fabricmc.net/v2/versions/installer";
    private static final String FABRIC_LOADER_METADATA_URL = "https://meta.fabricmc.net/v2/versions/loader";
    public static List<String> downloadLoaderVersionList(boolean onlyStable) throws IOException {
        try {
            return DownloadUtils.downloadStringCached(FABRIC_LOADER_METADATA_URL,
                    "fabric_loader_versions", (input)->{
                        final List<String> loaderList = new ArrayList<>();
                        try {
                            enumerateMetadata(input, (object) -> {
                                if (onlyStable && !object.getBoolean("stable")) return false;
                                loaderList.add(object.getString("version"));
                                return false;
                            });
                        }catch (JSONException e) {
                            throw new DownloadUtils.ParseException(e);
                        }
                        return loaderList;
                    });
        }catch (DownloadUtils.ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String[] getInstallerUrlAndVersion() throws IOException{
        String installerMetadata = DownloadUtils.downloadString(FABRIC_INSTALLER_METADATA_URL);
        try {
            return DownloadUtils.downloadStringCached(FABRIC_INSTALLER_METADATA_URL,
                    "fabric_installer_versions", input -> {
                        try {
                            JSONObject selectedMetadata = enumerateMetadata(installerMetadata, (object) ->
                                    object.getBoolean("stable"));
                            if (selectedMetadata == null) return null;
                            return new String[]{selectedMetadata.getString("url"),
                                    selectedMetadata.getString("version")};
                        } catch (JSONException e) {
                            throw new DownloadUtils.ParseException(e);
                        }
                    });
        }catch (DownloadUtils.ParseException e) {
            e.printStackTrace();
            return null;
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

    private static JSONObject enumerateMetadata(String inputMetadata, FabricMetaReader metaReader) throws JSONException{
            JSONArray fullMetadata = new JSONArray(inputMetadata);
            JSONObject metadataObject = null;
            for(int i = 0; i < fullMetadata.length(); i++) {
                metadataObject = fullMetadata.getJSONObject(i);
                if(metaReader.processMetadata(metadataObject)) return metadataObject;
            }
            return metadataObject;
    }
}
