package com.mankind.matrix_product_service.client;

import com.mankind.api.user.dto.UserDTO;
import com.mankind.services.config.MankindFeignDefaults;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(
    name = "user-service",
    contextId = "productUserClient",
    url = "${USER_SERVICE_URL:http://127.0.0.1:8085}",
    configuration = MankindFeignDefaults.class
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
