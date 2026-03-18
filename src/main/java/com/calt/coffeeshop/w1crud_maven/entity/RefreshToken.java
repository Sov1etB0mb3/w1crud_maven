package com.calt.coffeeshop.w1crud_maven.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
//@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "tbl_refresh_token")
public class RefreshToken {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name ="userid")
    private Integer userid;
    @Column(name ="refreshtoken")
    private String refreshtoken;
    @Column(name = "expirytime")
    private Instant expirytime;
}
