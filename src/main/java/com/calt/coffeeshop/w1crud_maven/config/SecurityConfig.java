package com.calt.coffeeshop.w1crud_maven.config;

import com.nimbusds.jose.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.spec.SecretKeySpec;
import java.util.List;

@Configuration
public class SecurityConfig {
    @Value("${jwt}")
    private String secretkey;
    private String[] publicEndpoints={"api/auth/token"};
    private String[] privateEndpoints={"api/users"};
    //after complete api, add has role adfter resquestMatchers
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .authorizeHttpRequests(auth->auth
                        .requestMatchers(HttpMethod.POST,publicEndpoints).permitAll()
                        .requestMatchers(HttpMethod.GET,privateEndpoints)
                        .hasAuthority("SCOPE_ADMIN")
                        .anyRequest()
                        .authenticated()
                );
        httpSecurity.csrf(AbstractHttpConfigurer::disable);

        //).httpBasic(basic-> basic.disable()) to use the basic auth from spring security.
        // We can also use default spring security with this:
        //httpSecurity.httpBasic(Customizer.withDefaults())
        //###############################
        //Configure the spring security so that we can use the key we generate to Authorize!
        httpSecurity.oauth2ResourceServer(o2Auth->
                o2Auth.jwt(jwtConfigurer -> jwtConfigurer.decoder(jwtDecoder()))
                );
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
    // this use to ignore the urls that aren't be secured! (In a nutshell, those urls can be accessed by anynone)
//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer(){
//        return (web)->web.ignoring().requestMatchers("rq1","rq1");
//    }

}
