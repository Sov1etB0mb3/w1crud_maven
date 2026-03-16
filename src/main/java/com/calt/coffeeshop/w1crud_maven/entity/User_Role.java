package com.calt.coffeeshop.w1crud_maven.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table
@Getter @Setter
@NoArgsConstructor
public class User_Role {
String user_id;
String role_name;
}
