package com.calt.coffeeshop.w1crud_maven.controller;

import com.calt.coffeeshop.w1crud_maven.dto.requestdto.RequestProduct;
import com.calt.coffeeshop.w1crud_maven.dto.responsedto.ApiResponse;
import com.calt.coffeeshop.w1crud_maven.entity.Product;
import com.calt.coffeeshop.w1crud_maven.service.ProductService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
    public ApiResponse<List<Product>> getProduct(){
        //List<Product> productList= productService.getAllProducts();
        ApiResponse apiResponse= new ApiResponse();
        apiResponse.setResult(productService.getAllProducts());
        apiResponse.setCode(3003);
        apiResponse.setMessage("GOT!");

        return apiResponse;
    }
    @GetMapping("/{id}")
    //return String becase the whole html site are Strings!!!
    public ApiResponse<Product> getProduct(@PathVariable("id") String id){

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setResult(productService.getProductByID(id));
        //Product rProduct= productService.getProductByID(id);
        apiResponse.setCode(3003);
        apiResponse.setMessage("GOT!");
//        if(isNull( productService.getProductByID(id))){
//            return ResponseEntity.noContent().build();
//        }
//
//        return ResponseEntity.ok(rProduct);
        return apiResponse;
    }

//    @PatchMapping("/{id}")
//    //return String becase the whole html site are Strings!!!
//    public ResponseEntity<String> updateProduct(@RequestBody RequestProduct rProduct){
//       if (!isNull(productService.getProductByID(rProduct.getId()))){
//           if(productService.saveProductfromDTO(rProduct))
//           return ResponseEntity.ok("Updated!");
//           else
//               return ResponseEntity.status(HttpStatus.CONFLICT).body("Can not Update!");
//       }
//       return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Not found product id!");
//    }
    @PatchMapping("/{id}")
    public ApiResponse<Product> updateProduct(@PathVariable("id") String id,@RequestBody RequestProduct rProduct){
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(222);
        apiResponse.setMessage("updated!");
        apiResponse.setResult(productService.updateProduct(id,rProduct));
        return apiResponse;
    }
    @PostMapping("")

    public ApiResponse<Product> addProduct(@RequestBody @Valid RequestProduct productDto){
        ApiResponse<Product> apiResponse = new ApiResponse<>();
        apiResponse.setResult( productService.saveProductfromDTO(productDto));
            return apiResponse;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable("id") String id){

        productService.deleteProduct(productService.getProductByID(id));

        return ResponseEntity.status(HttpStatus.OK).body("Deleted!");
    }
}
