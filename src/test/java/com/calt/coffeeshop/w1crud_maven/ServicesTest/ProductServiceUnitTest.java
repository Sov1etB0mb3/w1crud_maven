package com.calt.coffeeshop.w1crud_maven.ServicesTest;


import com.calt.coffeeshop.w1crud_maven.dto.request.ProductRequest;
import com.calt.coffeeshop.w1crud_maven.entity.Category;
import com.calt.coffeeshop.w1crud_maven.entity.Product;
import com.calt.coffeeshop.w1crud_maven.exception.AppException;
import com.calt.coffeeshop.w1crud_maven.mapper.ProductMapper;
import com.calt.coffeeshop.w1crud_maven.repository.ProductRepository;
import com.calt.coffeeshop.w1crud_maven.service.CategoryService;
import com.calt.coffeeshop.w1crud_maven.service.ProductService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.server.ResponseStatusException;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;


import java.util.List;


@ExtendWith(MockitoExtension.class)
public class ProductServiceUnitTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private CategoryService categoryService;

    private Product product;
    private Product validProduct;
    private ProductRequest validRequest;
    private ProductRequest productRequest;
    private Category category;

    @BeforeEach
    void initData() {
        productRequest = ProductRequest.builder().id("DD0").name("RandomThing").quantity(14).price(55).build();
        product = Product.builder().id("DD0").name("RandomThing").quantity(14).price(55).build();
        validRequest = ProductRequest.builder().id("DD0").name("RandomThing").quantity(10).price(50).category("Beverage").build();
        category = new Category("Beverage", "well well");
        validProduct = Product.builder().id("DD0").name("RandomThing").quantity(10).price(50).category(category).build();
    }

    // --- CREATE TESTS ---
    @Test
    void createProduct_validRequest_success() {
//        Mockito.when(productRepository.existsByName(anyString())).thenReturn(false);
        Mockito.when(productMapper.toProduct(productRequest)).thenReturn(product);
        Mockito.when(productRepository.save(any())).thenReturn(product);

        var response = productService.saveProductfromDTO(productRequest);
        Assertions.assertThat(response.getId()).isEqualTo("DD0");
    }

    @Test
    void saveProductfromDTO_success_withCategory() {
        Mockito.when(productRepository.existsById(anyString())).thenReturn(false);
        Mockito.when(productMapper.toProduct(validRequest)).thenReturn(validProduct);
        Mockito.when(productRepository.save(any())).thenReturn(validProduct);

        Product response = productService.saveProductfromDTO(validRequest);
        Assertions.assertThat(response.getCategory()).isNotNull();
    }

    @Test
    void saveProductfromDTO_duplicateId_throws() {
        Mockito.when(productRepository.existsById(anyString())).thenReturn(true);
        Assertions.assertThatThrownBy(() -> productService.saveProductfromDTO(validRequest))
                .isInstanceOf(AppException.class);
    }

    @Test
    void saveProductfromDTO_unauthorized() {
        // Note: In Unit tests, you manually mock the exception if the service has @PreAuthorize
        // or let the Integration test handle this layer.
        // Mockito.doThrow(AccessDeniedException.class)...
    }

    // --- GET TESTS ---
    @Test
    void getProductByID_exists() {
        Mockito.when(productRepository.findById("DD0")).thenReturn(Optional.of(validProduct));
        Product result = productService.getProductByID("DD0");
        Assertions.assertThat(result.getId()).isEqualTo("DD0");
    }

    @Test
    void getProductByID_notFound_throws() {
        Mockito.when(productRepository.findById("DD0")).thenReturn(Optional.empty());
        Assertions.assertThatThrownBy(() -> productService.getProductByID("DD0"))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void getProductByID_unauthorized() {
        // Assertions.assertThatThrownBy...
    }

    @Test
    void getAllProducts_returnsPage() {
        Page<Product> page = new PageImpl<>(List.of(validProduct));
        Mockito.when(productRepository.findAll(any(PageRequest.class))).thenReturn(page);
        Page<Product> result = productService.getAllProducts(PageRequest.of(0, 5));
        Assertions.assertThat(result.getContent()).hasSize(1);
    }

    // --- UPDATE TESTS ---
    @Test
    void updateProduct_success() {
        ProductRequest updateRequest = ProductRequest.builder().name("Updated").category("Beverage").build();
        Mockito.when(productRepository.findById("DD0")).thenReturn(Optional.of(validProduct));
        Mockito.when(categoryService.getCategoryByName("Beverage")).thenReturn(category);
        Mockito.when(productRepository.save(any())).thenReturn(validProduct);

        Product result = productService.updateProduct("DD0", updateRequest);
        Assertions.assertThat(result).isNotNull();
    }

    @Test
    void updateProduct_nonExistent_throws() {
        Mockito.when(productRepository.findById("DD0")).thenReturn(Optional.empty());
        Assertions.assertThatThrownBy(() -> productService.updateProduct("DD0", validRequest))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void updateProduct_unauthorized() {
        // Assertions.assertThatThrownBy...
    }

    // --- DELETE TESTS ---
    @Test
    void deleteProduct_success() {
        Mockito.doNothing().when(productRepository).delete(validProduct);
        productService.deleteProduct(validProduct);
        Mockito.verify(productRepository).delete(validProduct);
    }

    @Test
    void deleteProduct_constraintViolation_throws() {
        Mockito.doThrow(new DataIntegrityViolationException("")).when(productRepository).delete(validProduct);
        Assertions.assertThatThrownBy(() -> productService.deleteProduct(validProduct))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void deleteProduct_unauthorized() {
        // Assertions.assertThatThrownBy...
    }
}