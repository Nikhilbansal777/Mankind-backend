package com.mankind.matrix_payment_service.service;

import com.mankind.matrix_payment_service.dto.CreatePaymentIntentRequest;
import com.mankind.matrix_payment_service.dto.PaymentIntentResponse;
import com.mankind.matrix_payment_service.dto.PaymentVerificationRequest;
import com.mankind.matrix_payment_service.dto.PaymentVerificationResponse;
import com.mankind.matrix_payment_service.model.Payment;
import com.mankind.matrix_payment_service.model.PaymentProvider;
import com.mankind.matrix_payment_service.model.PaymentStatus;
import com.mankind.matrix_payment_service.repository.PaymentRepository;
import com.mankind.matrix_payment_service.exception.ProviderNotImplementedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final StripePaymentService stripePaymentService;

    @Transactional
    public PaymentIntentResponse createPaymentIntent(CreatePaymentIntentRequest request) {
        log.info("Creating payment intent for provider: {}, user: {}, amount: {} {}",
                request.getProvider(), request.getUserId(), request.getAmount(), request.getCurrency());

        return switch (request.getProvider()) {
            case STRIPE -> stripePaymentService.createPaymentIntent(request);
            default -> throw new ProviderNotImplementedException(
                    "Payment provider not implemented: " + request.getProvider());
        };
    }



    public PaymentIntentResponse getPaymentIntent(String stripePaymentIntentId) {
        Payment payment = paymentRepository.findByStripePaymentIntentId(stripePaymentIntentId)
                .orElseThrow(() -> new RuntimeException("Payment not found: " + stripePaymentIntentId));

        return PaymentIntentResponse.builder()
                .id(payment.getId())
                .stripePaymentIntentId(payment.getStripePaymentIntentId())
                .userId(payment.getUserId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .provider(payment.getProvider())
                .status(payment.getStatus())
                .description(payment.getDescription())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }

    public PaymentVerificationResponse verifyPayment(PaymentVerificationRequest request) {
        log.info("Verifying payment for order: {}, payment intent: {}", 
                request.getOrderId(), request.getPaymentIntentId());

        // Find payment by payment intent ID
        Payment payment = paymentRepository.findByStripePaymentIntentId(request.getPaymentIntentId())
                .orElseThrow(() -> new RuntimeException("Payment not found: " + request.getPaymentIntentId()));

        // Verify payment based on provider
        PaymentVerificationResponse response = switch (payment.getProvider()) {
            case STRIPE -> stripePaymentService.verifyPaymentWithStripe(request.getPaymentIntentId());
            default -> throw new ProviderNotImplementedException(
                    "Payment provider not supported for verification: " + payment.getProvider());
        };

        // Create a new response with the orderId included
        PaymentVerificationResponse finalResponse = PaymentVerificationResponse.builder()
                .id(response.getId())
                .stripePaymentIntentId(response.getStripePaymentIntentId())
                .userId(response.getUserId())
                .amount(response.getAmount())
                .currency(response.getCurrency())
                .provider(response.getProvider())
                .status(response.getStatus())
                .description(response.getDescription())
                .paymentSucceeded(response.isPaymentSucceeded())
                .orderId(request.getOrderId())
                .createdAt(response.getCreatedAt())
                .updatedAt(response.getUpdatedAt())
                .build();

        log.info("Payment verification completed for order {}: {}", request.getOrderId(), 
                finalResponse.isPaymentSucceeded() ? "SUCCESS" : "FAILED");

        return finalResponse;
    }
}
