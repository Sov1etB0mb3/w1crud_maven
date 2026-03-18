package com.calt.coffeeshop.w1crud_maven.repository;

import com.calt.coffeeshop.w1crud_maven.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {

    RefreshToken findRefreshTokenByUserid(Integer userid);
}
