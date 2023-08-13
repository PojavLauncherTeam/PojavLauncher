package net.kdt.pojavlaunch.modloaders;

import com.kdt.mcgui.ProgressLayout;

import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.progresskeeper.ProgressKeeper;
import net.kdt.pojavlaunch.utils.DownloadUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ForgeDownloadTask implements Runnable, Tools.DownloaderFeedback {
    private final String mForgeUrl;
    private final String mForgeVersion;
    private final ModloaderDownloadListener mListener;
    public ForgeDownloadTask(ModloaderDownloadListener listener, String forgeVersion) {
        this.mListener = listener;
        this.mForgeUrl = ForgeUtils.getInstallerUrl(forgeVersion);
        this.mForgeVersion = forgeVersion;
    }
    @Override
    public void run() {
        ProgressKeeper.submitProgress(ProgressLayout.INSTALL_MODPACK, 0, R.string.forge_dl_progress, mForgeVersion);
        try {
            File destinationFile = new File(Tools.DIR_CACHE, "forge-installer.jar");
            byte[] buffer = new byte[8192];
            DownloadUtils.downloadFileMonitored(mForgeUrl, destinationFile, buffer, this);
            mListener.onDownloadFinished(destinationFile);
        }catch (IOException e) {
            if(e instanceof FileNotFoundException) {
                mListener.onDataNotAvailable();
            }else{
                mListener.onDownloadError(e);
            }
        }
        ProgressKeeper.submitProgress(ProgressLayout.INSTALL_MODPACK, -1, -1);
    }

    @Override
    public void updateProgress(int curr, int max) {
        int progress100 = (int)(((float)curr / (float)max)*100f);
        ProgressKeeper.submitProgress(ProgressLayout.INSTALL_MODPACK, progress100, R.string.forge_dl_progress, mForgeVersion);
    }

}
