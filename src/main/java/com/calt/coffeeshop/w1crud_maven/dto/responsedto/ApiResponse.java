package com.calt.coffeeshop.w1crud_maven.dto.responsedto;

import com.calt.coffeeshop.w1crud_maven.entity.Product;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Builder
@Getter @Setter
@JsonInclude(JsonInclude.Include.NON_NULL)

public class ApiResponse<T> {
    @Builder.Default
    private int code=7000;
    private String message;
    private T result;

//    public int getCode() {
//        return code;
//    }
//
//    public void setCode(int code) {
//        this.code = code;
//    }
//
//    public String getMessage() {
//        return message;
//    }
//
//    public void setMessage(String message) {
//        this.message = message;
//    }
//
//    public void setResult(T result) {
//        this.result = result;
//    }
//
//
//    public T getResult() {
//        return result;
//    }

}
