/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.kdt.pojavlaunch.authenticator.microsoft;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;


import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.*;



public class Msa {
    /*
     private static final String loginUrl = "https://login.live.com/oauth20_authorize.srf" +
     "?client_id=00000000402b5328" +
     "&response_type=code" +
     "&scope=service%3A%3Auser.auth.xboxlive.com%3A%3AMBI_SSL" +
     "&redirect_uri=https%3A%2F%2Flogin.live.com%2Foauth20_desktop.srf";

     private static final String redirectUrlSuffix = "https://login.live.com/oauth20_desktop.srf?code=";
     */
    private static final String authTokenUrl = "https://login.live.com/oauth20_token.srf";
    private static final String xblAuthUrl = "https://user.auth.xboxlive.com/user/authenticate";
    private static final String xstsAuthUrl = "https://xsts.auth.xboxlive.com/xsts/authorize";
    private static final String mcLoginUrl = "https://api.minecraftservices.com/authentication/login_with_xbox";
    private static final String mcStoreUrl = "https://api.minecraftservices.com/entitlements/mcstore";
    private static final String mcProfileUrl = "https://api.minecraftservices.com/minecraft/profile";

    private MicrosoftAuthTask task;

    public String mcName;
    public String mcToken;
    public String mcUuid;
    public boolean doesOwnGame;

    public Msa(MicrosoftAuthTask task, String authCode) throws IOException, JSONException {
        this.task = task;
        acquireAccessToken(authCode);
    }

    public void acquireAccessToken(String authcode) throws IOException, JSONException {
        task.publishProgressPublic();

        URL url = new URL(authTokenUrl);
        Log.i("MicroAuth","authCode= "+authcode);
        Map<Object, Object> data = new HashMap<>();/*Map.of(
         "client_id", "00000000402b5328",
         "code", authcode,
         "grant_type", "authorization_code",
         "redirect_uri", "https://login.live.com/oauth20_desktop.srf",
         "scope", "service::user.auth.xboxlive.com::MBI_SSL"
         );*/
        data.put("client_id", "00000000402b5328");
        data.put("code", authcode);
        data.put("grant_type", "authorization_code");
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
            String s = ""; int len = 0; byte[] buf = new byte[256];
            InputStream is = conn.getInputStream();
            while((len = is.read(buf)) != -1) { //читаем строчку пока не получим всё
                s += new String(buf,0,len);
            }
            JSONObject jo = new JSONObject(s);
            Log.i("MicroAuth","Acess Token = "+jo.getString("access_token"));
            acquireXBLToken(jo.getString("access_token"));
        }else{
            Log.i("MicroAuth","Error code: " + conn.getResponseCode() + ": "+conn.getResponseMessage());
            throw new RuntimeException("MSA Error: " + conn.getResponseCode() + ": " + conn.getResponseMessage());
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
            String s = ""; int len = 0; byte[] buf = new byte[256];
            InputStream is = conn.getInputStream();
            while((len = is.read(buf)) != -1) { //читаем строчку пока не получим всё
                s += new String(buf,0,len);
            }
            JSONObject jo = new JSONObject(s);
            Log.i("MicroAuth","Xbl Token = "+jo.getString("Token"));
            acquireXsts(jo.getString("Token"));
        }else{
            Log.i("MicroAuth","Error code: " + conn.getResponseCode() + ": "+conn.getResponseMessage());
            throw new RuntimeException("MSA Error: " + conn.getResponseCode() + ": " + conn.getResponseMessage());
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
            String s = ""; int len = 0; byte[] buf = new byte[256];
            InputStream is = conn.getInputStream();
            while((len = is.read(buf)) != -1) { //читаем строчку пока не получим всё
                s += new String(buf,0,len);
            }
            JSONObject jo = new JSONObject(s);
            String uhs = jo.getJSONObject("DisplayClaims").getJSONArray("xui").getJSONObject(0).getString("uhs");
            Log.i("MicroAuth","Xbl Xsts = "+jo.getString("Token")+"; Uhs = " + uhs);
            acquireMinecraftToken(uhs,jo.getString("Token"));
        }else{
            Log.i("MicroAuth","Error code: " + conn.getResponseCode() + ": "+conn.getResponseMessage());
            throw new RuntimeException("MSA Error: " + conn.getResponseCode() + ": " + conn.getResponseMessage());
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
            String s = ""; int len = 0; byte[] buf = new byte[256];
            InputStream is = conn.getInputStream();
            while((len = is.read(buf)) != -1) { //читаем строчку пока не получим всё
                s += new String(buf,0,len);
            }
            JSONObject jo = new JSONObject(s);
            Log.i("MicroAuth","MC token: "+jo.getString("access_token"));
            mcToken = jo.getString("access_token");
            checkMcProfile(jo.getString("access_token"));
            checkMcStore(jo.getString("access_token"));

        }else{
            Log.i("MicroAuth","Error code: " + conn.getResponseCode() + ": "+conn.getResponseMessage());
            throw new RuntimeException("MSA Error: " + conn.getResponseCode() + ": " + conn.getResponseMessage());
        }
    }
    private void checkMcStore(String mcAccessToken) throws IOException, JSONException {
        task.publishProgressPublic();

        URL url = new URL(mcStoreUrl);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestProperty("Authorization", "Bearer " + mcAccessToken);
        conn.setRequestMethod("GET");
        conn.setUseCaches(false);
        conn.connect();
        if(conn.getResponseCode() >= 200 && conn.getResponseCode() < 300) {
            String s = ""; int len = 0; byte[] buf = new byte[256];
            InputStream is = conn.getInputStream();
            while((len = is.read(buf)) != -1) { //читаем строчку пока не получим всё
                s += new String(buf,0,len);
            }
            JSONObject jo = new JSONObject(s);
            JSONArray ja = jo.getJSONArray("items");
            Log.i("MicroAuth","Store Len = " + ja.length());
            for(int i = 0; i < ja.length(); i++) {
                String prod = ja.getJSONObject(i).getString("name");
                Log.i("MicroAuth","Product " + i +": " +prod);
            }
        }else{
            Log.i("MicroAuth","Error code: " + conn.getResponseCode() + ": "+conn.getResponseMessage());
            throw new RuntimeException("MSA Error: " + conn.getResponseCode() + ": " + conn.getResponseMessage());
        }
        /*
         HttpRequest request = HttpRequest.newBuilder(uri)
         .header("Authorization", "Bearer " + mcAccessToken)
         .GET().build();

         HttpClient.newBuilder().build().sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenAccept(resp -> {
         if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
         String body = resp.body();
         Log.i("MicroAuth","store: " + body);
         }
         });
         */
    }

    private void checkMcProfile(String mcAccessToken) throws IOException, JSONException {
        task.publishProgressPublic();

        URL url = new URL(mcProfileUrl);

        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestProperty("Authorization", "Bearer " + mcAccessToken);
        conn.setUseCaches(false);
        conn.connect();

        if(conn.getResponseCode() >= 200 && conn.getResponseCode() < 300) {
            String s = ""; int len = 0; byte[] buf = new byte[256];
            InputStream is = conn.getInputStream();
            while((len = is.read(buf)) != -1) { //читаем строчку пока не получим всё
                s += new String(buf,0,len);
            }
            Log.i("MicroAuth","profile:" + s);
            JSONObject jsonObject = new JSONObject(s);
            String name = (String) jsonObject.get("name");
            String uuid = (String) jsonObject.get("id");
            String uuidDashes = uuid .replaceFirst(
                "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"
            );
            doesOwnGame = true;
            Log.i("MicroAuth","UserName = " + name);
            Log.i("MicroAuth","Uuid Minecraft = " + uuidDashes);
            mcName=name;
            mcUuid=uuidDashes;
        }else{
            Log.i("MicroAuth","Error code: " + conn.getResponseCode() + ": "+conn.getResponseMessage());
            Log.i("MicroAuth","It seems that this Microshit Account does not own the game.");
            doesOwnGame = false;
            throw new RuntimeException("MSA Error: " + conn.getResponseCode() + ": " + conn.getResponseMessage());
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

}

