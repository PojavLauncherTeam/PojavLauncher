package net.kdt.pojavlaunch.mirrors;

import java.io.IOException;

public class HttpException extends IOException {
    private final int httpErrorCode;

    public HttpException(String msg, int httpErrorCode) {
        super(msg);
        this.httpErrorCode = httpErrorCode;
    }

    public final int getHttpErrorCode() {
        return this.httpErrorCode;
    }
}
