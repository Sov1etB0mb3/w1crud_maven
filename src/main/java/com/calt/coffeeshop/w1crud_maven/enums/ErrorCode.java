package com.calt.coffeeshop.w1crud_maven.enums;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(500, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(400, "Uncategorized error", HttpStatus.BAD_REQUEST),
    USER_EXISTED(400, "User existed", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(400, "Username must be at least {min} characters", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(400, "Password must be at least {min} characters", HttpStatus.BAD_REQUEST),
    EXISTED(409,"Existed!", HttpStatus.CONFLICT),
    NOT_FOUND(404,"Not found!",HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(401,"Unauthenticated!",HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED (403,"You do not have permission!",HttpStatus.FORBIDDEN),
    DATA_INTEGRITY(4009, "Cannot handle!", HttpStatus.CONFLICT),
    EXPIRED_KEY(400, "EXPIRED KEY!", HttpStatus.BAD_REQUEST),
    USER_NOTFOUND(4004, "Notfound User", HttpStatus.BAD_REQUEST),

    REVOKED_KEY(400, "REVOKED KEY!", HttpStatus.BAD_REQUEST);


    private int code;
    private String message;
    private HttpStatusCode httpStatusCode;

    ErrorCode(int code, String message, HttpStatusCode httpStatusCode) {
        this.code = code;
        this.message = message;
        this.httpStatusCode = httpStatusCode;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatusCode getHttpStatusCode() {
        return httpStatusCode;
    }
}
