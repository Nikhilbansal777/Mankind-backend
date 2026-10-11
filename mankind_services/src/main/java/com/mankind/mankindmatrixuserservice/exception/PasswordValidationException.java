package com.mankind.mankindmatrixuserservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

/**
 * Exception thrown when password validation fails
 * Results in HTTP 400 Bad Request status code
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PasswordValidationException extends RuntimeException {
    
    private final List<String> validationErrors;
    
    public PasswordValidationException(String message) {
        super(message);
        this.validationErrors = List.of(message);
    }
    
    public PasswordValidationException(List<String> validationErrors) {
        super("Password validation failed: " + String.join(", ", validationErrors));
        this.validationErrors = validationErrors;
    }
    
    public List<String> getValidationErrors() {
        return validationErrors;
    }
} 