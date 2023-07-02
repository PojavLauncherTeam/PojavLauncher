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
            oldProfile = profilesArray.optJSONObject(profileName, null);
        }catch (IOException | JSONException e) {
            System.out.println("Failed to store Forge profile: "+e);
        }
    }

    private static String pickProfileName(String profileName) {
        return profileName+random.nextInt();
    }
    public static void reinsertProfile(String profileName, boolean suppressProfileCreation) {
            try {
                JSONObject minecraftProfiles = new JSONObject(
                        new String(Files.readAllBytes(profilesPath),
                                StandardCharsets.UTF_8)
                );
                JSONObject profilesArray = minecraftProfiles.getJSONObject("profiles");
                if(oldProfile != null) {
                    if(suppressProfileCreation) profilesArray.put("forge", oldProfile); // restore the old profile
                    else {
                        String name = pickProfileName(profileName);
                        while(profilesArray.has(name)) name = pickProfileName(profileName);
                        profilesArray.put(name, oldProfile); // restore the old profile under a new name
                    }
                }else{
                    if(suppressProfileCreation) profilesArray.remove("forge"); // remove the new profile
                    // otherwise it wont be removed
                }
                minecraftProfiles.put("profiles", profilesArray);
                Files.write(profilesPath, minecraftProfiles.toString().getBytes(StandardCharsets.UTF_8),
                        StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
            }catch (IOException | JSONException e) {
                System.out.println("Failed to restore old Forge profile: "+e);
            }
    }
}
