package com.mankind.notification_service.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for sending notifications")
public class NotificationRequest {

    @Schema(description = "Email address of the recipient", example = "user@example.com", required = true)
    @NotBlank(message = "User email is required")
    @Email(message = "Invalid email format")
    private String userEmail;

    @Schema(description = "Subject of the notification", example = "Your Order Confirmation", required = true)
    @NotBlank(message = "Subject is required")
    private String subject;

    @Schema(description = "Content of the notification message", example = "Thank you for your order. Your order #12345 has been confirmed.", required = true)
    @NotBlank(message = "Message is required")
    private String message;

    @Schema(description = "Type of notification to send", example = "EMAIL", allowableValues = {"EMAIL", "SMS", "INAPP"}, required = true)
    @NotBlank(message = "Type is required")
    @Pattern(regexp = "EMAIL|SMS|INAPP", message = "Type must be one of: EMAIL, SMS, INAPP")
    private String type;

    // Manual getters to ensure compilation
    public String getUserEmail() {
        return userEmail;
    }

    public String getSubject() {
        return subject;
    }

    public String getMessage() {
        return message;
    }

    public String getType() {
        return type;
    }
}
