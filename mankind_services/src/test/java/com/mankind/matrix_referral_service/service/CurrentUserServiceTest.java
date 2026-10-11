package com.mankind.matrix_referral_service.service;

import com.mankind.api.user.dto.UserDTO;
import com.mankind.matrix_referral_service.client.UserClient;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

class CurrentUserServiceTest {

    @Test
    void getCurrentUserId_returnsIdWhenUserExists() {
        UserClient client = () -> {
            UserDTO user = new UserDTO();
            user.setId(123L);
            return user;
        };

        CurrentUserService service = new CurrentUserService(client);

        assertEquals(123L, service.getCurrentUserId());
    }

    @Test
    void getCurrentUserId_throwsUnauthorizedWhenUserIsNull() {
        CurrentUserService service = new CurrentUserService(() -> null);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, service::getCurrentUserId);

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
    }

    @Test
    void getCurrentUserId_throwsUnauthorizedWhenUserIdIsNull() {
        UserClient client = UserDTO::new;
        CurrentUserService service = new CurrentUserService(client);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, service::getCurrentUserId);

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
    }
}
