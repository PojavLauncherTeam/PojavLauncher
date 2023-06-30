package net.kdt.pojavlaunch.modloaders;

import com.kdt.mcgui.ProgressLayout;

import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.progresskeeper.ProgressKeeper;
import net.kdt.pojavlaunch.utils.DownloadUtils;

import java.io.File;
import java.io.IOException;

public class OptiFineDownloadTask implements Runnable, Tools.DownloaderFeedback{
    private final OptiFineUtils.OptiFineVersion mOptiFineVersion;
    private final File mDestinationFile;
    private final ModloaderDownloadListener mListener;

    public OptiFineDownloadTask(OptiFineUtils.OptiFineVersion mOptiFineVersion, File mDestinationFile, ModloaderDownloadListener mListener) {
        this.mOptiFineVersion = mOptiFineVersion;
        this.mDestinationFile = mDestinationFile;
        this.mListener = mListener;
    }

    @Override
    public void run() {
        ProgressKeeper.submitProgress(ProgressLayout.INSTALL_MODPACK, 0, R.string.of_dl_progress, mOptiFineVersion.downloadUrl);
        try {
            if(runCatching()) mListener.onDownloadFinished(mDestinationFile);
        }catch (IOException e) {
            mListener.onDownloadError(e);
        }
        ProgressKeeper.submitProgress(ProgressLayout.INSTALL_MODPACK, -1, -1);
    }

    public boolean runCatching() throws IOException {
        String downloadUrl = scrapeDownloadsPage();
        if(downloadUrl == null) return false;
        DownloadUtils.downloadFileMonitored(downloadUrl, mDestinationFile, new byte[8192], this);
        return true;
    }

    public String scrapeDownloadsPage() throws IOException{
        String scrapeResult = OFDownloadPageScraper.run(mOptiFineVersion.downloadUrl);
        if(scrapeResult == null) mListener.onDataNotAvailable();
        return scrapeResult;
    }

    @Override
    public void updateProgress(int curr, int max) {
        int progress100 = (int)(((float)curr / (float)max)*100f);
        ProgressKeeper.submitProgress(ProgressLayout.INSTALL_MODPACK, progress100, R.string.of_dl_progress, mOptiFineVersion.versionName);
    }
}
