package com.calt.coffeeshop.w1crud_maven.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tbl_user_role")
@Getter @Setter
@NoArgsConstructor

public class UserRole {

@Id
@GeneratedValue(strategy = GenerationType.AUTO)
private long id;

@ManyToOne
@JoinColumn(name = "user_id")
private User user;
@ManyToOne
@JoinColumn(name = "role_id")
private Role role;
}
