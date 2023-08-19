package net.kdt.pojavlaunch.modloaders;

import com.kdt.mcgui.ProgressLayout;

import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.progresskeeper.ProgressKeeper;
import net.kdt.pojavlaunch.utils.DownloadUtils;

import java.io.File;
import java.io.IOException;

public class FabricDownloadTask implements Runnable, Tools.DownloaderFeedback{
    private final File mDestinationDir;
    private final File mDestinationFile;
    private final ModloaderDownloadListener mModloaderDownloadListener;

    public FabricDownloadTask(ModloaderDownloadListener modloaderDownloadListener) {
        this.mModloaderDownloadListener = modloaderDownloadListener;
        this.mDestinationDir = new File(Tools.DIR_CACHE, "fabric-installer");
        this.mDestinationFile = new File(mDestinationDir, "fabric-installer.jar");
    }

    @Override
    public void run() {
        ProgressKeeper.submitProgress(ProgressLayout.INSTALL_MODPACK, 0, R.string.fabric_dl_progress);
        try {
            if(runCatching()) mModloaderDownloadListener.onDownloadFinished(mDestinationFile);
        }catch (IOException e) {
            mModloaderDownloadListener.onDownloadError(e);
        }
        ProgressLayout.clearProgress(ProgressLayout.INSTALL_MODPACK);
    }

    private boolean runCatching() throws IOException {
        if(!mDestinationDir.exists() && !mDestinationDir.mkdirs()) throw new IOException("Failed to create cache directory");
        String[] urlAndVersion = FabricUtils.getInstallerUrlAndVersion();
        if(urlAndVersion == null) {
            mModloaderDownloadListener.onDataNotAvailable();
            return false;
        }
        File versionFile = new File(mDestinationDir, "fabric-installer-version");
        boolean shouldDownloadInstaller = true;
        if(urlAndVersion[1] != null && versionFile.canRead()) { // if we know the latest version that we have and the server has
            try {
                shouldDownloadInstaller = !urlAndVersion[1].equals(Tools.read(versionFile.getAbsolutePath()));
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(shouldDownloadInstaller) {
            if (urlAndVersion[0] != null) {
                byte[] buffer = new byte[8192];
                DownloadUtils.downloadFileMonitored(urlAndVersion[0], mDestinationFile, buffer, this);
                if(urlAndVersion[1] != null) {
                    try {
                        Tools.write(versionFile.getAbsolutePath(), urlAndVersion[1]);
                    }catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return true;
            } else {
                mModloaderDownloadListener.onDataNotAvailable();
                return false;
            }
        }else{
            return true;
        }
    }

    @Override
    public void updateProgress(int curr, int max) {
        int progress100 = (int)(((float)curr / (float)max)*100f);
        ProgressKeeper.submitProgress(ProgressLayout.INSTALL_MODPACK, progress100, R.string.fabric_dl_progress);
    }
}
