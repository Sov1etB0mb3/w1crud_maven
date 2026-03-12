package com.calt.coffeeshop.w1crud_maven.dto.request;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class AuthRequest {
    private String username;
    private String password;
}
