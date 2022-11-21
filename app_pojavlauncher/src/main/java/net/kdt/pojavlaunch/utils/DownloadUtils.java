package net.kdt.pojavlaunch.utils;

import android.util.Log;
import androidx.annotation.Nullable;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import net.kdt.pojavlaunch.Logger;
import net.kdt.pojavlaunch.Tools;
import org.apache.commons.io.IOUtils;
import net.kdt.pojavlaunch.prefs.LauncherPreferences;

public class DownloadUtils {
    public static final String USER_AGENT = Tools.APP_NAME;
    public static final Charset utf8 = Charset.forName("UTF-8");

    public static void download(String url, OutputStream os) throws IOException {
        download(new URL(url), os);
    }

    public static void download(URL url, OutputStream os) throws IOException {
        InputStream is = null;
        try {
            // System.out.println("Connecting: " + url.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("User-Agent", USER_AGENT);
            conn.setConnectTimeout(10000);
            conn.setDoInput(true);
            conn.connect();
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException("Server returned HTTP " + conn.getResponseCode()
                        + ": " + conn.getResponseMessage());
            }
            is = conn.getInputStream();
            IOUtils.copy(is, os);
        } catch (IOException e) {
            throw new IOException("Unable to download from " + url.toString(), e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String downloadString(String url) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        download(url, bos);
        bos.close();
        return new String(bos.toByteArray(), utf8);
    }

    public static void downloadFile(String url, File out) throws IOException {
        out.getParentFile().mkdirs();
        File tempOut = File.createTempFile(out.getName(), ".part", out.getParentFile());
        BufferedOutputStream bos = null;
        try {
            OutputStream bos2 = new BufferedOutputStream(new FileOutputStream(tempOut));
            try {
                download(url, bos2);
                tempOut.renameTo(out);
                if (bos2 != null) {
                    bos2.close();
                }
                if (tempOut.exists()) {
                    tempOut.delete();
                }
            } catch (IOException th2) {
                if (bos != null) {
                    bos.close();
                }
                if (tempOut.exists()) {
                    tempOut.delete();
                }
                throw th2;
            }
        } catch (IOException th3) {

            if (bos != null) {
                bos.close();
            }
            if (tempOut.exists()) {
                tempOut.delete();
            }
            throw th3;
        }
    }
	
	public static void downloadFileWithRetry(String url, File out, int retryTimes, int retryAfter) {
		while(retryTimes > 0) {
			try {
				DownloadUtils.downloadFile(url, out);
				return;
			} catch (IOException e) {
				--retryTimes;
				Log.i("Downloader", "download " + url + "failed. Retrying.");
				if (out.exists())
				    out.delete();
				try {
					Thread.sleep(retryAfter);
				} catch (InterruptedException e0) {}
			}
		}
		throw new RuntimeException("Download " + url + "failed because retry times > " + retryTimes);
	}
	
	public static void downloadFileWithRetry(String url, File out) {
		downloadFileWithRetry(url, out, LauncherPreferences.PREF_DOWNLOAD_RETRY_TIMES, LauncherPreferences.PREF_DOWNLOAD_RETRY_AFTER_MS);
	}

    public static void downloadFileMonitored(String urlInput, String nameOutput, @Nullable byte[] buffer,
                                             Tools.DownloaderFeedback monitor) throws IOException {
        downloadFileMonitored(urlInput, new File(nameOutput), buffer, monitor);
    }
	
    public static void downloadFileMonitored(String urlInput,File outputFile, @Nullable byte[] buffer,
                                             Tools.DownloaderFeedback monitor) throws IOException {
        if (!outputFile.exists()) {
            outputFile.getParentFile().mkdirs();
        }

        HttpURLConnection conn = (HttpURLConnection) new URL(urlInput).openConnection();
        InputStream readStr = conn.getInputStream();
        FileOutputStream fos = new FileOutputStream(outputFile);
        int cur;
        int oval = 0;
        int len = conn.getContentLength();

        if(buffer == null) buffer = new byte[65535];

        while ((cur = readStr.read(buffer)) != -1) {
            oval += cur;
            fos.write(buffer, 0, cur);
            monitor.updateProgress(oval, len);
        }
        fos.close();
        conn.disconnect();
    }

	public static void downloadFileMonitoredWithRetry(String urlInput, String nameOutput, @Nullable byte[] buffer,
												Tools.DownloaderFeedback monitor) {
        downloadFileMonitoredWithRetry(urlInput, new File(nameOutput), buffer, monitor, 
			LauncherPreferences.PREF_DOWNLOAD_RETRY_TIMES, LauncherPreferences.PREF_DOWNLOAD_RETRY_AFTER_MS);
    }
	
	public static void downloadFileMonitoredWithRetry(String urlInput, File outputFile, @Nullable byte[] buffer,
		Tools.DownloaderFeedback monitor) {
			downloadFileMonitoredWithRetry(urlInput, outputFile, buffer, monitor, 
			    LauncherPreferences.PREF_DOWNLOAD_RETRY_TIMES, LauncherPreferences.PREF_DOWNLOAD_RETRY_AFTER_MS);
	}

	public static void downloadFileMonitoredWithRetry(String urlInput,File outputFile, @Nullable byte[] buffer,
												Tools.DownloaderFeedback monitor, int retryTimes, int retryAfterMs) {
		while(retryTimes > 0) {
			try {
				DownloadUtils.downloadFileMonitored(urlInput, outputFile, buffer, monitor);
				return;
			} catch (IOException e) {
				--retryTimes;
				Log.i("Downloader", "download " + urlInput + "failed. Retrying.");
				if (buffer != null)
				    Arrays.fill(buffer, (byte) 0);
				try {
					Thread.sleep(retryAfterMs);
				} catch (InterruptedException e0) {}
			}
		}
		throw new RuntimeException("Download " + urlInput + "failed because retry times > " + retryTimes);
	}	

    public static void downloadFileMonitoredWithHeaders(String urlInput,File outputFile, @Nullable byte[] buffer,
                                                        Tools.DownloaderFeedback monitor, String userAgent, String cookies) throws IOException {
        if (!outputFile.exists()) {
            outputFile.getParentFile().mkdirs();
        }

        HttpURLConnection conn = (HttpURLConnection) new URL(urlInput).openConnection();
        conn.setRequestProperty("User-Agent", userAgent);
        conn.setRequestProperty("Cookies", cookies);
        InputStream readStr = conn.getInputStream();
        FileOutputStream fos = new FileOutputStream(outputFile);
        int cur;
        int oval = 0;
        int len = conn.getContentLength();

        if(buffer == null) buffer = new byte[65535];

        while ((cur = readStr.read(buffer)) != -1) {
            oval += cur;
            fos.write(buffer, 0, cur);
            monitor.updateProgress(oval, len);
        }
        fos.close();
        conn.disconnect();
    }

}

