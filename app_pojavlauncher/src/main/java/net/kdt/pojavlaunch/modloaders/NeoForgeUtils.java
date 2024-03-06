package net.kdt.pojavlaunch.modloaders;

import android.content.Intent;
import net.kdt.pojavlaunch.utils.DownloadUtils;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

public class NeoForgeUtils {
    private static final String NEOFORGE_METADATA_URL = "https://maven.neoforged.net/releases/net/neoforged/neoforge/maven-metadata.xml";
    private static final String NEOFORGE_INSTALLER_URL = "https://maven.neoforged.net/releases/net/neoforged/neoforge/%1$s/neoforge-%1$s-installer.jar";
    public static List<String> downloadNeoForgeVersions() throws IOException {
        SAXParser saxParser;
        try {
            SAXParserFactory parserFactory = SAXParserFactory.newInstance();
            saxParser = parserFactory.newSAXParser();
        }catch (SAXException | ParserConfigurationException e) {
            e.printStackTrace();
            // if we cant make a parser we might as well not even try to parse anything
            return null;
        }
        try {
            //of_test();
            return DownloadUtils.downloadStringCached(NEOFORGE_METADATA_URL, "neoforge_versions", input -> {
                try {
                    ForgeVersionListHandler handler = new ForgeVersionListHandler();
                    saxParser.parse(new InputSource(new StringReader(input)), handler);
                    return handler.getVersions();
                    // IOException is present here StringReader throws it only if the parser called close()
                    // sooner than needed, which is a parser issue and not an I/O one
                }catch (SAXException | IOException e) {
                    throw new DownloadUtils.ParseException(e);
                }
            });
        }catch (DownloadUtils.ParseException e) {
            e.printStackTrace();
            return null;
        }

    }
    public static String getInstallerUrl(String version) {
        return String.format(NEOFORGE_INSTALLER_URL, version);
    }

    public static void addAutoInstallArgs(Intent intent, File modInstallerJar) {
        intent.putExtra("javaArgs", "-jar "+modInstallerJar.getAbsolutePath());
    }
}
