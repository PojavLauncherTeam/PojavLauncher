package net.kdt.pojavlaunch.modloaders;

import com.kdt.mcgui.ProgressLayout;

import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.progresskeeper.ProgressKeeper;
import net.kdt.pojavlaunch.utils.DownloadUtils;
import net.kdt.pojavlaunch.value.launcherprofiles.LauncherProfiles;
import net.kdt.pojavlaunch.value.launcherprofiles.MinecraftProfile;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class FabricDownloadTask implements Runnable, Tools.DownloaderFeedback{
    private final ModloaderDownloadListener mModloaderDownloadListener;
    private final String mGameVersion;
    private final String mLoaderVersion;
    private final boolean mCreateProfile;
    public FabricDownloadTask(ModloaderDownloadListener modloaderDownloadListener, String mGameVersion, String mLoaderVersion, boolean mCreateProfile) {
        this.mModloaderDownloadListener = modloaderDownloadListener;
        this.mGameVersion = mGameVersion;
        this.mLoaderVersion = mLoaderVersion;
        this.mCreateProfile = mCreateProfile;
    }

    @Override
    public void run() {
        ProgressKeeper.submitProgress(ProgressLayout.INSTALL_MODPACK, 0, R.string.fabric_dl_progress);
        try {
            if(runCatching()) mModloaderDownloadListener.onDownloadFinished(null);
            else mModloaderDownloadListener.onDataNotAvailable();
        }catch (IOException e) {
            mModloaderDownloadListener.onDownloadError(e);
        }
        ProgressLayout.clearProgress(ProgressLayout.INSTALL_MODPACK);
    }

    private boolean runCatching() throws IOException{
        String fabricJson = DownloadUtils.downloadString(FabricUtils.createJsonDownloadUrl(mGameVersion, mLoaderVersion));
        String versionId;
        try {
            JSONObject fabricJsonObject = new JSONObject(fabricJson);
            versionId = fabricJsonObject.getString("id");
        }catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        File versionJsonDir = new File(Tools.DIR_HOME_VERSION, versionId);
        File versionJsonFile = new File(versionJsonDir, versionId+".json");
        if(versionJsonDir.isFile()) throw new IOException("Version destination directory is a file!");
        if(!versionJsonDir.exists() && !versionJsonDir.mkdirs()) throw new IOException("Failed to create version directory");
        Tools.write(versionJsonFile.getAbsolutePath(), fabricJson);
        if(mCreateProfile) {
            LauncherProfiles.load();
            MinecraftProfile fabricProfile = new MinecraftProfile();
            fabricProfile.lastVersionId = versionId;
            fabricProfile.name = "Minecraft " + mGameVersion + " with Fabric " + mLoaderVersion;
            Map<String, MinecraftProfile> profiles = LauncherProfiles.mainProfileJson.profiles;
            String uuid = UUID.randomUUID().toString();
            while (profiles.get(uuid) != null) uuid = UUID.randomUUID().toString();
            profiles.put(uuid, fabricProfile);
            LauncherProfiles.write();
        }
        return true;
    }

    @Override
    public void updateProgress(int curr, int max) {
        int progress100 = (int)(((float)curr / (float)max)*100f);
        ProgressKeeper.submitProgress(ProgressLayout.INSTALL_MODPACK, progress100, R.string.fabric_dl_progress);
    }
}
