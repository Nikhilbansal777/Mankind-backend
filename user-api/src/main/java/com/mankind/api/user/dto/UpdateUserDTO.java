package com.mankind.api.user.dto;

import lombok.Data;

/**
 * DTO specifically for updating user details.
 * Only includes fields that are allowed to be updated via the PUT API.
 */
@Data
public class UpdateUserDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String profilePictureUrl;
}