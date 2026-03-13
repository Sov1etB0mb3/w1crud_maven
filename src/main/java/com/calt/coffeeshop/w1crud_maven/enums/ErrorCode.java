package com.calt.coffeeshop.w1crud_maven.enums;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public enum ErrorCode {
    EXISTED(707,"Existed!", HttpStatus.CONFLICT),
    USER_NOT_FOUND(704,"User not found!",HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(706,"Unauthenticated!",HttpStatus.UNAUTHORIZED);

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
