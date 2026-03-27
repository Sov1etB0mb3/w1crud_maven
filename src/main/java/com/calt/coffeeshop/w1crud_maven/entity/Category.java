package com.calt.coffeeshop.w1crud_maven.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@EntityListeners(AuditingEntityListener.class)

@Table(name="tbl_category")

public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    @Column(name = "name",columnDefinition = "VARCHAR(50)",nullable = false)
//    @NotBlank(message = "Name is a must have!")
    private String name;

    @Column(name = "description",columnDefinition = "VARCHAR(100)")
    private String description;
    @Column(name="created_at",updatable = false)
    @CreatedDate
    private Instant created_at;
    @Column(name="updated_at")
    @LastModifiedDate

    private Instant updated_at;
    @JsonIgnore
    @OneToMany(mappedBy = "category",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    List<Product> listProduct = new ArrayList<>();
    public Category( String name, String description, Instant createdAt, Instant updatedAt) {
        this.name = name;
        this.description = description;
        this.created_at = createdAt;
        this.updated_at = updatedAt;
    }

    public Category(String name, String description) {
        this.name = name;
        this.description = description;
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
