package com.calt.coffeeshop.w1crud_maven.repository;

import com.calt.coffeeshop.w1crud_maven.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {

    RefreshToken findRefreshTokenByUserid(Integer userid);


//    List<RefreshToken> findRefreshTokensByUseridByUserId(Integer userId);

    void deleteRefreshTokenByRefreshtoken(String refreshtoken);


    Optional<RefreshToken> findRefreshTokenByRefreshtoken(String refreshtoken);
}
