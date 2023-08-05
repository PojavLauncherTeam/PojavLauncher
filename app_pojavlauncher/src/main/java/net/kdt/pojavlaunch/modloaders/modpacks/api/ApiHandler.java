package net.kdt.pojavlaunch.modloaders.modpacks.api;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import net.kdt.pojavlaunch.Tools;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

public class ApiHandler {
    public final String baseUrl;

    public ApiHandler(String url) {
        baseUrl = url;
    }

    public <T> T get(String endpoint, Class<T> tClass) {
        return getFullUrl(baseUrl + "/" + endpoint, tClass);
    }

    public <T> T get(String endpoint, HashMap<String, Object> query, Class<T> tClass) {
        return getFullUrl(baseUrl + "/" + endpoint, query, tClass);
    }

    public <T> T post(String endpoint, T body, Class<T> tClass) {
        return postFullUrl(baseUrl + "/" + endpoint, body, tClass);
    }

    public <T> T post(String endpoint, HashMap<String, Object> query, T body, Class<T> tClass) {
        return postFullUrl(baseUrl + "/" + endpoint, query, body, tClass);
    }

    //Make a get request and return the response as a raw string;
    public static String getRaw(String url) {
        Log.d("ApiHandler", url);
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            InputStream inputStream = conn.getInputStream();
            String data = Tools.read(inputStream);
            Log.d(ApiHandler.class.toString(), data);
            inputStream.close();
            conn.disconnect();
            return data;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String postRaw(String url, String body) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            OutputStream outputStream = conn.getOutputStream();
            byte[] input = body.getBytes(StandardCharsets.UTF_8);
            outputStream.write(input, 0, input.length);
            outputStream.close();

            InputStream inputStream = conn.getInputStream();
            String data = Tools.read(inputStream);
            inputStream.close();

            conn.disconnect();
            return data;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String parseQueries(HashMap<String, Object> query) {
        StringBuilder params = new StringBuilder("?");
        for (String param : query.keySet()) {
            Object value = query.get(param);
            params.append(param).append("=").append(value).append("&");
        }
        return params.substring(0, params.length() - 1);
    }

    public static <T> T getFullUrl(String url, Class<T> tClass) {
        return new Gson().fromJson(getRaw(url), tClass);
    }

    public static <T> T getFullUrl(String url, HashMap<String, Object> query, Class<T> tClass) {
        return getFullUrl(url + parseQueries(query), tClass);
    }

    public static <T> T postFullUrl(String url, T body, Class<T> tClass) {
        return new Gson().fromJson(postRaw(url, body.toString()), tClass);
    }

    public static <T> T postFullUrl(String url, HashMap<String, Object> query, T body, Class<T> tClass) {
        return new Gson().fromJson(postRaw(url + parseQueries(query), body.toString()), tClass);
    }
}
