package com.calt.coffeeshop.w1crud_maven.dto.requestdto;

import com.calt.coffeeshop.w1crud_maven.entity.Category;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter

public class ProductRequestDto {
    @NotNull
    private String id;
    @NotBlank(message = "Name is required!")
    private String name;
//    @NotBlank(message = "Quantity is required!!!!!!")
    private int quantity;
    private double price;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    private Instant created_at;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    private Instant updated_at;
    private String category;


    public ProductRequestDto(String id, String name, int quantity, double price, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.created_at = createdAt;
        this.updated_at = updatedAt;
        this.category = category;
    }

    public ProductRequestDto(String id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
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
