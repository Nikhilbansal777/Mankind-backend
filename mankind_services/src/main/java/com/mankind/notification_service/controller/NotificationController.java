package com.mankind.notification_service.controller;

import com.mankind.notification_service.model.NotificationRequest;
import com.mankind.notification_service.service.NotificationService;
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
@Tag(name = "Notification Controller", description = "API for sending notifications to users")
public class NotificationController {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(NotificationController.class);
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Operation(
        summary = "Send notification",
        description = "Sends a notification to a user based on the provided details. Currently supports EMAIL type only."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Notification sent successfully",
            content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid request parameters",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Failed to send notification",
            content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))
        )
    })
    @PostMapping("/send")
    public ResponseEntity<String> sendNotification(
            @Parameter(description = "Notification request details", required = true)
            @Valid @RequestBody NotificationRequest request) {
        try {
            notificationService.sendNotification(request);
            return ResponseEntity.ok("Notification sent successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send notification: " + e.getMessage());
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
        return errors;
    }
}
