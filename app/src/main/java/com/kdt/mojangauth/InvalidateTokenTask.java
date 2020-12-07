package com.kdt.mojangauth;

import android.content.*;
import android.os.*;
import com.kdt.mojangauth.yggdrasil.*;
import java.io.*;
import java.util.*;
import net.kdt.pojavlaunch.*;

public class InvalidateTokenTask extends AsyncTask<String, Void, Throwable> {
    private YggdrasilAuthenticator authenticator = new YggdrasilAuthenticator();
    //private Gson gson = new Gson();
    private MCProfile.Builder profilePath;

    private Context ctx;
    private String path;

    public InvalidateTokenTask(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public Throwable doInBackground(String... args) {
        path = args[0];
        try {
            this.profilePath = MCProfile.load(args[0]);
            this.authenticator.invalidate(profilePath.getAccessToken(), UUID.fromString(profilePath.getClientID()));
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
        new File(path).delete();
    }
}

