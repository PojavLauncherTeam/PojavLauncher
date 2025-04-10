package net.kdt.pojavlaunch.tasks;

import net.kdt.pojavlaunch.JMinecraftVersionList;
import net.kdt.pojavlaunch.extra.ExtraConstants;
import net.kdt.pojavlaunch.extra.ExtraCore;
import net.kdt.pojavlaunch.value.launcherprofiles.MinecraftProfile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class AsyncMinecraftDownloader {
    public static String normalizeVersionId(String versionString) {
        JMinecraftVersionList versionList = (JMinecraftVersionList) ExtraCore.getValue(ExtraConstants.RELEASE_TABLE);
        if(versionList == null || versionList.versions == null) return versionString;
        if(MinecraftProfile.LATEST_RELEASE.equals(versionString)) versionString = versionList.latest.get("release");
        if(MinecraftProfile.LATEST_SNAPSHOT.equals(versionString)) versionString = versionList.latest.get("snapshot");
        return versionString;
    }

    public static JMinecraftVersionList.Version getListedVersion(String normalizedVersionString) {
        JMinecraftVersionList versionList = (JMinecraftVersionList) ExtraCore.getValue(ExtraConstants.RELEASE_TABLE);
        if(versionList == null || versionList.versions == null) return null; // can't have listed versions if there's no list
        for(JMinecraftVersionList.Version version : versionList.versions) {
            if(version.id.equals(normalizedVersionString)) return version;
        }
        return null;
    }


    /**
     * Minecraft has implemented demo mode in version 1.3, so this method return if the release time of a version is older than the 1.3 release
     *
     * @param releaseTimeString Version Release Time to check if is older than Minecraft 1.3 release time
     * @return {@code true} if the version is older than 1.3, {@code false} otherwise
     */
    public static boolean isOlderThan13(String releaseTimeString) {
        // change "+00:00" to "+0000" due API level
        releaseTimeString = releaseTimeString.replaceFirst("([+-]\\d{2}):(\\d{2})", "$1$2");

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            Date releaseTime = format.parse(releaseTimeString);
            Date v13ReleaseTime = format.parse("2012-07-25T22:00:00+0000");
            return releaseTime == null || releaseTime.before(v13ReleaseTime);
        } catch (ParseException e) {
            return true;
        }
    }

    public interface DoneListener{
        void onDownloadDone();
        void onDownloadFailed(Throwable throwable);
    }
}
