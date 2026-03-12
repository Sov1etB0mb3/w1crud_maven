package com.calt.coffeeshop.w1crud_maven.service;

import com.calt.coffeeshop.w1crud_maven.dto.requestdto.ProductRequest;
import com.calt.coffeeshop.w1crud_maven.entity.Category;
import com.calt.coffeeshop.w1crud_maven.entity.Product;
import com.calt.coffeeshop.w1crud_maven.exception.AppException;
import com.calt.coffeeshop.w1crud_maven.enums.ErrorCode;
import com.calt.coffeeshop.w1crud_maven.mapper.ProductMapper;
import com.calt.coffeeshop.w1crud_maven.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductMapper productMapper;
    public Product saveProductfromDTO(ProductRequest rProduct) {

        if (productRepository.existsById(rProduct.getId()))
            throw new AppException(ErrorCode.EXISTED);
        //use mapper later!

        //Product newProduct = new Product(rProduct.getId(), rProduct.getName(), rProduct.getQuantity(), rProduct.getPrice());
        //now is mapper
        Product newProduct = productMapper.toProduct(rProduct);
        if(newProduct.getCategory()!=null){
            return productRepository.save(newProduct);
        }
        newProduct.setCategory(null);
        return productRepository.save(newProduct);

    }
    public void saveProduct(Product rProduct) {
            productRepository.save(rProduct);
    }
    public Page<Product> getAllProducts(Pageable pageable){
        return productRepository.findAll(pageable);
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
    public Product updateProduct(String id, ProductRequest request){
        Product product=getProductByID(id);
        Category category = categoryService.getCategoryByName(request.getCategory());
//        request.setCreated_at(product.getCreated_at());
        productMapper.updateProduct(product,request);
        product.setCategory(category);
        return productRepository.save(product);
    }


}
