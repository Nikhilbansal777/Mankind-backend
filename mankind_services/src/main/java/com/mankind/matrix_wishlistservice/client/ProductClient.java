package com.mankind.matrix_wishlistservice.client;

import com.mankind.api.product.dto.product.ProductResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service", contextId = "wishlistProductClient", url = "${PRODUCT_SERVICE_URL:http://127.0.0.1:8085}", configuration = com.mankind.services.config.MankindFeignDefaults.class)
public interface ProductClient {
    @GetMapping("/products/{id}")
    ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Long id);
}
