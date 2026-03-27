package com.calt.coffeeshop.w1crud_maven.ServicesTest;

import com.calt.coffeeshop.w1crud_maven.dto.request.UserRequest;
import com.calt.coffeeshop.w1crud_maven.dto.response.UserResponse;
import com.calt.coffeeshop.w1crud_maven.entity.Role;
import com.calt.coffeeshop.w1crud_maven.entity.User;
import com.calt.coffeeshop.w1crud_maven.entity.UserRole;
import com.calt.coffeeshop.w1crud_maven.exception.AppException;
import com.calt.coffeeshop.w1crud_maven.mapper.UserMapper;
import com.calt.coffeeshop.w1crud_maven.repository.RoleRepository;
import com.calt.coffeeshop.w1crud_maven.repository.UserRepository;
import com.calt.coffeeshop.w1crud_maven.repository.UserRoleRepository;
import com.calt.coffeeshop.w1crud_maven.service.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Instant;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
@ExtendWith(MockitoExtension.class)
public class UserServiceUnitTest {
    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserRoleRepository userRoleRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserMapper userMapper;

    private  Role role;
    private UserRole userRole;
    private UserRequest validUserRequest;
    private User validUser;
    private UserResponse validUserResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup UserRequest
        validUserRequest = new UserRequest();
        validUserRequest.setUsername("johndoe");
        validUserRequest.setPassword("password123");
        validUserRequest.setRoles(new HashSet<>(Arrays.asList("USER")));

        // Setup User entity
        validUser = new User();
        validUser.setId(1);
        validUser.setUsername("johndoe");
        validUser.setPassword("hashedpassword");
        validUser.setCreated_at(Instant.now());
        validUser.setUpdated_at(Instant.now());
        validUser.setRoles(new HashSet<>());

        // Add a role to User
        role = new Role();
        role.setId(1L);
        role.setName("ADMIN");
        userRole = new UserRole();
        userRole.setUser(validUser);
        userRole.setRole(role);
        validUser.getRoles().add(userRole);

        // Setup UserResponse
        validUserResponse = new UserResponse();
        validUserResponse.setUsername("johndoe");
        validUserResponse.setRoles(Set.of("ADMIN"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})

    void saveUserfromDTO_success() {
        // Mock repository and mapper behavior
        Mockito.when(userRepository.existsByUsername(anyString())).thenReturn(false);
        Mockito.when(userMapper.toUser(any(UserRequest.class))).thenReturn(validUser);
        Mockito.when(userRepository.save(validUser)).thenReturn(validUser);

        // Call service
        User result = userService.saveUserfromDTO(validUserRequest);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getUsername()).isEqualTo("johndoe");
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})

    void saveUserfromDTO_alreadyExists() {
        Mockito.when(userRepository.existsByUsername(anyString())).thenReturn(true);

        Assertions.assertThatThrownBy(() -> userService.saveUserfromDTO(validUserRequest))
                .isInstanceOf(AppException.class);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})

    void getUserByUsername_success() {
        Mockito.when(userRepository.findUserByUsername("johndoe")).thenReturn(Optional.of(validUser));
        Mockito.when(userMapper.toUserResponse(validUser)).thenReturn(validUserResponse);

        UserResponse response = userService.getUserByUsername("johndoe");

        Assertions.assertThat(response.getUsername()).isEqualTo("johndoe");
        Assertions.assertThat(response.getRoles()).contains("ADMIN");
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})

    void getUserByUsername_notFound() {
        Mockito.when(userRepository.findUserByUsername("johndoe")).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> userService.getUserByUsername("johndoe"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})

    void updateUser_success() {
        Mockito.when(userRepository.findUserByUsername("johndoe")).thenReturn(Optional.of(validUser));
        validUserRequest.setPassword("newpassword");
        validUserRequest.setRoles(Set.of("ADMIN"));
        Mockito.when(roleRepository.findRoleByName("ADMIN")).thenReturn(Optional.of(role));
        Mockito.when(userRoleRepository.save(Mockito.any(UserRole.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(validUser);
        Mockito.when(userMapper.toUserResponse(validUser)).thenReturn(validUserResponse);


        UserResponse response = userService.updateUser("johndoe", validUserRequest);

        Assertions.assertThat(response.getUsername()).isEqualTo("johndoe");
        Assertions.assertThat(response.getRoles()).contains("ADMIN");
//
//        Assertions.assertThat(response.getRoles())
//                .containsExactlyInAnyOrder("ADMIN", "USER");
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})

    void deleteUser_byUsername_success() {
        Mockito.doNothing().when(userRepository).deleteUserByUsername("johndoe");

        userService.deleteUser("johndoe");

        Mockito.verify(userRepository).deleteUserByUsername("johndoe");
    }

}