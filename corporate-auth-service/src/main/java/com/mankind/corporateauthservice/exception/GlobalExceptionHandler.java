package com.mankind.corporateauthservice.exception;

import com.mankind.corporateauthservice.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        log.warn("Validation failed for path {}", request.getRequestURI());
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String key = error instanceof FieldError fieldError ? fieldError.getField() : error.getObjectName();
            errors.put(key, error.getDefaultMessage());
        });

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Validation Error",
                "Invalid request parameters",
                request,
                errors
        );
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(
            ResponseStatusException ex,
            HttpServletRequest request
    ) {
        log.warn(
                "ResponseStatusException on path {} with status {} and reason {}",
                request.getRequestURI(),
                ex.getStatusCode().value(),
                ex.getReason()
        );
        HttpStatusCode statusCode = ex.getStatusCode();
        HttpStatus status = HttpStatus.resolve(statusCode.value());
        String reason = ex.getReason() == null ? "Request failed" : ex.getReason();

        if (status == null) {
            return buildErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Internal Server Error",
                    reason,
                    request,
                    null
            );
        }

        return buildErrorResponse(
                status,
                status.getReasonPhrase(),
                reason,
                request,
                null
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex,
            HttpServletRequest request
    ) {
        log.warn("Data integrity violation on path {}", request.getRequestURI(), ex);
        return buildErrorResponse(
                HttpStatus.CONFLICT,
                "Conflict",
                "Resource already exists",
                request,
                null
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception on path {}", request.getRequestURI(), ex);
        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                "An unexpected error occurred",
                request,
                null
        );
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(
            HttpStatus status,
            String error,
            String message,
            HttpServletRequest request,
            Map<String, String> details
    ) {
        ErrorResponse body = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(error)
                .message(message)
                .path(request.getRequestURI())
                .details(details)
                .build();

        return ResponseEntity.status(status).body(body);
    }
}
