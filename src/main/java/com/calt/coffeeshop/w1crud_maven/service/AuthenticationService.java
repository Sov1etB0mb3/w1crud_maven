package com.calt.coffeeshop.w1crud_maven.service;

import com.calt.coffeeshop.w1crud_maven.dto.request.AuthRequest;
import com.calt.coffeeshop.w1crud_maven.dto.request.IntrospectRequest;
import com.calt.coffeeshop.w1crud_maven.dto.request.LogoutRequest;
import com.calt.coffeeshop.w1crud_maven.dto.response.AuthenticationResponse;
import com.calt.coffeeshop.w1crud_maven.dto.response.IntrospectResponse;
import com.calt.coffeeshop.w1crud_maven.entity.*;
import com.calt.coffeeshop.w1crud_maven.exception.AppException;
import com.calt.coffeeshop.w1crud_maven.enums.ErrorCode;
import com.calt.coffeeshop.w1crud_maven.repository.InvalidTokenRepository;
import com.calt.coffeeshop.w1crud_maven.repository.RefreshTokenRepository;
import com.calt.coffeeshop.w1crud_maven.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.Token;
import org.hibernate.mapping.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AuthenticationService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private InvalidTokenRepository invalidTokenRepository;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @NonFinal
    @Value("${jwt}")
    protected String key;
    @Value("${DURATION}")
    protected Integer DURATION;

    public AuthenticationResponse authenicate(AuthRequest authRequest){
    var user = userRepository.findUserByUsername(authRequest.getUsername()).orElseThrow(()-> new AppException(ErrorCode.NOT_FOUND));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean authenicated = passwordEncoder.matches(authRequest.getPassword(), user.getPassword());
        if(!authenicated)
            throw new AppException(ErrorCode.UNAUTHORIZED);
        var token=generateToken(user);
        String rtoken=UUID.randomUUID().toString();

        RefreshToken refreshToken = RefreshToken.builder()
                .userid(user.getId())
                .refreshtoken(rtoken)
                .expirytime(new Date( Instant.now()
                        .plus(1,ChronoUnit.HOURS)
                        .toEpochMilli()
                ).toInstant()
                )
                .build();
        refreshTokenRepository.save(refreshToken);

        return AuthenticationResponse.builder().token(token).refreshtoken(rtoken).authenicated(true).build();
    }


    public IntrospectResponse introspect(IntrospectRequest introspectRequest) throws JOSEException, ParseException {
        var token =  introspectRequest.getToken();
        boolean isValid=true;
        try {
            verifyToken(token);
        }catch (AppException e){
            isValid =false;
        }
        return IntrospectResponse.builder()
                .valid(isValid)
                .build();



    }
    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        var signToken = verifyToken(request.getToken());
        String jit = signToken.getJWTClaimsSet().getJWTID();
        Instant expiryTime= signToken.getJWTClaimsSet().getExpirationTime().toInstant();

        InvalidToken invalidToken = InvalidToken.builder()
                .id(jit)
                .expirytime(expiryTime)
                .build();
        invalidTokenRepository.save(invalidToken);
//        Integer userId= userRepository.findUserByUsername(signToken.getJWTClaimsSet().getSubject())
//                .get().getId();
        RefreshToken refreshToken = refreshTokenRepository
                .findRefreshTokenByRefreshtoken(request.getRefreshtoken())
                .orElseThrow(()-> new RuntimeException("CANNOT FOUND refreshtoken!"));
        refreshTokenRepository.delete(refreshToken);


    }
    private SignedJWT verifyToken(String token) throws JOSEException, ParseException {
        JWSVerifier jwsVerifier= new MACVerifier(key.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);
        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        var verified = signedJWT.verify(jwsVerifier);
        if (!verified && expiryTime.after(new Date()))
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        if (invalidTokenRepository.existsInvalidTokenById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new AppException(ErrorCode.INVALID_KEY);
        return signedJWT;
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
                        Instant.now()
                                .plus(DURATION, ChronoUnit.SECONDS).toEpochMilli()
                ))
                .claim("role",role)
                .jwtID(UUID.randomUUID().toString())
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
//String permission = user.getRoles().stream().flatMap(
//                userRole -> userRole.getRole()
//                        .getPermissions()
//                        .stream()
//                        .map(rolePermission ->
//                                rolePermission.getPermission().getName())
//                        .distinct()
//        )
//        .collect(Collectors.joining(" "));