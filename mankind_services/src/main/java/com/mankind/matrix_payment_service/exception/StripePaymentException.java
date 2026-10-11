package com.mankind.matrix_payment_service.exception;

import com.stripe.exception.StripeException;

public class StripePaymentException extends RuntimeException {
    
    private final StripeException stripeException;
    
    public StripePaymentException(String message, StripeException stripeException) {
        super(message, stripeException);
        this.stripeException = stripeException;
    }
    
    public StripePaymentException(String message) {
        super(message);
        this.stripeException = null;
    }
    
    public StripeException getStripeException() {
        return stripeException;
    }
    
    public boolean hasStripeException() {
        return stripeException != null;
    }
}
