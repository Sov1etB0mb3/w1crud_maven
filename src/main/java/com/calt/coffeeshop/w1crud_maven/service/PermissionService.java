package com.calt.coffeeshop.w1crud_maven.service;

import com.calt.coffeeshop.w1crud_maven.dto.request.PermissionRequest;
import com.calt.coffeeshop.w1crud_maven.dto.request.RoleRequest;
import com.calt.coffeeshop.w1crud_maven.dto.response.PermissionResponse;
import com.calt.coffeeshop.w1crud_maven.dto.response.RoleResponse;
import com.calt.coffeeshop.w1crud_maven.dto.response.UserResponse;
import com.calt.coffeeshop.w1crud_maven.entity.Permission;
import com.calt.coffeeshop.w1crud_maven.entity.Role;
import com.calt.coffeeshop.w1crud_maven.entity.User;
import com.calt.coffeeshop.w1crud_maven.enums.ErrorCode;
import com.calt.coffeeshop.w1crud_maven.exception.AppException;
import com.calt.coffeeshop.w1crud_maven.mapper.PermissionMapper;
import com.calt.coffeeshop.w1crud_maven.repository.PermissionRepository;
import com.calt.coffeeshop.w1crud_maven.repository.RolePermissionRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static java.rmi.server.LogStream.log;

@Service
@RequiredArgsConstructor
@AllArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PermissionService {
    @Autowired
    PermissionRepository permissionRepository;
    @Autowired
    PermissionMapper permissionMapper;
    @Autowired
    RolePermissionRepository rolePermissionRepository;
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('CREATE_PERMISSION')")
    public PermissionResponse create(PermissionRequest permissionRequest){
        Permission permission = permissionMapper.toPermisison(permissionRequest);
        log("After mapped: "+ permission);
        permission = permissionRepository.save(permission);
        log("After saved: "+permission);
        return permissionMapper.toPermissionResponse(permission);
    }
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('READ_PERMISSION')")
    public Page<PermissionResponse> getAllPermission(Pageable pageable){
        Page<Permission> permissionPage=permissionRepository.findAll(pageable);
        //return aPage.map(aMapper::toaResponse);
        return permissionPage.map(permission->permissionMapper
                .toPermissionResponse(permission));
    }
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('READ_PERMISSION')")
    public PermissionResponse getPermissionByName(String permissionName){
        return permissionRepository.findPermissionByName(permissionName)
                .map(permission -> permissionMapper
                        .toPermissionResponse(permission))
                .orElseThrow( () -> new AppException(ErrorCode.NOT_FOUND));
    }
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('DELETE_PERMISSION')")
    public void deletePermission(String permissionName){
        try {
            permissionRepository.deletePermissionByName(permissionName);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Can't delete!");

        }

    }
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('UPDATE_PERMISSION')")
    public PermissionResponse updatePermission(String permissionName,
                                               PermissionRequest request){
        Permission permission = permissionRepository
                .findPermissionByName(permissionName).orElseThrow(
                ()->new AppException(ErrorCode.NOT_FOUND));
        permissionMapper.updatePermission(request,permission);
        return permissionMapper
                .toPermissionResponse(permissionRepository.save(permission));
    }
}
