package net.kdt.pojavlaunch.mirrors;

import android.app.AlertDialog;
import android.content.Context;
import android.text.Html;
import android.util.Log;

import androidx.annotation.Nullable;

import net.kdt.pojavlaunch.R;
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

    private static final String[] MIRROR_BMCLAPI = {
            "https://bmclapi2.bangbang93.com/maven",
            "https://bmclapi2.bangbang93.com",
            "https://bmclapi2.bangbang93.com/assets"
    };

    private static final String[] MIRROR_MCBBS = {
            "https://download.mcbbs.net/maven",
            "https://download.mcbbs.net",
            "https://download.mcbbs.net/assets"
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
            Log.i("DownloadMirror", "Failling back to default source");
        }
        DownloadUtils.downloadFileMonitored(urlInput, outputFile, buffer, monitor);
    }

    public static boolean isMirrored() {
        return !LauncherPreferences.PREF_DOWNLOAD_SOURCE.equals("default");
    }

    public static boolean checkForTamperedException(Context context, Throwable e) {
        if(e instanceof MirrorTamperedException){
            showMirrorTamperedDialog(context);
            return true;
        }
        return false;
    }

    private static void showMirrorTamperedDialog(Context ctx) {
        Tools.runOnUiThread(()->{
            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            builder.setTitle(R.string.dl_tampered_manifest_title);
            builder.setMessage(Html.fromHtml(ctx.getString(R.string.dl_tampered_manifest)));
            builder.setPositiveButton(R.string.dl_switch_to_official_site,(d,w)->{
                LauncherPreferences.DEFAULT_PREF.edit().putString("downloadSource", "default").apply();
                LauncherPreferences.PREF_DOWNLOAD_SOURCE = "default";

            });
            builder.setNegativeButton(R.string.dl_turn_off_manifest_checks,(d,w)->{
                LauncherPreferences.DEFAULT_PREF.edit().putBoolean("verifyManifest", false).apply();
                LauncherPreferences.PREF_VERIFY_MANIFEST = false;
            });
            builder.setNeutralButton(android.R.string.cancel, (d,w)->{});
            builder.show();
        });
    }

    private static String[] getMirrorSettings() {
        switch (LauncherPreferences.PREF_DOWNLOAD_SOURCE) {
            case "mcbbs": return MIRROR_MCBBS;
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
        int protocolNameEnd = wholeUrl.indexOf("://");
        if(protocolNameEnd == -1) throw new MalformedURLException("No protocol");
        protocolNameEnd += 3;
        return wholeUrl.indexOf('/', protocolNameEnd);
    }
}
