package net.kdt.pojavlaunch.authenticator.microsoft;
import java.io.*;
import java.lang.reflect.*;
import net.kdt.pojavlaunch.*;

public class HttpResponse<T>
{
    private HttpRequest mRequest;
    public HttpResponse(HttpRequest request) {
        mRequest = request;
    }
    
    public static class BodyHandler<T> {
        // public BodySubscriber<T> apply(ResponseInfo responseInfo);
    }
    
    public static class BodyHandlers {
        public static BodyHandler<String> ofString() {
            return new BodyHandler<String>();
        }
    }

    public int statusCode() throws IOException {
        return mRequest.mBuilder.getBase().getResponseCode();
    }
    
    public T body() throws IOException {
        if (statusCode() >= 200 && statusCode() < 300) {
            return (T) Tools.read(mRequest.mBuilder.getBase().getInputStream());
        } else {
            return (T) Tools.read(mRequest.mBuilder.getBase().getErrorStream());
        }
        
/*
        if (T instanceof String) {
            if (statusCode() >= 200 && statusCode() < 300) {
                return (T) Tools.read(mRequest.mBuilder.getBase().getInputStream());
            } else {
                return (T) Tools.read(mRequest.mBuilder.getBase().getErrorStream());
            }
        } else {
            throw new UnsupportedOperationException();
        }
*/
    }
}
