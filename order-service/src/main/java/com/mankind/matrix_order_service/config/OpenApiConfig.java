package com.mankind.matrix_order_service.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {
    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI orderServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Mankind Matrix Order Service API")
                        .description("""
                            API documentation for the Mankind Matrix Order Service.
                            This service provides endpoints for managing orders, allowing users to:
                            - Create orders from carts
                            - View order history
                            - Cancel orders
                            - Track order status and payment status

                            All endpoints are prefixed with `/orders`.
                            
                            Swagger UI: http://localhost:8088/swagger-ui
                            """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Mankind Matrix Team")
                                .email("support@mankindmatrix.com")
                                .url("https://www.mankindmatrix.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8085/api/v1")
                                .description("Gateway Server (Production)"),
                        new Server()
                                .url("http://localhost:8088")
                                .description("Direct Service (Development)")
                ))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT token for authentication")));
    }
}
