package com.mankind.matrix_payment_service.controller;

import com.mankind.matrix_payment_service.dto.CreatePaymentIntentRequest;
import com.mankind.matrix_payment_service.dto.PaymentIntentResponse;
import com.mankind.matrix_payment_service.dto.PaymentVerificationRequest;
import com.mankind.matrix_payment_service.dto.PaymentVerificationResponse;
import com.mankind.matrix_payment_service.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Payment", description = "Payment management endpoints")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/intents")
    @Operation(
        summary = "Create a new payment intent",
        description = "Creates a Stripe payment intent and returns the client secret for frontend processing"
    )
    public ResponseEntity<PaymentIntentResponse> createPaymentIntent(
            @Valid @RequestBody CreatePaymentIntentRequest request) {
        
        log.info("Received payment intent creation request for user: {}", request.getUserId());
        
        PaymentIntentResponse response = paymentService.createPaymentIntent(request);
        
        log.info("Payment intent created successfully: {}", response.getStripePaymentIntentId());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/intents/{stripePaymentIntentId}")
    @Operation(
        summary = "Get payment intent details",
        description = "Retrieves payment intent details by Stripe payment intent ID"
    )
    public ResponseEntity<PaymentIntentResponse> getPaymentIntent(
            @PathVariable String stripePaymentIntentId) {
        
        log.info("Retrieving payment intent: {}", stripePaymentIntentId);
        
        PaymentIntentResponse response = paymentService.getPaymentIntent(stripePaymentIntentId);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify")
    @Operation(
        summary = "Verify payment",
        description = "Verifies if a payment intent has succeeded"
    )
    public ResponseEntity<PaymentVerificationResponse> verifyPayment(
            @Valid @RequestBody PaymentVerificationRequest request) {
        
        log.info("Verifying payment for order: {}, payment intent: {}", 
                request.getOrderId(), request.getPaymentIntentId());
        
        PaymentVerificationResponse response = paymentService.verifyPayment(request);
        
        return ResponseEntity.ok(response);
    }
}
