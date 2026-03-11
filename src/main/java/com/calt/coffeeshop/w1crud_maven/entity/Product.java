package com.calt.coffeeshop.w1crud_maven.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Getter @Setter
@Table(name="tbl_product")
public class Product {
    @Id
    @Column(name="id", columnDefinition = "CHAR(10)")

    private String id;
    @Column(name="name", columnDefinition = "VARCHAR(20)",nullable = false)

    private String name;

    @Column(name="quantity", nullable = false)

    private int quantity;


//    @Column(name="testcol")
//    private int testcol;
    @Column(name="price")
    private double price;
    @Column(name="created_at",updatable = false)
    private Instant created_at;
    @Column(name="updated_at")
    private Instant updated_at;
    @ManyToOne
    @JoinColumn(name = "categoryid")
    private Category category;



    public Product(String id, String name, int quantity, double price, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.created_at = createdAt;
        this.updated_at = updatedAt;
    }
}
