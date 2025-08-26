package com.mankind.matrix_order_service.exception;

public class CartValidationException extends RuntimeException {
    
    public CartValidationException(String message) {
        super(message);
    }
    
    public CartValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
