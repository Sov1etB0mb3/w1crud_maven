package com.calt.coffeeshop.w1crud_maven.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
//@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "tbl_invalid_token")
public class InvalidToken {
    @Id
    @Column(name = "id")
    private String id;
    @Column(name = "expirytime")
    private Instant expirytime;
}
