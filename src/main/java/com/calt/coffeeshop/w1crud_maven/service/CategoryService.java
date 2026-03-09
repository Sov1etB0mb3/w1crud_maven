package com.calt.coffeeshop.w1crud_maven.service;

import com.calt.coffeeshop.w1crud_maven.dto.RequestCategory;
import com.calt.coffeeshop.w1crud_maven.entity.Category;
import com.calt.coffeeshop.w1crud_maven.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    //no need to add autowire because there is only way to inject is through constructor with only one parameter
    public CategoryService(CategoryRepository categoryRepository){
        this.categoryRepository=categoryRepository;
    }

    public void saveCategory(Category category){
        categoryRepository.save(category);
    }
    public void saveCategoryfromDTO(RequestCategory rCategory){
        if(categoryRepository.existsByName(rCategory.getName())){
            throw new DataIntegrityViolationException("This category existed!");
        }
        categoryRepository.save(new Category(rCategory.getName(), rCategory.getDescription()));

    }
    public List<Category> getAllCategories(){
        return categoryRepository.findAll();
    }
    public Category getCategoryByID(Long id){
        Optional <Category> oCategory = categoryRepository.findById(id);
        if(!oCategory.isEmpty()){
            return oCategory.get();
        }
        return null;

    }
    @Transactional
    public void deleteCategory(Long id){

           categoryRepository.delete(getCategoryByID(id));

    }


}
