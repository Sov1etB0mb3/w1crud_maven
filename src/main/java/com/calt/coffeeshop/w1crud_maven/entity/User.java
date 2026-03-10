package com.calt.coffeeshop.w1crud_maven.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.id.IncrementGenerator;

@Getter @Setter
@NoArgsConstructor
@Entity
@Table(name = "tbl_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;
    @Column(name = "username")
    private String username;
    @Column(name="password")
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
