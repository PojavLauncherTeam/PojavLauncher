package net.kdt.pojavlaunch.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import net.kdt.pojavlaunch.*;
import android.os.*;

public class IoUtil {
	
	//public static volatile boolean cancel = false;
	
    private IoUtil() {
    }

    public static void pipe( /*PojavLauncherActivity activity,*/ InputStream is, OutputStream out, byte[] buf) throws IOException {
        while (true) {
			//if (cancel) throw new CancelException();
			
            int amt = is.read(buf);
            if (amt >= 0) {
                out.write(buf, 0, amt);
            } else {
                return;
            }
        }
    }

    public static void copy(File from, File to, byte[] buf) throws IOException {
        InputStream in = new FileInputStream(from);
        OutputStream out;
        try {
            out = new FileOutputStream(to);
            pipe(in, out, buf);
            out.close();
            in.close();
        } catch (Throwable th) {
            in.close();
        }
    }

    public static void copyZipWithoutEmptyDirectories(File inputFile, File outputFile) throws IOException {
        byte[] buf = new byte[8192];
        ZipFile inputZip = new ZipFile(inputFile);
        ZipOutputStream outputStream = new ZipOutputStream(new FileOutputStream(outputFile));
        try {
            Enumeration<? extends ZipEntry> e = inputZip.entries();
            ArrayList<ZipEntry> sortedList = new ArrayList();
            while (e.hasMoreElements()) {
                sortedList.add((ZipEntry) e.nextElement());
            }
            Collections.sort(sortedList, new Comparator<ZipEntry>() {
                public int compare(ZipEntry o1, ZipEntry o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });
            for (int i = sortedList.size() - 1; i >= 0; i--) {
                boolean isEmptyDirectory;
                ZipEntry inputEntry = (ZipEntry) sortedList.get(i);
                String name = inputEntry.getName();
                if (!inputEntry.isDirectory()) {
                    isEmptyDirectory = false;
                } else if (i == sortedList.size() - 1) {
                    isEmptyDirectory = true;
                } else {
                    isEmptyDirectory = !((ZipEntry) sortedList.get(i + 1)).getName().startsWith(name);
                }
                if (isEmptyDirectory) {
                    sortedList.remove(inputEntry);
                } else {
                    outputStream.putNextEntry(new ZipEntry(inputEntry));
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    InputStream is = inputZip.getInputStream(inputEntry);
                    pipe(is, baos, buf);
                    is.close();
                    outputStream.write(baos.toByteArray());
                }
            }
        } finally {
            outputStream.close();
        }
    }

    public static void clearDirectory(File dir) {
        File[] fileList = dir.listFiles();
        if (fileList != null) {
            for (File f : fileList) {
                if (f.isDirectory()) {
                    clearDirectory(f);
                }
                f.delete();
            }
        }
    }
}

