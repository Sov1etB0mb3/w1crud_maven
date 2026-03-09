package com.calt.coffeeshop.w1crud_maven.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name="Product")
public class Product {
    @Id
    @Column(name="ID", columnDefinition = "CHAR(10)")
    @NotNull
    private String id;
    @Column(name="Name", columnDefinition = "VARCHAR(20)",nullable = false)
//    @NotBlank(message = "Name is required!")
    private String name;

    @Column(name="Quantity", nullable = false)
//    @NotBlank(message = "Quantity is required!!!!!!")
    private int quantity;
    @Column(name="Price")
    private double price;
    @ManyToOne
    @JoinColumn(name = "CateID")
    private Category category;
    public Product() {
    }

    public Product(String id, String name, int quantity, double price) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.price = price;

    }

    public Product(String id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
    // one Product has only one category. One category has more than 1 Product

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                '}';
    }
}
