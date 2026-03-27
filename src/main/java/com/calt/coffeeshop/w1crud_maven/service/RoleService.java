package com.calt.coffeeshop.w1crud_maven.service;

import com.calt.coffeeshop.w1crud_maven.dto.request.RoleRequest;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;
import java.util.stream.Collectors;

import static java.rmi.server.LogStream.log;

@Slf4j
@AllArgsConstructor
@Service
public class RoleService {
    private RoleRepository roleRepository;
    private RolePermissionRepository rolePermissionRepository;
    private PermissionRepository permissionRepository;
    private RoleMapper roleMapper;

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('CREATE_ROLE')")

    public void addPermissionToRole(Long roleId, Long permissionId){
        Role role= roleRepository.findRoleById(roleId).orElseThrow(()->new AppException(ErrorCode.NOT_FOUND));
        Permission permission= permissionRepository.findPermissionById(permissionId);
        RolePermission rolePermission= new RolePermission(role,permission);
        rolePermissionRepository.save(rolePermission);
 }
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('CREATE_ROLE')")

    public void addPermissionToRole(String roleName, String permissionName){
        Role role= roleRepository.findRoleByName(roleName).orElseThrow(()->new AppException(ErrorCode.NOT_FOUND));
        Permission permission= permissionRepository.findPermissionByName(permissionName).orElseThrow(
                ()-> new RuntimeException("NOTFOUND Permission")
        );
        RolePermission rolePermission= new RolePermission(role,permission);
        rolePermissionRepository.save(rolePermission);
    }
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('CREATE_ROLE')")

    public RoleResponse create(RoleRequest roleRequest){
        Role role = roleMapper.toRole(roleRequest);
        log("After mapped: "+ role);
        role = roleRepository.save(role);
        log("After saved: "+role);
        return roleMapper.toRoleResponse(role);
    }
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('READ_ROLE')")
    public Page<RoleResponse> getAllRoles(Pageable pageable) {
        try {
            return roleRepository.findAll(pageable).map(role ->
                    getRoleWithPermission(role));
        } catch (Exception e) {
            throw new AppException(ErrorCode.NOT_FOUND);
        }
    }
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('DELETE_ROLE')")

    public void deleteRole(Long id){
        try{
            roleRepository.deleteById(id);
            rolePermissionRepository.deleteRolePermissionByRole_Id(id);
        }catch (DataIntegrityViolationException exception){
            throw new ResponseStatusException(HttpStatus.CONFLICT,ErrorCode.CANNOT_DELETE.getMessage());

        }
    }
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('DELETE_ROLE')")

    public void deleteRole(String roleName){
        try{
            roleRepository.deleteByName(roleName);
            rolePermissionRepository.deleteRolePermissionByRole_Id(
                    roleRepository.findRoleByName(roleName).orElseThrow().getId()
            );
        }catch (DataIntegrityViolationException exception){
            throw new ResponseStatusException(HttpStatus.CONFLICT,ErrorCode.CANNOT_DELETE.getMessage());

        }
    }
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('UPDATE_ROLE')")

    public RoleResponse updateRole(Long id, RoleRequest request){
        Role role = roleRepository.findRoleById(id).orElseThrow(
                ()->new AppException(ErrorCode.NOT_FOUND));
        roleMapper.updateRole(request,role);
        return roleMapper.toRoleResponse(roleRepository.save(role));
    }
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('UPDATE_ROLE')")

    public RoleResponse updateRole(String roleName, RoleRequest request){
        Role role = roleRepository.findRoleByName(roleName).orElseThrow(
                ()->new AppException(ErrorCode.NOT_FOUND));
        roleMapper.updateRole(request,role);
        return roleMapper.toRoleResponse(roleRepository.save(role));
    }
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('DELETE_ROLE')")
    public void deletePermissionFromRole(String permissionName, String roleName){
        Permission permission = permissionRepository.findPermissionByName(permissionName)
                .orElseThrow(()->new AppException(ErrorCode.NOT_FOUND));
        Role role = roleRepository.findRoleByName(roleName)
                .orElseThrow(()-> new AppException(ErrorCode.NOT_FOUND));
        role.getPermissions().removeIf(
                rolePermission -> rolePermission.getPermission().equals(permission)
        );
        roleRepository.save(role);
    }
    public RoleResponse getRoleWithPermission(Role role){
        log("DATA: " + role.toString());
        RoleResponse roleResponse = roleMapper.toRoleResponse(role);
        Set<String> permissions =
                role.getPermissions()
                        .stream()
                        .map(rolePermission ->
                                rolePermission.getPermission().getName())
                        .collect(Collectors.toSet());

        roleResponse.setPermissions(permissions);
        return roleResponse;

    }
}
