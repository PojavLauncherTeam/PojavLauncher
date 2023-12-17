package net.kdt.pojavlaunch.modloaders;

import com.kdt.mcgui.ProgressLayout;

import net.kdt.pojavlaunch.JMinecraftVersionList;
import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.progresskeeper.ProgressKeeper;
import net.kdt.pojavlaunch.tasks.AsyncMinecraftDownloader;
import net.kdt.pojavlaunch.tasks.MinecraftDownloader;
import net.kdt.pojavlaunch.utils.DownloadUtils;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OptiFineDownloadTask implements Runnable, Tools.DownloaderFeedback, AsyncMinecraftDownloader.DoneListener {
    private static final Pattern sMcVersionPattern = Pattern.compile("([0-9]+)\\.([0-9]+)\\.?([0-9]+)?");
    private final OptiFineUtils.OptiFineVersion mOptiFineVersion;
    private final File mDestinationFile;
    private final ModloaderDownloadListener mListener;
    private final Object mMinecraftDownloadLock = new Object();
    private Throwable mDownloaderThrowable;

    public OptiFineDownloadTask(OptiFineUtils.OptiFineVersion mOptiFineVersion, ModloaderDownloadListener mListener) {
        this.mOptiFineVersion = mOptiFineVersion;
        this.mDestinationFile = new File(Tools.DIR_CACHE, "optifine-installer.jar");
        this.mListener = mListener;
    }

    @Override
    public void run() {
        ProgressKeeper.submitProgress(ProgressLayout.INSTALL_MODPACK, 0, R.string.of_dl_progress, mOptiFineVersion.versionName);
        try {
            if(runCatching()) mListener.onDownloadFinished(mDestinationFile);
        }catch (IOException e) {
            mListener.onDownloadError(e);
        }
        ProgressLayout.clearProgress(ProgressLayout.INSTALL_MODPACK);
    }

    public boolean runCatching() throws IOException {
        String downloadUrl = scrapeDownloadsPage();
        if(downloadUrl == null) return false;
        String minecraftVersion = determineMinecraftVersion();
        if(minecraftVersion == null) return false;
        if(!downloadMinecraft(minecraftVersion)) {
            if(mDownloaderThrowable instanceof Exception) {
                mListener.onDownloadError((Exception) mDownloaderThrowable);
            }else {
                Exception exception = new Exception(mDownloaderThrowable);
                mListener.onDownloadError(exception);
            }
            return false;
        }
        DownloadUtils.downloadFileMonitored(downloadUrl, mDestinationFile, new byte[8192], this);
        return true;
    }

    public String scrapeDownloadsPage() throws IOException{
        String scrapeResult = OFDownloadPageScraper.run(mOptiFineVersion.downloadUrl);
        if(scrapeResult == null) mListener.onDataNotAvailable();
        return scrapeResult;
    }

    public String determineMinecraftVersion() {
        Matcher matcher = sMcVersionPattern.matcher(mOptiFineVersion.minecraftVersion);
        if(matcher.find()) {
            StringBuilder mcVersionBuilder = new StringBuilder();
            mcVersionBuilder.append(matcher.group(1));
            mcVersionBuilder.append('.');
            mcVersionBuilder.append(matcher.group(2));
            String thirdGroup = matcher.group(3);
            if(thirdGroup != null && !thirdGroup.isEmpty() && !"0".equals(thirdGroup)) {
                mcVersionBuilder.append('.');
                mcVersionBuilder.append(thirdGroup);
            }
            return mcVersionBuilder.toString();
        }else{
            mListener.onDataNotAvailable();
            return null;
        }
    }

    public boolean downloadMinecraft(String minecraftVersion) {
        // the string is always normalized
        JMinecraftVersionList.Version minecraftJsonVersion = AsyncMinecraftDownloader.getListedVersion(minecraftVersion);
        if(minecraftJsonVersion == null) return false;
        try {
            synchronized (mMinecraftDownloadLock) {
                new MinecraftDownloader().start(null, minecraftJsonVersion, minecraftVersion, this);
                mMinecraftDownloadLock.wait();
            }
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
        return mDownloaderThrowable == null;
    }

    @Override
    public void updateProgress(int curr, int max) {
        int progress100 = (int)(((float)curr / (float)max)*100f);
        ProgressKeeper.submitProgress(ProgressLayout.INSTALL_MODPACK, progress100, R.string.of_dl_progress, mOptiFineVersion.versionName);
    }

    @Override
    public void onDownloadDone() {
        synchronized (mMinecraftDownloadLock) {
            mDownloaderThrowable = null;
            mMinecraftDownloadLock.notifyAll();
        }
    }

    @Override
    public void onDownloadFailed(Throwable throwable) {
        synchronized (mMinecraftDownloadLock) {
            mDownloaderThrowable = throwable;
            mMinecraftDownloadLock.notifyAll();
        }
    }
}
