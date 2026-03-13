package com.calt.coffeeshop.w1crud_maven.enums;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public enum ErrorCode {
    EXISTED(709,"Existed!", HttpStatus.CONFLICT),
    USER_NOT_FOUND(704,"User not found!",HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(701,"Unauthenticated!",HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED (703,"You do not have permission!",HttpStatus.FORBIDDEN);

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
