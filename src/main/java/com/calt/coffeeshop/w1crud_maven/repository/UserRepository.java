package com.calt.coffeeshop.w1crud_maven.repository;

import com.calt.coffeeshop.w1crud_maven.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
    boolean existsByUsername(String username);
    Optional<User> findUserByUsername(String username);

    @Override
    Page<User> findAll(Pageable pageable);
}
