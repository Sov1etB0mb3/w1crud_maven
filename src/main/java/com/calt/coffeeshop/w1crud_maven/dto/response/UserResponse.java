package com.calt.coffeeshop.w1crud_maven.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter

public class UserResponse {
    private int id;
    @Column(name = "username")
    private String username;
    @Column(name="password")
    private String password;
    @Column(name="role")
    private String roles;
    @Column(name="created_at",updatable = false)
    private Instant created_at;
    @Column(name="updated_at")
    private Instant updated_at;

    public UserResponse(String username, String password, Instant created_at, Instant updated_at) {
        this.username = username;
        this.password = password;

        this.created_at = created_at;
        this.updated_at = updated_at;
    }
}
