package com.mankind.matrix_order_service.service;

import com.mankind.matrix_order_service.dto.CreateOrderRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderServiceTest {

    @Test
    void createOrderRequest_Validation() {
        // Test basic DTO validation
        CreateOrderRequest request = new CreateOrderRequest();
        request.setShippingAddressId(1L);
        request.setCouponCode("SAVE20");
        request.setNotes("Test order");

        assertEquals(1L, request.getShippingAddressId());
        assertEquals("SAVE20", request.getCouponCode());
        assertEquals("Test order", request.getNotes());
    }

    @Test
    void createOrderRequest_OptionalFields() {
        // Test DTO with optional fields
        CreateOrderRequest request = new CreateOrderRequest();
        request.setShippingAddressId(1L);
        // couponCode and notes are optional

        assertEquals(1L, request.getShippingAddressId());
        assertNull(request.getCouponCode());
        assertNull(request.getNotes());
    }

    @Test
    void createOrderRequest_DefaultValues() {
        // Test DTO default values
        CreateOrderRequest request = new CreateOrderRequest();
        
        assertNull(request.getShippingAddressId());
        assertNull(request.getCouponCode());
        assertNull(request.getNotes());
    }
}
