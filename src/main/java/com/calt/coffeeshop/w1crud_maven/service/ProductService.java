package com.calt.coffeeshop.w1crud_maven.service;

import com.calt.coffeeshop.w1crud_maven.dto.request.ProductRequest;
import com.calt.coffeeshop.w1crud_maven.dto.response.CategoryResponse;
import com.calt.coffeeshop.w1crud_maven.dto.response.ProductResponse;
import com.calt.coffeeshop.w1crud_maven.entity.Category;
import com.calt.coffeeshop.w1crud_maven.entity.Product;
import com.calt.coffeeshop.w1crud_maven.exception.AppException;
import com.calt.coffeeshop.w1crud_maven.enums.ErrorCode;
import com.calt.coffeeshop.w1crud_maven.mapper.ProductMapper;
import com.calt.coffeeshop.w1crud_maven.repository.ProductRepository;
import com.calt.coffeeshop.w1crud_maven.specs.ProductSpecs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('CREATE_PRODUCT')")

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
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('CREATE_PRODUCT')")

    public void saveProduct(Product rProduct) {
            productRepository.save(rProduct);
    }
    public Page<ProductResponse> getAllProducts(Pageable pageable){
        Page<Product> productPage =productRepository.findAll(pageable);
        return productPage.map(product -> {
            String category = product.getCategory().getName();
            ProductResponse productResponse= productMapper.toProductResponse(product);
            productResponse.setCategory(category);
            return  productResponse;
        });
    }

    public Page<ProductResponse> searchProduct(String keyword, Pageable pageable){
        Page<Product> productPage =productRepository.findAll(ProductSpecs.hasKeyword(keyword),pageable);

        return productPage.map(product -> {
            String category = product.getCategory().getName();
            ProductResponse productResponse= productMapper.toProductResponse(product);
            productResponse.setCategory(category);
            return  productResponse;
        });
    }
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('READ_PRODUCT')")
    @Cacheable(value = "products",key = "#id")
    public Product getProductByID(String id){
        return productRepository.findById(id).orElseThrow(()->new RuntimeException("Product not found!"));
        //productRepository.findById(id) will return an Optional<Type>
    }
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('DELETE_PRODUCT')")

    public void deleteProduct(Product product){
        try{
            productRepository.delete(product);
        }
          catch (DataIntegrityViolationException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Can't delete!");
        }

    }
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('UPDATE_PRODUCT')")

    public Product updateProduct(String id, ProductRequest request){
        Product product=getProductByID(id);
        Category category = categoryService.getCategoryByName(request.getCategory());
//        request.setCreated_at(product.getCreated_at());
        productMapper.updateProduct(product,request);
        product.setCategory(category);
        return productRepository.save(product);
    }


}
