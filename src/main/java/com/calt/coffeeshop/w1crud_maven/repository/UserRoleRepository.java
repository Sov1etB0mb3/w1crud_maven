package com.calt.coffeeshop.w1crud_maven.repository;

import com.calt.coffeeshop.w1crud_maven.entity.User;
import com.calt.coffeeshop.w1crud_maven.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole,Long> {
    long findByUser(User user);

    Set<String> findUserRoleById(long id);

    UserRole findUserRoleByUser(User user);
}
