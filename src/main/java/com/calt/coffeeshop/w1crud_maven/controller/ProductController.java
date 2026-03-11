package com.calt.coffeeshop.w1crud_maven.controller;

import com.calt.coffeeshop.w1crud_maven.dto.requestdto.ProductRequestDto;
import com.calt.coffeeshop.w1crud_maven.dto.responsedto.ApiResponseDto;
import com.calt.coffeeshop.w1crud_maven.entity.Product;
import com.calt.coffeeshop.w1crud_maven.enums.StatusCode;
import com.calt.coffeeshop.w1crud_maven.service.ProductService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public ApiResponseDto<List<Product>> getProduct(){
        //List<Product> productList= productService.getAllProducts();
        ApiResponseDto apiResponseDto = ApiResponseDto.builder().build();
        apiResponseDto.setResult(productService.getAllProducts());
        apiResponseDto.setCode(703);
        apiResponseDto.setMessage("GOT!");

        return apiResponseDto;
    }
    @GetMapping("/{id}")
    //return String becase the whole html site are Strings!!!
    public ApiResponseDto<Product> getProduct(@PathVariable("id") String id){

        ApiResponseDto apiResponseDto = ApiResponseDto.builder().build();
        apiResponseDto.setResult(productService.getProductByID(id));
        apiResponseDto.setCode(703);
        apiResponseDto.setMessage("GOT!");

        return apiResponseDto;
    }


    @PatchMapping("/{id}")
    public ApiResponseDto<Product> updateProduct(@PathVariable("id") String id, @RequestBody ProductRequestDto rProduct){
        ApiResponseDto apiResponseDto = ApiResponseDto.builder().build();
        apiResponseDto.setCode(StatusCode.UPDATED.getCode());
        apiResponseDto.setMessage(StatusCode.UPDATED.getMessage());
        apiResponseDto.setResult(productService.updateProduct(id,rProduct));
        return apiResponseDto;
    }
    @PostMapping("")
    public ApiResponseDto<Product> addProduct(@RequestBody @Valid ProductRequestDto productDto){
        ApiResponseDto apiResponseDto = ApiResponseDto.builder().build();
        apiResponseDto.setResult( productService.saveProductfromDTO(productDto));
            return apiResponseDto;
    }

    @DeleteMapping("/{id}")
    public ApiResponseDto<String> deleteProduct(@PathVariable("id") String id){

        ApiResponseDto apiResponseDto = ApiResponseDto.builder().build();
        productService.deleteProduct(productService.getProductByID(id));
        apiResponseDto.setMessage(StatusCode.DELETED.getMessage());
        apiResponseDto.setCode(StatusCode.DELETED.getCode());
        apiResponseDto.setResult(null);


        return apiResponseDto;
    }
}
