package com.calt.coffeeshop.w1crud_maven.service;

import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import javax.management.RuntimeMBeanException;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
public class DPoPService {
    public void validateDPoP(String dpopHeader, Jwt jwt, HttpServletRequest request){
        if (dpopHeader == null){
            throw new RuntimeException("Missing DPoP header");
        }
        try
        {
            SignedJWT dpopJwt= SignedJWT.parse(dpopHeader);
            Map<String, Object> cnf = jwt.getClaim("cnf");
            Map<String, Object> jwKMap = (Map<String, Object>) cnf.get("jwk");
            JWK jwk = JWK.parse(jwKMap);
            JWSVerifier jwsVerifier = new ECDSAVerifier((ECKey) jwk);

            if (!dpopJwt.verify(jwsVerifier)){
                throw new RuntimeException("Invalid DPoP verifier");
            }
            var claims = dpopJwt.getJWTClaimsSet();
            if (!request.getMethod().equalsIgnoreCase(claims.getClaimAsString("htm"))){
                throw  new RuntimeException("Invalid method");
            }
            String requestUrl = request.getRequestURI().toString();
            if(!requestUrl.equals(claims.getStringClaim("htu"))){
                throw  new RuntimeException("Invalid URL");
            }
            Instant iat = claims.getIssueTime().toInstant();
            Instant now = Instant.now();
            if(iat == null || Math.abs(now.toEpochMilli()-iat.toEpochMilli())>300_000){
                throw new RuntimeException("Expired DPoP");
            }
            String jit = claims.getJWTID();
            if(jit.isBlank() || jit == null || jit.length()<10){
                throw  new RuntimeException("Missing JWTID");
            }
        } catch (Exception e){
            throw  new RuntimeException("INVALID DPOP!",e);

        }
    }
}
