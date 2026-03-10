package com.calt.coffeeshop.w1crud_maven;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
// This include all notations like @Component, @Service, @Reposistory
// Automatically Inject Dependencies to corresponded classes
//@EnableAutoConfiguration: Automatically new, config: JPA/hibernate, tomcat, MVC, etcerete.
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);

        BCryptPasswordEncoder encoder= new BCryptPasswordEncoder(10);

        System.out.println(encoder.encode("123"));



    }

}
