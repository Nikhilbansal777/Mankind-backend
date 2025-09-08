package com.mankind.matrix_order_service.client;

import com.mankind.api.payment.dto.CreatePaymentIntentRequest;
import com.mankind.api.payment.dto.PaymentIntentResponse;
import com.mankind.api.payment.dto.PaymentVerificationRequest;
import com.mankind.api.payment.dto.PaymentVerificationResponse;
import com.mankind.matrix_order_service.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "payment-service",
    url = "${PAYMENT_SERVICE_URL:http://localhost:8084}",
    configuration = FeignConfig.class
)
public interface PaymentClient {
    
    @PostMapping("/payments/intents")
    PaymentIntentResponse createPaymentIntent(@RequestBody CreatePaymentIntentRequest request);

    @PostMapping("/payments/verify")
    PaymentVerificationResponse verifyPayment(@RequestBody PaymentVerificationRequest request);
}
