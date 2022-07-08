/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.kdt.pojavlaunch.authenticator.microsoft;

import android.util.*;
import java.io.*;
import java.net.*;
import java.util.*;
import net.kdt.pojavlaunch.*;
import org.json.*;


public class Msa {
    private static final String authTokenUrl = "https://login.live.com/oauth20_token.srf";
    private static final String xblAuthUrl = "https://user.auth.xboxlive.com/user/authenticate";
    private static final String xstsAuthUrl = "https://xsts.auth.xboxlive.com/xsts/authorize";
    private static final String mcLoginUrl = "https://api.minecraftservices.com/authentication/login_with_xbox";
    private static final String mcProfileUrl = "https://api.minecraftservices.com/minecraft/profile";

    private MicrosoftAuthTask task;

    public boolean isRefresh;
    public String msRefreshToken;
    public String mcName;
    public String mcToken;
    public String mcUuid;
    public boolean doesOwnGame;
    public long expiresAt;

    public Msa(MicrosoftAuthTask task, boolean isRefresh, String authCode) throws IOException, JSONException {
        this.task = task;
        acquireAccessToken(isRefresh, authCode);
    }

    public void acquireAccessToken(boolean isRefresh, String authcode) throws IOException, JSONException {
        task.publishProgressPublic();

        URL url = new URL(authTokenUrl);
        Log.i("MicroAuth", "isRefresh=" + isRefresh + ", authCode= "+authcode);
        Map<Object, Object> data = new HashMap<>();
        /*Map.of(
         "client_id", "00000000402b5328",
         "code", authcode,
         "grant_type", "authorization_code",
         "redirect_uri", "https://login.live.com/oauth20_desktop.srf",
         "scope", "service::user.auth.xboxlive.com::MBI_SSL"
         );*/
        data.put("client_id", "00000000402b5328");
        data.put(isRefresh ? "refresh_token" : "code", authcode);
        data.put("grant_type", isRefresh ? "refresh_token" : "authorization_code");
        data.put("redirect_url", "https://login.live.com/oauth20_desktop.srf");
        data.put("scope", "service::user.auth.xboxlive.com::MBI_SSL");

        //да пошла yf[eq1 она ваша джава 11
        String req = ofFormData(data);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("charset", "utf-8");
        conn.setRequestProperty("Content-Length", Integer.toString(req.getBytes("UTF-8").length));
        conn.setRequestMethod("POST");
        conn.setUseCaches(false);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.connect();
        try(OutputStream wr = conn.getOutputStream()) {
            wr.write(req.getBytes("UTF-8"));
        }
        if(conn.getResponseCode() >= 200 && conn.getResponseCode() < 300) {
            JSONObject jo = new JSONObject(Tools.read(conn.getInputStream()));
            msRefreshToken = jo.getString("refresh_token");
            Log.i("MicroAuth","Acess Token = "+jo.getString("access_token"));
            acquireXBLToken(jo.getString("access_token"));
        }else{
            throwResponseError(conn);
        }

    }

    private void acquireXBLToken(String accessToken) throws IOException, JSONException {
        task.publishProgressPublic();

        URL url = new URL(xblAuthUrl);

        Map<Object, Object> data = new HashMap<>();
        Map<Object, Object> properties = new HashMap<>();
        properties.put("AuthMethod", "RPS");
        properties.put("SiteName", "user.auth.xboxlive.com");
        properties.put("RpsTicket", accessToken);
        data.put("Properties",properties);
        data.put("RelyingParty", "http://auth.xboxlive.com");
        data.put("TokenType", "JWT");
        /*Map.of(

         "Properties", Map.of(
         "AuthMethod", "RPS",
         "SiteName", "user.auth.xboxlive.com",
         "RpsTicket", accessToken
         ),
         "RelyingParty", "http://auth.xboxlive.com",
         "TokenType", "JWT"
         );*/
        String req = ofJSONData(data);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("charset", "utf-8");
        conn.setRequestProperty("Content-Length", Integer.toString(req.getBytes("UTF-8").length));
        conn.setRequestMethod("POST");
        conn.setUseCaches(false);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.connect();
        try(OutputStream wr = conn.getOutputStream()) {
            wr.write(req.getBytes("UTF-8"));
        }
        if(conn.getResponseCode() >= 200 && conn.getResponseCode() < 300) {
            JSONObject jo = new JSONObject(Tools.read(conn.getInputStream()));
            Log.i("MicroAuth","Xbl Token = "+jo.getString("Token"));
            acquireXsts(jo.getString("Token"));
        }else{
            throwResponseError(conn);
        }
    }

    private void acquireXsts(String xblToken) throws IOException, JSONException {
        task.publishProgressPublic();

        URL url = new URL(xstsAuthUrl);
        Map<Object, Object> data = new HashMap<>();
        Map<Object, Object> properties = new HashMap<>();
        properties.put("SandboxId", "RETAIL");
        properties.put("UserTokens",Collections.singleton(xblToken));
        data.put("Properties",properties);
        data.put("RelyingParty", "rp://api.minecraftservices.com/");
        data.put("TokenType", "JWT");
        /*Map<Object, Object> data = Map.of(
         "Properties", Map.of(
         "SandboxId", "RETAIL",
         "UserTokens", List.of(xblToken)
         ),
         "RelyingParty", "rp://api.minecraftservices.com/",
         "TokenType", "JWT"
         );
         */
        String req = ofJSONData(data);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("charset", "utf-8");
        conn.setRequestProperty("Content-Length", Integer.toString(req.getBytes("UTF-8").length));
        conn.setRequestMethod("POST");
        conn.setUseCaches(false);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.connect();
        try(OutputStream wr = conn.getOutputStream()) {
            wr.write(req.getBytes("UTF-8"));
        }

        if(conn.getResponseCode() >= 200 && conn.getResponseCode() < 300) {
            JSONObject jo = new JSONObject(Tools.read(conn.getInputStream()));
            String uhs = jo.getJSONObject("DisplayClaims").getJSONArray("xui").getJSONObject(0).getString("uhs");
            Log.i("MicroAuth","Xbl Xsts = "+jo.getString("Token")+"; Uhs = " + uhs);
            acquireMinecraftToken(uhs,jo.getString("Token"));
        }else{
            throwResponseError(conn);
        }
    }

    private void acquireMinecraftToken(String xblUhs, String xblXsts) throws IOException, JSONException {
        task.publishProgressPublic();

        URL url = new URL(mcLoginUrl);

        Map<Object, Object> data = new HashMap<>();
        data.put("identityToken", "XBL3.0 x=" + xblUhs + ";" + xblXsts);

        String req = ofJSONData(data);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("charset", "utf-8");
        conn.setRequestProperty("Content-Length", Integer.toString(req.getBytes("UTF-8").length));
        conn.setRequestMethod("POST");
        conn.setUseCaches(false);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.connect();
        try(OutputStream wr = conn.getOutputStream()) {
            wr.write(req.getBytes("UTF-8"));
        }

        if(conn.getResponseCode() >= 200 && conn.getResponseCode() < 300) {
            expiresAt = System.currentTimeMillis() + 86400000;
            JSONObject jo = new JSONObject(Tools.read(conn.getInputStream()));
            Log.i("MicroAuth","MC token: "+jo.getString("access_token"));
            mcToken = jo.getString("access_token");
            checkMcProfile(jo.getString("access_token"));

        }else{
            throwResponseError(conn);
        }
    }

    private void checkMcProfile(String mcAccessToken) throws IOException, JSONException {
        task.publishProgressPublic();

        URL url = new URL(mcProfileUrl);

        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestProperty("Authorization", "Bearer " + mcAccessToken);
        conn.setUseCaches(false);
        conn.connect();

        if(conn.getResponseCode() >= 200 && conn.getResponseCode() < 300) {
            String s= Tools.read(conn.getInputStream());
            Log.i("MicroAuth","profile:" + s);
            JSONObject jsonObject = new JSONObject(s);
            String name = (String) jsonObject.get("name");
            String uuid = (String) jsonObject.get("id");
            String uuidDashes = uuid.replaceFirst(
                "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"
            );
            doesOwnGame = true;
            Log.i("MicroAuth","UserName = " + name);
            Log.i("MicroAuth","Uuid Minecraft = " + uuidDashes);
            mcName=name;
            mcUuid=uuidDashes;
        }else{
            Log.i("MicroAuth","It seems that this Microsoft Account does not own the game.");
            doesOwnGame = false;
            throwResponseError(conn);
        }
    }

    public static String ofJSONData(Map<Object, Object> data) {
        return new JSONObject(data).toString();
    }

    public static String ofFormData(Map<Object, Object> data) {
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
                //Should not happen
            }
        }
        return builder.toString();
    }

    private static void throwResponseError(HttpURLConnection conn) throws IOException {
        String otherErrStr = "";
        String errStr = Tools.read(conn.getErrorStream());
        Log.i("MicroAuth","Error code: " + conn.getResponseCode() + ": " + conn.getResponseMessage() + "\n" + errStr);
        
        if (errStr.contains("NOT_FOUND") &&
            errStr.contains("The server has not found anything matching the request URI"))
        {
            // TODO localize this
            otherErrStr = "It seems that this Microsoft Account does not own the game. Make sure that you have bought/migrated to your Microsoft account.";
        }
        
        throw new RuntimeException(otherErrStr + "\n\nMSA Error: " + conn.getResponseCode() + ": " + conn.getResponseMessage() + ", error stream:\n" + errStr);
    }
}

