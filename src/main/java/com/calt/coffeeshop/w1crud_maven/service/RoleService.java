package com.calt.coffeeshop.w1crud_maven.service;

import com.calt.coffeeshop.w1crud_maven.dto.request.PermissionRequest;
import com.calt.coffeeshop.w1crud_maven.dto.request.ProductRequest;
import com.calt.coffeeshop.w1crud_maven.dto.request.RoleRequest;
import com.calt.coffeeshop.w1crud_maven.dto.response.PermissionResponse;
import com.calt.coffeeshop.w1crud_maven.dto.response.RoleResponse;
import com.calt.coffeeshop.w1crud_maven.entity.*;
import com.calt.coffeeshop.w1crud_maven.enums.ErrorCode;
import com.calt.coffeeshop.w1crud_maven.exception.AppException;
import com.calt.coffeeshop.w1crud_maven.mapper.RoleMapper;
import com.calt.coffeeshop.w1crud_maven.repository.PermissionRepository;
import com.calt.coffeeshop.w1crud_maven.repository.RolePermissionRepository;
import com.calt.coffeeshop.w1crud_maven.repository.RoleRepository;
import lombok.AllArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static java.rmi.server.LogStream.log;

@Slf4j
@AllArgsConstructor
@Service
public class RoleService {
    private RoleRepository roleRepository;
    private RolePermissionRepository rolePermissionRepository;
    private PermissionRepository permissionRepository;
    private RoleMapper roleMapper;


    public void addPermissionToRole(Long roleId, Long permissionId){
        Role role= roleRepository.findRoleById(roleId).orElseThrow(()->new AppException(ErrorCode.NOT_FOUND));
        Permission permission= permissionRepository.findPermissionById(permissionId);
        RolePermission rolePermission= new RolePermission(role,permission);
        rolePermissionRepository.save(rolePermission);
 }
    public void addPermissionToRole(String roleName, String permissionName){
        Role role= roleRepository.findRoleByName(roleName).orElseThrow(()->new AppException(ErrorCode.NOT_FOUND));
        Permission permission= permissionRepository.findPermissionByName(permissionName);
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
    public Page<Role> getAllRoles(Pageable pageable) {
        try {
            return roleRepository.findAll(pageable);
        } catch (Exception e) {
            throw new AppException(ErrorCode.NOT_FOUND);
        }
    }
    public void deleteRole(Long id){
        try{
            roleRepository.deleteById(id);

        }catch (DataIntegrityViolationException exception){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Can't delete!");

        }
    }
    public void deleteRole(String roleName){
        try{
            roleRepository.deleteByName(roleName);

        }catch (DataIntegrityViolationException exception){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Can't delete!");

        }
    }
    public RoleResponse updateRole(Long id, RoleRequest request){
        Role role = roleRepository.findRoleById(id).orElseThrow(()->new AppException(ErrorCode.NOT_FOUND));
        roleMapper.updateRole(request,role);
        return roleMapper.toRoleResposne(roleRepository.save(role));
    }
}
