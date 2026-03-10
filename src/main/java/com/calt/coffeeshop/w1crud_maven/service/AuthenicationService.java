package com.calt.coffeeshop.w1crud_maven.service;

import com.calt.coffeeshop.w1crud_maven.dto.requestdto.RequestAuth;
import com.calt.coffeeshop.w1crud_maven.exception.AppException;
import com.calt.coffeeshop.w1crud_maven.exception.ErrorCode;
import com.calt.coffeeshop.w1crud_maven.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenicationService {
    @Autowired
    private UserRepository userRepository;

    public boolean authenicate(RequestAuth requestAuth){
    var user = userRepository.findUserByUsername(requestAuth.getUsername()).orElseThrow(()-> new AppException(ErrorCode.USER_NOT_FOUND));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        System.out.println(requestAuth.getPassword());
        return passwordEncoder.matches(requestAuth.getPassword(), user.getPassword());



    }
}
