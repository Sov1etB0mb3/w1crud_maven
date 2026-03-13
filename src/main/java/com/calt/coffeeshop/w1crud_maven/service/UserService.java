package com.calt.coffeeshop.w1crud_maven.service;

import com.calt.coffeeshop.w1crud_maven.dto.request.UserRequest;

import com.calt.coffeeshop.w1crud_maven.dto.response.UserResponse;
import com.calt.coffeeshop.w1crud_maven.entity.User;
import com.calt.coffeeshop.w1crud_maven.enums.ErrorCode;
import com.calt.coffeeshop.w1crud_maven.enums.Role;
import com.calt.coffeeshop.w1crud_maven.exception.AppException;
import com.calt.coffeeshop.w1crud_maven.mapper.UserMapper;
import com.calt.coffeeshop.w1crud_maven.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;

@Slf4j
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;
    public User saveUserfromDTO(UserRequest userRequest) {

        if (userRepository.existsByUsername(userRequest.getUsername()))
            throw new AppException(ErrorCode.EXISTED);
        User newUser = userMapper.toUser(userRequest);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        if(newUser.getRoles()!=null){
            return userRepository.save(newUser);
        }

        newUser.setRoles(Role.USER.name());
        return userRepository.save(newUser);

    }

    @PostAuthorize( "returnObject.username.compareTo(authentication.name)==0")
    public User getUserByUsername(String userName){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.info(auth.getName());
        return userRepository.findUserByUsername(userName)
                .orElseThrow(()->new RuntimeException("User not found!"));
    }
    public void saveUser(User user) {
            userRepository.save(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Page<UserResponse> getAllUser(Pageable pageable){
        Page<User> userPage=userRepository.findAll(pageable);
        //return userPage.map(userMapper::toUserResponse);
        return userPage.map(user->userMapper.toUserResponse(user));
    }


    public void deleteUser(UserRequest userRequest){
        try{
            userRepository.delete(userMapper.toUser(userRequest));
        }
          catch (DataIntegrityViolationException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Can't delete!");
        }

    }
    public void deleteUser(User user){
        try{
            userRepository.delete(user);
        }
        catch (DataIntegrityViolationException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Can't delete!");
        }

    }

    public User updateUser(String username, UserRequest request){
        User user=userRepository.findUserByUsername(username).get();
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        request.setPassword(passwordEncoder.encode(request.getPassword()));
        userMapper.updateUser(request,user);
        return userRepository.save(user);
    }


}
