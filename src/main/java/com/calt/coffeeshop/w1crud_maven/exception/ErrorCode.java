package com.calt.coffeeshop.w1crud_maven.exception;

public enum ErrorCode {
    EXISTED(707,"Existed");
    private int code;
    private String message;
    ErrorCode(int code, String message){
        this.code=code;
        this.message=message;
    }

    public int getCode() {
        return code;
    }


    public String getMessage() {
        return message;
    }

}
