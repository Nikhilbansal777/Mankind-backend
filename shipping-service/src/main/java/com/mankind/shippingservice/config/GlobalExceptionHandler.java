package com.mankind.shippingservice.config;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<?> onBodyValidation(MethodArgumentNotValidException ex) {
    var errors = ex.getBindingResult().getFieldErrors().stream()
        .collect(Collectors.groupingBy(
            e -> e.getField(),
            Collectors.mapping(e -> e.getDefaultMessage(), Collectors.toList())
        ));
    return ResponseEntity.badRequest().body(Map.of("message", "Validation failed", "errors", errors));
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<?> onConstraint(ConstraintViolationException ex) {
    return ResponseEntity.badRequest().body(Map.of("message", "Validation failed", "errors", ex.getMessage()));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<?> onIllegalArg(IllegalArgumentException ex) {
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(Map.of("message", ex.getMessage()));
  }
}
