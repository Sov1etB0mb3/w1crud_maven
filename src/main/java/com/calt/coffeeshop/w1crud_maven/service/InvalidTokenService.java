package com.calt.coffeeshop.w1crud_maven.service;

import com.calt.coffeeshop.w1crud_maven.repository.InvalidTokenRepository;
import org.springframework.stereotype.Service;

@Service
public class InvalidTokenService {
    private final InvalidTokenRepository invalidTokenRepository;

    public InvalidTokenService(InvalidTokenRepository invalidTokenRepository) {
        this.invalidTokenRepository = invalidTokenRepository;
    }
    //redis can understand existById but existsInvalidTokenById()!
    public boolean validateToken(String id){
        if(invalidTokenRepository.existsById(id))
            return true;
        return false;
    }

}
