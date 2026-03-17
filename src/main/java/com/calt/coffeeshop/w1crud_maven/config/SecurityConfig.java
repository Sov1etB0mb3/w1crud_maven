package com.calt.coffeeshop.w1crud_maven.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.spec.SecretKeySpec;

@Configuration
//without this, post condtion won't work
@EnableMethodSecurity
public class SecurityConfig {
    @Value("${jwt}")
    private String secretkey;
    private String[] publicEndpoints={"/api/auth/token"};
    private String[] privateEndpoints={"/api/users","/api/role/**","/api/permission/**"};
    private static final String[] WHITE_LIST_URL = { "/api/v1/auth/**", "/v2/api-docs", "/v3/api-docs",
            "/v3/api-docs/**", "/swagger-resources", "/swagger-resources/**", "/configuration/ui",
            "/configuration/security", "/swagger-ui/**", "/webjars/**", "/swagger-ui.html", "/api/auth/**",
            "/api/test/**", "/authenticate",
            "/v3/api-docs/swagger-config"};
    //after complete api, add has role adfter resquestMatchers
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, CustomAccessDeniedHandler customAccessDeniedHandler) throws Exception {
        httpSecurity
                .authorizeHttpRequests(auth->auth
                        .requestMatchers(WHITE_LIST_URL).permitAll()
                        .requestMatchers(HttpMethod.POST,publicEndpoints).permitAll()
                        .requestMatchers(privateEndpoints)
                        .hasRole("ADMIN")
                        .anyRequest()
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
                o2Auth.jwt(jwtConfigurer -> jwtConfigurer.decoder(jwtDecoder()).
                        jwtAuthenticationConverter(jwtAuthenticationConverter()))
                        .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
                );
        httpSecurity.exceptionHandling(e->e
                .accessDeniedHandler(customAccessDeniedHandler));

    return httpSecurity.build();
    }
    //Decoder to decode the JWT we generate
    @Bean
    public JwtDecoder jwtDecoder(){
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretkey.getBytes(), "HS512");
        return NimbusJwtDecoder.withSecretKey(secretKeySpec)
                .macAlgorithm(MacAlgorithm.HS512)
                .build();
    }
    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter(){
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
        JwtAuthenticationConverter jwtAuthenticationConverter= new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return  jwtAuthenticationConverter;
    }

    // this use to ignore the urls that aren't be secured! (In a nutshell, those urls can be accessed by anynone)
//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer(){
//        return (web)->web.ignoring().requestMatchers("rq1","rq1");
//    }

}
