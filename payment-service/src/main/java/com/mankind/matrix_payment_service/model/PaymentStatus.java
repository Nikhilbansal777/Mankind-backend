package com.mankind.matrix_payment_service.model;

public enum PaymentStatus {
    PENDING,        // Payment intent created, waiting for confirmation
    REQUIRES_PAYMENT_METHOD,  // Payment failed, requires new payment method
    REQUIRES_CONFIRMATION,    // Payment requires confirmation (3D Secure, etc.)
    REQUIRES_ACTION,          // Payment requires additional action
    PROCESSING,     // Payment is being processed
    SUCCEEDED,      // Payment completed successfully
    CANCELED,       // Payment was canceled
    FAILED          // Payment failed
}
