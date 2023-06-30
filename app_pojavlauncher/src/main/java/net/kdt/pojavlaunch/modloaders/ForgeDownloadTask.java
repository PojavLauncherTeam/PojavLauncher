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
    private final File mDestinationFile;
    private final ModloaderDownloadListener mListener;
    public ForgeDownloadTask(ModloaderDownloadListener listener, String forgeVersion, File destinationFile) {
        this.mListener = listener;
        this.mForgeUrl = ForgeUtils.getInstallerUrl(forgeVersion);
        this.mForgeVersion = forgeVersion;
        this.mDestinationFile = destinationFile;
    }
    @Override
    public void run() {
        ProgressKeeper.submitProgress(ProgressLayout.INSTALL_MODPACK, 0, R.string.forge_dl_progress, mForgeVersion);
        try {
            byte[] buffer = new byte[8192];
            DownloadUtils.downloadFileMonitored(mForgeUrl, mDestinationFile, buffer, this);
            mListener.onDownloadFinished(mDestinationFile);
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
