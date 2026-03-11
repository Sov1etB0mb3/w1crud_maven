package com.calt.coffeeshop.w1crud_maven.controller;

import com.calt.coffeeshop.w1crud_maven.dto.requestdto.AuthRequestDto;
import com.calt.coffeeshop.w1crud_maven.dto.responsedto.ApiResponseDto;
import com.calt.coffeeshop.w1crud_maven.dto.responsedto.AuthenicationResponseDto;
import com.calt.coffeeshop.w1crud_maven.service.AuthenicationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenicationController {
    //@Autowired already a final so don't need to use
    AuthenicationService authenicationService;

    @PostMapping("/login")
     public ApiResponseDto<AuthenicationResponseDto> login(@RequestBody AuthRequestDto authRequestDto) {
        var result = authenicationService.authenicate(authRequestDto);

        return ApiResponseDto.<AuthenicationResponseDto>builder().
                result(
                        AuthenicationResponseDto.builder().
                                authenicated(result.isAuthenicated()).
                                token(result.getToken()).
                                build()).
                build();


    }
}
