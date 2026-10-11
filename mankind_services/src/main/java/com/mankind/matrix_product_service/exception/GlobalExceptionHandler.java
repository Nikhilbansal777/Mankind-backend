package com.mankind.matrix_product_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        log.error("Resource not found: {}", ex.getMessage());
        return new ResponseEntity<>(
            ErrorResponse.of(HttpStatus.NOT_FOUND.value(), ex.getMessage()),
            HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResourceException(DuplicateResourceException ex, WebRequest request) {
        log.error("Duplicate resource: {}", ex.getMessage());
        return new ResponseEntity<>(
            ErrorResponse.of(HttpStatus.CONFLICT.value(), ex.getMessage()),
            HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(InvalidOperationException.class)
    public ResponseEntity<ErrorResponse> handleInvalidOperationException(InvalidOperationException ex, WebRequest request) {
        log.error("Invalid operation: {}", ex.getMessage());
        return new ResponseEntity<>(
            ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), ex.getMessage()),
            HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = error.getObjectName() + "." + error.getDefaultMessage();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        String message = "Validation failed: " + String.join(", ", errors.values());
        log.error("Validation error: {}", message);
        return new ResponseEntity<>(
            ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), message),
            HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("Invalid argument: {}", ex.getMessage());
        return new ResponseEntity<>(
            ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), ex.getMessage()),
            HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException ex) {
        log.error("ResponseStatusException: {}", ex.getMessage());
        return new ResponseEntity<>(
            ErrorResponse.of(ex.getStatusCode().value(), ex.getReason()),
            ex.getStatusCode()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
        String errorMessage = getDescriptiveErrorMessage(ex);
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(
            ErrorResponse.of(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                errorMessage
            ),
            HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    private String getDescriptiveErrorMessage(Exception ex) {
        String className = ex.getClass().getSimpleName();
        String message = ex.getMessage();

        // Handle common exceptions with more descriptive messages
        if (ex instanceof NullPointerException) {
            return "A required value was not provided";
        } else if (ex instanceof IllegalStateException) {
            return message != null ? message : "Operation cannot be performed in the current state";
        } else if (ex instanceof ArithmeticException) {
            return "A calculation error occurred";
        } else if (ex instanceof IndexOutOfBoundsException) {
            return "An attempt was made to access an invalid position";
        } else if (ex instanceof ClassCastException) {
            return "Invalid type conversion attempted";
        } else if (message != null && !message.isEmpty()) {
            // If the exception has a message, use it but sanitize it
            return sanitizeErrorMessage(message);
        } else {
            // For unknown exceptions, provide a descriptive message based on the exception type
            return String.format("An error occurred while processing your request: %s", 
                className.replace("Exception", "").replaceAll("([A-Z])", " $1").trim());
        }
    }

    private String sanitizeErrorMessage(String message) {
        // Remove any potential sensitive information or stack traces
        return message.split("\n")[0]  // Take only the first line
                     .replaceAll("at\\s+[\\w\\.]+\\.[\\w\\$]+\\([^)]+\\)", "") // Remove stack trace patterns
                     .replaceAll("\\s+", " ")  // Normalize whitespace
                     .trim();
    }
}
