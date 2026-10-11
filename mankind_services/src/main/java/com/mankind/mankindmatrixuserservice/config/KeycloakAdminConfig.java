package com.mankind.mankindmatrixuserservice.config;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakAdminConfig {

    @Value("${keycloak.auth-server-url}")
    private String serverUrl;
    @Value("${keycloak.realm}")
    private String appRealm;
    @Value("${keycloak.admin-realm}")
    private String adminRealm;
    @Value("${keycloak.admin-user}")
    private String username;
    @Value("${keycloak.admin-password}")
    private String password;
    @Value("${keycloak.admin-client-id}")
    private String adminClientId;
    @Value("${keycloak.admin-client-secret:}")
    private String clientSecret;

    @Bean
    public Keycloak keycloakAdminClient() {
        KeycloakBuilder builder = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(adminRealm)
                .grantType(OAuth2Constants.PASSWORD)
                .clientId(adminClientId)
                .username(username)
                .password(password);

        if (!clientSecret.isBlank()) {
            builder.clientSecret(clientSecret);
        }

        return builder.build();
    }

}
