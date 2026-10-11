package com.mankind.notification_service.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for sending SMS notifications")
public class SmsRequest {

    @Schema(description = "Phone number of the recipient", example = "+1234567890", required = true)
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Phone number must be in E.164 format (e.g., +1234567890)")
    private String phoneNumber;

    @Schema(description = "Content of the SMS message", example = "Your verification code is 123456", required = true)
    @NotBlank(message = "Message is required")
    private String message;

    // Manual getters to ensure compilation
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getMessage() {
        return message;
    }
}