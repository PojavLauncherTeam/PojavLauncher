package net.kdt.pojavlaunch.authenticator.microsoft;

import android.app.*;
import android.content.*;
import android.os.*;
import android.util.*;

import java.lang.ref.WeakReference;
import java.net.*;
import java.text.*;
import java.util.*;
import net.kdt.pojavlaunch.*;
import net.kdt.pojavlaunch.authenticator.mojang.*;
import net.kdt.pojavlaunch.authenticator.microsoft.*;
import org.json.*;

import java.text.ParseException;
import java.io.*;
import net.kdt.pojavlaunch.value.launcherprofiles.*;
import net.kdt.pojavlaunch.value.*;

public class MicrosoftAuthTask extends AsyncTask<String, Void, Object> {
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

    public MicrosoftAuthTask(Context ctx, RefreshListener listener) {
        this.ctx = new WeakReference<>(ctx);
        this.listener = listener;
    }

    @Override
    public void onPreExecute() {
        build = new ProgressDialog(ctx.get());
        build.setMessage(ctx.get().getString(R.string.global_waiting));
        build.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        build.setCancelable(false);
        build.setMax(6);
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
            Msa msa = new Msa(this, Boolean.parseBoolean(args[0]), args[1]);

            MinecraftAccount acc = new MinecraftAccount();
            if (msa.doesOwnGame) {
                acc.clientToken = "0"; /* FIXME */
                acc.accessToken = msa.mcToken;
                acc.username = msa.mcName;
                acc.profileId = msa.mcUuid;
                acc.isMicrosoft = true;
                acc.msaRefreshToken = msa.msRefreshToken;
                acc.updateSkinFace();
            }
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
    /*
    private final class XSTSXUI {
        private String uhs;
    }
     
    private final class XSTSDisplayClaims {
        private XSTSXUI[] xui;
    }
     
    private final class GlobalToken {
        // MSA AccessToken
        private String access_token;
        
        // XBL, XSTS Token
        private String Token;
        
        // XSTS 
        private XSTSDisplayClaims DisplayClaims;
        
        // Minecraft side
        private String id;
        private String name;
    }

    private String acquireAccessToken(String authcode) throws IOException, URISyntaxException{
        URI uri = new URI(authTokenUrl);

        Map<Object, Object> data = new ArrayMap<>();
        data.put("client_id", "00000000402b5328");
        data.put("code", authcode);
        data.put("grant_type", "authorization_code");
        data.put("redirect_url", "https://login.live.com/oauth20_desktop.srf");
        data.put("scope", "service::user.auth.xboxlive.com::MBI_SSL");

        HttpRequest request = HttpRequest.newBuilder(uri)
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(ofFormData(data)).build();

        HttpResponse resp = HttpClient.newBuilder().build().sendRequest(request, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
            return Tools.GLOBAL_GSON.fromJson((String) resp.body(), GlobalToken.class).access_token;
        } else {
            throw new RuntimeException("Error " + resp.statusCode() + ": " + (String) resp.body());
        }
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
        if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
            return Tools.GLOBAL_GSON.fromJson((String) resp.body(), GlobalToken.class).Token;
        } else {
            throw new RuntimeException("Error " + resp.statusCode() + ": " + (String) resp.body());
        }
    }
    
    private String[] acquireXsts(String xblToken) throws IOException, URISyntaxException {
        URI uri = new URI(xstsAuthUrl);

        Map<Object, Object> dataProp = new ArrayMap<>();
        dataProp.put("SandboxId", "RETAIL");
        dataProp.put("UserTokens", Arrays.asList(xblToken));
        
        Map<Object, Object> data = new ArrayMap<>();
        data.put("Properties", dataProp);
        data.put("RelyingParty", "rp://api.minecraftservices.com/");
        data.put("TokenType", "JWT");

        HttpRequest request = HttpRequest.newBuilder(uri)
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .POST(ofJSONData(data)).build();

        HttpResponse resp = HttpClient.newBuilder().build().sendRequest(request, HttpResponse.BodyHandlers.ofString());
        
        if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
            String body = (String) resp.body();
            GlobalToken token = Tools.GLOBAL_GSON.fromJson(body, GlobalToken.class);
            String xblXsts = token.Token;
            String uhs = token.DisplayClaims.xui[0].uhs;
            
            return new String[]{uhs, xblXsts};
        } else {
            throw new RuntimeException("Error " + resp.statusCode() + ": " + (String) resp.body());
        }
    }

    private String acquireMinecraftToken(String xblUhs, String xblXsts) throws IOException, URISyntaxException {
        URI uri = new URI(mcLoginUrl);

        Map<Object, Object> data = new ArrayMap<>();
        data.put("identityToken", "XBL3.0 x=" + xblUhs + ";" + xblXsts);

        HttpRequest request = HttpRequest.newBuilder(uri)
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .POST(ofJSONData(data)).build();

        HttpResponse resp = HttpClient.newBuilder().build().sendRequest(request, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
            String body = (String) resp.body();
            return Tools.GLOBAL_GSON.fromJson(body, GlobalToken.class).access_token;
        } else {
            throw new RuntimeException("Error " + resp.statusCode() + ": " + (String) resp.body());
        }
    }

    private MinecraftAccount checkMcProfile(String mcAccessToken) throws IOException, URISyntaxException {
        URI uri = new URI(mcProfileUrl);

        HttpRequest request = HttpRequest.newBuilder(uri)
            .header("Authorization", "Bearer " + mcAccessToken)
             .header("Content-Type", "application/json")
             .header("Accept", "application/json")
            .GET().build();

        HttpResponse resp = HttpClient.newBuilder().build().sendRequest(request, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
            String body = (String) resp.body();
            GlobalToken token = Tools.GLOBAL_GSON.fromJson(body, GlobalToken.class);
            String uuid = token.id;
            String uuidDashes = uuid.replaceFirst(
                "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"
            );
            
            MinecraftAccount acc = new MinecraftAccount();
            acc.isMicrosoft = true;
            acc.username = token.name;
            acc.accessToken = mcAccessToken;
            acc.profileId = uuidDashes;
            return acc;
        } else {
            throw new RuntimeException("Error " + resp.statusCode() + ": " + (String) resp.body());
        }
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

     */
}

