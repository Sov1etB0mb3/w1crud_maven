package com.calt.coffeeshop.w1crud_maven.controller;

import com.calt.coffeeshop.w1crud_maven.dto.request.PermissionRequest;
import com.calt.coffeeshop.w1crud_maven.dto.request.RoleRequest;
import com.calt.coffeeshop.w1crud_maven.dto.response.ApiResponse;
import com.calt.coffeeshop.w1crud_maven.dto.response.RoleResponse;

import com.calt.coffeeshop.w1crud_maven.enums.StatusCode;
import com.calt.coffeeshop.w1crud_maven.mapper.RoleMapper;
import com.calt.coffeeshop.w1crud_maven.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/role")
@RequiredArgsConstructor
@Slf4j
public class RoleController {
    @Autowired
    private RoleService roleService;
    @Autowired
    private RoleMapper roleMapper;

    @PostMapping
    public ApiResponse<RoleResponse> createRole(@RequestBody RoleRequest roleRequest) {
        return ApiResponse.<RoleResponse>builder()
                .result(roleService.create(roleRequest))
                .build();

    }
//    @PostMapping
//    public ApiResponse<RoleResponse> createRoleWithPermission(@RequestBody RoleRequest roleRequest) {
//        return ApiResponse.<RoleResponse>builder()
//                .result(roleService.create(roleRequest))
//                .build();
//
//    }
@PostMapping("/{roleName}/permission")
public ApiResponse<RoleResponse> addPermission(
        @RequestParam("roleName") String roleName
        ,@RequestBody String permissionName) {
    roleService.addPermissionToRole(roleName,permissionName);
    return ApiResponse.<RoleResponse>builder()
            .result(null)
            .build();

}

    @GetMapping
    public ApiResponse<List<RoleResponse>> getAllRole(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending) {
        //List<Product> productList= productService.getAllProducts();
        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, pageSize, sort);
        ApiResponse apiResponse = ApiResponse.builder().build();
        apiResponse.setResult(roleService.getAllRoles(pageable));
        apiResponse.setCode(StatusCode.FOUND.getCode());
        apiResponse.setMessage(StatusCode.FOUND.getMessage());
        return apiResponse;
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteRole(@PathVariable("id") Long id) {

        ApiResponse apiResponse = ApiResponse.builder().build();
        roleService.deleteRole(id);
        apiResponse.setMessage(StatusCode.DELETED.getMessage());
        apiResponse.setCode(StatusCode.DELETED.getCode());
        apiResponse.setResult(null);
        return apiResponse;
    }
    @PatchMapping("/{id}")
    public ApiResponse<String> deleteRole(@PathVariable("id") Long id, @RequestBody RoleRequest roleRequest) {

        ApiResponse apiResponse = ApiResponse.builder().build();
        roleService.updateRole(id,roleRequest);
        apiResponse.setResult(null);
        return apiResponse;
    }
    @PatchMapping("/{roleName}")
    public ApiResponse<String> deleteRole(@PathVariable("roleName") String roleName, @RequestBody RoleRequest roleRequest) {

        ApiResponse apiResponse = ApiResponse.builder().build();
        roleService.updateRole(roleName,roleRequest);
        apiResponse.setResult(null);
        return apiResponse;
    }
}
