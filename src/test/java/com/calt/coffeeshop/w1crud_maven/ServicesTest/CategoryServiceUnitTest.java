package com.calt.coffeeshop.w1crud_maven.ServicesTest;

import com.calt.coffeeshop.w1crud_maven.dto.request.CategoryRequest;
import com.calt.coffeeshop.w1crud_maven.dto.response.CategoryResponse;
import com.calt.coffeeshop.w1crud_maven.entity.Category;
import com.calt.coffeeshop.w1crud_maven.entity.Product;
import com.calt.coffeeshop.w1crud_maven.exception.AppException;
import com.calt.coffeeshop.w1crud_maven.repository.CategoryRepository;
import com.calt.coffeeshop.w1crud_maven.service.CategoryService;
import com.calt.coffeeshop.w1crud_maven.mapper.CategoryMapper;
import com.calt.coffeeshop.w1crud_maven.mapper.ProductMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceUnitTest {
    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private ProductMapper productMapper;

    private Category validCategory;
    private CategoryRequest validCategoryRequest;
    private CategoryResponse validCategoryResponse;


    @BeforeEach
    void setup() {

        validCategoryRequest = CategoryRequest.builder()
                .id(144L)
                .name("Beverage")
                .build();

        validCategory = Category.builder()
                .id(144L)
                .name("Beverage")
                .listProduct(new ArrayList<>())
                .build();
        validCategoryResponse = CategoryResponse.builder()
                .id(144L)
                .name("Beverage")
                .listProduct(new ArrayList<>())
                .build();
    }

    // --------------------- saveCategory ---------------------
    @Test
    void saveCategory_success() {
        Mockito.when(categoryRepository.save(validCategory)).thenReturn(validCategory);

        categoryService.saveCategory(validCategory);

        Mockito.verify(categoryRepository).save(validCategory);
    }

    // --------------------- saveCategoryfromDTO ---------------------
    @Test
    void saveCategoryfromDTO_success() {
        Mockito.when(categoryRepository.existsByName(anyString())).thenReturn(false);
        Mockito.when(categoryRepository.save(validCategory)).thenReturn(validCategory);

        Mockito.when(categoryMapper.toCategory(validCategoryRequest)).thenReturn(validCategory);

        categoryService.saveCategoryfromDTO(validCategoryRequest);

        Mockito.verify(categoryRepository).save(validCategory);
    }

    @Test
    void saveCategoryfromDTO_duplicateName_throws() {
        Mockito.when(categoryRepository.existsByName(anyString())).thenReturn(true);

        Assertions.assertThatThrownBy(() ->
                        categoryService.saveCategoryfromDTO(validCategoryRequest))
                .isInstanceOf(DataIntegrityViolationException.class);
        Mockito.verify(categoryRepository, Mockito.never()).save(any());
    }

    // --------------------- getAllCategories ---------------------
    @Test
    void getAllCategories_returnsList() {
        List<Category> list = List.of(validCategory);
        Mockito.when(categoryRepository.findAll()).thenReturn(list);

        List<Category> result = categoryService.getAllCategories();

        Assertions.assertThat(result).hasSize(1).contains(validCategory);
    }

    @Test
    void getAllCategories_emptyList() {
        Mockito.when(categoryRepository.findAll()).thenReturn(new ArrayList<>());

        List<Category> result = categoryService.getAllCategories();

        Assertions.assertThat(result).isEmpty();
    }

    // --------------------- getCategoryByID ---------------------
    @Test
    void getCategoryByID_exists() {
        Mockito.when(categoryRepository.findById(144L)).thenReturn(Optional.of(validCategory));
        Mockito.when(categoryMapper.toCategoryResponse(validCategory)).thenReturn(validCategoryResponse);

        CategoryResponse response = categoryService.getCategoryByID(144L);

        Assertions.assertThat(response.getName()).isEqualTo("Beverage");
    }

    @Test
    void getCategoryByID_notFound_throws() {
        Mockito.when(categoryRepository.findById(144L)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> categoryService.getCategoryByID(144L))
                .isInstanceOf(AppException.class);
    }

    // --------------------- getCategoryRByName ---------------------
    @Test
    void getCategoryRByName_exists() {
        Mockito.when(categoryRepository.findCategoryByName("Beverage")).thenReturn(Optional.of(validCategory));
        Mockito.when(categoryMapper.toCategoryResponse(validCategory)).thenReturn(validCategoryResponse);

        CategoryResponse response = categoryService.getCategoryRByName("Beverage");

        Assertions.assertThat(response.getName()).isEqualTo("Beverage");
    }

    @Test
    void getCategoryRByName_notFound_throws() {
        Mockito.when(categoryRepository.findCategoryByName("Beverage")).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> categoryService.getCategoryRByName("Beverage"))
                .isInstanceOf(AppException.class);
    }

    // --------------------- getCategoryByName ---------------------
    @Test
    void getCategoryByName_exists() {
        Mockito.when(categoryRepository.findCategoryByName("Beverage")).thenReturn(Optional.of(validCategory));

        Category result = categoryService.getCategoryByName("Beverage");

        Assertions.assertThat(result).isEqualTo(validCategory);
    }

    @Test
    void getCategoryByName_notFound_returnsNull() {
        Mockito.when(categoryRepository.findCategoryByName("Beverage")).thenReturn(Optional.empty());

        Category result = categoryService.getCategoryByName("Beverage");

        Assertions.assertThat(result).isNull();
    }

    // --------------------- deleteCategory ---------------------
    @Test
    void deleteCategory_success() {
        Mockito.doNothing().when(categoryRepository).deleteById(anyLong());

        categoryService.deleteCategory(144L);

        Mockito.verify(categoryRepository).deleteById(144L);
    }

    // --------------------- updateCategory ---------------------
    @Test
    void updateCategory_success() {
        Mockito.when(categoryRepository.findCategoryByName("Beverage")).thenReturn(Optional.of(validCategory));
        Mockito.when(categoryMapper.updateCategory(validCategoryRequest, validCategory)).thenReturn(validCategory);
        Mockito.when(categoryMapper.toCategoryResponse(validCategory)).thenReturn(validCategoryResponse);
        Mockito.when(categoryRepository.save(validCategory)).thenReturn(validCategory);

        CategoryResponse response = categoryService.updateCategory(validCategoryRequest);

        Assertions.assertThat(response.getName()).isEqualTo("Beverage");
    }

    @Test
    void updateCategory_notFound_throws() {
        Mockito.when(categoryRepository.findCategoryByName("Beverage")).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> categoryService.updateCategory(validCategoryRequest))
                .isInstanceOf(AppException.class);
    }

    // --------------------- convertToCategoryResponse ---------------------
    @Test
    void convertToCategoryResponse_withProducts() {
        Mockito.when(categoryMapper.toCategoryResponse(validCategory)).thenReturn(validCategoryResponse);

        Product product = Product.builder().name("Coffee").build();

         validCategory.getListProduct().add(product);

        CategoryResponse response = categoryService.convertToCategoryResponse(validCategory);

        Assertions.assertThat(response.getListProduct()).contains("Coffee");
    }

    @Test
    void convertToCategoryResponse_emptyProducts() {
        Mockito.when(categoryMapper.toCategoryResponse(validCategory)).thenReturn(validCategoryResponse);

        CategoryResponse response = categoryService.convertToCategoryResponse(validCategory);

        Assertions.assertThat(response.getListProduct()).isEmpty();
    }
}
