package net.kdt.pojavlaunch.value;
import net.kdt.pojavlaunch.*;
import java.io.*;
import com.google.gson.*;

public class MinecraftAccount
{
    public String accessToken; // access token
    public String clientToken; // clientID: refresh and invalidate
    public String profileId; // authenticate UUID
    public String username;
    public String selectedVersion = "1.7.10";
    public boolean isMicrosoft;
    public String msaRefreshToken;
    
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
    
    public static MinecraftAccount load(String path) throws IOException, JsonSyntaxException {
        return parse(Tools.read(path));
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
