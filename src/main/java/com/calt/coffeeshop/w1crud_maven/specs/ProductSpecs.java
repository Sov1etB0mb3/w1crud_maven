package com.calt.coffeeshop.w1crud_maven.specs;

import com.calt.coffeeshop.w1crud_maven.entity.Product;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecs {

    public static Specification<Product> hasKeyword(String keyword){

    return (root, query, criteriaBuilder) ->{
        String likePattern = "%"+keyword.toLowerCase()+"%";
        Predicate name= criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),likePattern );
        Predicate id = criteriaBuilder.like(criteriaBuilder.lower(root.get("id")),likePattern);
//        Predicate price = criteriaBuilder.like(
//                criteriaBuilder.function("CAST",String.class,root.get("price")),likePattern);
//        Predicate quantity = criteriaBuilder.like(
//                criteriaBuilder.function("CAST",String.class,root.get("quantity")),likePattern);
        return criteriaBuilder.or(name,id);
    };
    }
}
