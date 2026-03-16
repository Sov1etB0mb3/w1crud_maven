package com.calt.coffeeshop.w1crud_maven.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter

public class RegisterRequest {
    private int id;
    private String username;
    private String password;
    private Set<String> roles;
    private Instant created_at;
    private Instant updated_at;

    public RegisterRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
