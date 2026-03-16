package com.calt.coffeeshop.w1crud_maven.mapper;


import com.calt.coffeeshop.w1crud_maven.dto.request.PermissionRequest;
import com.calt.coffeeshop.w1crud_maven.dto.request.UserRequest;
import com.calt.coffeeshop.w1crud_maven.dto.response.PermissionResponse;
import com.calt.coffeeshop.w1crud_maven.dto.response.UserResponse;
import com.calt.coffeeshop.w1crud_maven.entity.Permission;
import com.calt.coffeeshop.w1crud_maven.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermisison(PermissionRequest permissionRequest);
    PermissionResponse toPermissionResponse(Permission permission);
}
