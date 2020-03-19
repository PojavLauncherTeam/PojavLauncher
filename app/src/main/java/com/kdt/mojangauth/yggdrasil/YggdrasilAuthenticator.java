package com.kdt.mojangauth.yggdrasil;

import android.util.*;
import com.google.gson.*;
import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.util.*;

public class YggdrasilAuthenticator {
    private static final String API_URL = "https://authserver.mojang.com/";
    private String clientName = "Minecraft";
    private int clientVersion = 1;
    private Gson gson = new Gson();

    private <T> T makeRequest(String endpoint, Object inputObject, Class<T> responseClass) throws IOException, Throwable {
        Throwable th;
        InputStream is = null;
        byte[] buf = new byte[16384];
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        String requestJson = this.gson.toJson(inputObject);
        try {
            URL url = new URL(API_URL + endpoint);
            OutputStream os;
            try {
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("User-Agent", "Minecraft");
                conn.setDoInput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.connect();
                os = null;
                os = conn.getOutputStream();
                os.write(requestJson.getBytes(Charset.forName("UTF-8")));
                if (os != null) {
                    os.close();
                }
                int statusCode = conn.getResponseCode();
                if (statusCode != 200) {
                    is = conn.getErrorStream();
                } else {
                    is = conn.getInputStream();
                }
                pipe(is, bos, buf);
                if (is != null) {
                    try {
                        is.close();
                    } catch (Exception e) {
						throw e;
                    }
                }
                String outString = new String(bos.toByteArray(), Charset.forName("UTF-8"));
                if (statusCode == 200){
					Log.i("Result", "Login successful");
					
                    return this.gson.fromJson(outString, responseClass);
                }
				throw new RuntimeException("Invalid username or password");
            } catch (UnknownHostException e) {
				throw new RuntimeException("Can't connect to the server");
			} catch (Throwable th2) {
                th = th2;
                if (is != null) {
                    try {
                        is.close();
                    } catch (Exception e2) {
						throw e2;
                    }
                }
                throw th;
            }
        } catch (Throwable th3) {
            th = th3;
            if (is != null) {
                is.close();
            }
            throw th;
        }
	}

    public AuthenticateResponse authenticate(String username, String password, UUID clientId) throws IOException, Throwable {
        return (AuthenticateResponse) makeRequest("authenticate", new AuthenticateRequest(username, password, clientId, this.clientName, this.clientVersion), AuthenticateResponse.class);
    }

    public RefreshResponse refresh(String authToken, UUID clientId) throws IOException, Throwable {
        return (RefreshResponse) makeRequest("refresh", new RefreshRequest(authToken, clientId), RefreshResponse.class);
    }
	private void pipe(InputStream is, OutputStream out, byte[] buf) throws IOException {
        while (true) {
            int amt = is.read(buf);
            if (amt >= 0) {
                out.write(buf, 0, amt);
            } else {
                return;
            }
        }
    }
}

