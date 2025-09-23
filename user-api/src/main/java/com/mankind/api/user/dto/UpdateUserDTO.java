package com.mankind.api.user.dto;

import com.mankind.api.user.enums.Role;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * DTO specifically for updating user details.
 * Only includes fields that are allowed to be updated via the PUT API.
 */
@Data
public class UpdateUserDTO {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private Map<String,String> customAttributes = new HashMap<>();
    private Role role;
    private String profilePictureUrl;
}