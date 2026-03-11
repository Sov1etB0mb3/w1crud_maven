package com.calt.coffeeshop.w1crud_maven.dto.requestdto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class AuthRequestDto {
    private String username;
    private String password;
}
