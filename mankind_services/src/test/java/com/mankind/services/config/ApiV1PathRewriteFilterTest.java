package com.mankind.services.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ApiV1PathRewriteFilterTest {

    @Test
    void rewritesAuthLoginPath() {
        assertEquals("/auth/login", ApiV1PathRewriteFilter.rewrite("/api/v1/auth/login"));
    }

    @Test
    void rewritesProductsRootAndById() {
        assertEquals("/products", ApiV1PathRewriteFilter.rewrite("/api/v1/products"));
        assertEquals("/products/1", ApiV1PathRewriteFilter.rewrite("/api/v1/products/1"));
    }

    @Test
    void rewritesCartPaths() {
        assertEquals("/cart", ApiV1PathRewriteFilter.rewrite("/api/v1/cart"));
        assertEquals("/cart/items", ApiV1PathRewriteFilter.rewrite("/api/v1/cart/items"));
    }

    @Test
    void rewritesAdminOrdersPath() {
        assertEquals("/orders/42", ApiV1PathRewriteFilter.rewrite("/api/v1/admin/orders/42"));
    }
}
