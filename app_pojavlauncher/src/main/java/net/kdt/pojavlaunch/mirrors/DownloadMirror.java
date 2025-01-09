package net.kdt.pojavlaunch.mirrors;

import android.util.Log;

import androidx.annotation.Nullable;

import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.prefs.LauncherPreferences;
import net.kdt.pojavlaunch.utils.DownloadUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

public class DownloadMirror {
    public static final int DOWNLOAD_CLASS_LIBRARIES = 0;
    public static final int DOWNLOAD_CLASS_METADATA = 1;
    public static final int DOWNLOAD_CLASS_ASSETS = 2;

    private static final String URL_PROTOCOL_TAIL = "://";
    private static final String[] MIRROR_BMCLAPI = {
            "https://bmclapi2.bangbang93.com/maven",
            "https://bmclapi2.bangbang93.com",
            "https://bmclapi2.bangbang93.com/assets"
    };

    /**
     * Download a file with the current mirror. If the file is missing on the mirror,
     * fall back to the official source.
     * @param downloadClass Class of the download. Can either be DOWNLOAD_CLASS_LIBRARIES,
     *                      DOWNLOAD_CLASS_METADATA or DOWNLOAD_CLASS_ASSETS
     * @param urlInput The original (Mojang) URL for the download
     * @param outputFile The output file for the download
     * @param buffer The shared buffer
     * @param monitor The download monitor.
     */
    public static void downloadFileMirrored(int downloadClass, String urlInput, File outputFile,
                                            @Nullable byte[] buffer, Tools.DownloaderFeedback monitor) throws IOException {
        try {
            DownloadUtils.downloadFileMonitored(getMirrorMapping(downloadClass, urlInput),
                    outputFile, buffer, monitor);
            return;
        }catch (FileNotFoundException e) {
            Log.w("DownloadMirror", "Cannot find the file on the mirror", e);
            Log.i("DownloadMirror", "Falling back to default source");
        }
        DownloadUtils.downloadFileMonitored(urlInput, outputFile, buffer, monitor);
    }

    /**
     * Download a file with the current mirror. If the file is missing on the mirror,
     * fall back to the official source.
     * @param downloadClass Class of the download. Can either be DOWNLOAD_CLASS_LIBRARIES,
     *                      DOWNLOAD_CLASS_METADATA or DOWNLOAD_CLASS_ASSETS
     * @param urlInput The original (Mojang) URL for the download
     * @param outputFile The output file for the download
     */
    public static void downloadFileMirrored(int downloadClass, String urlInput, File outputFile) throws IOException {
        try {
            DownloadUtils.downloadFile(getMirrorMapping(downloadClass, urlInput),
                    outputFile);
            return;
        }catch (FileNotFoundException e) {
            Log.w("DownloadMirror", "Cannot find the file on the mirror", e);
            Log.i("DownloadMirror", "Falling back to default source");
        }
        DownloadUtils.downloadFile(urlInput, outputFile);
    }

    /**
     * Get the content length of a file on the current mirror. If the file is missing on the mirror,
     * or the mirror does not give out the length, request the length from the original source
     * @param downloadClass Class of the download. Can either be DOWNLOAD_CLASS_LIBRARIES,
     *                      DOWNLOAD_CLASS_METADATA or DOWNLOAD_CLASS_ASSETS
     * @param urlInput The original (Mojang) URL for the download
     * @return the length of the file denoted by the URL in bytes, or -1 if not available
     */
    public static long getContentLengthMirrored(int downloadClass, String urlInput) throws IOException {
        long length = DownloadUtils.getContentLength(getMirrorMapping(downloadClass, urlInput));
        if(length < 1) {
            Log.w("DownloadMirror", "Unable to get content length from mirror");
            Log.i("DownloadMirror", "Falling back to default source");
            return DownloadUtils.getContentLength(urlInput);
        }else {
            return length;
        }
    }

    /**
     * Check if the current download source is a mirror and not an official source.
     * @return true if the source is a mirror, false otherwise
     */
    public static boolean isMirrored() {
        return !LauncherPreferences.PREF_DOWNLOAD_SOURCE.equals("default");
    }

    private static String[] getMirrorSettings() {
        switch (LauncherPreferences.PREF_DOWNLOAD_SOURCE) {
            case "bmclapi": return MIRROR_BMCLAPI;
            case "default":
            default:
                return null;
        }
    }

    private static String getMirrorMapping(int downloadClass, String mojangUrl) throws MalformedURLException{
        String[] mirrorSettings = getMirrorSettings();
        if(mirrorSettings == null) return mojangUrl;
        int urlTail = getBaseUrlTail(mojangUrl);
        String baseUrl = mojangUrl.substring(0, urlTail);
        String path = mojangUrl.substring(urlTail);
        switch(downloadClass) {
            case DOWNLOAD_CLASS_ASSETS:
            case DOWNLOAD_CLASS_METADATA:
                baseUrl = mirrorSettings[downloadClass];
                break;
            case DOWNLOAD_CLASS_LIBRARIES:
                if(!baseUrl.endsWith("libraries.minecraft.net")) break;
                baseUrl = mirrorSettings[downloadClass];
                break;
        }
        return baseUrl + path;
    }

    private static int getBaseUrlTail(String wholeUrl) throws MalformedURLException{
        int protocolNameEnd = wholeUrl.indexOf(URL_PROTOCOL_TAIL);
        if(protocolNameEnd == -1)
            throw new MalformedURLException("No protocol, or non path-based URL");
        protocolNameEnd += URL_PROTOCOL_TAIL.length();
        int hostnameEnd = wholeUrl.indexOf('/', protocolNameEnd);
        if(protocolNameEnd >= wholeUrl.length() || hostnameEnd == protocolNameEnd)
            throw new MalformedURLException("No hostname");
        if(hostnameEnd == -1) hostnameEnd = wholeUrl.length();
        return hostnameEnd;
    }
}
