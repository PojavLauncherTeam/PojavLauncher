package net.kdt.pojavlaunch.utils;

import android.util.*;
import java.io.*;
import java.net.*;
import java.nio.charset.*;
import net.kdt.pojavlaunch.*;
import org.apache.commons.io.*;

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
}

