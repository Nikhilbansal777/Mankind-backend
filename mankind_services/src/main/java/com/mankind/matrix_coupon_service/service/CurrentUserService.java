package com.mankind.matrix_coupon_service.service;

import com.mankind.api.user.dto.UserDTO;
import com.mankind.matrix_coupon_service.client.UserClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrentUserService {
    private final UserClient userClient;

    public UserDTO getCurrentUser() {
        return userClient.getCurrentUser();
    }

    public Long getCurrentUserId() {
        UserDTO currentUser = getCurrentUser();
        if (currentUser == null) {
            log.error("Current user not found - user is null");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }
        return currentUser.getId();
    }
} 