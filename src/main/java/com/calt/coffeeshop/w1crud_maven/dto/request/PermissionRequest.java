package com.calt.coffeeshop.w1crud_maven.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter

public class PermissionRequest {
    private int id;
    private String username;
    private String password;
    private Set<String> roles;
    private Instant created_at;
    private Instant updated_at;

    public PermissionRequest(String username, String password, Set<String> roles) {
        this.username = username;
        this.password = password;
        this.roles = roles;
    }

    public PermissionRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public PermissionRequest(String username, String password, Set<String> roles, Instant created_at, Instant updated_at) {
        this.username = username;
        this.password = password;
        this.roles = roles;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }
}
