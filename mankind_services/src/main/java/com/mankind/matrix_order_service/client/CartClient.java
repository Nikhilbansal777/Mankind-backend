package com.mankind.matrix_order_service.client;

import com.mankind.api.cart.dto.CartResponseDTO;
import com.mankind.services.config.MankindFeignDefaults;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(
    name = "cart-service",
    contextId = "orderCartClient",
    url = "${CART_SERVICE_URL:http://127.0.0.1:8085}",
    configuration = MankindFeignDefaults.class
)
public interface CartClient {
    @GetMapping("/cart")
    CartResponseDTO getCurrentUserCart();

    @PostMapping("/cart/convert")
    CartResponseDTO markCartAsConverted(@org.springframework.web.bind.annotation.RequestParam("orderId") Long orderId);
}
