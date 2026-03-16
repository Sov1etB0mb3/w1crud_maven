package com.calt.coffeeshop.w1crud_maven.entity;

import jakarta.persistence.Id;

public class Permission {
    @Id
    private String name;
    private String description;
}
