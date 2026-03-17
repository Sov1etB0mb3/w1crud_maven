package com.calt.coffeeshop.w1crud_maven.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RoleResponse {
    private long id;
    private String name;
    private String description;
    private Set <String> permissions;
}
