package com.mankind.matrix_wishlistservice.dto;

/**
 * Standard success response format for API operations
 */
public class SuccessResponse {
    private String message;
    private int status;

    public SuccessResponse() {
    }

    public SuccessResponse(String message, int status) {
        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}