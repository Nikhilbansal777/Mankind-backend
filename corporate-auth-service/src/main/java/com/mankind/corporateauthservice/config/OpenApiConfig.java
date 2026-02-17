package com.mankind.corporateauthservice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI corporateAuthOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Corporate Auth Service API")
                        .description("Authentication and profile APIs for corporate users.")
                        .version("v1")
                        .contact(new Contact().name("Mankind Matrix").email("support@mankind.local"))
                        .license(new License().name("Proprietary")))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
