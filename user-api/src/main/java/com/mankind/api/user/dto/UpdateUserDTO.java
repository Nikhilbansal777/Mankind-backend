package com.mankind.api.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO specifically for updating user details.
 * Only includes fields that are allowed to be updated via the PUT API.
 */
@Data
public class UpdateUserDTO {
    @Size(max = 100)
    private String firstName;
    @Size(max = 100)
    private String lastName;
    @Email
    @Size(max = 255)
    private String email;
    @Size(max = 500)
    private String profilePictureUrl;
}