package com.calt.coffeeshop.w1crud_maven.config;

import com.calt.coffeeshop.w1crud_maven.dto.response.ApiResponse;
import com.calt.coffeeshop.w1crud_maven.exception.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    private final GlobalExceptionHandler globalExceptionHandler;

    public CustomAccessDeniedHandler(GlobalExceptionHandler globalExceptionHandler) {
        this.globalExceptionHandler = globalExceptionHandler;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        ApiResponse apiResponse = globalExceptionHandler
                .handleAccess(accessDeniedException)
                .getBody();
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(apiResponse.getCode());
        response.getWriter().write(
                new ObjectMapper().writeValueAsString(apiResponse));
        response.flushBuffer();
    }
}
