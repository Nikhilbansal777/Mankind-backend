package com.mankind.matrix_order_service.exception;

public class CouponValidationException extends RuntimeException {
    
    public CouponValidationException(String message) {
        super(message);
    }
    
    public CouponValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
