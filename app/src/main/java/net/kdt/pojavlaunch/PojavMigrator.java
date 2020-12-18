package net.kdt.pojavlaunch;
import android.content.*;
import java.io.*;
import net.kdt.pojavlaunch.value.*;

public class PojavMigrator
{
    public static void migrateAccountData(Context ctx) {
        File oldAccDir = new File(Tools.DIR_ACCOUNT_OLD);
        
        if (oldAccDir.exists() && oldAccDir.isDirectory()) {
            for (String account : oldAccDir.list()) {
                File oldAccFile = new File(oldAccDir, account);

                try {
                    MCProfile.Builder oldAccStruct = MCProfile.load(oldAccFile.getAbsolutePath());

                    MinecraftAccount newAccStruct = new MinecraftAccount();
                    newAccStruct.accessToken = oldAccStruct.getAccessToken();
                    newAccStruct.clientToken = oldAccStruct.getClientID();
                    newAccStruct.isMicrosoft = false;
                    newAccStruct.profileId = oldAccStruct.getProfileID();
                    newAccStruct.selectedVersion = oldAccStruct.getVersion();
                    newAccStruct.username = oldAccStruct.getUsername();
                    newAccStruct.save();
                } catch (IOException e) {
                    Tools.showError(ctx, e);
                }

                oldAccFile.delete();
            }
        }
    }
    
    public static void migrateGameDir() throws IOException, InterruptedException {
        File oldGameDir = new File(Tools.DIR_GAME_OLD);
        if (oldGameDir.exists() && oldGameDir.isDirectory()) {
            Process p = Runtime.getRuntime().exec(
                new String[]{"mv", Tools.DIR_GAME_OLD, Tools.DIR_GAME_NEW});
            int exitCode = p.waitFor();
            if (exitCode != 0) {
                throw new IOException("Could not move game dir! Exit code " + exitCode +
                    ", message:\n" + Tools.read(p.getErrorStream()));
            }
        }
    }
}
