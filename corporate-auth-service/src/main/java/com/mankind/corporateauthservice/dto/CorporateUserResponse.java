package com.mankind.corporateauthservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@Schema(name = "CorporateUserResponse", description = "Corporate user details")
public class CorporateUserResponse {
    @Schema(description = "Corporate user id", example = "1")
    private Long id;
    @Schema(description = "Corporate display name", example = "Acme Corp")
    private String corporateName;
    @Schema(description = "Corporate email", example = "admin@acme.com")
    private String email;
    @Schema(description = "Joining date", example = "2024-01-15")
    private LocalDate dateOfJoining;
    @Schema(description = "Record creation timestamp", example = "2024-01-16T10:00:00")
    private LocalDateTime createdAt;
    @Schema(description = "Record last update timestamp", example = "2024-01-20T11:00:00")
    private LocalDateTime updatedAt;
}
