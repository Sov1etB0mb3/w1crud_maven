package com.calt.coffeeshop.w1crud_maven.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenicationResponse {
    String token;
    boolean authenicated;
}
