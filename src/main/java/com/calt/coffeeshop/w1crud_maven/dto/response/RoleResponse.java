package com.calt.coffeeshop.w1crud_maven.dto.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RoleRequest {
    private long id;
    private String name;
    private String description;
}
