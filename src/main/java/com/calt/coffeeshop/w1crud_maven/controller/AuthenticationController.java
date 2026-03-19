package com.calt.coffeeshop.w1crud_maven.controller;

import com.calt.coffeeshop.w1crud_maven.dto.request.AuthRequest;
import com.calt.coffeeshop.w1crud_maven.dto.request.IntrospectRequest;
import com.calt.coffeeshop.w1crud_maven.dto.request.LogoutRequest;
import com.calt.coffeeshop.w1crud_maven.dto.request.RefreshRequest;
import com.calt.coffeeshop.w1crud_maven.dto.response.ApiResponse;
import com.calt.coffeeshop.w1crud_maven.dto.response.AuthenticationResponse;
import com.calt.coffeeshop.w1crud_maven.dto.response.IntrospectResponse;
import com.calt.coffeeshop.w1crud_maven.dto.response.RefreshResponse;
import com.calt.coffeeshop.w1crud_maven.enums.StatusCode;
import com.calt.coffeeshop.w1crud_maven.service.AuthenticationService;
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
public class AuthenticationController {
    //@Autowired already a final so don't need to use
    AuthenticationService authenticationService;

    @PostMapping("/token")
     public ApiResponse<AuthenticationResponse> login(@RequestBody AuthRequest authRequest) {
        var result = authenticationService.authenicate(authRequest);

        return ApiResponse.<AuthenticationResponse>builder().
                result(
                        AuthenticationResponse.builder().
                                authenicated(result.isAuthenicated()).
                                token(result.getToken()).
                                refreshtoken(result.getRefreshtoken()).
                                build()).
                build();


    }
    @PostMapping("/introspect")
    public ApiResponse<IntrospectResponse> login(@RequestBody IntrospectRequest introspectRequest) throws ParseException, JOSEException {
        var result = authenticationService.introspect(introspectRequest);

        return ApiResponse.<IntrospectResponse>builder().
                result(result).
                build();


    }
    @PostMapping("/logout")
    public ApiResponse <Void> logout(@RequestBody LogoutRequest logoutRequest) throws ParseException, JOSEException {
        authenticationService.logout(logoutRequest);
        return ApiResponse.<Void>builder().
                build();


    }
    @PostMapping("/refresh")
    public ApiResponse <RefreshResponse> refresh(@RequestBody RefreshRequest refreshRequest) throws ParseException, JOSEException {
        RefreshResponse refreshResponse = authenticationService.refreshToken(refreshRequest);
        return ApiResponse.<RefreshResponse>builder()
                .message(StatusCode.OK.getMessage())
                .code(StatusCode.OK.getCode())
                .result(refreshResponse)
                .build();


    }

}
