package com.calt.coffeeshop.w1crud_maven.controller;

import com.calt.coffeeshop.w1crud_maven.dto.request.PermissionRequest;
import com.calt.coffeeshop.w1crud_maven.dto.request.RoleRequest;
import com.calt.coffeeshop.w1crud_maven.dto.response.ApiResponse;
import com.calt.coffeeshop.w1crud_maven.dto.response.PermissionResponse;
import com.calt.coffeeshop.w1crud_maven.entity.Permission;
import com.calt.coffeeshop.w1crud_maven.entity.Product;
import com.calt.coffeeshop.w1crud_maven.enums.StatusCode;
import com.calt.coffeeshop.w1crud_maven.repository.PermissionRepository;
import com.calt.coffeeshop.w1crud_maven.service.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permission")
@RequiredArgsConstructor
@Slf4j
public class PermissionController {
    @Autowired
    PermissionService permissionService;

    @PostMapping
    ApiResponse<PermissionResponse> create(@RequestBody PermissionRequest permissionRequest) {
        return ApiResponse.<PermissionResponse>builder()
                .result(permissionService.create(permissionRequest))
                .build();

    }

    @GetMapping
    //return String becase the whole html site are Strings!!!
    public ApiResponse<List<Permission>> getAllPermission(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "true") boolean ascending) {
        //List<Product> productList= productService.getAllProducts();
        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, pageSize, sort);
        ApiResponse apiResponse = ApiResponse.builder().build();
        apiResponse.setResult(permissionService.getAllPermission(pageable));
        apiResponse.setCode(StatusCode.FOUND.getCode());
        apiResponse.setMessage(StatusCode.FOUND.getMessage());
        return apiResponse;
    }
    @PatchMapping("/{permissionName}")
    public ApiResponse<String> updatePermission(@PathVariable("permissionName") String permissionName, @RequestBody PermissionRequest permissionRequest) {

        ApiResponse apiResponse = ApiResponse.builder().build();
        permissionService.updatePermission(permissionName,permissionRequest);
        apiResponse.setResult(null);
        return apiResponse;
    }
    @DeleteMapping("/{permisison}")
    public ApiResponse<String> deletePermission(@PathVariable("permission") String permisison) {

        ApiResponse apiResponse = ApiResponse.builder().build();
        permissionService.deletePermission(permisison);
        apiResponse.setMessage(StatusCode.DELETED.getMessage());
        apiResponse.setCode(StatusCode.DELETED.getCode());
        apiResponse.setResult(null);
        return apiResponse;
    }
}