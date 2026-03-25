package com.calt.coffeeshop.w1crud_maven.config;

import com.calt.coffeeshop.w1crud_maven.dto.response.AuthInforResponse;
import com.calt.coffeeshop.w1crud_maven.entity.User;
import com.calt.coffeeshop.w1crud_maven.repository.UserRepository;
import com.calt.coffeeshop.w1crud_maven.service.AuthenticationService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.spec.SecretKeySpec;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
//without this, post condtion won't work
@EnableMethodSecurity
public class SecurityConfig {
    @Value("${jwt}")
    private String secretkey;

    @Value("${rsa.key.public}")
    private String rsaPublic;
    private String[] publicEndpoints={"/api/auth/token","/api/auth/refresh"};
    private String[] privateEndpoints={"/api/users/**","/api/role/**","/api/permission/**","/api/auth/logout"};
    private static final String[] WHITE_LIST_URL = { "/api/v1/auth/**", "/v2/api-docs", "/v3/api-docs",
            "/v3/api-docs/**", "/swagger-resources", "/swagger-resources/**", "/configuration/ui",
            "/configuration/security", "/swagger-ui/**", "/webjars/**", "/swagger-ui.html", "/api/auth/**",
            "/api/test/**", "/authenticate",
            "/v3/api-docs/swagger-config","/api/products/**","/api/categories/**"};


    @Autowired
    private UserRepository userRepository;
//    @Autowired
//    private CustomJwtDecoder customJwtDecoder;
//    @Autowired
//    private DPoPFilter dPoPFilter;
    //after complete api, add has role adfter resquestMatchers
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity,
                                                   CustomAccessDeniedHandler customAccessDeniedHandler,
                                                   JwtDecoder jwtDecoder,
                                                   RSAPublicKey publicKey,
                                                   AuthenticationService authenticationService) throws Exception {
        httpSecurity
                .authorizeHttpRequests(auth->auth
                        .requestMatchers(WHITE_LIST_URL).permitAll()
                        .requestMatchers(HttpMethod.POST,publicEndpoints).permitAll()
                        .requestMatchers(privateEndpoints)
                        .authenticated()


                );
        //use .hasRole("USER") instead of .has Authority("${Prefix}ROLE") to be more idiomatic
        // the mechanism isn't much different, hasRole will automatically find in Authority
        // that contains the value we pass in!

        httpSecurity.csrf(AbstractHttpConfigurer::disable);
        httpSecurity.httpBasic(http->http.disable());
//    httpSecurity.httpBasic(Customizer.withDefaults());
        //).httpBasic(basic-> basic.disable()) to use the basic auth from spring security.
        // We can also use default spring security with this:
        //httpSecurity.httpBasic(Customizer.withDefaults())
        //###############################
        //Configure the spring security so that we can use the key we generate to Authorize!
        httpSecurity.oauth2ResourceServer(o2Auth->
                o2Auth.jwt(jwtConfigurer -> jwtConfigurer
                        .decoder(jwtDecoder)
                                .jwtAuthenticationConverter(jwtAuthenticationConverter(authenticationService)))
                        .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
                );
        httpSecurity.exceptionHandling(e->e
                .accessDeniedHandler(customAccessDeniedHandler));
//        httpSecurity.addFilterAfter(dPoPFilter, BearerTokenAuthenticationFilter.class);
    return httpSecurity.build();
    }
    //Decoder to decode the JWT we generated
//    @Bean
//    public JwtDecoder jwtDecoder(){
//        SecretKeySpec secretKeySpec = new SecretKeySpec(secretkey.getBytes(), "HS512");
//        return NimbusJwtDecoder.withSecretKey(secretKeySpec)
//                .macAlgorithm(MacAlgorithm.HS512)
//                .build();
//    }
    @Bean
    public JwtDecoder jwtDecoder(RSAPublicKey publicKey) {
        return NimbusJwtDecoder.withPublicKey(publicKey).build();
    }
    @Bean
    public RSAPublicKey publicKey() throws Exception {
        String key = rsaPublic; // from config

        byte[] decoded = Base64.getDecoder().decode(key);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);

        KeyFactory kf = KeyFactory.getInstance("RSA");
        return (RSAPublicKey) kf.generatePublic(spec);
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter(AuthenticationService authenticationService){
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
            converter.setJwtGrantedAuthoritiesConverter(jwt -> {
                String username = jwt.getSubject();

                AuthInforResponse authInforResponse = authenticationService.getAuthInfor(username);

                return authInforResponse.getAuthorities();

        });

    return converter;
}

}


//old converter


//@Bean
//JwtAuthenticationConverter jwtAuthenticationConverter(){
//    JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
//    converter.setJwtGrantedAuthoritiesConverter(jwt -> {
//        String username = jwt.getSubject();
//        List<GrantedAuthority> roleAuthorities = new ArrayList<>();
//
//        String roles = jwt.getClaimAsString("role");
//        if (roles != null) {
//            Arrays.stream(roles.split(" "))
//                    .map(r ->
//                            new SimpleGrantedAuthority("ROLE_"+r))
//                    .forEach(roleAuthorities::add);
//
//        }
//        User user = userRepository.findUserWithRolesAndPermissions(username)
//                .orElseThrow();
//        Stream<GrantedAuthority> permissionAuthorities = user.getRoles().stream()
//                .flatMap(userRole ->
//                        userRole.getRole()
//                                .getPermissions()
//                                .stream()
//                                .map(rolePermission ->
//                                        new SimpleGrantedAuthority(
//                                                rolePermission.getPermission().getName()
//                                        ))
//                );
//        return Stream.concat(roleAuthorities.stream(),permissionAuthorities).collect(Collectors.toSet());
//
//    });
//
//    return converter;
//}