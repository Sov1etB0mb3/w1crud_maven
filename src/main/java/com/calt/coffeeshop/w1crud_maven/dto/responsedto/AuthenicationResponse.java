package com.calt.coffeeshop.w1crud_maven.dto.responsedto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenicationResponse {
boolean authenicated;
}
