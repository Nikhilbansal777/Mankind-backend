package com.mankind.matrix_order_service.config;

import com.mankind.matrix_order_service.exception.CartValidationException;
import com.mankind.matrix_order_service.exception.CouponValidationException;
import com.mankind.matrix_order_service.exception.AccessDeniedException;
import feign.Logger;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
@Slf4j
public class FeignConfig {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(FeignConfig.class);

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomErrorDecoder();
    }

    @Bean
    public RequestInterceptor propagateBearerToken() {
        return template -> {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                String auth = attrs.getRequest().getHeader(HttpHeaders.AUTHORIZATION);
                if (StringUtils.hasText(auth)) {
                    template.header(HttpHeaders.AUTHORIZATION, auth);
                    logger.debug("Propagated authorization header to Feign request");
                } else {
                    logger.debug("No authorization header found in current request");
                }
            } else {
                logger.warn("No ServletRequestAttributes found in RequestContextHolder");
            }
        };
    }

    /**
     * Custom error decoder that throws appropriate exceptions based on HTTP status codes
     */
    public static class CustomErrorDecoder implements ErrorDecoder {
        @Override
        public Exception decode(String methodKey, feign.Response response) {
            String errorMessage = String.format("[%d] during [%s] to [%s] [%s]: %s", 
                response.status(), 
                response.request().httpMethod(), 
                response.request().url(), 
                methodKey, 
                getErrorMessage(response));

            switch (response.status()) {
                case 400:
                    return new IllegalArgumentException(errorMessage);
                case 401:
                    return new AccessDeniedException("Authentication failed - please provide a valid JWT token");
                case 403:
                    return new AccessDeniedException("Access denied - insufficient permissions");
                case 404:
                    if (methodKey.contains("CartClient")) {
                        return new CartValidationException("No active cart found. Please add items to your cart before creating an order.");
                    } else if (methodKey.contains("CouponClient")) {
                        return new CouponValidationException("Coupon not found or invalid");
                    } else if (methodKey.contains("UserClient")) {
                        return new AccessDeniedException("User or address not found");
                    } else {
                        return new IllegalArgumentException("Resource not found: " + errorMessage);
                    }
                case 409:
                    return new IllegalStateException("Resource conflict: " + errorMessage);
                case 422:
                    return new IllegalArgumentException("Validation failed: " + errorMessage);
                case 500:
                    return new RuntimeException("Internal server error in external service: " + errorMessage);
                case 502:
                case 503:
                case 504:
                    return new RuntimeException("External service unavailable: " + errorMessage);
                default:
                    return new RuntimeException("Unexpected error from external service: " + errorMessage);
            }
        }

        private String getErrorMessage(feign.Response response) {
            try {
                if (response.body() != null) {
                    return new String(response.body().asInputStream().readAllBytes());
                }
            } catch (Exception e) {
                logger.warn("Could not read error response body", e);
            }
            return "No error details available";
        }
    }
}
