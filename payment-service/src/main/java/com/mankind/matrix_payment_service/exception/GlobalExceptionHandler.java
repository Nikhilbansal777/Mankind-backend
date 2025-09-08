package com.mankind.matrix_payment_service.exception;

import com.mankind.matrix_payment_service.dto.ErrorResponse;
import com.stripe.exception.StripeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Error")
                .message("Invalid request parameters")
                .details(errors)
                .build();

        log.warn("Validation error: {}", errors);
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message(ex.getMessage())
                .build();

        log.error("Runtime exception: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(ProviderNotImplementedException.class)
    public ResponseEntity<ErrorResponse> handleProviderNotImplemented(ProviderNotImplementedException ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_IMPLEMENTED.value())
                .error("Provider Not Implemented")
                .message(ex.getMessage())
                .build();

        log.warn("Provider not implemented: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(errorResponse);
    }

    @ExceptionHandler(StripePaymentException.class)
    public ResponseEntity<ErrorResponse> handleStripePaymentException(StripePaymentException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String errorType = "Payment Error";
        
        // Determine appropriate HTTP status based on the underlying Stripe exception
        if (ex.hasStripeException()) {
            StripeException stripeException = ex.getStripeException();
            // Map Stripe error codes to appropriate HTTP status codes
            if (stripeException.getStripeError() != null) {
                switch (stripeException.getStripeError().getType()) {
                    case "card_error":
                        status = HttpStatus.BAD_REQUEST;
                        errorType = "Card Error";
                        break;
                    case "invalid_request_error":
                        status = HttpStatus.BAD_REQUEST;
                        errorType = "Invalid Request";
                        break;
                    case "api_error":
                        status = HttpStatus.SERVICE_UNAVAILABLE;
                        errorType = "Payment Service Error";
                        break;
                    case "authentication_error":
                        status = HttpStatus.UNAUTHORIZED;
                        errorType = "Authentication Error";
                        break;
                    default:
                        status = HttpStatus.INTERNAL_SERVER_ERROR;
                        errorType = "Payment Processing Error";
                }
            }
        }
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(errorType)
                .message(ex.getMessage())
                .build();

        log.error("Stripe payment exception: {}", ex.getMessage(), ex);
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message("An unexpected error occurred")
                .build();

        log.error("Unexpected exception: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
