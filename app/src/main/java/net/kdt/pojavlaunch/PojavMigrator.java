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
}
