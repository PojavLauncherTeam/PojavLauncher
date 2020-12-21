package net.kdt.pojavlaunch.authenticator.microsoft;

import java.io.*;
import java.util.*;

public class HttpClient
{
    public static class Builder {
        public HttpClient build() {
            return new HttpClient(/* this */);
        }
    }
    
    public static Builder newBuilder() {
        return new Builder();
    }
    
    // private Builder mBuilder;
    protected HttpClient(/* Builder b */) {
        // mBuilder = b;
    }
    
    // This method is not official java.net.http.HttpClient API.
    public <T> HttpResponse<T> sendRequest(HttpRequest req, HttpResponse.BodyHandler<T> responseBodyHandler) throws IOException {
        req.mBuilder.getBase().connect();
        
        if (req.mBuilder.mData != null) {
            byte[] byteArr = req.mBuilder.mData.getBody().getBytes();
            OutputStream os = req.mBuilder.getBase().getOutputStream();
            os.write(byteArr, 0, byteArr.length);
            os.close();
        }

        HttpResponse<T> response = new HttpResponse<T>(req);
        return response;
    }
    
/*
    public <T> CompletableFuture<HttpResponse<T>> sendAsync(HttpRequest req, HttpResponse.BodyHandler<T> responseBodyHandler) throws IOException {
        CompletableFuture<HttpResponse<T>> result = new CompletableFuture<HttpResponse<T>>();
        result.complete(sendRequest(req, responseBodyHandler));
        return result;
    }
*/
}
