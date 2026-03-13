package com.calt.coffeeshop.w1crud_maven.enums;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public enum StatusCode {
    UPDATED(705,"Updated!", HttpStatus.OK),
    DELETED(702,"Deleted",HttpStatus.OK);

    private int code;
    private String message;
    private HttpStatusCode httpStatusCode;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatusCode getHttpStatusCode() {
        return httpStatusCode;
    }

    StatusCode(int code, String message, HttpStatusCode httpStatusCode) {
        this.code = code;
        this.message = message;
        this.httpStatusCode = httpStatusCode;
    }
}
