package com.calt.coffeeshop.w1crud_maven.service;

import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.util.Base64URL;
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

    public void validateDPoP(String dpopHeader, Jwt jwt, HttpServletRequest request) {

        if (dpopHeader == null) {
            throw new RuntimeException("Missing DPoP header");
        }

        try {
            SignedJWT dpopJwt = SignedJWT.parse(dpopHeader);

            JWK jwk = dpopJwt.getHeader().getJWK();

            JWSVerifier verifier;

            if (jwk instanceof RSAKey rsaKey) {
                verifier = new RSASSAVerifier(rsaKey);
            } else if (jwk instanceof ECKey ecKey) {
                verifier = new ECDSAVerifier(ecKey);
            } else {
                throw new RuntimeException("Unsupported key type");
            }

            if (!dpopJwt.verify(verifier)) {
                throw new RuntimeException("Invalid DPoP signature");
            }

            var claims = dpopJwt.getJWTClaimsSet();

            if (!request.getMethod().equalsIgnoreCase(claims.getStringClaim("htm"))) {
                throw new RuntimeException("Invalid method");
            }

            String requestUrl = request.getRequestURL().toString();
            if (!requestUrl.equals(claims.getStringClaim("htu"))) {
                throw new RuntimeException("Invalid URL");
            }

            Instant iat = claims.getIssueTime().toInstant();
            Instant now = Instant.now();

            if (iat == null || Math.abs(now.toEpochMilli() - iat.toEpochMilli()) > 300_000) {
                throw new RuntimeException("Expired DPoP");
            }

            String jti = claims.getJWTID();
            if (jti == null || jti.isBlank() || jti.length() < 10) {
                throw new RuntimeException("Invalid jti");
            }

            Base64URL thumbprint = jwk.computeThumbprint();
            Map<String, Object> cnf = jwt.getClaim("cnf");
            String expectedJkt = (String) cnf.get("jkt");

            if (!thumbprint.toString().equals(expectedJkt)) {
                throw new RuntimeException("DPoP key mismatch");
            }

        } catch (Exception e) {
            throw new RuntimeException("INVALID DPOP!", e);
        }
    }
}
