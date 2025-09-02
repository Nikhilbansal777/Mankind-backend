package com.mankind.matrix_payment_service.exception;

public class ProviderNotImplementedException extends RuntimeException {
    public ProviderNotImplementedException(String message) {
        super(message);
    }
}
