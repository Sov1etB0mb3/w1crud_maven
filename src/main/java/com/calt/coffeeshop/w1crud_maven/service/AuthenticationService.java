package com.calt.coffeeshop.w1crud_maven.service;

import com.calt.coffeeshop.w1crud_maven.dto.request.AuthRequest;
import com.calt.coffeeshop.w1crud_maven.dto.request.IntrospectRequest;
import com.calt.coffeeshop.w1crud_maven.dto.response.AuthenticationResponse;
import com.calt.coffeeshop.w1crud_maven.dto.response.IntrospectResponse;
import com.calt.coffeeshop.w1crud_maven.entity.Role;
import com.calt.coffeeshop.w1crud_maven.entity.User;
import com.calt.coffeeshop.w1crud_maven.entity.UserRole;
import com.calt.coffeeshop.w1crud_maven.exception.AppException;
import com.calt.coffeeshop.w1crud_maven.enums.ErrorCode;
import com.calt.coffeeshop.w1crud_maven.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.mapping.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AuthenticationService {
    @Autowired
    private UserRepository userRepository;
    @NonFinal
    @Value("${jwt}")
    protected String key;

    public AuthenticationResponse authenicate(AuthRequest authRequest){
    var user = userRepository.findUserByUsername(authRequest.getUsername()).orElseThrow(()-> new AppException(ErrorCode.USER_NOT_FOUND));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean authenicated = passwordEncoder.matches(authRequest.getPassword(), user.getPassword());
        if(!authenicated)
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        var token=generateToken(user);
        return AuthenticationResponse.builder().token(token).authenicated(true).build();
    }

    public IntrospectResponse introspect(IntrospectRequest introspectRequest) throws JOSEException, ParseException {
        var token =  introspectRequest.getToken();
        JWSVerifier jwsVerifier= new MACVerifier(key.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);
        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        var verified = signedJWT.verify(jwsVerifier);
        return IntrospectResponse.builder()
                .valid(verified&&expiryTime.after(new Date()))
                .build();

    }
//    public String buildRole(User user){
//        StringJoiner stringJoiner = new StringJoiner(" ");
//        if(CollectionUtils.isEmpty(user.getRoles()))
//            user.getRoles().forEach(stringJoiner::add);
//        return stringJoiner.toString();
//
//    }
    private String generateToken(User user){
        JWSHeader jweHeader = new JWSHeader(JWSAlgorithm.HS512);
        String role = user.getRoles().stream().map(u->u.getRole().getName())
                .collect(Collectors.joining(" "));
        // claim("customClaim","Custom")
        JWTClaimsSet jwtClaimsSet= new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("mrx.com")//domain
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()
                ))
                .claim("scope",role)
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject=new JWSObject(jweHeader,payload);
        try {
            jwsObject.sign(new MACSigner(key.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create token: "+e);
            throw new RuntimeException(e);
        }

    }
}
