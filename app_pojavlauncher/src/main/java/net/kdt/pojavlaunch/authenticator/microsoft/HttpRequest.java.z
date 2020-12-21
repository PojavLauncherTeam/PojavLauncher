package net.kdt.pojavlaunch.authenticator.microsoft;

import java.net.*;
import java.io.*;
import java.util.*;

public class HttpRequest
{
    public static class Builder {
        private URI mURI;
        private HttpURLConnection mConn;
        
        protected BodyPublisher mData;
        public Builder(URI uri) throws IOException {
            mURI = uri;
            mConn = (HttpURLConnection) uri.toURL().openConnection();
            mConn.setUseCaches(false);
            mConn.setDoOutput(true);
        }
        
        public HttpURLConnection getBase() {
            return mConn;
        }
        
        public Builder header(String key, String value) {
            mConn.setRequestProperty(key, value);
            return this;
        }
        
        public Builder GET() throws ProtocolException {
            mConn.setRequestMethod("GET");
            return this;
        }
        
        public Builder POST(BodyPublisher data) throws ProtocolException {
            mConn.setDoInput(true);
            mConn.setRequestMethod("POST");
            mData = data;
            return this;
        }
        
        public HttpRequest build() throws IOException {
            return new HttpRequest(this);
        }
    }
    
    public static class BodyPublisher {
        private String mStr;
        private BodyPublisher(String str) {
            mStr = str;
        }
        
        public String getBody() {
            return mStr;
        }
    }
    
    public static class BodyPublishers {
        public static BodyPublisher ofString(String str) {
            return new BodyPublisher(str);
        }
    }
    
    public static Builder newBuilder(URI uri) throws IOException {
        return new Builder(uri);
    }
    
    protected Builder mBuilder;
    protected HttpRequest(Builder b) {
        mBuilder = b;
    }
}
