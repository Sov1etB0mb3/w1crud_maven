package com.calt.coffeeshop.w1crud_maven.repository;

import com.calt.coffeeshop.w1crud_maven.entity.InvalidToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvalidTokenRepository extends JpaRepository<InvalidToken,String> {
     Optional<InvalidToken> findById(String id);


    boolean existsInvalidTokenById(String id);
}
