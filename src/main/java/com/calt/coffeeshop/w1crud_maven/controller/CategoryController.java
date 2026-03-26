package com.calt.coffeeshop.w1crud_maven.controller;

import com.calt.coffeeshop.w1crud_maven.dto.request.CategoryRequest;
import com.calt.coffeeshop.w1crud_maven.dto.response.ApiResponse;
import com.calt.coffeeshop.w1crud_maven.dto.response.CategoryResponse;
import com.calt.coffeeshop.w1crud_maven.entity.Category;
import com.calt.coffeeshop.w1crud_maven.enums.StatusCode;
import com.calt.coffeeshop.w1crud_maven.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.util.Objects.isNull;
@RestController
@RequestMapping("/api/categories")
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
    @GetMapping("/{cateName}")
    //return String becase the whole html site are Strings!!!
    public ApiResponse<CategoryResponse> getCategory(@PathVariable("cateName") String cateName){

        CategoryResponse categoryResponse= categoryService.getCategoryRByName(cateName);
        ApiResponse apiResponse= ApiResponse.<CategoryResponse>builder()
                .result(categoryResponse)
                .message(StatusCode.FOUND.getMessage())
                .code(StatusCode.FOUND.getCode())
                .build();

        return apiResponse;
    }

    @PutMapping("/{id}")
    //return String becase the whole html site are Strings!!!
    public String editCategory(@PathVariable("id") Long id,
                               @RequestBody CategoryRequest categoryRequest){

        categoryService.updateCategory(categoryRequest);
        return "OK";
    }

    @PostMapping("")

    public ResponseEntity<String> addProduct(@RequestBody CategoryRequest categoryRequest){

        categoryService.saveCategoryfromDTO(categoryRequest);
        return ResponseEntity.status(201).body("Created!");



    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable("id") Long id){

        categoryService.deleteCategory(id);

        return ResponseEntity.status(HttpStatus.OK).body("Deleted!");
    }
}
