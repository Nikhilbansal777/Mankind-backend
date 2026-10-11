package com.mankind.matrix_cart_service.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

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
    private List<String> stackTrace;

    public static ErrorResponseBuilder builder() {
        return new ErrorResponseBuilder();
    }

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

    public static class ErrorResponseBuilder {
        private int status;
        private String message;
        private LocalDateTime timestamp = LocalDateTime.now();
        private String error;
        private List<String> stackTrace;

        public ErrorResponseBuilder status(int status) {
            this.status = status;
            return this;
        }

        public ErrorResponseBuilder message(String message) {
            this.message = message;
            return this;
        }

        public ErrorResponseBuilder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public ErrorResponseBuilder error(String error) {
            this.error = error;
            return this;
        }

        public ErrorResponseBuilder stackTrace(List<String> stackTrace) {
            this.stackTrace = stackTrace;
            return this;
        }

        public ErrorResponse build() {
            return new ErrorResponse(status, message, timestamp, error, stackTrace);
        }
    }
} 