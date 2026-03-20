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
import com.calt.coffeeshop.w1crud_maven.service.DPoPService;
import com.nimbusds.jose.JOSEException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    //@Autowired already a final so don't need to use
    AuthenticationService authenticationService;

     DPoPService dPoPService;
    @PostMapping("/token")
     public ApiResponse<AuthenticationResponse> login(
             @RequestHeader("DPoP") String dpopHeader,
             @RequestBody AuthRequest authRequest)
            throws ParseException, NoSuchAlgorithmException, InvalidKeySpecException, JOSEException {
        if (dpopHeader == null || dpopHeader.isBlank()) {
            throw new RuntimeException("Missing DPoP header");
        }

        var result = authenticationService.authenicate(authRequest,dpopHeader);

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
    public ApiResponse <Void> logout(@RequestBody LogoutRequest logoutRequest) throws ParseException, JOSEException, InvalidKeySpecException, NoSuchAlgorithmException {
        authenticationService.logout(logoutRequest);
        return ApiResponse.<Void>builder().
                build();


    }
    @PostMapping("/refresh")
    public ApiResponse <RefreshResponse> refresh(
            @RequestHeader("DPoP") String dpopHeader,
            @RequestBody RefreshRequest refreshRequest)
            throws ParseException, JOSEException, NoSuchAlgorithmException, InvalidKeySpecException {
        if (dpopHeader == null || dpopHeader.isBlank()) {
            throw new RuntimeException("Missing DPoP header");
        }
        RefreshResponse refreshResponse = authenticationService.refreshToken(refreshRequest,dpopHeader);
        return ApiResponse.<RefreshResponse>builder()
                .message(StatusCode.OK.getMessage())
                .code(StatusCode.OK.getCode())
                .result(refreshResponse)
                .build();


    }
    @GetMapping("/debug-plain")
    public Object debug(Authentication authentication) {

        return authentication.getAuthorities();
    }

}
