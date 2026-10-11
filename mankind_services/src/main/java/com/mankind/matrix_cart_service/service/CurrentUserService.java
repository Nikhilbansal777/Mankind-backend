package com.mankind.matrix_cart_service.service;

import com.mankind.api.user.dto.UserDTO;
import com.mankind.matrix_cart_service.client.UserClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrentUserService {
    private final UserClient userClient;

    public UserDTO getCurrentUser() {
        return userClient.getCurrentUser();
    }

    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }
} 