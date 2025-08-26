package com.mankind.matrix_order_service.service;

import com.mankind.api.user.dto.UserDTO;
import com.mankind.matrix_order_service.client.UserClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrentUserService {
    private final UserClient userClient;

    public UserDTO getCurrentUser() {
        try {
            log.debug("Attempting to get current user from user service");
            UserDTO user = userClient.getCurrentUser();
            if (user == null) {
                log.error("User service returned null user");
                throw new RuntimeException("User service returned null user - authentication may have failed");
            }
            log.debug("Successfully retrieved current user: {}", user.getId());
            return user;
        } catch (Exception e) {
            log.error("Failed to get current user: {}", e.getMessage(), e);
            
            // Provide more specific error messages
            if (e.getMessage().contains("401") || e.getMessage().contains("Unauthorized")) {
                throw new RuntimeException("Authentication failed - please provide a valid JWT token", e);
            } else if (e.getMessage().contains("404") || e.getMessage().contains("Not Found")) {
                throw new RuntimeException("User service endpoint not found - user service may not be running", e);
            } else if (e.getMessage().contains("Connection refused") || e.getMessage().contains("ConnectException")) {
                throw new RuntimeException("Cannot connect to user service - service may not be running on port 8081", e);
            } else {
                throw new RuntimeException("Failed to get current user: " + e.getMessage(), e);
            }
        }
    }

    public Long getCurrentUserId() {
        UserDTO user = getCurrentUser();
        return user.getId();
    }
}
