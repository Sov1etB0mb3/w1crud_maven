package com.calt.coffeeshop.w1crud_maven.enums;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public enum ErrorCode {
    EXISTED(409,"Existed!", HttpStatus.CONFLICT),
    USER_NOT_FOUND(404,"User not found!",HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(401,"Unauthenticated!",HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED (403,"You do not have permission!",HttpStatus.FORBIDDEN);

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
