package com.mankind.mankindmatrixuserservice.service;

import com.mankind.mankindmatrixuserservice.model.User;
import com.mankind.api.user.enums.Role;
import com.mankind.mankindmatrixuserservice.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserContextService {

    private final UserRepository userRepository;

    public UserContextService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Get the current authenticated user from the JWT token
     */
    public Optional<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            String keycloakId = jwt.getSubject(); // sub claim
            if (keycloakId != null) {
                return userRepository.findByKeycloakId(keycloakId);
            }
        }
        
        return Optional.empty();
    }

    /**
     * Get the current user ID from the JWT token
     */
    public Optional<Long> getCurrentUserId() {
        return getCurrentUser().map(User::getId);
    }

    /**
     * Get the current username from the JWT token
     */
    public Optional<String> getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            return Optional.ofNullable(jwt.getSubject());
        }
        
        return Optional.empty();
    }

    /**
     * Check if the current user is an admin
     */
    public boolean isAdmin() {
        return getCurrentUser()
                .map(User::getRole)
                .map(r -> r == Role.ADMIN)
                .orElse(false);
    }

    /**
     * Check if the current user can access a specific user's data
     */
    public boolean canAccessUser(Long userId) {
        Optional<User> currentUser = getCurrentUser();
        if (currentUser.isEmpty()) {
            return false;
        }
        
        // Admin can access any user's data
        if (isAdmin()) {
            return true;
        }
        
        // User can only access their own data
        return currentUser.get().getId().equals(userId);
    }
} 