package com.mankind.matrix_order_service.client;

import com.mankind.api.payment.dto.CreatePaymentIntentRequest;
import com.mankind.api.payment.dto.PaymentIntentResponse;
import com.mankind.api.payment.dto.PaymentVerificationRequest;
import com.mankind.api.payment.dto.PaymentVerificationResponse;
import com.mankind.services.config.MankindFeignDefaults;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "payment-service",
    contextId = "orderPaymentClient",
    url = "${PAYMENT_SERVICE_URL:http://127.0.0.1:8085}",
    configuration = MankindFeignDefaults.class
)
public interface PaymentClient {
    
    @PostMapping("/payments/intents")
    PaymentIntentResponse createPaymentIntent(@RequestBody CreatePaymentIntentRequest request);

    @PostMapping("/payments/verify")
    PaymentVerificationResponse verifyPayment(@RequestBody PaymentVerificationRequest request);
}
