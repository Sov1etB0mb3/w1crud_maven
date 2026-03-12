package com.calt.coffeeshop.w1crud_maven.service;

import com.calt.coffeeshop.w1crud_maven.dto.requestdto.ProductRequest;
import com.calt.coffeeshop.w1crud_maven.dto.requestdto.UserRequest;
import com.calt.coffeeshop.w1crud_maven.entity.User;
import com.calt.coffeeshop.w1crud_maven.enums.ErrorCode;
import com.calt.coffeeshop.w1crud_maven.enums.Role;
import com.calt.coffeeshop.w1crud_maven.exception.AppException;
import com.calt.coffeeshop.w1crud_maven.mapper.ProductMapper;
import com.calt.coffeeshop.w1crud_maven.mapper.UserMapper;
import com.calt.coffeeshop.w1crud_maven.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;
    public User saveUserfromDTO(UserRequest userRequest) {

        if (userRepository.existsByUsername(userRequest.getName()))
            throw new AppException(ErrorCode.EXISTED);
        User newUser = userMapper.toUser(userRequest);
        if(newUser.getRoles()!=null){
            return userRepository.save(newUser);
        }
        HashSet<String>  roles = new HashSet<>();
        roles.add(Role.USER.name());

        newUser.setRoles(roles);
        return userRepository.save(newUser);

    }
    public void saveUser(User user) {
            userRepository.save(user);
    }
    public Page<User> getAllUser(Pageable pageable){
        return userRepository.findAll(pageable);
    }

    public void deleteUser(UserRequest userRequest){
        try{
            userRepository.delete(userMapper.toUser(userRequest));
        }
          catch (DataIntegrityViolationException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Can't delete!");
        }

    }
    public User updateUser(String name, UserRequest request){
        User user=userRepository.findUserByUsername(name).get();
        userMapper.updateUser(request,user);
        return userRepository.save(user);
    }


}
