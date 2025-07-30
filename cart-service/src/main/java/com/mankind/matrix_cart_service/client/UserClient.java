package com.mankind.matrix_cart_service.client;

import com.mankind.api.user.dto.UserDTO;
import com.mankind.matrix_cart_service.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@FeignClient(
    name = "user-service",
    url = "${USER_SERVICE_URL:http://localhost:8081}",
    configuration = FeignConfig.class
)
public interface UserClient {
    @GetMapping("/users/{id}")
    UserDTO getUserById(@PathVariable("id") Long id);

    @GetMapping("/users")
    List<UserDTO> getAllUsers();

    @GetMapping("/users/me")
    UserDTO getCurrentUser();

    @GetMapping("/users/batch")
    List<UserDTO> getUsersByIds(@RequestParam("ids") List<Long> userIds);
} 