package com.calt.coffeeshop.w1crud_maven.service;

import com.calt.coffeeshop.w1crud_maven.dto.RequestProduct;
import com.calt.coffeeshop.w1crud_maven.entity.Product;
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
    public boolean saveProductfromDTO(RequestProduct rProduct) {
        if (!productRepository.existsById(rProduct.getId())){
            //use mapper later!
            Product newProduct = new Product(rProduct.getId(), rProduct.getName(), rProduct.getQuantity(), rProduct.getPrice());
            productRepository.save(newProduct);
            return true;
        }
        else
        return false;
    }
    public void saveProduct(Product rProduct) {
            productRepository.save(rProduct);
    }
    public List<Product> getAllProducts(){
        return productRepository.findAll();
    }
    public Product getProductByID(String id){
        return productRepository.findById(id).get();
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


}
