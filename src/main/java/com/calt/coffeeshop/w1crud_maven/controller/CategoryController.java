package com.calt.coffeeshop.w1crud_maven.controller;

import com.calt.coffeeshop.w1crud_maven.dto.requestdto.CategoryRequestDto;
import com.calt.coffeeshop.w1crud_maven.entity.Category;
import com.calt.coffeeshop.w1crud_maven.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.util.Objects.isNull;
@RestController
@RequestMapping("/categories")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    @GetMapping("")
    //return String becase the whole html site are Strings!!!
    public ResponseEntity<List<Category>> getProduct(){
        List<Category> categoryList= categoryService.getAllCategories();

        if(categoryList.isEmpty()){
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(categoryList);
    }
    @GetMapping("/{id}")
    //return String becase the whole html site are Strings!!!
    public ResponseEntity<Category> getProduct(@PathVariable("id") Long id){

        Category rProduct= categoryService.getCategoryByID(id);

        if(isNull( categoryService.getCategoryByID(id))){
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(rProduct);
    }

    @PutMapping("/{id}")
    //return String becase the whole html site are Strings!!!
    public String editProduct(@PathVariable("id") Long id){


        return "product-form";
    }

    @PostMapping("")

    public ResponseEntity<String> addProduct(@RequestBody CategoryRequestDto categoryRequestDto){

        categoryService.saveCategoryfromDTO(categoryRequestDto);
        return ResponseEntity.status(201).body("Created!");



    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable("id") Long id){

        categoryService.deleteCategory(id);

        return ResponseEntity.status(HttpStatus.OK).body("Deleted!");
    }
}
