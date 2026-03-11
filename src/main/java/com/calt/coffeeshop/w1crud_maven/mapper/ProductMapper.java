package com.calt.coffeeshop.w1crud_maven.mapper;

import com.calt.coffeeshop.w1crud_maven.dto.requestdto.ProductRequestDto;
import com.calt.coffeeshop.w1crud_maven.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target= "category",ignore = true)
    Product toProduct(ProductRequestDto productRequestDto);
    @Mapping(target= "category",ignore = true)
    void updateProduct(@MappingTarget Product product, ProductRequestDto productRequestDto);
}
