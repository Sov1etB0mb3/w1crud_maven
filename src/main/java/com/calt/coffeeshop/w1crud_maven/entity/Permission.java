package com.calt.coffeeshop.w1crud_maven.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tbl_permisson")
@Getter @Setter
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private long id;
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
    @OneToMany(mappedBy = "permission", cascade = CascadeType.ALL,orphanRemoval = false)
    private Set<RolePermission> rolePermissions = new HashSet<>();
}
