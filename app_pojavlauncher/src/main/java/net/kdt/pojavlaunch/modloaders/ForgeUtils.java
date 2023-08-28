package net.kdt.pojavlaunch.modloaders;

import android.content.Intent;

import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.utils.DownloadUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.github.underscore.U;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import net.kdt.pojavlaunch.modloaders.ForgeVersionListHandler.ForgeForks;

public class ForgeUtils {
    private static final String FORGE_METADATA_URL = "`https://maven.minecraftforge.net/net/minecraftforge/forge/maven-metadata.xml`";
    private static final String FORGE_INSTALLER_URL = "https://maven.minecraftforge.net/net/minecraftforge/forge/%1$s/forge-%1$s-installer.jar";
    private static final String NEOFORGE_METADATA_URL = "https://maven.neoforged.net/api/maven/versions/releases/net/neoforged/forge";
    private static final String NEOFORGE_INSTALLER_URL = "https://maven.neoforged.net/net/neoforged/forge/%1$s/forge-%1$s-installer.jar";
    public static List<String> downloadForgeVersions() throws IOException {
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
            return DownloadUtils.downloadStringCached(FORGE_METADATA_URL, "forge_versions", input -> {
                try {
                    ForgeVersionListHandler handler = new ForgeVersionListHandler(ForgeForks.FORGE);
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
                    input = input.replace("versions", "version");
                    String xml = U.jsonToXml(input);
                    ForgeVersionListHandler handler = new ForgeVersionListHandler(ForgeForks.NEOFORGE);
                    saxParser.parse(new InputSource(new StringReader(xml)), handler);
                    return handler.getVersions();
                    // IOException is present here StringReader throws it only if the parser called close()
                    // sooner than needed, which is a parser issue and not an I/O one
                } catch (SAXException | IOException e) {
                    throw new DownloadUtils.ParseException(e);
                }
            });
        }catch (DownloadUtils.ParseException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static List<String> downloadAllForgeVersions() throws IOException {
        List<String> forgeVersionList = downloadForgeVersions();
        List<String> neoforgeVersionList = downloadNeoForgeVersions();
        if (forgeVersionList != null && neoforgeVersionList != null) forgeVersionList.addAll(neoforgeVersionList);
        return forgeVersionList;
    }

    public static String getInstallerUrl(String version) {
        String ret = String.format(version.contains("NeoForge") ? NEOFORGE_INSTALLER_URL : FORGE_INSTALLER_URL, version.replace(" (NeoForge)", ""));
        System.out.println(ret);
        return ret;
    }

    public static void addAutoInstallArgs(Intent intent, File modInstallerJar, boolean createProfile) {
        intent.putExtra("javaArgs", "-javaagent:"+ Tools.DIR_DATA+"/forge_installer/forge_installer.jar"
                + (createProfile ? "=NPS" : "") + // No Profile Suppression
                " -jar "+modInstallerJar.getAbsolutePath());
        intent.putExtra("skipDetectMod", true);
    }
    public static void addAutoInstallArgs(Intent intent, File modInstallerJar, String modpackFixupId) {
        intent.putExtra("javaArgs", "-javaagent:"+ Tools.DIR_DATA+"/forge_installer/forge_installer.jar"
                + "=\"" + modpackFixupId +"\"" +
                " -jar "+modInstallerJar.getAbsolutePath());
        intent.putExtra("skipDetectMod", true);
    }
}
