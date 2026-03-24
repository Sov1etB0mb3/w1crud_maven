package com.calt.coffeeshop.w1crud_maven.repository;

import com.calt.coffeeshop.w1crud_maven.entity.InvalidToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
//use CrudRepository for redis instead of JpaRepository
public interface InvalidTokenRepository extends CrudRepository<InvalidToken,String> {
     Optional<InvalidToken> findById(String id);


    boolean existsInvalidTokenById(String id);
    boolean existsById(String id);
}
