package com.calt.coffeeshop.w1crud_maven.config;

import com.calt.coffeeshop.w1crud_maven.entity.Category;
import com.calt.coffeeshop.w1crud_maven.entity.Product;
import com.calt.coffeeshop.w1crud_maven.entity.User;
import com.calt.coffeeshop.w1crud_maven.service.CategoryService;
import com.calt.coffeeshop.w1crud_maven.service.ProductService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;

//This class used for create all tables at first run to create some sample!
//
//@Component
public class DataInitializer implements CommandLineRunner {


    private final CategoryService categoryService;
    private final ProductService productService;


    public DataInitializer(CategoryService categoryService, ProductService productService) {
        this.categoryService = categoryService;
        this.productService = productService;

    }

    @Override
    public void run(String... args) throws Exception {
       Category category1 = new Category("Cafe0","Something", Instant.now(),Instant.now());
       Category category2 = new Category("Cafe1","Something1", Instant.now(),Instant.now());
       Category category3 = new Category("Cafe2","Something2", Instant.now(),Instant.now());
        Product product1=new Product("CF0","Cafe 000",14,55, Instant.now(),Instant.now());
        category1.addProduct(product1);
        Product product2=new Product("CF1","Cafe 001",17,65, Instant.now(),Instant.now());
        category2.addProduct(product2);
        Product product3=new Product("CF2","Cafe 002",10,45, Instant.now(),Instant.now());
        category3.addProduct(product3);
       categoryService.saveCategory(category1);
       categoryService.saveCategory(category2);
       categoryService.saveCategory(category3);

       productService.saveProduct(product1);

        productService.saveProduct(product2);
        productService.saveProduct(product3);

    }


}
