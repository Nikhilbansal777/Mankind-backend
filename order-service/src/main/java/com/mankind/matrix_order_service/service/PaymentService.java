package com.mankind.matrix_order_service.service;

import com.mankind.api.payment.dto.CreatePaymentIntentRequest;
import com.mankind.api.payment.dto.PaymentIntentResponse;
import com.mankind.api.payment.model.PaymentProvider;
import com.mankind.matrix_order_service.client.PaymentClient;
import com.mankind.matrix_order_service.dto.PaymentIntentRequest;
import com.mankind.matrix_order_service.dto.OrderPaymentIntentResponse;
import com.mankind.matrix_order_service.exception.PaymentServiceException;
import com.mankind.matrix_order_service.exception.PaymentConfigurationException;
import com.mankind.matrix_order_service.model.Order;
import com.mankind.matrix_order_service.model.OrderStatusHistory;
import com.mankind.matrix_order_service.repository.OrderStatusHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentClient paymentClient;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;

    /**
     * Creates a payment intent for an order to initiate payment processing.
     * 
     * @param order The order to create payment intent for
     * @param request The payment intent request containing the payment provider
     * @param userId The current user ID from the JWT token
     * @return OrderPaymentIntentResponse containing the payment intent details with provider information
     * @throws PaymentServiceException if payment intent creation fails
     */
    public OrderPaymentIntentResponse createPaymentIntent(Order order, PaymentIntentRequest request, Long userId) {
        log.info("Creating payment intent for order: {} with provider: {}", order.getId(), request.getProvider());
        
        try {
            // Create payment intent request for payment service
            CreatePaymentIntentRequest paymentRequest = CreatePaymentIntentRequest.builder()
                    .userId(userId.toString())
                    .amount(order.getTotal())
                    .currency("USD")
                    .description("")
                    .metadata(createPaymentMetadata(order))
                    .provider(request.getProvider())
                    .paymentMethodTypes("card")
                    .automaticPaymentMethods(true)
                    .build();
            
            log.info("Sending payment intent request to payment service: userId={}, amount={}, provider={}", 
                    paymentRequest.getUserId(), paymentRequest.getAmount(), paymentRequest.getProvider());
            
            // Call payment service to create payment intent
            PaymentIntentResponse paymentIntent = paymentClient.createPaymentIntent(paymentRequest);
            
            log.info("Payment intent created successfully: {} for order {}", 
                    paymentIntent.getStripePaymentIntentId(), order.getId());
            
            // Create status history for payment intent creation
            createOrderStatusHistory(order, "Payment intent created with provider: " + request.getProvider());
            
            // Return enhanced response with provider information
            return OrderPaymentIntentResponse.from(paymentIntent, request.getProvider());
            
        } catch (Exception e) {
            log.error("Failed to create payment intent for order {}: {}", order.getId(), e.getMessage(), e);
            
            // Enhanced error handling with user-friendly messages
            String userMessage = createUserFriendlyErrorMessage(e, request.getProvider());
            throw new PaymentServiceException(userMessage, e);
        }
    }

    /**
     * Creates user-friendly error messages based on the exception type and provider.
     * 
     * @param e The exception that occurred
     * @param provider The payment provider being used
     * @return User-friendly error message
     */
    private String createUserFriendlyErrorMessage(Exception e, PaymentProvider provider) {
        String errorMessage = e.getMessage();
        
        // Handle Stripe-specific errors
        if (errorMessage != null && errorMessage.contains("Invalid API Key")) {
            throw new PaymentConfigurationException(
                String.format("Payment service configuration issue detected. Please contact support. (Provider: %s)", provider)
            );
        }
        
        // Handle connection errors
        if (errorMessage != null && (errorMessage.contains("Connection refused") || errorMessage.contains("ConnectException"))) {
            return String.format("Unable to connect to payment service. Please try again later. (Provider: %s)", provider);
        }
        
        // Handle timeout errors
        if (errorMessage != null && (errorMessage.contains("timeout") || errorMessage.contains("Read timed out"))) {
            return String.format("Payment service is taking longer than expected. Please try again. (Provider: %s)", provider);
        }
        
        // Handle provider-specific errors
        if (errorMessage != null && errorMessage.contains("Provider not implemented")) {
            return String.format("Payment method '%s' is not currently supported. Please try a different payment method.", provider);
        }
        
        // Handle general payment service errors
        if (errorMessage != null && (errorMessage.contains("500") || errorMessage.contains("Internal Server Error"))) {
            return String.format("Payment service is experiencing technical difficulties. Please try again later. (Provider: %s)", provider);
        }
        
        // Default user-friendly message
        return String.format("Unable to process payment at this time. Please try again later or contact support. (Provider: %s)", provider);
    }

    /**
     * Creates payment metadata for the payment service request.
     * 
     * @param order The order to create metadata for
     * @return Map containing payment metadata
     */
    private Map<String, String> createPaymentMetadata(Order order) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("order_id", order.getId().toString());
        metadata.put("order_number", order.getOrderNumber());
        metadata.put("user_id", order.getUserId());
        metadata.put("cart_id", order.getCartId().toString());
        
        if (order.getCouponCode() != null && !order.getCouponCode().isEmpty()) {
            metadata.put("coupon_code", order.getCouponCode());
        }
        
        return metadata;
    }

    /**
     * Creates order status history entry for payment operations.
     * 
     * @param order The order to create history for
     * @param statusNote The status note to record
     */
    private void createOrderStatusHistory(Order order, String statusNote) {
        try {
            OrderStatusHistory statusHistory = OrderStatusHistory.builder()
                    .order(order)
                    .status(order.getStatus())
                    .paymentStatus(order.getPaymentStatus())
                    .notes(statusNote)
                    .createdBy("system")
                    .build();
            
            orderStatusHistoryRepository.save(statusHistory);
            log.debug("Order status history created for order {}: {}", order.getId(), statusNote);
            
        } catch (Exception e) {
            log.warn("Failed to create order status history for order {}: {}", order.getId(), e.getMessage());
        }
    }
}
