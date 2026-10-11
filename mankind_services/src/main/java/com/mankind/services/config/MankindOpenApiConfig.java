package com.mankind.services.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MankindOpenApiConfig {

    @Bean
    OpenAPI mankindOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Mankind Matrix API")
                        .description("Mankind Matrix unified backend API")
                        .version("1.0.0"));
    }
}
