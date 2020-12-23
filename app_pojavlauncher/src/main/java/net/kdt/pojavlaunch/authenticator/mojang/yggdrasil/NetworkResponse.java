package net.kdt.pojavlaunch.authenticator.mojang.yggdrasil;

import java.util.*;

public class NetworkResponse
{
    public final int statusCode;
    public final Object response;
    public NetworkResponse(int statusCode, Object response) {
        this.statusCode = statusCode;
        this.response = response;
    }
    
    public void throwExceptionIfNeed(String msg) {
        if (statusCode < 200 || statusCode >= 300) {
            throw new RuntimeException(msg);
        }
    }
    
    public void throwExceptionIfNeed() {
        throwExceptionIfNeed("Respone error, code: " + statusCode + ", message: " + response);
    }
}
