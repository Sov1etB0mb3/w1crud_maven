package com.calt.coffeeshop.w1crud_maven.controller;

import com.calt.coffeeshop.w1crud_maven.dto.requestdto.AuthRequest;
import com.calt.coffeeshop.w1crud_maven.dto.responsedto.ApiResponse;
import com.calt.coffeeshop.w1crud_maven.dto.responsedto.AuthenicationResponse;
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
     public ApiResponse<AuthenicationResponse> login(@RequestBody AuthRequest authRequest) {
        var result = authenicationService.authenicate(authRequest);

        return ApiResponse.<AuthenicationResponse>builder().
                result(
                        AuthenicationResponse.builder().
                                authenicated(result.isAuthenicated()).
                                token(result.getToken()).
                                build()).
                build();


    }

}
