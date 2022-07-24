/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.kdt.pojavlaunch.authenticator.elyby;

import android.util.Log;

import net.kdt.pojavlaunch.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class Eba {
    private static final String authTokenUrl = "https://account.ely.by/api/oauth2/v1/token";
    private static final String accountInfoUrl = "https://account.ely.by/api/account/v1/info";

    private ElyByAuthTask task;

    public boolean isRefresh;
    public String msRefreshToken;
    public String mcName;
    public String mcToken;
    public String mcUuid;

    public Eba(ElyByAuthTask task, boolean isRefresh, String authCode) throws IOException, JSONException {
        this.task = task;
        acquireAccessToken(isRefresh, authCode);
    }

    public void acquireAccessToken(boolean isRefresh, String authcode) throws IOException, JSONException {
        task.publishProgressPublic();

        URL url = new URL(authTokenUrl);
        Log.i("ElyByAuth", "isRefresh=" + isRefresh + ", authCode= "+authcode);
        Map<Object, Object> data = new HashMap<>();
        /*Map.of(
         "client_id", "00000000402b5328",
         "code", authcode,
         "grant_type", "authorization_code",
         "redirect_uri", "https://login.live.com/oauth20_desktop.srf",
         "scope", "service::user.auth.xboxlive.com::MBI_SSL"
         );*/
        data.put("client_id", "pojavlauncher");
        data.put("client_secret", "HfVpuanfZtSdUBV28OQkB4XcbvipaCkyQv7gcy-OawzUyFIVi4NPSqf8ZKkATtDY");
        data.put(isRefresh ? "refresh_token" : "code", authcode);
        data.put("grant_type", isRefresh ? "refresh_token" : "authorization_code");
        data.put("redirect_uri", "pojavlauncher://ely.by/login");
        data.put("scope", "account_info minecraft_server_session");
        Log.i("sdas", data.toString());

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
            if (!isRefresh){
                msRefreshToken = jo.getString("refresh_token");
            } else {
                msRefreshToken = authcode;
            }
            Log.i("ElyByAuth","Acess Token = "+jo.getString("access_token"));
            mcToken = jo.getString("access_token");
            getMcProfileInfo(mcToken);
        }else{
            throwResponseError(conn);
        }

    }

    private void getMcProfileInfo(String mcAccessToken) throws IOException, JSONException {
        task.publishProgressPublic();

        URL url = new URL(accountInfoUrl);

        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestProperty("Authorization", "Bearer " + mcAccessToken);
        conn.setUseCaches(false);
        conn.connect();

        if(conn.getResponseCode() >= 200 && conn.getResponseCode() < 300) {
            String s= Tools.read(conn.getInputStream());
            Log.i("ElyBy","profile:" + s);
            JSONObject jsonObject = new JSONObject(s);
            String name = (String) jsonObject.get("username");
            String uuid = (String) jsonObject.get("uuid");
            String uuidDashes = uuid .replaceFirst(
                "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"
            );
            Log.i("ElyByAuth","UserName = " + name);
            Log.i("ElyByAuth","Uuid Minecraft = " + uuidDashes);
            mcName=name;
            mcUuid=uuidDashes;
        }else{
            Log.i("ElyByAuth","Error getting account info.");
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
        Log.i("ElyByAuth","Error code: " + conn.getResponseCode() + ": " + conn.getResponseMessage() + "\n" + errStr);

        if (errStr.contains("NOT_FOUND") &&
            errStr.contains("The server has not found anything matching the request URI"))
        {
            // TODO localize this
            otherErrStr = "Error getting account info.";
        }

        throw new RuntimeException(otherErrStr + "\n\nEBA Error: " + conn.getResponseCode() + ": " + conn.getResponseMessage() + ", error stream:\n" + errStr);
    }
}

