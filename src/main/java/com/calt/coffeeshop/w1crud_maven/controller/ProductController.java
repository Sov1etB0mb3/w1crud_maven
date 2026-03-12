package com.calt.coffeeshop.w1crud_maven.controller;

import com.calt.coffeeshop.w1crud_maven.dto.requestdto.ProductRequest;
import com.calt.coffeeshop.w1crud_maven.dto.responsedto.ApiResponse;
import com.calt.coffeeshop.w1crud_maven.entity.Product;
import com.calt.coffeeshop.w1crud_maven.enums.StatusCode;
import com.calt.coffeeshop.w1crud_maven.service.ProductService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/products")
//@CrossOrigin("http://localhost:xxx")//let domain http://localhost:xxx access resut of api to avoid CORS
@Tag(name = "Product Controller", description = "")
public class ProductController {
    @Autowired
    private ProductService productService;
    @GetMapping("")
    //return String becase the whole html site are Strings!!!
    public ApiResponse<List<Product>> getProduct(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending){
        //List<Product> productList= productService.getAllProducts();
        Sort sort= ascending ? Sort.by(sortBy).ascending():Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page,pageSize,sort);
        ApiResponse apiResponse = ApiResponse.builder().build();
        apiResponse.setResult(productService.getAllProducts(pageable));
        apiResponse.setCode(703);
        apiResponse.setMessage("GOT!");

        return apiResponse;
    }
    @GetMapping("/{id}")
    //return String becase the whole html site are Strings!!!
    public ApiResponse<Product> getProduct(@PathVariable("id") String id){

        ApiResponse apiResponse = ApiResponse.builder().build();
        apiResponse.setResult(productService.getProductByID(id));
        apiResponse.setCode(703);
        apiResponse.setMessage("GOT!");

        return apiResponse;
    }


    @PatchMapping("/{id}")
    public ApiResponse<Product> updateProduct(@PathVariable("id") String id, @RequestBody ProductRequest rProduct){
        ApiResponse apiResponse = ApiResponse.builder().build();
        apiResponse.setCode(StatusCode.UPDATED.getCode());
        apiResponse.setMessage(StatusCode.UPDATED.getMessage());
        rProduct.setUpdated_at(Instant.now());
        apiResponse.setResult(productService.updateProduct(id,rProduct));
        return apiResponse;
    }
    @PostMapping("")
    public ApiResponse<Product> addProduct(@RequestBody @Valid ProductRequest productDto){
        ApiResponse apiResponse = ApiResponse.builder().build();
        productDto.setCreated_at(Instant.now());
        productDto.setUpdated_at(Instant.now());
        apiResponse.setResult( productService.saveProductfromDTO(productDto));
            return apiResponse;
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteProduct(@PathVariable("id") String id){

        ApiResponse apiResponse = ApiResponse.builder().build();
        productService.deleteProduct(productService.getProductByID(id));
        apiResponse.setMessage(StatusCode.DELETED.getMessage());
        apiResponse.setCode(StatusCode.DELETED.getCode());
        apiResponse.setResult(null);
        return apiResponse;
    }
}
