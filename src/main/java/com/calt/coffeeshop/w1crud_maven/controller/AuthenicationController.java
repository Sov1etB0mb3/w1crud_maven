package com.calt.coffeeshop.w1crud_maven.controller;

import com.calt.coffeeshop.w1crud_maven.dto.request.AuthRequest;
import com.calt.coffeeshop.w1crud_maven.dto.request.IntrospectRequest;
import com.calt.coffeeshop.w1crud_maven.dto.response.ApiResponse;
import com.calt.coffeeshop.w1crud_maven.dto.response.AuthenicationResponse;
import com.calt.coffeeshop.w1crud_maven.dto.response.IntrospectResponse;
import com.calt.coffeeshop.w1crud_maven.service.AuthenicationService;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenicationController {
    //@Autowired already a final so don't need to use
    AuthenicationService authenicationService;

    @PostMapping("/token")
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
    @PostMapping("/introspect")
    public ApiResponse<IntrospectResponse> login(@RequestBody IntrospectRequest introspectRequest) throws ParseException, JOSEException {
        var result = authenicationService.introspect(introspectRequest);

        return ApiResponse.<IntrospectResponse>builder().
                result(result).
                build();


    }

}
