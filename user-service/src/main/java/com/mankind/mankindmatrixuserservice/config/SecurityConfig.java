package com.mankind.mankindmatrixuserservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        
                        // User management - users can only access their own data
                        .requestMatchers(HttpMethod.GET, "/users/me/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/users/me/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/users/me/addresses/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/users/me/addresses/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/users/me/addresses/**").authenticated()
                        
                        // Batch user lookup - require authentication (not admin)
                        .requestMatchers(HttpMethod.GET, "/users/batch").authenticated()
                        
                        // Admin endpoints: require authentication;
                        .requestMatchers("/users/**").authenticated()
                        
                        // Default: require authentication
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(Customizer.withDefaults())
                );

        return http.build();
    }
}
