package com.calt.coffeeshop.w1crud_maven.service;

import com.calt.coffeeshop.w1crud_maven.dto.request.UserRequest;

import com.calt.coffeeshop.w1crud_maven.dto.response.UserResponse;
import com.calt.coffeeshop.w1crud_maven.entity.Role;
import com.calt.coffeeshop.w1crud_maven.entity.User;
import com.calt.coffeeshop.w1crud_maven.entity.UserRole;
import com.calt.coffeeshop.w1crud_maven.enums.ErrorCode;
import com.calt.coffeeshop.w1crud_maven.exception.AppException;
import com.calt.coffeeshop.w1crud_maven.mapper.UserMapper;
import com.calt.coffeeshop.w1crud_maven.repository.RoleRepository;
import com.calt.coffeeshop.w1crud_maven.repository.UserRepository;
import com.calt.coffeeshop.w1crud_maven.repository.UserRoleRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static java.rmi.server.LogStream.log;

@Slf4j
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    CacheManager cacheManager;

    @PostConstruct
    public void checkCacheManager() {
        log.info("Cache manager in use: {}", cacheManager.getClass());
    }
    @Autowired

    private UserMapper userMapper;
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('CREATE_USER')")
    @CacheEvict(value = "users",allEntries = true)
    public User saveUserfromDTO(UserRequest userRequest) {

        if (userRepository.existsByUsername(userRequest.getUsername()))
            throw new AppException(ErrorCode.EXISTED);
        User newUser = userMapper.toUser(userRequest);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        newUser.setCreated_at(Instant.now());
        newUser.setUpdated_at(Instant.now());
        if(newUser.getRoles()!=null){
            return userRepository.save(newUser);

        }
//        roles.add(Role.ADMIN.name());
//        newUser.setRoles(roles);
        return userRepository.save(newUser);

    }
    @PostAuthorize( "returnObject.username.compareTo(authentication.name)==0")
    public UserResponse getMyInfor(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        return userRepository.findUserByUsername(auth.getName()).map(u->getUserWithRole(u))
                .orElseThrow(()->new RuntimeException("User not found!"));
    }
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('READ_USER')")
    @Cacheable(value = "users",key = "#userName",cacheManager = "redisCacheManager")
    public UserResponse getUserByUsername(String userName){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        UserService.log.info(auth.getName());
         User user=userRepository.findUserByUsername(userName)
                .orElseThrow(()->new RuntimeException("User not found!"));

        return getUserWithRole(user);
    }
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('CREATE_USER')")
    @CacheEvict(value = "users",key = "#user.username")
    public void saveUser(User user) {
            userRepository.save(user);
    }
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('READ_USER')")

    public Page<UserResponse> getAllUser(Pageable pageable){
        Page<User> userPage=userRepository.findAll(pageable);
//        return userPage.map(userMapper::toUserResponse);
        try {
            return userPage.map(user -> getUserWithRole(user));

        }catch (AccessDeniedException exception){

            throw new AccessDeniedException(ErrorCode.UNAUTHORIZED.getMessage());
        }

    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('DELETE_USER')")
    @CacheEvict(value = "users",key = "#username")
    public void deleteUser(UserRequest userRequest){
        try{
            userRepository.delete(userMapper.toUser(userRequest));
        }
          catch (DataIntegrityViolationException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Can't delete!");
        }

    }
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('DELETE_USER')")
    @CacheEvict(value = "users",key = "#username")
    public void deleteUser(User user){
        try{
            userRepository.delete(user);
        }
        catch (DataIntegrityViolationException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Can't delete!");
        }

    }
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('DELETE_USER')")
    @CacheEvict(value = "users",key = "#username")
    public void deleteUser(String username){
        try{
            userRepository.deleteUserByUsername(username);
        }
        catch (DataIntegrityViolationException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Can't delete!");
        }

    }
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('UPDATE_USER')")
    @Transactional
    @CachePut(value = "users",key = "#username")
    public UserResponse updateUser(String username, UserRequest request){
        User user=userRepository.findUserByUsername(username).get();
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        request.setPassword(passwordEncoder.encode(request.getPassword()));
        userMapper.updateUser(request,user);

        user.setUpdated_at(Instant.now());
//        String role= request.getRoles().stream().collect(Collectors.joining(","));
        for(String role : request.getRoles()){
            addRoleToUser(role,username);
        }
        UserResponse result =userMapper.toUserResponse(userRepository.save(user));

        return result;

    }
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('CREATE_USER')")
    @CacheEvict(value = "users",allEntries = true)
    public void addRoleToUser(Long roleId, Integer userId){
        Role role = roleRepository.findRoleById(roleId).orElseThrow(()->new AppException(ErrorCode.NOT_FOUND));
        User user = userRepository.findById(userId).get();
        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(role);
        userRoleRepository.save(userRole);

    }
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('CREATE_USER')")
    @Transactional
    public void addRoleToUser(String roleName, String userName){
        Role role = roleRepository.findRoleByName(roleName).get();
        if(role == null){
            role = roleRepository.findRoleByName("USER").orElseThrow(
                    ()-> new AppException(ErrorCode.NOT_FOUND));
        }
        User user = userRepository.findUserByUsername(userName).get();
        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(role);
        userRoleRepository.save(userRole);

    }
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('DELETE_USER')")
    public void deleteRoleFromUser(String userName, String roleName){
        User user = userRepository.findUserByUsername(userName)
                .orElseThrow(()->new AppException(ErrorCode.NOT_FOUND));
        Role role = roleRepository.findRoleByName(roleName)
                .orElseThrow(()-> new AppException(ErrorCode.NOT_FOUND));
        user.getRoles().removeIf(
                userRole -> userRole.getRole().equals(role)
        );
        userRepository.save(user);
    }


    @Cacheable(value = "users", key = "#userName", cacheManager = "redisCacheManager")
    public String testCache(String userName) {
        System.out.println("Method executed for " + userName);
        return userName;
    }
    public UserResponse getUserWithRole(User user){
//        log("DATA: " + user.toString());
        UserResponse userResponse = userMapper.toUserResponse(user);
        Set<String> roles =
                user.getRoles()
                        .stream()
                        .map(userRole -> userRole.getRole().getName())
                        .collect(Collectors.toSet());
//                UserRole userRole = userRoleRepository.findUserRoleByUser(user);
//                userResponse.setRoles(Collections.singleton(userRole.getRole().getName()));
        userResponse.setRoles(roles);
        return userResponse;

    }

}
