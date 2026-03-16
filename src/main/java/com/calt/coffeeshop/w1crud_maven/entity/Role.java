package com.calt.coffeeshop.w1crud_maven.entity;

import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

public class Role {
    @Id
    private String name;
    private String description;

}
