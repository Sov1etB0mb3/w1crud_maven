package com.calt.coffeeshop.w1crud_maven.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
//@FieldDefaults(level = AccessLevel.PRIVATE)
@RedisHash("tbl_invalid_token")
public class InvalidToken {
    @Id

    private String id;
    @TimeToLive(unit = TimeUnit.DAYS)
    private Long expirytime;
}
