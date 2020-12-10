package net.kdt.pojavlaunch.authenticator.microsoft;

import android.app.*;
import android.content.*;
import android.os.*;
import android.util.*;
import com.kdt.mojangauth.*;
import com.kdt.mojangauth.yggdrasil.*;

import java.net.*;
import java.text.*;
import java.util.*;
import net.kdt.pojavlaunch.*;
import org.json.*;

import java.text.ParseException;
import java.io.*;

public class MicrosoftAuthenticator extends AsyncTask<String, Void, Throwable> {
    private static final String authTokenUrl = "https://login.live.com/oauth20_token.srf";
    private static final String xblAuthUrl = "https://user.auth.xboxlive.com/user/authenticate";
    private static final String xstsAuthUrl = "https://xsts.auth.xboxlive.com/xsts/authorize";
    private static final String mcLoginUrl = "https://api.minecraftservices.com/authentication/login_with_xbox";
    private static final String mcStoreUrl = "https://api.minecraftservices.com/entitlements/mcstore";
    private static final String mcProfileUrl = "https://api.minecraftservices.com/minecraft/profile";
    
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
            String msaAccessToken = acquireAccessToken(authCode);
            
            publishProgress();
            String xblToken = acquireXBLToken(msaAccessToken);
            
            publishProgress();
            // TODO
            
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
    
    // Based on https://github.com/MiniDigger/MiniLauncher/blob/master/launcher/src/main/java/me/minidigger/minecraftlauncher/launcher/gui/MsaFragmentController.java
    /*
     MIT License

     Copyright (c) 2018 Ammar Ahmad
     Copyright (c) 2018 Martin Benndorf
     Copyright (c) 2018 Mark Vainomaa

     Permission is hereby granted, free of charge, to any person obtaining a copy
     of this software and associated documentation files (the "Software"), to deal
     in the Software without restriction, including without limitation the rights
     to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
     copies of the Software, and to permit persons to whom the Software is
     furnished to do so, subject to the following conditions:

     The above copyright notice and this permission notice shall be included in all
     copies or substantial portions of the Software.

     THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
     IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
     FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
     AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
     LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
     OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
     SOFTWARE.
     */
    private final class GlobalToken {
        // MSA AccessToken
        private String access_token;
        
        // XBL Token
        private String Token;
    }

    private String acquireAccessToken(String authcode) throws IOException, URISyntaxException{
        URI uri = new URI(authTokenUrl);

        Map<Object, Object> data = new ArrayMap<>();
        data.put("client_id", "00000000402b5328");
        data.put("code", authcode);
        data.put("grant_type", "authorization_code");
        data.put("redirect_uri", "https://login.live.com/oauth20_desktop.srf");
        data.put("scope", "service::user.auth.xboxlive.com::MBI_SSL");

        HttpRequest request = HttpRequest.newBuilder(uri)
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(ofFormData(data)).build();

        HttpResponse response = HttpClient.newBuilder().build().sendRequest(request, HttpResponse.BodyHandlers.ofString());
        return Tools.GLOBAL_GSON.fromJson((String) response.body(), GlobalToken.class).access_token;
    }
    
    private String acquireXBLToken(String accessToken) throws IOException, URISyntaxException {
        URI uri = new URI(xblAuthUrl);

        Map<Object, Object> dataProp = new ArrayMap<>();
        dataProp.put("AuthMethod", "RPS");
        dataProp.put("SiteName", "user.auth.xboxlive.com");
        dataProp.put("RpsTicket", accessToken);
        
        Map<Object, Object> data = new ArrayMap<>();
        data.put("Properties", dataProp);
        data.put("RelyingParty", "http://auth.xboxlive.com");
        data.put("TokenType", "JWT");

        HttpRequest request = HttpRequest.newBuilder(uri)
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .POST(ofJSONData(data)).build();

        HttpResponse resp = HttpClient.newBuilder().build().sendRequest(request, HttpResponse.BodyHandlers.ofString());
        return Tools.GLOBAL_GSON.fromJson((String) resp.body(), GlobalToken.class).Token;
    }
    
    public static HttpRequest.BodyPublisher ofJSONData(Map<Object, Object> data) {
        return HttpRequest.BodyPublishers.ofString(Tools.GLOBAL_GSON.toJson(data));
    }
    
    public static HttpRequest.BodyPublisher ofFormData(Map<Object, Object> data) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<Object, Object> entry : data.entrySet()) {
            if (builder.length() > 0) {
                builder.append("&");
            }
            try {
                builder.append(URLEncoder.encode(entry.getKey().toString(), "UTF-8"));
                builder.append("=");
                builder.append(URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        return HttpRequest.BodyPublishers.ofString(builder.toString());
    }
}

