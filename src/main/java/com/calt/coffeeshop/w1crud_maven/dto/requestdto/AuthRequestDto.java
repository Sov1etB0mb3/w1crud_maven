package com.calt.coffeeshop.w1crud_maven.dto.requestdto;

import jakarta.persistence.Column;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class RequestAuth {
    private String username;
    private String password;
}
