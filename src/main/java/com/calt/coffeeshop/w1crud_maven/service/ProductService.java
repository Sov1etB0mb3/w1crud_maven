package com.calt.coffeeshop.w1crud_maven.service;

import com.calt.coffeeshop.w1crud_maven.dto.requestdto.RequestProduct;
import com.calt.coffeeshop.w1crud_maven.entity.Product;
import com.calt.coffeeshop.w1crud_maven.exception.AppException;
import com.calt.coffeeshop.w1crud_maven.exception.ErrorCode;
import com.calt.coffeeshop.w1crud_maven.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    public Product saveProductfromDTO(RequestProduct rProduct) {

        if (productRepository.existsById(rProduct.getId()))
            throw new AppException(ErrorCode.EXISTED);
        //use mapper later!
        Product newProduct = new Product(rProduct.getId(), rProduct.getName(), rProduct.getQuantity(), rProduct.getPrice());
        return productRepository.save(newProduct);

    }
    public void saveProduct(Product rProduct) {
            productRepository.save(rProduct);
    }
    public List<Product> getAllProducts(){
        return productRepository.findAll();
    }
    public Product getProductByID(String id){
        return productRepository.findById(id).orElseThrow(()->new RuntimeException("Product not found!"));
        //productRepository.findById(id) will return an Optional<Type>
    }
    public void deleteProduct(Product product){
        try{
            productRepository.delete(product);
        }
          catch (DataIntegrityViolationException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Can't delete!");
        }

    }
    public Product updateProduct(String id,RequestProduct request){
        Product product=getProductByID(id);
        product.setName(request.getName());
        product.setCategory(request.getCategory());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());
        return productRepository.save(product);
    }


}
