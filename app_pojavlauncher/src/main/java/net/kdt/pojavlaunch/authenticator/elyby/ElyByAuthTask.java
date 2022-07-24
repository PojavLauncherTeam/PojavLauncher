package net.kdt.pojavlaunch.authenticator.elyby;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import net.kdt.pojavlaunch.R;
import net.kdt.pojavlaunch.authenticator.mojang.RefreshListener;
import net.kdt.pojavlaunch.value.MinecraftAccount;

import java.lang.ref.WeakReference;

public class ElyByAuthTask extends AsyncTask<String, Void, Object> {
/*
    private static final String authTokenUrl = "https://login.live.com/oauth20_token.srf";
    private static final String xblAuthUrl = "https://user.auth.xboxlive.com/user/authenticate";
    private static final String xstsAuthUrl = "https://xsts.auth.xboxlive.com/xsts/authorize";
    private static final String mcLoginUrl = "https://api.minecraftservices.com/authentication/login_with_xbox";
    private static final String mcStoreUrl = "https://api.minecraftservices.com/entitlements/mcstore";
    private static final String mcProfileUrl = "https://api.minecraftservices.com/minecraft/profile";
*/

    //private Gson gson = new Gson();
    private final RefreshListener listener;

    private final WeakReference<Context> ctx;
    private ProgressDialog build;

    public ElyByAuthTask(Context ctx, RefreshListener listener) {
        this.ctx = new WeakReference<>(ctx);
        this.listener = listener;
    }

    @Override
    public void onPreExecute() {
        build = new ProgressDialog(ctx.get());
        build.setMessage(ctx.get().getString(R.string.global_waiting));
        build.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        build.setCancelable(false);
        build.setMax(2);
        build.show();
    }

    @Override
    public Object doInBackground(String... args) {
        try {
            /*
            publishProgress();
            String msaAccessToken = acquireAccessToken(args[0]);
            
            publishProgress();
            String xblToken = acquireXBLToken(msaAccessToken);
            
            publishProgress();
            String[] xstsData = acquireXsts(xblToken);

            publishProgress();
            String mcAccessToken = acquireMinecraftToken(xstsData[0], xstsData[1]);
            
            publishProgress();

             */
            Eba eba = new Eba(this, Boolean.parseBoolean(args[0]), args[1]);

            MinecraftAccount acc = MinecraftAccount.load(eba.mcName);
            if(acc == null) acc = new MinecraftAccount();
            acc.clientToken = "0"; /* FIXME */
            acc.accessToken = eba.mcToken;
            acc.username = eba.mcName;
            acc.profileId = eba.mcUuid;
            acc.isElyBy = true;
            acc.msaRefreshToken = eba.msRefreshToken;
            acc.updateSkinFace();
            acc.save();
           
            return acc;
        } catch (Throwable e) {
            return e;
        }
    }

    public void publishProgressPublic() {
        super.publishProgress();
    }

    @Override
    protected void onProgressUpdate(Void[] p1) {
        super.onProgressUpdate(p1);
        build.setProgress(build.getProgress() + 1);
    }
    
    @Override
    public void onPostExecute(Object result) {
        build.dismiss();
        if (result instanceof MinecraftAccount) {
            listener.onSuccess((MinecraftAccount) result);
        } else {
            listener.onFailed((Throwable) result);
        }
    }
}

