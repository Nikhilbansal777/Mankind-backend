package com.mankind.matrix_order_service.client;

import com.mankind.api.user.dto.UserDTO;
import com.mankind.api.user.dto.AddressDTO;
import com.mankind.services.config.MankindFeignDefaults;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "user-service",
    contextId = "orderUserClient",
    url = "${USER_SERVICE_URL:http://127.0.0.1:8085}",
    configuration = MankindFeignDefaults.class
)
public interface UserClient {
    @GetMapping("/users/{id}")
    UserDTO getUserById(@PathVariable("id") Long id);

    @GetMapping("/users/me")
    UserDTO getCurrentUser();

    @GetMapping("/users/me/addresses/{addressId}")
    AddressDTO getAddressById(@PathVariable("addressId") Long addressId);
}
