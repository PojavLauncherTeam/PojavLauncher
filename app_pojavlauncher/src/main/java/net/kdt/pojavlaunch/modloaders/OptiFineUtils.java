package net.kdt.pojavlaunch.modloaders;

import net.kdt.pojavlaunch.utils.DownloadUtils;

import java.io.IOException;
import java.util.List;

public class OptiFineUtils {

    public static OptiFineVersions downloadOptiFineVersions() throws IOException {
        try {
            return DownloadUtils.downloadStringCached("https://optifine.net/downloads",
                    "of_downloads_page", new OptiFineScraper());
        }catch (DownloadUtils.ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static class OptiFineVersions {
        public List<String> minecraftVersions;
        public List<List<OptiFineVersion>> optifineVersions;
    }
    public static class OptiFineVersion {
        public String versionName;
        public String downloadUrl;
    }
}
