// COMMENTED OUT: UserClient is disabled, all endpoints are open.
/*
package com.mankind.matrix_coupon_service.client;

import com.mankind.api.user.dto.UserDTO;
import com.mankind.matrix_coupon_service.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
    name = "user-service", 
    url = "${USER_SERVICE_URL:http://localhost:8081}",
    configuration = FeignConfig.class
)
public interface UserClient {
    @GetMapping("/users/me")
    UserDTO getCurrentUser();
}
*/