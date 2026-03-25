//package com.calt.coffeeshop.w1crud_maven.config;
//
//import com.calt.coffeeshop.w1crud_maven.dto.request.IntrospectRequest;
//import com.calt.coffeeshop.w1crud_maven.service.AuthenticationService;
//import com.calt.coffeeshop.w1crud_maven.service.DPoPService;
//import com.calt.coffeeshop.w1crud_maven.service.InvalidTokenService;
//import com.nimbusds.jwt.SignedJWT;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.boot.autoconfigure.jdbc.metadata.DataSourcePoolMetadataProvidersConfiguration;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.oauth2.jwt.Jwt;
//import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//@Component
//public class DPoPFilter extends OncePerRequestFilter {
//
//    private final DPoPService dPoPService;
//    private final AuthenticationService authenticationService;
//    private final InvalidTokenService invalidTokenService;
//
//    public DPoPFilter(DPoPService dPoPService, AuthenticationService authenticationService, InvalidTokenService invalidTokenService) {
//        this.dPoPService = dPoPService;
//        this.authenticationService = authenticationService;
//        this.invalidTokenService = invalidTokenService;
//    }
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain filterChain)
//            throws ServletException, IOException {
//
//        String path = request.getRequestURI();
//        if (path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs")) {
//            filterChain.doFilter(request, response); // skip DPoP for Swagger
//            return;
//        }
//        String dpopHeader = request.getHeader("DPoP");
//        dPoPService.validateDPoP(dpopHeader,request);
//        try {
//            SignedJWT jwt = SignedJWT.parse(dpopHeader);
//
//            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//
//            var claims = jwt.getJWTClaimsSet();
//
////            String htm =claims.getStringClaim("htm");
////            String htu =claims.getStringClaim("htu");
////
//            if (auth instanceof JwtAuthenticationToken jwtAuth) {
//                String id = jwtAuth.getToken().getId();
//                if(invalidTokenService.validateToken(id))
//                    throw new RuntimeException("Revoked Token!");
//
//                dPoPService.validateDPoPWithJwt(
//                        dpopHeader,
//                        jwtAuth.getToken(),
//                        request
//                );
//            }
//
//
//            filterChain.doFilter(request, response);
//
//        } catch (Exception e) {
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            response.setContentType("application/json");
//            if ("Revoked Token!".equals(e.getMessage())) {
//                response.getWriter().write("{\"error\":\"Revoked Token\"}");
//            } else {
//                response.getWriter().write("{\"error\":\"Invalid DPoP\"}");
//            }
//        }
//    }
//}
