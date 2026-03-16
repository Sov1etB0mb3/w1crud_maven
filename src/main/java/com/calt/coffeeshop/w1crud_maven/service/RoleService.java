package com.calt.coffeeshop.w1crud_maven.service;

import com.calt.coffeeshop.w1crud_maven.dto.request.PermissionRequest;
import com.calt.coffeeshop.w1crud_maven.dto.request.RoleRequest;
import com.calt.coffeeshop.w1crud_maven.dto.response.PermissionResponse;
import com.calt.coffeeshop.w1crud_maven.dto.response.RoleResponse;
import com.calt.coffeeshop.w1crud_maven.entity.Permission;
import com.calt.coffeeshop.w1crud_maven.entity.Role;
import com.calt.coffeeshop.w1crud_maven.entity.RolePermission;
import com.calt.coffeeshop.w1crud_maven.mapper.RoleMapper;
import com.calt.coffeeshop.w1crud_maven.repository.PermissionRepository;
import com.calt.coffeeshop.w1crud_maven.repository.RolePermissionRepository;
import com.calt.coffeeshop.w1crud_maven.repository.RoleRepository;
import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;

import static java.rmi.server.LogStream.log;

@AllArgsConstructor
@Service
public class RoleService {
    private RoleRepository roleRepository;
    private RolePermissionRepository rolePermissionRepository;
    private PermissionRepository permissionRepository;
    private RoleMapper roleMapper;


    public void addPermissionToRole(Long roleId, Long permissionId){
        Role role= roleRepository.findRoleById(roleId);
        Permission permission= permissionRepository.findPermissionById(permissionId);
        RolePermission rolePermission= new RolePermission(role,permission);
        rolePermissionRepository.save(rolePermission);
 }
    public RoleResponse create(RoleRequest roleRequest){
        Role role = roleMapper.toRole(roleRequest);
        log("After mapped: "+ role);
        role = roleRepository.save(role);
        log("After saved: "+role);
        return roleMapper.toRoleResposne(role);
    }
}
