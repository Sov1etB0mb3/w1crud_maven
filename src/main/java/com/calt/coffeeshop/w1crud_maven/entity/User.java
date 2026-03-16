package com.calt.coffeeshop.w1crud_maven.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.id.IncrementGenerator;

import java.time.Instant;
import java.util.Set;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
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

    @Column(name="role")
    private Set<String> role;
    @Column(name="created_at",updatable = false)
    private Instant created_at;
    @Column(name="updated_at")
    private Instant updated_at;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(String username, String password, Set<String> roles) {
        this.username = username;
        this.password = password;
        this.roles = roles;
    }

    public User(String username, String password, Set<String> roles, Instant created_at, Instant updated_at) {
        this.username = username;
        this.password = password;
        this.roles = roles;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }
}
