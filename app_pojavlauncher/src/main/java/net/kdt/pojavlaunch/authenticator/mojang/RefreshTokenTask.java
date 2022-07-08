package net.kdt.pojavlaunch.authenticator.mojang;

import android.content.*;
import android.os.*;
import com.google.gson.*;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.*;
import net.kdt.pojavlaunch.*;
import net.kdt.pojavlaunch.authenticator.mojang.yggdrasil.*;
import android.app.*;
import net.kdt.pojavlaunch.value.*;

public class RefreshTokenTask extends AsyncTask<String, Void, Throwable> {
    private YggdrasilAuthenticator authenticator = new YggdrasilAuthenticator();
    //private Gson gson = new Gson();
    private RefreshListener listener;
    private MinecraftAccount profilePath;

    private final WeakReference<Context> ctx;
    private ProgressDialog build;

    public RefreshTokenTask(Context ctx, RefreshListener listener) {
        this.ctx = new WeakReference<>(ctx);
        this.listener = listener;
    }

    @Override
    public void onPreExecute() {
        build = new ProgressDialog(ctx.get());
        build.setMessage(ctx.get().getString(R.string.global_waiting));
        build.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        build.setCancelable(false);
        build.show();
    }

    @Override
    public Throwable doInBackground(String... args) {
        try {
            this.profilePath = MinecraftAccount.load(args[0]);
            if(profilePath == null) {
                return new NullPointerException();
            }
            int responseCode = 400;
            try {
                responseCode = this.authenticator.validate(profilePath.accessToken).statusCode;
            }catch(RuntimeException e) {}

            if (responseCode == 403) {
                RefreshResponse response = this.authenticator.refresh(profilePath.accessToken, UUID.fromString(profilePath.clientToken));
                if (response == null) {
                    // Refresh when offline?
                    return null;
                } else if (response.selectedProfile == null) {
                    throw new IllegalArgumentException("Can't refresh a demo account!");
                }

                profilePath.clientToken = response.clientToken.toString();
                profilePath.accessToken = response.accessToken;
                profilePath.username = response.selectedProfile.name;
                profilePath.profileId = response.selectedProfile.id;
            }
            profilePath.updateSkinFace();
            profilePath.save();
            return null;
        } catch (Throwable e) {
            return e;
        }
    }

    @Override
    public void onPostExecute(Throwable result) {
        build.dismiss();
        if (result == null) {
            listener.onSuccess(profilePath);
        } else {
            listener.onFailed(result);
        }
    }
}