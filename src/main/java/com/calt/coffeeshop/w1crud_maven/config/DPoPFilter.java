package com.calt.coffeeshop.w1crud_maven.config;

import com.calt.coffeeshop.w1crud_maven.service.DPoPService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class DPoPFilter extends OncePerRequestFilter {
    private final DPoPService dPoPService;

    public DPoPFilter(DPoPService dPoPService) {
        this.dPoPService = dPoPService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ((authentication instanceof JwtAuthenticationToken jwtAuthenticationToken)){
            Jwt jwt = jwtAuthenticationToken.getToken();
            String dpopHeader = request.getHeader("DPoP");
            dPoPService.validateDPoP(dpopHeader,jwt,request);
            filterChain.doFilter(request,response);
        }
    }
}
