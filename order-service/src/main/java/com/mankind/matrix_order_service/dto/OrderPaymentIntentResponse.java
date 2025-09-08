package com.mankind.matrix_order_service.dto;

import com.mankind.api.payment.dto.PaymentIntentResponse;
import com.mankind.api.payment.model.PaymentProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderPaymentIntentResponse {

    private Long id;
    private String stripePaymentIntentId;
    private String userId;
    private String amount;
    private String currency;
    private String status;
    private String description;
    private String clientSecret;
    private String createdAt;
    private String updatedAt;
    
    // Additional fields specific to order-service
    private PaymentProvider provider;
    private String providerName;

    /**
     * Creates an OrderPaymentIntentResponse from a PaymentIntentResponse and provider
     */
    public static OrderPaymentIntentResponse from(PaymentIntentResponse paymentResponse, PaymentProvider provider) {
        return OrderPaymentIntentResponse.builder()
                .id(paymentResponse.getId())
                .stripePaymentIntentId(paymentResponse.getStripePaymentIntentId())
                .userId(paymentResponse.getUserId())
                .amount(paymentResponse.getAmount() != null ? paymentResponse.getAmount().toString() : null)
                .currency(paymentResponse.getCurrency())
                .status(paymentResponse.getStatus() != null ? paymentResponse.getStatus().name() : null)
                .description(paymentResponse.getDescription())
                .clientSecret(paymentResponse.getClientSecret())
                .createdAt(paymentResponse.getCreatedAt() != null ? paymentResponse.getCreatedAt().toString() : null)
                .updatedAt(paymentResponse.getUpdatedAt() != null ? paymentResponse.getUpdatedAt().toString() : null)
                .provider(provider)
                .providerName(provider != null ? provider.name() : null)
                .build();
    }
}
