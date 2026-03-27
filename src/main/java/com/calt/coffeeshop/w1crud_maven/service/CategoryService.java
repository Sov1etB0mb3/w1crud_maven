package com.calt.coffeeshop.w1crud_maven.service;

import com.calt.coffeeshop.w1crud_maven.dto.request.CategoryRequest;
import com.calt.coffeeshop.w1crud_maven.dto.response.CategoryResponse;
import com.calt.coffeeshop.w1crud_maven.dto.response.ProductResponse;
import com.calt.coffeeshop.w1crud_maven.entity.Category;
import com.calt.coffeeshop.w1crud_maven.enums.ErrorCode;
import com.calt.coffeeshop.w1crud_maven.exception.AppException;
import com.calt.coffeeshop.w1crud_maven.mapper.CategoryMapper;
import com.calt.coffeeshop.w1crud_maven.mapper.ProductMapper;
import com.calt.coffeeshop.w1crud_maven.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private ProductMapper productMapper;

    //no need to add autowire because there is only way to inject is through constructor with only one parameter
//    public CategoryService(CategoryRepository categoryRepository){
//        this.categoryRepository=categoryRepository;
//    }

    public void saveCategory(Category category){
        categoryRepository.save(category);
    }
    public void saveCategoryfromDTO(CategoryRequest rCategory){
        if(categoryRepository.existsByName(rCategory.getName())){
            throw new DataIntegrityViolationException("This category existed!");
        }

        categoryRepository.save(categoryMapper.toCategory(rCategory));

    }
    public List<Category> getAllCategories(){
        return categoryRepository.findAll();
    }
    public CategoryResponse getCategoryByID(Long id){
       Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOTFOUND));

        return convertToCategoryResponse(category);

    }
    public CategoryResponse getCategoryRByName(String name){
        Category category = categoryRepository.findCategoryByName(name)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOTFOUND));

        return convertToCategoryResponse(category);

    }
    public Category getCategoryByName(String name){
        Optional<Category> optionalCategory= categoryRepository.findCategoryByName(name);
        if(!optionalCategory.isEmpty()){
            return optionalCategory.get();
        }
        return null;
    }
    @Transactional
    public void deleteCategory(Long id){
           categoryRepository.deleteById(id);

    }

    public CategoryResponse updateCategory(CategoryRequest categoryRequest) {
        Category category = categoryRepository.findCategoryByName(categoryRequest
                .getName()).orElseThrow(()->new AppException(ErrorCode.CATEGORY_NOTFOUND));
        category = categoryMapper.updateCategory(categoryRequest,category);
        category= categoryRepository.save(category);
        CategoryResponse categoryResponse = convertToCategoryResponse(category);
        return categoryResponse;
    }

    public CategoryResponse convertToCategoryResponse(Category category){
        List<String> productList= category.getListProduct().stream()
                .map(product -> product.getName())
                .collect(Collectors.toList());
        CategoryResponse categoryResponse = categoryMapper.toCategoryResponse(category) ;
        categoryResponse.setListProduct(productList);
        return categoryResponse;
    }
}
