package com.mankind.corporateauthservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class CorporateUserResponse {
    private Long id;
    private String corporateName;
    private String email;
    private LocalDate dateOfJoining;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
