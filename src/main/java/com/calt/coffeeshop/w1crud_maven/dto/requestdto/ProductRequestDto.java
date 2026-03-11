package com.calt.coffeeshop.w1crud_maven.dto.requestdto;

import com.calt.coffeeshop.w1crud_maven.entity.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public class ProductRequestDto {
    @NotNull
    private String id;
    @NotBlank(message = "Name is required!")
    private String name;
//    @NotBlank(message = "Quantity is required!!!!!!")
    private int quantity;
    private double price;
    private Long category;
    public ProductRequestDto() {
    }

    public ProductRequestDto(String id, String name, int quantity, double price) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.price = price;

    }

    public ProductRequestDto(String id, String name, double price) {
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

    public Long getCategory() {
        return category;
    }

    public void setCategory(Long category) {
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
