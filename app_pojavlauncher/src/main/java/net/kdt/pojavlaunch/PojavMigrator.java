package net.kdt.pojavlaunch;
import android.content.*;
import java.io.*;
import net.kdt.pojavlaunch.value.*;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

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

    public static boolean migrateGameDir(PojavLoginActivity ctx) throws IOException {
        if (android.os.Build.VERSION.SDK_INT < 30) return false;

        ctx.runOnUiThread(() -> {
            ctx.startupTextView.setText(ctx.getString(R.string.toast_copy_home_dir, Tools.DIR_GAME_OLD, Tools.DIR_GAME_HOME));
            // Toast.makeText(ctx, ctx.getString(R.string.b), Toast.LENGTH_LONG).show();
        });

        File oldGameDir = new File(Tools.DIR_GAME_OLD);

        if (oldGameDir.exists() && oldGameDir.isDirectory()) {
            FileUtils.copyDirectory(oldGameDir, new File(Tools.DIR_GAME_HOME));
        }

        return true;
    }

    private static void command(String cmd) throws IOException, InterruptedException {
        Process p = Runtime.getRuntime().exec(cmd);
        int exitCode = p.waitFor();
        if (exitCode != 0) {
            throw new IOException("Exit code " + exitCode +
                                  ", message:\n" + Tools.read(p.getErrorStream()));
        }
    }
}
