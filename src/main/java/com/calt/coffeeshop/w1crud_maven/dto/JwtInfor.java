package com.calt.coffeeshop.w1crud_maven.dto;

import lombok.*;

import java.io.Serializable;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtInfor implements Serializable {
    private String jwtId;
    private Long expireDate;
}
