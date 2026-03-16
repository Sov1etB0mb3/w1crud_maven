package com.calt.coffeeshop.w1crud_maven.repository;

import com.calt.coffeeshop.w1crud_maven.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role,Long> {
    Role findRoleById(long id);

    Role findRoleByName(String name);
}
