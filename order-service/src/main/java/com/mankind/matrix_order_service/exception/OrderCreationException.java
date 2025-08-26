package com.mankind.matrix_order_service.exception;

public class OrderCreationException extends RuntimeException {
    
    public OrderCreationException(String message) {
        super(message);
    }
    
    public OrderCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
