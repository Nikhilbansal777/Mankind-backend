package com.mankind.matrix_payment_service.service;

import com.mankind.matrix_payment_service.dto.CreatePaymentIntentRequest;
import com.mankind.matrix_payment_service.dto.PaymentIntentResponse;
import com.mankind.matrix_payment_service.dto.PaymentVerificationResponse;
import com.mankind.matrix_payment_service.model.Payment;
import com.mankind.matrix_payment_service.model.PaymentProvider;
import com.mankind.matrix_payment_service.model.PaymentStatus;
import com.mankind.matrix_payment_service.repository.PaymentRepository;
import com.mankind.matrix_payment_service.exception.StripePaymentException;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class StripePaymentService {

    private final PaymentRepository paymentRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public PaymentIntentResponse createPaymentIntent(CreatePaymentIntentRequest request) {
        try {
            log.info("Creating Stripe payment intent for user: {}, amount: {} {}", 
                    request.getUserId(), request.getAmount(), request.getCurrency());

            // Convert amount to cents (Stripe expects amounts in smallest currency unit)
            long amountInCents = request.getAmount()
                    .multiply(BigDecimal.valueOf(100))
                    .longValue();

            // Build Stripe payment intent parameters
            PaymentIntentCreateParams.Builder paramsBuilder = PaymentIntentCreateParams.builder()
                    .setAmount(amountInCents)
                    .setCurrency(request.getCurrency().toLowerCase())
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                    .setEnabled(request.getAutomaticPaymentMethods())
                                    .build()
                    );

            if (request.getDescription() != null && !request.getDescription().trim().isEmpty()) {
                paramsBuilder.setDescription(request.getDescription());
            }

            // Add user ID to metadata for tracking
            Map<String, String> metadata = new HashMap<>(request.getMetadata() != null ? request.getMetadata() : new HashMap<>());
            metadata.put("user_id", request.getUserId());
            
            // Set metadata if not empty
            if (!metadata.isEmpty()) {
                metadata.forEach(paramsBuilder::putMetadata);
            }

            // Create payment intent with Stripe
            PaymentIntent paymentIntent = PaymentIntent.create(paramsBuilder.build());

            log.info("Stripe payment intent created: {}", paymentIntent.getId());

            // Save payment record to database
            Payment payment = Payment.builder()
                    .stripePaymentIntentId(paymentIntent.getId())
                    .userId(request.getUserId())
                    .amount(request.getAmount())
                    .currency(request.getCurrency().toUpperCase())
                    .provider(PaymentProvider.STRIPE)
                    .status(mapStripeStatusToPaymentStatus(paymentIntent.getStatus()))
                    .description(request.getDescription())
                    .metadata(convertMetadataToString(metadata))
                    .build();

            Payment savedPayment = paymentRepository.save(payment);
            log.info("Payment record saved to database with ID: {}", savedPayment.getId());

            // Build response
            return PaymentIntentResponse.builder()
                    .id(savedPayment.getId())
                    .stripePaymentIntentId(paymentIntent.getId())
                    .userId(request.getUserId())
                    .amount(request.getAmount())
                    .currency(request.getCurrency().toUpperCase())
                    .provider(PaymentProvider.STRIPE)
                    .status(savedPayment.getStatus())
                    .description(request.getDescription())
                    .clientSecret(paymentIntent.getClientSecret())
                    .createdAt(savedPayment.getCreatedAt())
                    .updatedAt(savedPayment.getUpdatedAt())
                    .build();

        } catch (StripeException e) {
            log.error("Error creating Stripe payment intent: {}", e.getMessage(), e);
            throw new StripePaymentException("Failed to create Stripe payment intent: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error creating Stripe payment intent: {}", e.getMessage(), e);
            throw new StripePaymentException("Failed to create Stripe payment intent", e);
        }
    }

    private PaymentStatus mapStripeStatusToPaymentStatus(String stripeStatus) {
        return switch (stripeStatus) {
            case "requires_payment_method" -> PaymentStatus.REQUIRES_PAYMENT_METHOD;
            case "requires_confirmation" -> PaymentStatus.REQUIRES_CONFIRMATION;
            case "requires_action" -> PaymentStatus.REQUIRES_ACTION;
            case "processing" -> PaymentStatus.PROCESSING;
            case "succeeded" -> PaymentStatus.SUCCEEDED;
            case "canceled" -> PaymentStatus.CANCELED;
            default -> PaymentStatus.PENDING;
        };
    }

    private String convertMetadataToString(Map<String, String> metadata) {
        if (metadata == null || metadata.isEmpty()) {
            return null;
        }
        
        try {
            return objectMapper.writeValueAsString(metadata);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize metadata to JSON: {}", e.getMessage(), e);
            // Fallback to simple string representation if JSON serialization fails
            return metadata.toString();
        }
    }

    public PaymentVerificationResponse verifyPaymentWithStripe(String paymentIntentId) {
        try {
            log.info("Verifying payment with Stripe: {}", paymentIntentId);
            
            // Retrieve payment intent from Stripe
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
            
            // Map Stripe status to our status
            PaymentStatus status = mapStripeStatusToPaymentStatus(paymentIntent.getStatus());
            
            // Update payment in database with latest status
            Payment payment = paymentRepository.findByStripePaymentIntentId(paymentIntentId)
                    .orElseThrow(() -> new StripePaymentException("Payment not found: " + paymentIntentId));
            
            payment.setStatus(status);
            Payment updatedPayment = paymentRepository.save(payment);
            
            log.info("Payment verification completed. Status: {}", status);
            
            return PaymentVerificationResponse.builder()
                    .id(updatedPayment.getId())
                    .stripePaymentIntentId(paymentIntentId)
                    .userId(updatedPayment.getUserId())
                    .amount(updatedPayment.getAmount())
                    .currency(updatedPayment.getCurrency())
                    .provider(PaymentProvider.STRIPE)
                    .status(status)
                    .description(updatedPayment.getDescription())
                    .paymentSucceeded(PaymentStatus.SUCCEEDED.equals(status))
                    .createdAt(updatedPayment.getCreatedAt())
                    .updatedAt(updatedPayment.getUpdatedAt())
                    .build();
                    
        } catch (StripeException e) {
            log.error("Error verifying payment with Stripe: {}", e.getMessage(), e);
            throw new StripePaymentException("Failed to verify payment with Stripe: " + e.getMessage(), e);
        }
    }
}
