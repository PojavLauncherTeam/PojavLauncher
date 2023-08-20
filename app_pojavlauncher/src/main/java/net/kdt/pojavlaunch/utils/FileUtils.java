package net.kdt.pojavlaunch.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileUtils {
    public static boolean exists(String filePath){
        return new File(filePath).exists();
    }

    public static String getFileName(String pathOrUrl) {
        int lastSlashIndex = pathOrUrl.lastIndexOf('/');
        if(lastSlashIndex == -1) return null;
        return pathOrUrl.substring(lastSlashIndex);
    }
}