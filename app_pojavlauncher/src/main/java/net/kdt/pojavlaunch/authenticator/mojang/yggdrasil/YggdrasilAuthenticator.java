package net.kdt.pojavlaunch.authenticator.mojang.yggdrasil;

import android.util.*;
import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.util.*;
import net.kdt.pojavlaunch.*;
import org.apache.commons.io.*;

public class YggdrasilAuthenticator {
    private static final String API_URL = "https://authserver.mojang.com/";
    private String clientName = "Minecraft";
    private int clientVersion = 1;

    private NetworkResponse makeRequest(String endpoint, Object inputObject, Class<?> responseClass) throws IOException, Throwable {
        Throwable th;
        InputStream is = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        String requestJson = Tools.GLOBAL_GSON.toJson(inputObject);
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
                
                if (is != null) {
                    IOUtils.copy(is, bos);
                    try {
                        is.close();
                    } catch (Exception e) {
						throw e;
                    }
                }
                
                String outString = new String(bos.toByteArray(), Charset.forName("UTF-8"));
                if (statusCode == 200 || statusCode == 204){
					Log.i("Result", "Task " + endpoint + " successful");
					
                    if (responseClass != null) {
                        return new NetworkResponse(statusCode, Tools.GLOBAL_GSON.fromJson(outString, responseClass));
                    }
                } else {
                    Log.i("Result", "Task " + endpoint + " failure");
                }

                return new NetworkResponse(statusCode, outString);
            } catch (UnknownHostException e) {
				if (endpoint.equals("refresh")) {
                    return null;
                } else {
                    throw new RuntimeException("Can't connect to the server", e);
                }
			} catch (Throwable th2) {
                th = th2;
                if (is != null) {
                    try {
                        is.close();
                    } catch (Exception e2) {
                        e2.addSuppressed(th2);
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

    public NetworkResponse invalidate(String authToken, UUID clientId) throws Throwable {
        return makeRequest("invalidate", new RefreshRequest(authToken, clientId), null);
    }
}

