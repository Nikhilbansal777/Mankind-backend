package com.mankind.mankindmatrixuserservice.service;

import com.mankind.mankindmatrixuserservice.model.TokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class KeycloakTokenService {

    private final WebClient webClient;

    @Value("${keycloak.auth-server-url}")
    private String serverUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    public KeycloakTokenService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public Mono<TokenResponse> getToken(String username, String password) {
        String tokenEndpoint = serverUrl
            + "/realms/" + realm
            + "/protocol/openid-connect/token";

        var formData = BodyInserters
            .fromFormData("grant_type", "password")
            .with("client_id", clientId)
            .with("username", username)
            .with("password", password);

        // Only add client_secret if it's not empty (for confidential clients)
        if (clientSecret != null && !clientSecret.trim().isEmpty()) {
            formData = formData.with("client_secret", clientSecret);
        }

        return webClient.post()
            .uri(tokenEndpoint)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(formData)
            .retrieve()
            .bodyToMono(TokenResponse.class);
    }

    public Mono<Void> revokeRefreshToken(String refreshToken) {
        String revokeEndpoint = serverUrl
            + "/realms/" + realm
            + "/protocol/openid-connect/revoke";

        var formData = BodyInserters
            .fromFormData("token", refreshToken)
            .with("client_id", clientId);

        // Only add client_secret if it's not empty (for confidential clients)
        if (clientSecret != null && !clientSecret.trim().isEmpty()) {
            formData = formData.with("client_secret", clientSecret);
        }

        return webClient.post()
            .uri(revokeEndpoint)
            .body(formData)
            .retrieve()
            .toBodilessEntity()
            .then();
    }
}
