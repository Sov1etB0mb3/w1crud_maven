package com.calt.coffeeshop.w1crud_maven.ServicesTest;


import com.calt.coffeeshop.w1crud_maven.dto.request.PermissionRequest;
import com.calt.coffeeshop.w1crud_maven.dto.response.PermissionResponse;
import com.calt.coffeeshop.w1crud_maven.entity.Permission;
import com.calt.coffeeshop.w1crud_maven.exception.AppException;
import com.calt.coffeeshop.w1crud_maven.mapper.PermissionMapper;
import com.calt.coffeeshop.w1crud_maven.repository.PermissionRepository;

import com.calt.coffeeshop.w1crud_maven.service.PermissionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PermisisonServiceUnitTest {
    @InjectMocks
    PermissionService permissionService;
    @Mock
    PermissionRepository permissionRepository;

    @Mock
    PermissionMapper permissionMapper;

    Permission permission;
    PermissionRequest request;
    PermissionResponse response;
    PermissionResponse readResponse;


    @BeforeEach
    void setUp() {
        permission = new Permission();
        permission.setName("READ_USER");

        request = new PermissionRequest();
        request.setName("CREATE_USER");

        response = new PermissionResponse();
        response.setName("CREATE_USER");
        readResponse = new PermissionResponse();
        readResponse.setName("READ_USER");
    }

    // CREATE test
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void create_success() {
        when(permissionMapper.toPermisison(request)).thenReturn(permission);
        when(permissionRepository.save(permission)).thenReturn(permission);
        when(permissionMapper.toPermissionResponse(permission)).thenReturn(response);

        PermissionResponse result = permissionService.create(request);

        assertNotNull(result);
        assertEquals("CREATE_USER", result.getName());

        verify(permissionRepository).save(permission);
    }

    // GET ALL test
    @Test
    @WithMockUser(roles = {"ADMIN"})

    void getAllPermission_success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Permission> page = new PageImpl<>(List.of(permission));

        when(permissionRepository.findAll(pageable)).thenReturn(page);
        when(permissionMapper.toPermissionResponse(permission)).thenReturn(response);

        Page<PermissionResponse> result = permissionService.getAllPermission(pageable);

        assertEquals(1, result.getTotalElements());
    }

    // GET BY NAME - FOUND test
    @Test
    @WithMockUser(roles = {"ADMIN"})

    void getPermissionByName_found() {
        when(permissionRepository.findPermissionByName("READ_USER"))
                .thenReturn(Optional.of(permission));
        when(permissionMapper.toPermissionResponse(permission)).thenReturn(readResponse);

        PermissionResponse result = permissionService.getPermissionByName("READ_USER");

        assertEquals("READ_USER", result.getName());
    }

    // GET BY NAME - NOT FOUND
    @Test
    @WithMockUser(roles = {"ADMIN"})

    void getPermissionByName_notFound() {
        when(permissionRepository.findPermissionByName("READ_USER"))
                .thenReturn(Optional.empty());

        assertThrows(AppException.class,
                () -> permissionService.getPermissionByName("READ_USER"));
    }

    // DELETE SUCCESS
    @Test
    @WithMockUser(roles = {"ADMIN"})

    void deletePermission_success() {
        doNothing().when(permissionRepository).deletePermissionByName("READ_USER");

        assertDoesNotThrow(() ->
                permissionService.deletePermission("READ_USER"));

        verify(permissionRepository).deletePermissionByName("READ_USER");
    }

    // DELETE CONFLICT
    @Test
    @WithMockUser(roles = {"ADMIN"})

    void deletePermission_conflict() {
        doThrow(new DataIntegrityViolationException("error"))
                .when(permissionRepository)
                .deletePermissionByName("READ_USER");

        assertThrows(RuntimeException.class,
                () -> permissionService.deletePermission("READ_USER"));
    }

    //UPDATE SUCCESS
    @Test
    @WithMockUser(roles = {"ADMIN"})

    void updatePermission_success() {
        when(permissionRepository.findPermissionByName("READ_USER"))
                .thenReturn(Optional.of(permission));
        doNothing().when(permissionMapper).updatePermission(request, permission);
        when(permissionRepository.save(permission)).thenReturn(permission);
        when(permissionMapper.toPermissionResponse(permission)).thenReturn(response);

        PermissionResponse result =
                permissionService.updatePermission("READ_USER", request);

        assertEquals("CREATE_USER", result.getName());

        verify(permissionMapper).updatePermission(request, permission);
    }

    //UPDATE NOT FOUND
    @Test
    @WithMockUser(roles = {"ADMIN"})

    void updatePermission_notFound() {
        when(permissionRepository.findPermissionByName("CREATE_USER"))
                .thenReturn(Optional.empty());

        assertThrows(AppException.class,
                () -> permissionService.updatePermission("CREATE_USER", request));
    }
}
