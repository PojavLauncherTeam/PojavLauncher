package net.kdt.pojavlaunch.authenticator.mojang;

import android.content.*;
import android.os.*;
import net.kdt.pojavlaunch.authenticator.mojang.yggdrasil.*;
import java.io.*;
import java.util.*;
import net.kdt.pojavlaunch.*;
import net.kdt.pojavlaunch.value.*;

public class InvalidateTokenTask extends AsyncTask<String, Void, Throwable> {
    private YggdrasilAuthenticator authenticator = new YggdrasilAuthenticator();
    //private Gson gson = new Gson();
    private MinecraftAccount profilePath;

    private Context ctx;
    private String path;

    public InvalidateTokenTask(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public Throwable doInBackground(String... args) {
        path = args[0];
        try {
            this.profilePath = MinecraftAccount.load(args[0]);
            this.authenticator.invalidate(profilePath.accessToken,
                UUID.fromString(profilePath.isMicrosoft ? profilePath.profileId : profilePath.clientToken /* should be? */));
            return null;
        } catch (Throwable e) {
            return e;
        }
    }

    @Override
    public void onPostExecute(Throwable result) {
        if (result != null) {
            Tools.showError(ctx, result);
        }
        new File(Tools.DIR_ACCOUNT_NEW + "/" + path + ".json").delete();
    }
}

