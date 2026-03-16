package com.calt.coffeeshop.w1crud_maven.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = "tbl_role_permisison")

public class Role_Permission {
    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;


    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "role_id")
    private Role role;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "permission_id")
    private Permission permission;
}
