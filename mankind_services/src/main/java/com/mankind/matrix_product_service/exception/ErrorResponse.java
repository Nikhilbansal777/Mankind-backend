package com.mankind.matrix_product_service.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Standardized error response for the API.
 * Note: Stack traces are only included in development mode and are sanitized
 * to include only the first 5 frames for security reasons.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private int status;
    private String message;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    
    private String error;
    
    /**
     * Stack trace information. Only included in development mode and limited to 5 frames
     * for security reasons. Always null in production.
     */
    private List<String> stackTrace;

    public static ErrorResponse of(int status, String message) {
        return ErrorResponse.builder()
                .status(status)
                .message(message)
                .build();
    }

    public static ErrorResponse of(int status, String message, String error, List<String> stackTrace) {
        return ErrorResponse.builder()
                .status(status)
                .message(message)
                .error(error)
                .stackTrace(stackTrace)
                .build();
    }
}
