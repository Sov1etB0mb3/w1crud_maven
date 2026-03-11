package com.calt.coffeeshop.w1crud_maven.enums;

public enum StatusCode {
    UPDATED(705,"Updated!"),
    DELETED(702,"Deleted");

    private int code;
    private String message;
    StatusCode(int code, String message){
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
