package com.calt.coffeeshop.w1crud_maven.dto.response;

import com.calt.coffeeshop.w1crud_maven.entity.Product;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Builder
@ToString
public class CategoryResponse {

    private Long id;

    private String name;
    private String description;
    private Instant created_at;
    private Instant updated_at;
    List<String> listProduct = new ArrayList<>();

    public CategoryResponse(String name, String description) {
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
