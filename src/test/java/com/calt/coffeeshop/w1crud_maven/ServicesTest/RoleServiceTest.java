package com.calt.coffeeshop.w1crud_maven.ServicesTest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.calt.coffeeshop.w1crud_maven.dto.request.RoleRequest;
import com.calt.coffeeshop.w1crud_maven.dto.response.RoleResponse;
import com.calt.coffeeshop.w1crud_maven.entity.Permission;
import com.calt.coffeeshop.w1crud_maven.entity.Role;
import com.calt.coffeeshop.w1crud_maven.entity.RolePermission;
import com.calt.coffeeshop.w1crud_maven.enums.ErrorCode;
import com.calt.coffeeshop.w1crud_maven.exception.AppException;
import com.calt.coffeeshop.w1crud_maven.mapper.RoleMapper;
import com.calt.coffeeshop.w1crud_maven.repository.PermissionRepository;
import com.calt.coffeeshop.w1crud_maven.repository.RolePermissionRepository;
import com.calt.coffeeshop.w1crud_maven.repository.RoleRepository;
import com.calt.coffeeshop.w1crud_maven.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class RoleServiceTest {
    @Autowired
    private RoleService roleService;
    @MockitoBean
    private RoleRepository roleRepository;

    @MockitoBean
    private RolePermissionRepository rolePermissionRepository;

    @MockitoBean
    private PermissionRepository permissionRepository;

    @MockitoBean
    private RoleMapper roleMapper;



    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})

    void testCreateRole_Success() {
        RoleRequest request = new RoleRequest();
        request.setName("ADMIN");

        Role role = new Role();
        role.setId(1L);
        role.setName("ADMIN");

        RoleResponse response = new RoleResponse();
        response.setId(1L);
        response.setName("ADMIN");

        when(roleMapper.toRole(request)).thenReturn(role);
        when(roleRepository.save(role)).thenReturn(role);
        when(roleMapper.toRoleResponse(role)).thenReturn(response);

        RoleResponse result = roleService.create(request);

        assertNotNull(result);
        assertEquals("ADMIN", result.getName());
        verify(roleRepository, times(1)).save(role);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})

    void testAddPermissionToRole_ById_Success() {
        Role role = new Role();
        role.setId(1L);

        Permission permission = new Permission();
        permission.setId(1L);

        when(roleRepository.findRoleById(1L)).thenReturn(Optional.of(role));
        when(permissionRepository.findPermissionById(1L)).thenReturn(permission);

        roleService.addPermissionToRole(1L, 1L);

        verify(rolePermissionRepository, times(1)).save(any(RolePermission.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})

    void testAddPermissionToRole_RoleNotFound() {
        when(roleRepository.findRoleById(1L)).thenReturn(Optional.empty());

        assertThrows(AppException.class, () -> roleService.addPermissionToRole(1L, 1L));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})

    void testGetAllRoles_Success() {
        Role role = new Role();
        role.setId(1L);
        role.setName("ADMIN");

        Page<Role> page = new PageImpl<>(Collections.singletonList(role));
        Pageable pageable = PageRequest.of(0, 10);

        when(roleRepository.findAll(pageable)).thenReturn(page);
        when(roleMapper.toRoleResponse(role)).thenReturn(new RoleResponse());

        Page<RoleResponse> result = roleService.getAllRoles(pageable);

        assertEquals(1, result.getTotalElements());
        verify(roleRepository, times(1)).findAll(pageable);
    }

    // ===================== DELETE ROLE =====================
    @Test
    @WithMockUser(roles = {"ADMIN"})

    void testDeleteRole_ById_Success() {
        doNothing().when(roleRepository).deleteById(1L);
        doNothing().when(rolePermissionRepository).deleteRolePermissionByRole_Id(1L);

        roleService.deleteRole(1L);

        verify(roleRepository, times(1)).deleteById(1L);
        verify(rolePermissionRepository, times(1)).deleteRolePermissionByRole_Id(1L);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})

    void testDeleteRole_ById_Conflict() {
        doThrow(DataIntegrityViolationException.class).when(roleRepository).deleteById(1L);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> roleService.deleteRole(1L));
        assertEquals(ErrorCode.CANNOT_DELETE.getMessage(), ex.getReason());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})

    void testDeleteRole_ByName_Success() {
        Role role = new Role();
        role.setId(1L);
        role.setName("ADMIN");

        when(roleRepository.findRoleByName("ADMIN")).thenReturn(Optional.of(role));
        doNothing().when(roleRepository).deleteByName("ADMIN");
        doNothing().when(rolePermissionRepository).deleteRolePermissionByRole_Id(1L);

        roleService.deleteRole("ADMIN");

        verify(roleRepository, times(1)).deleteByName("ADMIN");
        verify(rolePermissionRepository, times(1)).deleteRolePermissionByRole_Id(1L);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})

    void testDeleteRole_ByName_Conflict() {
        doThrow(DataIntegrityViolationException.class).when(roleRepository).deleteByName("ADMIN");
        when(roleRepository.findRoleByName("ADMIN")).thenReturn(Optional.of(new Role()));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> roleService.deleteRole("ADMIN"));
        assertEquals(ErrorCode.CANNOT_DELETE.getMessage(), ex.getReason());
    }

    // ===================== UPDATE ROLE =====================
    @Test
    @WithMockUser(roles = {"ADMIN"})

    void testUpdateRole_ById_Success() {
        RoleRequest request = new RoleRequest();
        request.setName("NEW_ADMIN");

        Role role = new Role();
        role.setId(1L);
        role.setName("ADMIN");

        RoleResponse response = new RoleResponse();
        response.setId(1L);
        response.setName("NEW_ADMIN");

        when(roleRepository.findRoleById(1L)).thenReturn(Optional.of(role));
        doNothing().when(roleMapper).updateRole(request, role);
        when(roleRepository.save(role)).thenReturn(role);
        when(roleMapper.toRoleResponse(role)).thenReturn(response);

        RoleResponse result = roleService.updateRole(1L, request);

        assertEquals("NEW_ADMIN", result.getName());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})

    void testUpdateRole_ByName_Success() {
        RoleRequest request = new RoleRequest();
        request.setName("NEW_ADMIN");

        Role role = new Role();
        role.setId(1L);
        role.setName("ADMIN");

        RoleResponse response = new RoleResponse();
        response.setId(1L);
        response.setName("NEW_ADMIN");

        when(roleRepository.findRoleByName("ADMIN")).thenReturn(Optional.of(role));
        doNothing().when(roleMapper).updateRole(request, role);
        when(roleRepository.save(role)).thenReturn(role);
        when(roleMapper.toRoleResponse(role)).thenReturn(response);

        RoleResponse result = roleService.updateRole("ADMIN", request);

        assertEquals("NEW_ADMIN", result.getName());
    }

    // ===================== DELETE PERMISSION FROM ROLE =====================
    @Test
    @WithMockUser(roles = {"ADMIN"})

    void testDeletePermissionFromRole_Success() {
        Role role = new Role();
        Permission permission = new Permission();
        permission.setName("READ");

        role.setPermissions(new HashSet<>(Collections.singletonList(new RolePermission(role, permission))));

        when(roleRepository.findRoleByName("ADMIN")).thenReturn(Optional.of(role));
        when(permissionRepository.findPermissionByName("READ")).thenReturn(Optional.of(permission));
        when(roleRepository.save(role)).thenReturn(role);

        roleService.deletePermissionFromRole("READ", "ADMIN");

        assertTrue(role.getPermissions().isEmpty());
        verify(roleRepository, times(1)).save(role);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})

    void testDeletePermissionFromRole_RoleOrPermissionNotFound() {
        when(roleRepository.findRoleByName("ADMIN")).thenReturn(Optional.empty());
        when(permissionRepository.findPermissionByName("READ")).thenReturn(Optional.empty());

        assertThrows(AppException.class, () -> roleService.deletePermissionFromRole("READ", "ADMIN"));
    }
}
