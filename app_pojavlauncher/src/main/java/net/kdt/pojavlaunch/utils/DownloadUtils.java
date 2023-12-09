package net.kdt.pojavlaunch.utils;

import android.util.Log;

import androidx.annotation.Nullable;

import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.util.concurrent.Callable;

import net.kdt.pojavlaunch.*;
import org.apache.commons.io.*;

@SuppressWarnings("IOStreamConstructor")
public class DownloadUtils {
    public static final String USER_AGENT = Tools.APP_NAME;

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
            throw new IOException("Unable to download from " + url, e);
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
        return new String(bos.toByteArray(), StandardCharsets.UTF_8);
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

    public static <T> T downloadStringCached(String url, String cacheName, ParseCallback<T> parseCallback) throws IOException, ParseException{
        File cacheDestination = new File(Tools.DIR_CACHE, "string_cache/"+cacheName);
        File cacheDestinationDir = cacheDestination.getParentFile();
        if(cacheDestinationDir != null &&
            !cacheDestinationDir.exists() &&
            !cacheDestinationDir.mkdirs()) throw new IOException("Failed to create the cache directory");
        if(cacheDestination.isFile() &&
                cacheDestination.canRead() &&
                System.currentTimeMillis() < (cacheDestination.lastModified() + 86400000)) {
            try {
                String cachedString = Tools.read(new FileInputStream(cacheDestination));
                return parseCallback.process(cachedString);
            }catch(IOException e) {
                Log.i("DownloadUtils", "Failed to read the cached file", e);
            }catch (ParseException e) {
                Log.i("DownloadUtils", "Failed to parse the cached file", e);
            }
        }
        String urlContent = DownloadUtils.downloadString(url);
        // if we download the file and fail parsing it, we will yeet outta there
        // and not cache the unparseable sting. We will return this after trying to save the downloaded
        // string into cache
        T parseResult = parseCallback.process(urlContent);

        boolean tryWriteCache = false;
        if(cacheDestination.exists()) {
            tryWriteCache = cacheDestination.canWrite();
        } else {
            // if it is null, then cacheDestination is the file system root. Very bad.
            // but let's shield ourselves and just never try to cache the file if that happens
            if(cacheDestinationDir != null && !cacheDestinationDir.isFile()) tryWriteCache = cacheDestinationDir.canWrite();
        }
        if(tryWriteCache) try {
            Tools.write(cacheDestination.getAbsolutePath(), urlContent);
        }catch(IOException e) {
            Log.i("DownloadUtils", "Failed to cache the string", e);
        }
        return parseResult;
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

    public static <T> T ensureSha1(File outputFile, @Nullable String sha1, Callable<T> downloadFunction) throws IOException {
        // Skip if needed
        if(sha1 == null){
            try {
                return downloadFunction.call();
            } catch (IOException e){
                throw e;
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        int attempts = 0;
        T result = null;
        while (attempts < 5 && (!outputFile.exists() || Tools.compareSHA1(outputFile, sha1))){
            attempts++;
            try {
                result = downloadFunction.call();
            } catch (IOException e){
                throw e;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return result;

    }

    public interface ParseCallback<T> {
        T process(String input) throws ParseException;
    }
    public static class ParseException extends Exception {
        public ParseException(Exception e) {
            super(e);
        }
    }
}

