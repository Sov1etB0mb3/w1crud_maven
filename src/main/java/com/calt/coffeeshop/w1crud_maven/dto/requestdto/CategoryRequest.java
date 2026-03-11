package com.calt.coffeeshop.w1crud_maven.dto.requestdto;

import com.calt.coffeeshop.w1crud_maven.entity.Product;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@ToString
public class CategoryRequestDto {

    private Long id;

    private String name;
    private String description;
    private Instant created_at;
    private Instant updated_at;
    List<Product> listProduct = new ArrayList<>();

    public CategoryRequestDto(String name, String description) {
        this.name = name;
        this.description = description;
    }

//    public void addProduct(Product product){
//        this.listProduct.add(product);
//        product.setCategory(this);
//    }
//    public void deleteProduct(Product product){
//        this.listProduct.remove(product);
//        product.setCategory(null);
//    }
}
