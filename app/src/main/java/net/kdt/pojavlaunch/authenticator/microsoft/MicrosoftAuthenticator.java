package net.kdt.pojavlaunch.authenticator.microsoft;

import android.app.*;
import android.content.*;
import android.os.*;
import com.kdt.mojangauth.*;
import com.kdt.mojangauth.yggdrasil.*;
import java.util.*;
import net.kdt.pojavlaunch.*;

public class MicrosoftAuthenticator extends AsyncTask<String, Void, Throwable> {
    private YggdrasilAuthenticator authenticator = new YggdrasilAuthenticator();
    //private Gson gson = new Gson();
    private RefreshListener listener;

    private Context ctx;
    private ProgressDialog build;

    public MicrosoftAuthenticator(Context ctx, RefreshListener listener) {
        this.ctx = ctx;
        this.listener = listener;
    }

    @Override
    public void onPreExecute() {
        build = new ProgressDialog(ctx);
        build.setMessage(ctx.getString(R.string.global_waiting));
        build.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        build.setCancelable(false);
        build.show();
    }

    @Override
    public Throwable doInBackground(String... args) {
        try {
            MCProfile.Builder profilePath = MCProfile.load(args[0]);
            String authCode = args[1];
            
            publishProgress();
            
           /*
            profilePath.setClientID(response.clientToken.toString());
            profilePath.setAccessToken(response.accessToken);
            profilePath.setUsername(response.selectedProfile.name);
            profilePath.setProfileID(response.selectedProfile.id);
            MCProfile.build(profilePath);
           */
            return null;
        } catch (Throwable e) {
            return e;
        }
    }
    
    

    @Override
    protected void onProgressUpdate(Void[] p1) {
        super.onProgressUpdate(p1);
        build.setProgress(build.getProgress() + 1);
        
    }
    
    @Override
    public void onPostExecute(Throwable result) {
        build.dismiss();
        if (result == null) {
            listener.onSuccess();
        } else {
            listener.onFailed(result);
        }
    }
}

