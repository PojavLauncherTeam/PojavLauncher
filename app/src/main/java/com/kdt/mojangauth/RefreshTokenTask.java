package com.kdt.mojangauth;

import android.content.*;
import android.os.*;
import com.google.gson.*;
import java.util.*;
import net.kdt.pojavlaunch.*;
import com.kdt.mojangauth.yggdrasil.*;
import android.app.*;

public class RefreshTokenTask extends AsyncTask<String, Void, Throwable> {
	private YggdrasilAuthenticator authenticator = new YggdrasilAuthenticator();
	//private Gson gson = new Gson();
	private RefreshListener listener;
	private MCProfile.Builder profilePath;

	private Context ctx;
	private ProgressDialog build;

	public RefreshTokenTask(Context ctx, RefreshListener listener) {
		this.ctx = ctx;
		this.listener = listener;
	}

	@Override
	public void onPreExecute() {
		build = new ProgressDialog(ctx);
		build.setMessage("Refreshing");
		build.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		build.setCancelable(false);
		build.show();
	}
	
	@Override
	public Throwable doInBackground(String... args) {
		try {
			this.profilePath = MCProfile.load(args[0]);
			RefreshResponse response = this.authenticator.refresh(profilePath.getAccessToken(), UUID.fromString(profilePath.getClientID()));
			if (response == null) {
				throw new NullPointerException("Response is null?");
			}
			if (response.selectedProfile == null) {
				throw new IllegalArgumentException("Can't refresh a demo account!");
			}
			profilePath.setClientID(response.clientToken.toString());
			profilePath.setAccessToken(response.accessToken);
			profilePath.setUsername(response.selectedProfile.name);
			profilePath.setProfileID(response.selectedProfile.id);
			MCProfile.build(profilePath);
			return null;
		} catch (Throwable e) {
			return e;
		}
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

