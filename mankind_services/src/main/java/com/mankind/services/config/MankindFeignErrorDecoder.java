package com.mankind.services.config;

import com.mankind.matrix_order_service.exception.AccessDeniedException;
import com.mankind.matrix_order_service.exception.CartValidationException;
import com.mankind.matrix_order_service.exception.CouponValidationException;
import com.mankind.matrix_order_service.exception.PaymentServiceException;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

/**
 * Maps Feign HTTP errors to domain exceptions (used for in-process calls between packages).
 */
@Slf4j
public class MankindFeignErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, feign.Response response) {
        String errorMessage = String.format("[%d] during [%s] to [%s] [%s]",
                response.status(),
                response.request().httpMethod(),
                response.request().url(),
                methodKey);

        return switch (response.status()) {
            case 400 -> new IllegalArgumentException(errorMessage);
            case 401 -> new AccessDeniedException("Authentication failed - please provide a valid JWT token");
            case 403 -> new AccessDeniedException("Access denied - insufficient permissions");
            case 404 -> decodeNotFound(methodKey, errorMessage);
            case 409 -> new IllegalStateException("Resource conflict: " + errorMessage);
            case 422 -> new IllegalArgumentException("Validation failed: " + errorMessage);
            case 500 -> decodeServerError(methodKey, errorMessage, response);
            case 502, 503, 504 -> decodeUnavailable(methodKey, errorMessage);
            default -> defaultDecoder.decode(methodKey, response);
        };
    }

    private Exception decodeNotFound(String methodKey, String errorMessage) {
        if (methodKey.contains("CartClient")) {
            return new CartValidationException(
                    "No active cart found. Please add items to your cart before creating an order.");
        }
        if (methodKey.contains("CouponClient")) {
            return new CouponValidationException("Coupon not found or invalid");
        }
        if (methodKey.contains("UserClient")) {
            return new AccessDeniedException("User or address not found");
        }
        if (methodKey.contains("PaymentClient")) {
            return new PaymentServiceException("Payment service resource not found: " + errorMessage);
        }
        return new IllegalArgumentException("Resource not found: " + errorMessage);
    }

    private Exception decodeServerError(String methodKey, String errorMessage, feign.Response response) {
        if (methodKey.contains("PaymentClient")) {
            return new PaymentServiceException("Payment service error: " + readBody(response));
        }
        return new RuntimeException("Internal server error in external service: " + errorMessage);
    }

    private Exception decodeUnavailable(String methodKey, String errorMessage) {
        if (methodKey.contains("PaymentClient")) {
            return new PaymentServiceException("Payment service unavailable: " + errorMessage);
        }
        return new RuntimeException("External service unavailable: " + errorMessage);
    }

    private static String readBody(feign.Response response) {
        try {
            if (response.body() != null) {
                String body = new String(response.body().asInputStream().readAllBytes());
                return body.length() > 200 ? body.substring(0, 200) + "..." : body;
            }
        } catch (Exception e) {
            log.warn("Could not read Feign error body", e);
        }
        return "No error details available";
    }
}
