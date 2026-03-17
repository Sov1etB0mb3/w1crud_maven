package com.calt.coffeeshop.w1crud_maven.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@Entity
@Table(name = "tbl_role")
@Getter @Setter
public class Role {
    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL,orphanRemoval = false)
    private Set<UserRole> users = new HashSet<>();

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL,orphanRemoval = true)
    private Set<RolePermission> permissions = new HashSet<>();

}
