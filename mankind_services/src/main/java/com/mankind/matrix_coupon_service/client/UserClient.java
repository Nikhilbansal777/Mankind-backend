package com.mankind.matrix_coupon_service.client;

import com.mankind.api.user.dto.UserDTO;
import com.mankind.services.config.MankindFeignDefaults;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
    name = "user-service",
    contextId = "couponUserClient",
    url = "${USER_SERVICE_URL:http://127.0.0.1:8085}",
    configuration = MankindFeignDefaults.class
)
public interface UserClient {
    @GetMapping("/users/me")
    UserDTO getCurrentUser();
}