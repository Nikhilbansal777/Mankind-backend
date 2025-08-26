package com.mankind.mankindgatewayservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOriginPatterns(Arrays.asList(
                        "http://localhost:3000",
                        "http://localhost:8085",
                        "http://127.0.0.1:8085",
                        "http://localhost:8080",
                        "http://localhost:8081",
                        "http://localhost:8082",
                        "http://localhost:8083",
                        "http://localhost:8088"
                    ));
                    config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
                    config.setAllowedHeaders(Arrays.asList("*"));
                    config.setAllowCredentials(true);
                    config.setMaxAge(3600L);
                    return config;
                }))
                .authorizeExchange(ex -> ex
                        // Public endpoints (no authentication required)
                        .pathMatchers("/", "/index.html").permitAll()
                        .pathMatchers("/actuator/**").permitAll()  // Allow public access to actuator endpoints
                        .pathMatchers("/api/v1/auth/**").permitAll()
                        
                        // User profile endpoints (require authentication)
                        .pathMatchers("/api/v1/users/me/**").authenticated()
                        
                        // Product service - public read access, protected write access
                        .pathMatchers(HttpMethod.GET, "/api/v1/products/**").permitAll()  // All GET requests to products
                        // Review by product - public
                        .pathMatchers(HttpMethod.GET, "/api/v1/products/reviews/product/**").permitAll()
                        
                        // Protected product endpoints (require authentication for write operations)
                        .pathMatchers(HttpMethod.POST, "/api/v1/products/**").authenticated()
                        .pathMatchers(HttpMethod.PUT, "/api/v1/products/**").authenticated()
                        .pathMatchers(HttpMethod.DELETE, "/api/v1/products/**").authenticated()
                        .pathMatchers(HttpMethod.PATCH, "/api/v1/products/**").authenticated()
                        
                        // Protected endpoints (authentication required)
                        .pathMatchers("/api/v1/users/**").authenticated()  // Admin endpoints - just require authentication
                        .pathMatchers("/api/v1/cart/**").authenticated()
                        .pathMatchers("/api/v1/wishlist/**").authenticated()
                        .pathMatchers("/api/v1/payments/**").authenticated()
                        .pathMatchers("/api/v1/admin/payments/**").authenticated()
                        .pathMatchers("/api/v1/notifications/**").authenticated()
                        .pathMatchers("/api/v1/coupons/**").authenticated()  // Coupon service requires authentication
                        .pathMatchers("/api/v1/orders/**").authenticated()  // Order service requires authentication
                        
                        // Default: require authentication for any other endpoints
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }
}
