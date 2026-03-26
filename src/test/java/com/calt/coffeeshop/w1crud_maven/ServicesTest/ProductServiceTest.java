package com.calt.coffeeshop.w1crud_maven.ServicesTest;

import com.calt.coffeeshop.w1crud_maven.dto.request.ProductRequest;
import com.calt.coffeeshop.w1crud_maven.dto.response.ProductResponse;
import com.calt.coffeeshop.w1crud_maven.entity.Product;
import com.calt.coffeeshop.w1crud_maven.repository.ProductRepository;
import com.calt.coffeeshop.w1crud_maven.service.ProductService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@SpringBootTest
public class ProductServiceTest {
    @Autowired
    private ProductService productService;
    @MockitoBean
    private ProductRepository productRepository;

    private ProductResponse productResponse;
    private Product product;
    private ProductRequest productRequest;

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

    }
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})

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

        var response = productService.saveProductfromDTO(productRequest);

        Assertions.assertThat(response.getId()).isEqualTo("DD0");
        Assertions.assertThat(response.getName()).isEqualTo("RandomThing");
    }
}

