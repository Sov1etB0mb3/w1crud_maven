package com.calt.coffeeshop.w1crud_maven.mapper;

import com.calt.coffeeshop.w1crud_maven.dto.request.CategoryRequest;
import com.calt.coffeeshop.w1crud_maven.dto.response.CategoryResponse;
import com.calt.coffeeshop.w1crud_maven.entity.Category;
import org.hibernate.sql.Update;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoryMapper{

    Category toCategory(CategoryRequest categoryRequest);
    @Mapping(target ="listProduct",ignore = true)
    CategoryResponse toCategoryResponse(Category category);
    @Mapping(target = "created_at",ignore = true)
    @Mapping(target = "id",ignore = true)
    @Mapping(target ="listProduct",ignore = true)
    Category updateCategory(CategoryRequest categoryRequest, @MappingTarget Category category);
}
