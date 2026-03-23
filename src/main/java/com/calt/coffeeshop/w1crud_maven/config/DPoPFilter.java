package com.calt.coffeeshop.w1crud_maven.config;

import com.calt.coffeeshop.w1crud_maven.service.DPoPService;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.jdbc.metadata.DataSourcePoolMetadataProvidersConfiguration;
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
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String dpopHeader = request.getHeader("DPoP");
        dPoPService.validateDPoP(dpopHeader,request);
        try {
            SignedJWT jwt = SignedJWT.parse(dpopHeader);

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            var claims = jwt.getJWTClaimsSet();
//            String htm =claims.getStringClaim("htm");
//            String htu =claims.getStringClaim("htu");
//
            if (auth instanceof JwtAuthenticationToken jwtAuth) {


                dPoPService.validateDPoPWithJwt(
                        dpopHeader,
                        jwtAuth.getToken(),
                        request
                );
            }


            filterChain.doFilter(request, response);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Invalid DPoP\"}");
        }
    }
}
