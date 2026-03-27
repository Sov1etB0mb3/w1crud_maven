package com.calt.coffeeshop.w1crud_maven.ServicesTest;

import com.calt.coffeeshop.w1crud_maven.dto.request.ProductRequest;
import com.calt.coffeeshop.w1crud_maven.dto.response.ProductResponse;
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
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@SpringBootTest
public class ProductServiceTest {
    @Autowired
    private ProductService productService;
    @MockitoBean
    private ProductRepository productRepository;
    @MockitoBean
    private ProductMapper productMapper;

    @MockitoBean
    private CategoryService categoryService;

    private ProductResponse productResponse;
    private Product product;
    private Product validProduct;
    private ProductRequest validRequest;
    private ProductRequest productRequest;
    private Category category;

    private List<Product> productList;
    @BeforeEach
    void initData(){
        productRequest = productRequest.builder()
                .id("DD0")
                .name("RandomThing")
                .quantity(14)
                .price(55)
                .build();

        product = Product.builder()
                .id("DD0")
                .name("RandomThing")
                .quantity(14)
                .price(55)
                .build();
        validRequest = ProductRequest.builder()
                .id("DD0")
                .name("RandomThing")
                .quantity(10)
                .price(50)
                .category("Beverage")
                .build();

        validProduct = Product.builder()
                .id(validRequest.getId())
                .name(validRequest.getName())
                .quantity(validRequest.getQuantity())
                .price(validRequest.getPrice())
                .category(new Category(validRequest.getCategory(),"huuhuhu"))
                .build();
        category = new Category("Beverage","well well");


    }
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    //---Create test
    void createProduct_validRequest_success(){
        //IF DON'T USE MOCK NOTATION
//        var auth = new UsernamePasswordAuthenticationToken(
//                "user",
//                "password",
//                List.of(new SimpleGrantedAuthority("CREATE_PRODUCT"))
//        );
//        SecurityContextHolder.getContext().setAuthentication(auth);


        Mockito.when(productRepository.existsByName(anyString()))
                .thenReturn( false);
        Mockito.when(productRepository.save(any())).thenReturn(product);
        Mockito.when(productMapper.toProduct(productRequest)).thenReturn(product);

        var response = productService.saveProductfromDTO(productRequest);

        Assertions.assertThat(response.getId()).isEqualTo("DD0");
        Assertions.assertThat(response.getName()).isEqualTo("RandomThing");

    }
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void saveProductfromDTO_success_withCategory() {
        Mockito.when(productRepository.existsById(anyString())).thenReturn(false);
        Mockito.when(productMapper.toProduct(validRequest)).thenReturn(validProduct);
        Mockito.when(productRepository.save(any())).thenReturn(validProduct);

        Product response = productService.saveProductfromDTO(validRequest);

        Assertions.assertThat(response.getId()).isEqualTo(validRequest.getId());
        Assertions.assertThat(response.getCategory()).isNotNull();
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void saveProductfromDTO_duplicateId_throws() {
        Mockito.when(productRepository.existsById(anyString())).thenReturn(true);

        Assertions.assertThatThrownBy(() -> productService.saveProductfromDTO(validRequest))
                .isInstanceOf(AppException.class);
        Mockito.verify(productRepository, Mockito.never()).save(any());
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void saveProductfromDTO_unauthorized() {
        Assertions.assertThatThrownBy(() -> productService.saveProductfromDTO(validRequest))
                .isInstanceOf(org.springframework.security.access.AccessDeniedException.class);
    }





// ---------get products test
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getProductByID_exists() {
        Mockito.when(productRepository.findById("DD0")).thenReturn(Optional.of(validProduct));

        Product result = productService.getProductByID("DD0");

        Assertions.assertThat(result.getId()).isEqualTo("DD0");
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getProductByID_notFound_throws() {
        Mockito.when(productRepository.findById("DD0")).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> productService.getProductByID("DD0"))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void getProductByID_unauthorized() {
        Assertions.assertThatThrownBy(() -> productService.getProductByID("DD0"))
                .isInstanceOf(org.springframework.security.access.AccessDeniedException.class);
    }
    // --------------------- getAllProducts ---------------------
    @Test
    void getAllProducts_returnsPage() {
        Page<Product> page = new PageImpl<>(List.of(validProduct));
        Mockito.when(productRepository.findAll(PageRequest.of(0,5))).thenReturn(page);

        Page<ProductResponse> result = productService.getAllProducts(PageRequest.of(0,5));

        Assertions.assertThat(result.getContent()).hasSize(1);
    }

    // --------------------- updateProduct ---------------------
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void updateProduct_success() {
        ProductRequest updateRequest = ProductRequest.builder()
                .name("UpdatedName")
                .quantity(20)
                .price(100)
                .category("Beverage")
                .build();

        Product updatedProduct = Product.builder()
                .id("DD0")
                .name("UpdatedName")
                .quantity(20)
                .price(100)
                .category(category)
                .build();

        Mockito.when(productRepository.findById("DD0")).thenReturn(Optional.of(validProduct));
        Mockito.when(categoryService.getCategoryByName("Beverage")).thenReturn(category);
        Mockito.doAnswer(invocation -> {
            validProduct.setName(updateRequest.getName());
            validProduct.setQuantity(updateRequest.getQuantity());
            validProduct.setPrice(updateRequest.getPrice());
            return null;
        }).when(productMapper).updateProduct(validProduct, updateRequest);
        Mockito.when(productRepository.save(validProduct)).thenReturn(updatedProduct);

        Product result = productService.updateProduct("DD0", updateRequest);

        Assertions.assertThat(result.getName()).isEqualTo("UpdatedName");
        Assertions.assertThat(result.getCategory()).isEqualTo(category);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void updateProduct_nonExistent_throws() {
        Mockito.when(productRepository.findById("DD0")).thenReturn(Optional.empty());

        ProductRequest updateRequest = ProductRequest.builder().build();

        Assertions.assertThatThrownBy(() -> productService.updateProduct("DD0", updateRequest))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void updateProduct_unauthorized() {
        ProductRequest updateRequest = ProductRequest.builder().build();

        Assertions.assertThatThrownBy(() -> productService.updateProduct("DD0", updateRequest))
                .isInstanceOf(org.springframework.security.access.AccessDeniedException.class);
    }

    // --------------------- deleteProduct ---------------------
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void deleteProduct_success() {
        Mockito.doNothing().when(productRepository).delete(validProduct);

        productService.deleteProduct(validProduct);

        Mockito.verify(productRepository).delete(validProduct);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void deleteProduct_constraintViolation_throws() {
        Mockito.doThrow(new DataIntegrityViolationException("")).when(productRepository).delete(validProduct);

        Assertions.assertThatThrownBy(() -> productService.deleteProduct(validProduct))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void deleteProduct_unauthorized() {
        Assertions.assertThatThrownBy(() -> productService.deleteProduct(validProduct))
                .isInstanceOf(org.springframework.security.access.AccessDeniedException.class);
    }

}

