package com.calt.coffeeshop.w1crud_maven.exception;

public enum ErrorCode {
    EXISTED(707,"Existed!"),
    USER_NOT_FOUND(704,"User not found!"),
    UNAUTHENICATED(706,"Unauthenicated!");

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
