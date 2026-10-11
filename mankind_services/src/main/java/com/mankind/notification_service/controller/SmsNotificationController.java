package com.mankind.notification_service.controller;

import com.mankind.notification_service.model.SmsRequest;
import com.mankind.notification_service.service.SmsNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/notifications")
@Tag(name = "SMS Notification Controller", description = "API for sending SMS notifications to users")
public class SmsNotificationController {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SmsNotificationController.class);
    private final SmsNotificationService smsNotificationService;

    public SmsNotificationController(SmsNotificationService smsNotificationService) {
        this.smsNotificationService = smsNotificationService;
    }

    @Operation(
        summary = "Send SMS notification",
        description = "Sends an SMS notification to a user based on the provided phone number and message."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "SMS notification sent successfully",
            content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid request parameters",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Failed to send SMS notification",
            content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))
        )
    })
    @PostMapping("/sms")
    public ResponseEntity<String> sendSmsNotification(
            @Parameter(description = "SMS notification request details", required = true)
            @Valid @RequestBody SmsRequest request) {
        try {
            log.info("Received request to send SMS to {}", request.getPhoneNumber());
            smsNotificationService.sendSms(request.getPhoneNumber(), request.getMessage());
            return ResponseEntity.ok("SMS notification sent successfully");
        } catch (Exception e) {
            log.error("Failed to send SMS notification", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send SMS notification: " + e.getMessage());
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        log.warn("Validation error in SMS request: {}", errors);
        return errors;
    }
}