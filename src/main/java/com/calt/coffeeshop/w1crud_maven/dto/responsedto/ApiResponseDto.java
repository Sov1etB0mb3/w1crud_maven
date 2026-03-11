package com.calt.coffeeshop.w1crud_maven.dto.responsedto;

import com.calt.coffeeshop.w1crud_maven.entity.Product;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Builder
@Getter @Setter
@JsonInclude(JsonInclude.Include.NON_NULL)

public class ApiResponse<T> {
    @Builder.Default
    private int code=700;
    private String message;
    private T result;
    
}
