package com.calt.coffeeshop.w1crud_maven.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

//@NoArgsConstructor
//@AllArgsConstructor
@Entity
@Table(name="Category")

public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;
    @Column(name = "Name",columnDefinition = "VARCHAR(50)",nullable = false)
//    @NotBlank(message = "Name is a must have!")
    private String name;

    @Column(name = "Description",columnDefinition = "VARCHAR(100)")
    private String description;
    @OneToMany(mappedBy = "category",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    List<Product> listProduct = new ArrayList<>();
    public Category() {
    }

    public Category( String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
    public void addProduct(Product product){
        this.listProduct.add(product);
        product.setCategory(this);
    }
    public void deleteProduct(Product product){
        this.listProduct.remove(product);
        product.setCategory(null);
    }
}
