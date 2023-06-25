package net.kdt.pojavlaunch.modloaders;

public interface ForgeDownloadListener {
    void onDownloadFinished();
    void onInstallerNotAvailable();
    void onDownloadError(Exception e);
}
