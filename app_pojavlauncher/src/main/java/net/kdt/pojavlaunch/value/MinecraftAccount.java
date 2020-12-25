package net.kdt.pojavlaunch.value;
import net.kdt.pojavlaunch.*;
import java.io.*;
import com.google.gson.*;

public class MinecraftAccount
{
    public String accessToken = "0"; // access token
    public String clientToken = "0"; // clientID: refresh and invalidate
    public String profileId = "0"; // authenticate UUID
    public String username = "Steve";
    public String selectedVersion = "1.7.10";
    public boolean isMicrosoft = false;
    public String msaRefreshToken = "0";
    
    public String save(String outPath) throws IOException {
        Tools.write(outPath, Tools.GLOBAL_GSON.toJson(this));
        return outPath;
    }
    
    public String save() throws IOException {
        return save(Tools.DIR_ACCOUNT_NEW + "/" + username + ".json");
    }
    
    public static MinecraftAccount parse(String content) throws JsonSyntaxException {
        return Tools.GLOBAL_GSON.fromJson(content, MinecraftAccount.class);
    }
    
    public static MinecraftAccount load(String name) throws IOException, JsonSyntaxException {
        MinecraftAccount acc = parse(Tools.read(Tools.DIR_ACCOUNT_NEW + "/" + name + ".json"));
        if (acc.accessToken == null) {
            acc.accessToken = "0";
        } if (acc.clientToken == null) {
            acc.clientToken = "0";
        } if (acc.profileId == null) {
            acc.profileId = "0";
        } if (acc.username == null) {
            acc.username = "0";
        } if (acc.selectedVersion == null) {
            acc.selectedVersion = "1.7.10";
        } if (acc.msaRefreshToken == null) {
            acc.msaRefreshToken = "0";
        }
        return acc;
    }
    
    public static void clearTempAccount() {
        File tempAccFile = new File(Tools.DIR_DATA, "cache/tempacc.json");
        tempAccFile.delete();
    }
    
    public static void saveTempAccount(MinecraftAccount acc) throws IOException {
        File tempAccFile = new File(Tools.DIR_DATA, "cache/tempacc.json");
        tempAccFile.delete();
        acc.save(tempAccFile.getAbsolutePath());
    }
}
