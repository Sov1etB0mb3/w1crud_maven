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
    private String username;
    private String roles;
    private Instant created_at;
    private Instant updated_at;

    public UserResponse(String username, Instant created_at, Instant updated_at) {
        this.username = username;

        this.created_at = created_at;
        this.updated_at = updated_at;
    }
}
