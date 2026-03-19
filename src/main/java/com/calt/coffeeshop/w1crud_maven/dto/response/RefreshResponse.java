package com.calt.coffeeshop.w1crud_maven.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RefreshResponse {
    String rtoken;
    String atoken;
    boolean authenicated;
}
