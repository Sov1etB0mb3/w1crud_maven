package com.calt.coffeeshop.w1crud_maven.controller;

import com.calt.coffeeshop.w1crud_maven.dto.requestdto.RequestProduct;
import com.calt.coffeeshop.w1crud_maven.entity.Product;
import com.calt.coffeeshop.w1crud_maven.service.ProductService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.util.Objects.isNull;

@RestController
@RequestMapping("/api/products")
@CrossOrigin("http://localhost:xxx")//let domain http://localhost:xxx access resut of api to avoid CORS
@Tag(name = "API coffee store", description = "ALl endpoint to manipulate product, category CRUD")
public class ProductController {
    @Autowired
    private ProductService productService;
    @GetMapping("")
    //return String becase the whole html site are Strings!!!
    public ResponseEntity<List<Product>> getProduct(){
        List<Product> productList= productService.getAllProducts();

        if(productList.isEmpty()){
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(productList);
    }
    @GetMapping("/{id}")
    //return String becase the whole html site are Strings!!!
    public ResponseEntity<Product> getProduct(@PathVariable("id") String id){

        Product rProduct= productService.getProductByID(id);

        if(isNull( productService.getProductByID(id))){
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(rProduct);
    }

    @PatchMapping("/{id}")
    //return String becase the whole html site are Strings!!!
    public ResponseEntity<String> updateProduct(@RequestBody RequestProduct rProduct){
       if (!isNull(productService.getProductByID(rProduct.getId()))){
           if(productService.saveProductfromDTO(rProduct))
           return ResponseEntity.ok("Updated!");
           else
               return ResponseEntity.status(HttpStatus.CONFLICT).body("Can not Update!");
       }
       return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Not found product id!");
    }

    @PostMapping("")

    public ResponseEntity<String> addProduct(@RequestBody RequestProduct productDto){
            if(productService.saveProductfromDTO(productDto)){
                return ResponseEntity.status(201).body("Created!");
            }
//            throw new RuntimeException("Can't create!");
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error ID! "+productDto.getId()+" already existed!");



    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable("id") String id){

        productService.deleteProduct(productService.getProductByID(id));

        return ResponseEntity.status(HttpStatus.OK).body("Deleted!");
    }
}
