package com.mankind.matrix_order_service.exception;

public class PaymentConfigurationException extends RuntimeException {
    
    public PaymentConfigurationException(String message) {
        super(message);
    }
    
    public PaymentConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
