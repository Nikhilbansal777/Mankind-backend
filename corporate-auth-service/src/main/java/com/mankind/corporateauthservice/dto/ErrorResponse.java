package com.mankind.corporateauthservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ErrorResponse", description = "Standard API error response")
public class ErrorResponse {
    @Schema(description = "Error timestamp", example = "2026-02-16T14:20:00")
    private LocalDateTime timestamp;
    @Schema(description = "HTTP status code", example = "400")
    private int status;
    @Schema(description = "HTTP error type", example = "Validation Error")
    private String error;
    @Schema(description = "Human-readable message", example = "Invalid request parameters")
    private String message;
    @Schema(description = "Request path", example = "/api/v1/corporate/auth/register")
    private String path;
    @Schema(description = "Optional field-level validation errors")
    private Map<String, String> details;
}
