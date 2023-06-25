package net.kdt.pojavlaunch.modloaders;

import android.content.Intent;

import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.utils.DownloadUtils;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class ForgeUtils {
    private static final String FORGE_METADATA_URL = "https://maven.minecraftforge.net/net/minecraftforge/forge/maven-metadata.xml";
    private static final String FORGE_INSTALLER_URL = "https://maven.minecraftforge.net/net/minecraftforge/forge/%1$s/forge-%1$s-installer.jar";
    public static List<String> downloadForgeVersions() throws IOException {
        String forgeMetadata = DownloadUtils.downloadString(FORGE_METADATA_URL);
        try {
            SAXParserFactory parserFactory = SAXParserFactory.newInstance();
            SAXParser parser = parserFactory.newSAXParser();
            ForgeVersionListHandler handler = new ForgeVersionListHandler();
            parser.parse(new InputSource(new StringReader(forgeMetadata)), handler);
            return handler.getVersions();
        }catch (SAXException | ParserConfigurationException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static String getInstallerUrl(String version) {
        return String.format(FORGE_INSTALLER_URL, version);
    }

    public static void addAutoInstallArgs(Intent intent, File modInstallerJar, boolean createProfile) {
        intent.putExtra("javaArgs", "-javaagent:"+ Tools.DIR_DATA+"/forge_installer/forge_installer.jar"
                + (createProfile ? "=NPS" : "") + // No Profile Suppression
                " -jar "+modInstallerJar.getAbsolutePath());
        intent.putExtra("skipDetectMod", true);
    }
}
