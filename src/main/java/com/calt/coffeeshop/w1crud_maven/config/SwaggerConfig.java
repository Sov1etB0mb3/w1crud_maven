package com.calt.coffeeshop.w1crud_maven.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;

@Configuration
public class SwaggerConfig {

        @Bean
        public OpenAPI customOpenAPI() {
                final String securitySchemeName = "bearerAuth";

                return new OpenAPI()
                        .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                        .components(new Components()
                                .addSecuritySchemes(securitySchemeName,
                                        new SecurityScheme()
                                                .name(securitySchemeName)
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("DPoP")));
        }
        @Bean
        public OperationCustomizer addDPoPHeader(){
                return (Operation operation,HandlerMethod handlerMethod)
                -> {
                        Parameter dpopHeader = new Parameter()
                                .in("header")
                                .name("DPoP")
                                .description("DPoP with JWT")
                                .required(true);
                        operation.addParametersItem(dpopHeader);
                        return operation;
                };
        }
}
