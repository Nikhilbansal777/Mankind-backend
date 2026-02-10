package com.mankind.corporateauthservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CorporateRegistrationRequest {
    @NotBlank
    private String corporateName;

    @NotBlank
    @Email
    @Pattern(
            regexp = "^(?i)(?!.*@(gmail|yahoo)\\.com$).+$",
            message = "Only corporate email addresses are allowed"
    )
    private String email;

    @NotBlank
    private String password;

    @NotNull
    @PastOrPresent
    private LocalDate dateOfJoining;
}
