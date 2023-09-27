package git.artdeell.installer_agent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Random;
import java.util.Set;

public class ProfileFixer {
    private static final Random random = new Random();
    private static final Path profilesPath = Paths.get(System.getProperty("user.home"), ".minecraft", "launcher_profiles.json");
    private static JSONObject oldProfile = null;
    public static void storeProfile(String profileName) {
        try {
            JSONObject minecraftProfiles = new JSONObject(
                    new String(Files.readAllBytes(profilesPath),
                            StandardCharsets.UTF_8)
            );
            JSONObject profilesArray = minecraftProfiles.getJSONObject("profiles");
            profileName = findProfileName(profileName, profilesArray);
            oldProfile = profileName != null ? minecraftProfiles.getJSONObject(profileName) : null;
        }catch (IOException | JSONException e) {
            System.out.println("Failed to store Forge profile: "+e);
        }
    }

    private static String pickProfileName(String profileName) {
        return profileName+random.nextInt();
    }
    public static void reinsertProfile(String profileName, String modpackFixupId, boolean suppressProfileCreation) {
            try {
                JSONObject minecraftProfiles = new JSONObject(
                        new String(Files.readAllBytes(profilesPath),
                                StandardCharsets.UTF_8)
                );
                JSONObject profilesArray = minecraftProfiles.getJSONObject("profiles");
                profileName = findProfileName(profileName, profilesArray);
                if(modpackFixupId != null) fixupModpackProfile(profileName, modpackFixupId, profilesArray);
                if(oldProfile != null) {
                    if(suppressProfileCreation) profilesArray.put(profileName, oldProfile); // restore the old profile
                    else {
                        String name = pickProfileName(profileName);
                        while(profilesArray.has(name)) name = pickProfileName(profileName);
                        profilesArray.put(name, oldProfile); // restore the old profile under a new name
                    }
                }else{
                    if(suppressProfileCreation) profilesArray.remove(profileName); // remove the new profile
                    // otherwise it wont be removed
                }
                minecraftProfiles.put("profiles", profilesArray);
                Files.write(profilesPath, minecraftProfiles.toString().getBytes(StandardCharsets.UTF_8),
                        StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
            }catch (IOException | JSONException e) {
                System.out.println("Failed to restore old Forge profile: "+e);
            }
    }

    private static void fixupModpackProfile(String profileId, String expectedVersionId, JSONObject profilesArray) {
        System.out.println("Fixing up modpack profile version ID...");
        JSONObject modloaderProfile = profilesArray.optJSONObject(profileId);
        if(modloaderProfile == null) {
            System.out.println("Failed to find the modloader profile, keys:" + profilesArray.keySet().toString());
            return;
        }
        String modloaderVersionId = modloaderProfile.optString("lastVersionId");
        if(modloaderVersionId == null) {
            System.out.println("Failed to find the modloader profile version, keys:" + modloaderProfile.keySet().toString());
            return;
        }
        System.out.println("Expected version ID: "+expectedVersionId+" Modloader version ID: "+modloaderVersionId);
        if(expectedVersionId.equals(modloaderVersionId)) return;
        for(String profileKey : profilesArray.keySet()) {
            if(profileKey.equals(profileId)) continue;
            JSONObject profile = profilesArray.getJSONObject(profileKey);
            if(!expectedVersionId.equals(profile.optString("lastVersionId"))) continue;
            profile.put("lastVersionId", modloaderVersionId);
            System.out.println("Replacing version ID in profile "+profileKey);
        }
    }

    private static String findProfileName(String profileId, JSONObject profilesArray) {
        Set<String> profiles = profilesArray.keySet();
        for(String profile : profiles) {
            if(profile.equalsIgnoreCase(profileId)) return profile;
        }
        return null;
    }
}
