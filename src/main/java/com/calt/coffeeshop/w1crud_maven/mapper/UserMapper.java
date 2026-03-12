package com.calt.coffeeshop.w1crud_maven.mapper;


import com.calt.coffeeshop.w1crud_maven.dto.request.UserRequest;
import com.calt.coffeeshop.w1crud_maven.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserRequest userRequest);
    @Mapping(target = "created_at",ignore = true)
    void updateUser(UserRequest userRequest,@MappingTarget User user);
}
