package com.calt.coffeeshop.w1crud_maven.mapper;


import com.calt.coffeeshop.w1crud_maven.dto.request.RoleRequest;
import com.calt.coffeeshop.w1crud_maven.dto.response.RoleResponse;
import com.calt.coffeeshop.w1crud_maven.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    Role toRole(RoleRequest roleRequest);
    RoleResponse toRoleResponse(Role role);
    void updateRole(RoleRequest roleRequest, @MappingTarget Role role);
}
