package com.calt.coffeeshop.w1crud_maven.service;

import com.calt.coffeeshop.w1crud_maven.dto.request.AuthRequest;
import com.calt.coffeeshop.w1crud_maven.dto.request.IntrospectRequest;
import com.calt.coffeeshop.w1crud_maven.dto.request.LogoutRequest;
import com.calt.coffeeshop.w1crud_maven.dto.request.RefreshRequest;
import com.calt.coffeeshop.w1crud_maven.dto.response.AuthenticationResponse;
import com.calt.coffeeshop.w1crud_maven.dto.response.IntrospectResponse;
import com.calt.coffeeshop.w1crud_maven.dto.response.RefreshResponse;
import com.calt.coffeeshop.w1crud_maven.entity.*;
import com.calt.coffeeshop.w1crud_maven.exception.AppException;
import com.calt.coffeeshop.w1crud_maven.enums.ErrorCode;
import com.calt.coffeeshop.w1crud_maven.repository.InvalidTokenRepository;
import com.calt.coffeeshop.w1crud_maven.repository.RefreshTokenRepository;
import com.calt.coffeeshop.w1crud_maven.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.Token;
import org.hibernate.mapping.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
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
    @Autowired
    private DPoPService dPoPService;

    @NonFinal
    @Value("${jwt}")
    protected String key;
    @Value("${DURATION}")
    protected Integer DURATION;
    @Value("${REFRESH_DURATION}")
    protected Integer REFRESH_DURATION;
    @Value("${rsa.key.public}")
    protected String publicKey;
    @Value("${rsa.key.private}")
    protected String privateKey;

    public AuthenticationResponse authenicate(AuthRequest authRequest,String dpopHeader)
            throws Exception {
        var user = userRepository.findUserByUsername(authRequest.getUsername())
                .orElseThrow(()-> new AppException(ErrorCode.NOT_FOUND));

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean authenicated = passwordEncoder
                .matches(authRequest.getPassword(), user.getPassword());
        if(!authenicated)
            throw new AppException(ErrorCode.UNAUTHORIZED);
        var token=generateToken(user,dpopHeader);

        RefreshToken refreshToken = generateRefreshToken(user,dpopHeader);

        return AuthenticationResponse.builder()
                .token(token)
                .refreshtoken(refreshToken.getRefreshtoken())
                .authenicated(true).build();
    }

    private RefreshToken generateRefreshToken(User user,String dpopHeader) throws Exception {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32]; // 256 bits
        random.nextBytes(bytes);
        String rtoken = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        String dpopJkt = extractJwkThumbprint(dpopHeader);
        //String rtoken=UUID.randomUUID().toString();
        RefreshToken refreshToken = RefreshToken.builder()
                .userid(user.getId())
                .refreshtoken(rtoken)
                .jkt(dpopJkt)
                .valid(true)
                .expirytime(new Date( Instant.now()
                                .plus(REFRESH_DURATION,ChronoUnit.DAYS)
                                .toEpochMilli()
                        ).toInstant()
                )
                .build();
        return refreshTokenRepository.save(refreshToken);
    }
    public IntrospectResponse introspect(IntrospectRequest introspectRequest) throws JOSEException, ParseException {
        var token =  introspectRequest.getToken();
        boolean isValid=true;
        try {
            verifyToken(token);
        }catch (AppException | InvalidKeySpecException | NoSuchAlgorithmException e){
            isValid =false;
        }
        return IntrospectResponse.builder()
                .valid(isValid)
                .build();



    }
    public void logout(String dpopHeader,
                       HttpServletRequest httpServletRequest,
                       Authentication authentication,
                       LogoutRequest logoutRequest) throws ParseException, JOSEException, InvalidKeySpecException, NoSuchAlgorithmException {
        log.info("HTU expected: " + httpServletRequest.getRequestURI());
        log.info("HTM expected: " + httpServletRequest.getMethod());

        if (!(authentication instanceof JwtAuthenticationToken jwtAuth)) {
            throw new RuntimeException("Invalid authentication type: " + authentication);
        }

        Jwt jwt = jwtAuth.getToken();
        try {
            dPoPService.validateDPoPWithJwt(dpopHeader, jwt, httpServletRequest);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
//        dPoPService.validateDPoPWithJwt(dpopHeader, jwt,httpServletRequest);
        SignedJWT signToken;
        SignedJWT dpopToken= SignedJWT.parse(dpopHeader);
        try {
             signToken = verifyToken(jwt.getTokenValue());
            log.info("Parsed OK: " + signToken);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        log.info("HTM actual: " + dpopToken.getJWTClaimsSet().getStringClaim("htm"));


        log.info("HTU actual: " + dpopToken.getJWTClaimsSet().getStringClaim("htu"));
        String jit = signToken.getJWTClaimsSet().getJWTID();
        Instant iat= dpopToken.getJWTClaimsSet().getIssueTime().toInstant();
        Instant now = Instant.now();
        if (Math.abs(now.toEpochMilli() - iat.toEpochMilli()) > 300_000) {
            throw new RuntimeException("Expired DPoP");
        }
        InvalidToken invalidToken = InvalidToken.builder()
                .id(jit)
                .expirytime(signToken.getJWTClaimsSet().getExpirationTime().toInstant())
                .build();

        invalidTokenRepository.save(invalidToken);
//        Integer userId= userRepository.findUserByUsername(signToken.getJWTClaimsSet().getSubject())
//                .get().getId();
        log.info("RT: "+logoutRequest.getRefreshtoken());
        RefreshToken refreshToken = refreshTokenRepository
                .findRefreshTokenByRefreshtoken(logoutRequest.getRefreshtoken())
                .orElseThrow(()-> new RuntimeException("CANNOT FOUND refreshtoken!"));
//        refreshTokenRepository.delete(refreshToken);
        refreshToken.setValid(false);
        refreshTokenRepository.save(refreshToken);


    }

    public RefreshResponse refreshToken (RefreshRequest request, String dpopHeader) throws Exception {
        RefreshToken refreshToken= refreshTokenRepository
                .findRefreshTokenByRefreshtoken(request.getToken())
                .orElseThrow( () -> new RuntimeException("Not found refresh token!"));

        if(refreshToken.getExpirytime().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new RuntimeException("Expired KEY!");
        }
        if(!refreshToken.isValid())
            throw new RuntimeException("Revoked Key!");
        String incomingJkt = extractJwkThumbprint(dpopHeader);
        if (!incomingJkt.equals(refreshToken.getJkt()))
            throw new RuntimeException("DPoP proof doesn't sastify");
        User user = userRepository.findUserById(refreshToken.getUserid());
        RefreshResponse refreshResponse = RefreshResponse.builder()
                .rtoken(generateRefreshToken(user,dpopHeader).getRefreshtoken())
                .atoken(generateToken(user,dpopHeader))
                .authenicated(true)
                .build();
        refreshToken.setValid(false);
        refreshTokenRepository.save(refreshToken);
        return refreshResponse;
    }
    private SignedJWT verifyToken(String token) throws JOSEException, ParseException, InvalidKeySpecException, NoSuchAlgorithmException {
        byte[] decodedKey = Base64.getDecoder().decode(publicKey);
        //PKCS8 standardlizer
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decodedKey);
        //create keyfactory
        KeyFactory kf = KeyFactory.getInstance("RSA");
        //generate public key
        PublicKey publicKeyP= kf.generatePublic(spec);
        JWSVerifier jwsVerifier= new RSASSAVerifier((RSAPublicKey) publicKeyP);

        SignedJWT signedJWT = SignedJWT.parse(token);
        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        var verified = signedJWT.verify(jwsVerifier);
        if (!verified )
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        if ( expiryTime.before(new Date()))
            throw new RuntimeException("Expired key");

        if (invalidTokenRepository.existsInvalidTokenById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new AppException(ErrorCode.INVALID_KEY);
        return signedJWT;
    }
//    private SignedJWT verifyTokenForRefresh(String token) throws JOSEException, ParseException {
//        JWSVerifier jwsVerifier= new MACVerifier(key.getBytes());
//        SignedJWT signedJWT = SignedJWT.parse(token);
//        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
//        var verified = signedJWT.verify(jwsVerifier);
//        if (!verified )
//            throw new AppException(ErrorCode.UNAUTHENTICATED);
//        if ( expiryTime.after(new Date()))
//            throw new RuntimeException("Valid key!");
//
//        return signedJWT;
//    }
//    public String buildRole(User user){
//        StringJoiner stringJoiner = new StringJoiner(" ");
//        if(CollectionUtils.isEmpty(user.getRoles()))
//            user.getRoles().forEach(stringJoiner::add);
//        return stringJoiner.toString();
//
//    }
    //User user, JWK jwk
    private String generateToken(User user, String dpopHeader  ) throws ParseException, JOSEException, NoSuchAlgorithmException, InvalidKeySpecException {
        //get header to extract infors

        SignedJWT dpopJwt = SignedJWT.parse(dpopHeader);
        //get jwk from header sent form client
        RSAKey clientKey = (RSAKey) dpopJwt.getHeader().getJWK();
        //compute jkt
        Base64URL thumbprint = clientKey.computeThumbprint();
        // verify client public key;

        //get Role from user!
        String role = user.getRoles().stream().map(u->u.getRole().getName())
                .collect(Collectors.joining(" "));
        //build cnf claim
        Map <String,Object> cnf = Map.of("jkt",thumbprint.toString());
        //rsa decoded
        byte[] decodedKey = Base64.getDecoder().decode(privateKey);
        //PKCS8 standardlizer
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decodedKey);
        //create keyfactory
        KeyFactory kf = KeyFactory.getInstance("RSA");
        //generate private key
        PrivateKey privateKeyP= kf.generatePrivate(spec);
        //create header
        JWSHeader jweHeader = new JWSHeader.Builder(JWSAlgorithm.RS256)
                .type(JOSEObjectType.JWT)
                .keyID(thumbprint.toString())
                .build()
                ;


        // claim("customClaim","Custom")
        //create claim set
        JWTClaimsSet jwtClaimsSet= new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("mrx.com")//domain
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now()
                                .plus(DURATION, ChronoUnit.SECONDS).toEpochMilli()
                ))
                .claim("role",role)
                .claim("cnf",cnf)
                .jwtID(UUID.randomUUID().toString())
                .build();

//        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        SignedJWT signedJWT=new SignedJWT(jweHeader,jwtClaimsSet);
        try {
            signedJWT.sign(new RSASSASigner(privateKeyP));
            return signedJWT.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create token: "+e);
            throw new RuntimeException(e);
        }

    }

    private String extractJwkThumbprint(String dpopJwt) throws Exception {
        JWSObject jwsObject = JWSObject.parse(dpopJwt); // parse the JWT
        JWSHeader header = jwsObject.getHeader();       // get header
        JWK jwk = header.getJWK();                       // extract JWK
        return jwk.computeThumbprint().toString();      // compute thumbprint
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