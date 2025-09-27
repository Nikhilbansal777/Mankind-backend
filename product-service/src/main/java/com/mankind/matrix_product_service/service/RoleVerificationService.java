package com.mankind.matrix_product_service.service;

import com.mankind.api.user.dto.UserDTO;
import com.mankind.api.user.enums.Role;
import com.mankind.matrix_product_service.client.UserClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleVerificationService {

    private final UserClient userClient;

    /**
     * Verify if the current user has ADMIN or SUPER_ADMIN role
     * @throws ResponseStatusException if user doesn't have required role
     */
    public void verifyAdminOrSuperAdminRole() {
        try {
            UserDTO currentUser = userClient.getCurrentUser();

            if (currentUser == null) {
                log.error("Current user is null - admin verification failed");
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin access required - user not found");
            }

            if (currentUser.getRole() == null) {
                log.error("User {} has null role - admin verification failed", currentUser.getUsername());
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin access required - user role is null");
            }

            // Check if user has admin role
            boolean hasAdminRole = (currentUser.getRole() == Role.ADMIN || currentUser.getRole() == Role.SUPER_ADMIN);

            if (!hasAdminRole) {
                log.warn("User {} does not have ADMIN or SUPER_ADMIN role. Current role: {}", 
                        currentUser.getUsername(), currentUser.getRole());
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                    String.format("Admin access required. Current role: %s", currentUser.getRole()));
            }

            log.debug("Admin/Super Admin role verification successful for user: {} with role: {}", 
                    currentUser.getUsername(), currentUser.getRole());
        } catch (ResponseStatusException e) {
            // Re-throw ResponseStatusException as-is
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error verifying admin role: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unable to verify admin role: " + e.getMessage());
        }
    }

    /**
     * Verify if the current user has a specific role
     * @param requiredRole the role that the user must have
     * @throws ResponseStatusException if user doesn't have required role
     */
    public void verifyRole(Role requiredRole) {
        try {
            UserDTO currentUser = userClient.getCurrentUser();

            if (currentUser == null) {
                log.error("Current user is null - role verification failed");
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access required - user not found");
            }

            if (currentUser.getRole() == null) {
                log.error("User {} has null role - role verification failed", currentUser.getUsername());
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access required - user role is null");
            }

            if (currentUser.getRole() != requiredRole) {
                log.warn("User {} does not have required role {}. Current role: {}", 
                        currentUser.getUsername(), requiredRole, currentUser.getRole());
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                    String.format("Access denied. Required role: %s, Current role: %s", requiredRole, currentUser.getRole()));
            }

            log.debug("Role verification successful for user: {} with role: {}", 
                    currentUser.getUsername(), currentUser.getRole());
        } catch (ResponseStatusException e) {
            // Re-throw ResponseStatusException as-is
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error verifying role: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unable to verify role: " + e.getMessage());
        }
    }

    /**
     * Verify if the current user has any of the specified roles
     * @param requiredRoles the roles that the user must have (any of them)
     * @throws ResponseStatusException if user doesn't have any of the required roles
     */
    public void verifyAnyRole(Role... requiredRoles) {
        try {
            UserDTO currentUser = userClient.getCurrentUser();

            if (currentUser == null) {
                log.error("Current user is null - role verification failed");
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access required - user not found");
            }

            if (currentUser.getRole() == null) {
                log.error("User {} has null role - role verification failed", currentUser.getUsername());
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access required - user role is null");
            }

            boolean hasRequiredRole = false;
            for (Role role : requiredRoles) {
                if (currentUser.getRole() == role) {
                    hasRequiredRole = true;
                    break;
                }
            }

            if (!hasRequiredRole) {
                log.warn("User {} does not have any of the required roles. Current role: {}, Required roles: {}", 
                        currentUser.getUsername(), currentUser.getRole(), java.util.Arrays.toString(requiredRoles));
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                    String.format("Access denied. Required roles: %s, Current role: %s", 
                        java.util.Arrays.toString(requiredRoles), currentUser.getRole()));
            }

            log.debug("Role verification successful for user: {} with role: {}", 
                    currentUser.getUsername(), currentUser.getRole());
        } catch (ResponseStatusException e) {
            // Re-throw ResponseStatusException as-is
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error verifying roles: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unable to verify roles: " + e.getMessage());
        }
    }

    /**
     * Verify if the current user has CORPORATE role
     * @throws ResponseStatusException if user doesn't have CORPORATE role
     */
    public void verifyCorporateRole() {
        try {
            UserDTO currentUser = userClient.getCurrentUser();

            if (currentUser == null) {
                log.error("Current user is null - corporate verification failed");
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Corporate access required - user not found");
            }

            if (currentUser.getRole() == null) {
                log.error("User {} has null role - corporate verification failed", currentUser.getUsername());
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Corporate access required - user role is null");
            }

            // Check if user has corporate role
            boolean hasCorporateRole = (currentUser.getRole() == Role.CORPORATE);

            if (!hasCorporateRole) {
                log.warn("User {} does not have CORPORATE role. Current role: {}", 
                        currentUser.getUsername(), currentUser.getRole());
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                    String.format("Corporate access required. Current role: %s", currentUser.getRole()));
            }

            log.debug("Corporate role verification successful for user: {} with role: {}", 
                    currentUser.getUsername(), currentUser.getRole());
        } catch (ResponseStatusException e) {
            // Re-throw ResponseStatusException as-is
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error verifying corporate role: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unable to verify corporate role: " + e.getMessage());
        }
    }

    /**
     * Get the current user if they have CORPORATE role
     * @return the current user with CORPORATE role
     * @throws ResponseStatusException if user doesn't have CORPORATE role
     */
    public UserDTO getCurrentCorporateUser() {
        try {
            UserDTO currentUser = userClient.getCurrentUser();

            if (currentUser == null) {
                log.error("Current user is null - corporate verification failed");
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Corporate access required - user not found");
            }

            if (currentUser.getRole() == null) {
                log.error("User {} has null role - corporate verification failed", currentUser.getUsername());
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Corporate access required - user role is null");
            }

            // Check if user has corporate role
            boolean hasCorporateRole = (currentUser.getRole() == Role.CORPORATE);

            if (!hasCorporateRole) {
                log.warn("User {} does not have CORPORATE role. Current role: {}", 
                        currentUser.getUsername(), currentUser.getRole());
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                    String.format("Corporate access required. Current role: %s", currentUser.getRole()));
            }

            log.debug("Corporate role verification successful for user: {} with role: {}", 
                    currentUser.getUsername(), currentUser.getRole());

            return currentUser;
        } catch (ResponseStatusException e) {
            // Re-throw ResponseStatusException as-is
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error verifying corporate role: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unable to verify corporate role: " + e.getMessage());
        }
    }
} 
