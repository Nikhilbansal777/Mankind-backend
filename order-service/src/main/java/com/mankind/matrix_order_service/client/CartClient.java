package com.mankind.matrix_order_service.client;

import com.mankind.api.cart.dto.CartResponseDTO;
import com.mankind.matrix_order_service.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(
    name = "cart-service",
    url = "${CART_SERVICE_URL:http://localhost:8082}",
    configuration = FeignConfig.class
)
public interface CartClient {
    @GetMapping("/cart")
    CartResponseDTO getCurrentUserCart();

    @PostMapping("/cart/convert")
    CartResponseDTO markCartAsConverted(@org.springframework.web.bind.annotation.RequestParam("orderId") Long orderId);
}
